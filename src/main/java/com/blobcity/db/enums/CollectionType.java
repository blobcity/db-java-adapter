/**
 * Copyright 2013, BlobCity iSolutions Pvt. Ltd.
 */
package com.blobcity.db.enums;

/**
 * @author Prikshit Kumar
 */
public enum CollectionType {

  ON_DISK("on-disk"),
  IN_MEMORY("in-memory"),
  IN_MEMORY_NON_DURABLE("in-memory-nd");
  private final String type;

  CollectionType(final String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
