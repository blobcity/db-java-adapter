package com.blobcity.db.update;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanketsarang on 04/02/17.
 */
public class Update {

  private Map<String, Object> updateMap = new HashMap<String, Object>();

  public Update set(final String column, final Object value) {
    this.updateMap.put(column, value);
    return this;
  }

  public Update where(final String column, final Object value) {
    return this;
  }
}
