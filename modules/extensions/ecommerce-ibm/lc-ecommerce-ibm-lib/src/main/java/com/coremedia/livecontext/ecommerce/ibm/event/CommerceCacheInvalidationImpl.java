package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidation;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * A cache invalidation message received form the commerce system.
 */
class CommerceCacheInvalidationImpl implements CommerceCacheInvalidation {

  private final Map<String, Object> delegate = new HashMap<>();

  @Override
  public long getTimestamp() {
    return DataMapHelper.getValueForKey(delegate, "timestamp", -1L);
  }

  @Override
  public String getTechId() {
    return DataMapHelper.getValueForKey(delegate, "techId", String.class);
  }

  void setTechId(String techId) {
    delegate.put("techId", techId);
  }

  @Override
  public String getContentType() {
    return DataMapHelper.getValueForKey(delegate, "contentType", String.class);
  }

  void setContentType(String contentType) {
    delegate.put("contentType", contentType);
  }

  @Override
  public String getId() {
    return DataMapHelper.getValueForKey(delegate, "id", String.class);
  }

  void setName(String name) {
    delegate.put("name", name);
  }

  void putAll(Map<String, Object> delegate) {
    this.delegate.putAll(delegate);
  }

  Map<String, Object> getDelegate() {
    return delegate;
  }
}