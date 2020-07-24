package com.google.sps.servlets;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**A comment from the comments section in the home page*/
public final class Comment {

  private final Key id;
  private final String title;
  private final long timestamp;

  public Comment(Key id, String title, long timestamp) {
    this.id = id;
    this.title = title;
    this.timestamp = timestamp;
  }
}
