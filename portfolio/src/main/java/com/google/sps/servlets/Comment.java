package com.google.sps.servlets;

/*A comment from the comments section in the home page*/
public final class Comment {

  private final long id;
  private final String title;
  private final String dateAndTime;

  public Comment(long id, String title, String dateAndTime) {
    this.id = id;
    this.title = title;
    this.dateAndTime = dateAndTime;
  }
}