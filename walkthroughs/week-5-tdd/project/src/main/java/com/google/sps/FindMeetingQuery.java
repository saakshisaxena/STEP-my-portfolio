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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    //The total (common) free time we have to conduct the meeting. 
    ArrayList<TimeRange> free_time = new ArrayList<TimeRange>();
    free_time.addAll(Arrays.asList(TimeRange.WHOLE_DAY));

    int meeting_duration = (int) request.getDuration();
    
    //If duration of the meeting is greater than a whole day, then return an empty list.
    if (meeting_duration > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    //If there are no attendees then return the whole day (ie the free time we have till now).
    if (request.getAttendees().isEmpty()) {
      return free_time;
    }

    /**Go through every event. If an event has attendees of the requested meeting,
     * then check if the time overlaps with our free time.
     * If yes then remove that conflicted time slot from our free time.*/ 
    for (Event event : events) {
      if (!Collections.disjoint(new ArrayList<>(request.getAttendees()), event.getAttendees())) {
        int conflicted_slot_index = containsEvent(free_time, event.getWhen());
        if (conflicted_slot_index != -1) {
          free_time = getNewFreeTime(free_time.get(conflicted_slot_index), event.getWhen(), free_time);
        }
      }
    }

    // If any of the TimeRange in our freetime list has a 
    // duration less than the requested meeting duration then remove that slot.
    free_time = checkDuration(free_time, meeting_duration);

    return free_time;
  }

  // This method checks if an event overlaps with any of our free time slots.
  // It returns the conflicted slot's 
  private int containsEvent(ArrayList<TimeRange> free_time, TimeRange event_timerange) {
    for (TimeRange slot : free_time) {
      if (slot.overlaps(event_timerange)) return free_time.indexOf(slot);
    }
    return -1;
  }

  /** This method free time , conflicted free time slot and current event which we are looping through
   * and checks all conditions in which they could overlap.
   * Then returns the free time minus overlapping part. */
  private ArrayList<TimeRange> getNewFreeTime(
      TimeRange free_slot, TimeRange event_slot, ArrayList<TimeRange> free_time) {

    int free_slot_start = free_slot.start();
    int free_slot_end = free_slot.end();

    int event_slot_start = event_slot.start();
    int event_slot_end = event_slot.end();

    // Case: Free time: |---|
    // Event slot:    |-------|
    if (event_slot_start < free_slot_start && event_slot_end > free_slot_end) {
      free_time.remove(free_slot);
    } 
    
    // Case: Free time: |--------|
    // Event slot:     |-----|
    // or
    // |------|
    // |--|
    else if (event_slot_start <= free_slot_start && event_slot_end < free_slot_end) {
      free_time.remove(free_slot);
      free_time.addAll(Arrays.asList(TimeRange.fromStartEnd(event_slot_end, free_slot_end, false)));
    } 
    
    // Case: Free time: |--------|
    // Event slot:            |-----|
    // or
    // |------|
    //     |--|    
    else if (event_slot_start > free_slot_start && event_slot_end >= free_slot_end) {
      free_time.remove(free_slot);
      free_time.addAll(
          Arrays.asList(TimeRange.fromStartEnd(free_slot_start, event_slot_start, false)));
    } 
    
    // Case: Free time: |--------|
    // Event slot:        |-----|    
    else if (event_slot_start > free_slot_start && event_slot_end < free_slot_end) {
      free_time.remove(free_slot);
      free_time.addAll(
          Arrays.asList(
              TimeRange.fromStartEnd(free_slot_start, event_slot_start, false),
              TimeRange.fromStartEnd(event_slot_end, free_slot_end, false)));
    }

    //else return the original free time
    return free_time;
  }

  /** This method checks the duration of each free time slot compares it to the requested meeting duration.
   * If the free time slot can not accommodate the meeting duration then
   * it removes that free time slot from the options (ie free time) */
  private ArrayList<TimeRange> checkDuration(ArrayList<TimeRange> free_time, int meeting_duration) {
    for (int i = 0; i < free_time.size(); i++) {
      if (free_time.get(i).duration() < meeting_duration) free_time.remove(i);
    }
    return free_time;
  }
}
