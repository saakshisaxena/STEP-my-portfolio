// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

public final class FindMeetingQuery {
  private int meetingDuration;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    // The total (common) free time we have to conduct the meeting.
    LinkedList<TimeRange> freeTime = new LinkedList<TimeRange>();
    freeTime.addAll(Arrays.asList(TimeRange.WHOLE_DAY));

    meetingDuration = (int) request.getDuration();
    LinkedList<String> optionalAttendees = new LinkedList<>(request.getOptionalAttendees());

    // If duration of the meeting is greater than a whole day, then return an empty list.
    if (meetingDuration > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    // If there are no attendees then return the whole day (ie the free time we have till now).
    if (request.getAttendees().isEmpty() && optionalAttendees.isEmpty()) {
      return freeTime;
    }

    // To store all the events which the optional attendees are attending.
    LinkedList<Event> optionalAttendeeEvents = new LinkedList<>();

    /* Changes freeTime such that it now conatins all the free time slots when the meeting can
    happen, updating the events that optional attendees can attend. */
    freeSlotsWithMandatoryAttendees(
        freeTime, events, optionalAttendeeEvents, optionalAttendees, request);

    /* If free time is not empty and optional attendees are not empty
    then go ahead and look for time slots to have meeting with our optional attendees */
    if (!freeTime.isEmpty() && !optionalAttendeeEvents.isEmpty()) {
      freeTime =
          freeSlotsWithOptionalAttendees(optionalAttendeeEvents, freeTime, optionalAttendees);
    }

    return freeTime;
  }

  // Loop through all the events given and modify freeTime List
  private void freeSlotsWithMandatoryAttendees(
      LinkedList<TimeRange> freeTime,
      Collection<Event> events,
      LinkedList<Event> optionalAttendeeEvents,
      LinkedList<String> optionalAttendees,
      MeetingRequest request) {

    for (Event event : events) {
      /* Check if the event has any optional attendees and
        save it in the optionalAtttendeeEvents list which we will deal with
        later in the freeSlotsWithOptionalAttendees method */
      if (!Collections.disjoint(optionalAttendees, event.getAttendees()))
        optionalAttendeeEvents.add(event);
      else findConflictAndResolve(freeTime, new LinkedList<>(request.getAttendees()), event);
    }

  }

  // Try checking for any time slots we can get for the meeting with optional attendees
  private LinkedList<TimeRange> freeSlotsWithOptionalAttendees(
      LinkedList<Event> optionalAttendeeEvents,
      LinkedList<TimeRange> freeTime,
      LinkedList<String> optionalAttendees) {
    // Create a new OptionalFreeTime list so that we still have our time slots with mandatory
    // attendees
    LinkedList<TimeRange> optionalFreeTime = new LinkedList<>(freeTime);
    /* Loop through all the events our optional attendees are attending and
       find free time slots we can have for our meeting */
    for (Event event : optionalAttendeeEvents) {
      findConflictAndResolve(optionalFreeTime, optionalAttendees, event);
    }
    /* If we can't find any free time slots with optional attendees
      then return the original freeTime slots */
    if (optionalFreeTime.isEmpty()) optionalFreeTime = freeTime;

    return optionalFreeTime;
  }

  // This method finds the overlapping free time slot and event; altering the freeTime so that it
  // doesn't overlap anymore.
  private void findConflictAndResolve(
      LinkedList<TimeRange> time, LinkedList<String> attendees, Event event) {
    if (!Collections.disjoint(attendees, event.getAttendees())) {
      // Go through all the free time slots and check from which one is the event overlapping
      ListIterator<TimeRange> iterator = time.listIterator();
      while (iterator.hasNext()) {
        TimeRange freeSlot = iterator.next();
        // When th eoverlapping free slot is found send our free time and event slot to get altered.
        if (freeSlot.overlaps(event.getWhen())) {
          updateFreeTime(freeSlot, event.getWhen(), time, iterator);
          // After updating free time is done, check if the event ends in this free slots
          if (event.getWhen().end() <= freeSlot.end()) break;
        }
      }
    }
  }

  /* This method free time, free time slot and current event with our free time slot and the iterator
    and checks all conditions in which the event and time slot could overlap. 
    Then calculates the free time minus overlapping part. 
    It checks the new slot with the meeting slot and then adds or removes it. 
    (By removing/ setting or adding in the iterator). */
  private void updateFreeTime(
      TimeRange freeSlot,
      TimeRange eventSlot,
      LinkedList<TimeRange> freeTime,
      ListIterator<TimeRange> iterator) {

    int freeSlotStart = freeSlot.start();
    int freeSlotEnd = freeSlot.end();

    int eventSlotStart = eventSlot.start();
    int eventSlotEnd = eventSlot.end();

    // Case: Free time: |---|
    // Event slot:    |-------|
    if (eventSlotStart <= freeSlotStart && eventSlotEnd >= freeSlotEnd) {
      iterator.remove();
    }

    // Case: Free time: |--------|
    // Event slot:     |-----|
    // or
    // |------|
    // |--|
    else if (eventSlotStart <= freeSlotStart && eventSlotEnd < freeSlotEnd) {
      TimeRange newSlot = TimeRange.fromStartEnd(eventSlotEnd, freeSlotEnd, false);
      if (isSlotTimeEnough(newSlot)) iterator.set(newSlot);
      else iterator.remove();
    }

    // Case: Free time: |--------|
    // Event slot:            |-----|
    // or
    // |------|
    //     |--|
    else if (eventSlotStart > freeSlotStart && eventSlotEnd >= freeSlotEnd) {
      TimeRange newSlot = TimeRange.fromStartEnd(freeSlotStart, eventSlotStart, false);
      if (isSlotTimeEnough(newSlot)) iterator.set(newSlot);
      else iterator.remove();

    }

    // Case: Free time: |---------|
    // Event slot:        |-----|
    else if (eventSlotStart > freeSlotStart && eventSlotEnd < freeSlotEnd) {
      boolean removedFirst= false;

      TimeRange newSlot = TimeRange.fromStartEnd(freeSlotStart, eventSlotStart, false);
      if (isSlotTimeEnough(newSlot)) iterator.set(newSlot);
      else {
          iterator.remove();
          removedFirst = true;
      }

      newSlot = TimeRange.fromStartEnd(eventSlotEnd, freeSlotEnd, false);

      if (isSlotTimeEnough(newSlot)) iterator.add(newSlot);
      else {
          if(!removedFirst)
          iterator.remove();
      }
    }
  }

  private boolean isSlotTimeEnough(TimeRange slot) {
    return (slot.duration() >= meetingDuration);
  }
}
