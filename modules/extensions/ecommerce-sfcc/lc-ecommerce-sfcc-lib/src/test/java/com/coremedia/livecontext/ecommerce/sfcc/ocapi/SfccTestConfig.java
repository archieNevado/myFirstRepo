package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.blueprint.lc.test.TestConfig;

public abstract class SfccTestConfig implements TestConfig {

  @Override
  public String getConnectionId() {
    return "sfcc1";
  }

  @Override
  public String getCatalogName() {
    return null;
  }
}
