package com.google.sps.servlets;

/** A comment from the comments section in the home page */
public final class ImageAndMessage {

  private final long id;
  private final String message;
  private final String blobKey;
  private final long timestamp;

  public ImageAndMessage(long id, String message, String blobKey, long timestamp) {
    this.id = id;
    this.message = message;
    this.blobKey = blobKey;
    this.timestamp = timestamp;
  }
}
