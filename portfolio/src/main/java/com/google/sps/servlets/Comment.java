package com.google.sps.servlets;

/**A comment from the comments section in the home page*/
public final class Comment {

  private final long id;
  private final String title;
  private final long timestamp;

  public Comment(long id, String title, long timestamp) {
    this.id = id;
    this.title = title;
    this.timestamp = timestamp;
  }
}
