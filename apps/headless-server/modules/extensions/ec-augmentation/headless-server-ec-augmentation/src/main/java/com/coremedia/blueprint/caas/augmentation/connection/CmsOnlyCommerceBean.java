package com.coremedia.blueprint.caas.augmentation.connection;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

@DefaultAnnotation(NonNull.class)
class CmsOnlyCommerceBean implements CommerceBean {

  private final CommerceId id;
  private final StoreContext storeContext;

  public CmsOnlyCommerceBean(CommerceId id, StoreContext storeContext) {
    this.id = id;
    this.storeContext = storeContext;
  }

  @NonNull
  @Override
  public CommerceId getId() {
    return id;
  }

  @Override
  public StoreContext getContext() {
    return storeContext;
  }

  @NonNull
  @Override
  public CommerceId getReference() {
    return getId();
  }

  @Override
  public Locale getLocale() {
    return storeContext.getLocale();
  }

  @Override
  public String getExternalId() {
    return id.getExternalId().orElseThrow();
  }

  @Override
  public String getExternalTechId() {
    // no tech IDs
    return getExternalId();
  }

  @NonNull
  @Override
  public Map<String, Object> getCustomAttributes() {
    return Map.of();
  }

  @Nullable
  @Override
  public <T> T getCustomAttribute(@NonNull String s, @NonNull Class<T> aClass) {
    return null;
  }

  @Override
  public void load() {
    throw new UnsupportedOperationException("load");
  }
}
