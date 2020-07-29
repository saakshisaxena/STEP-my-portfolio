package com.google.sps.servlets;

/**A comment from the comments section in the home page*/
public final class ImageAndMessage {

  private final long id;
  private final String message;
  private final String imageUrl;
  private final long timestamp;

  public ImageAndMessage(long id, String message,String imageUrl, long timestamp) {
    this.id = id;
    this.message = message;
    this.imageUrl = imageUrl;
    this.timestamp = timestamp;
  }
}
