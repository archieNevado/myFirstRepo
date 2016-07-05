package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.login.WcCredentials;

import javax.annotation.Nonnull;

class PreviewUserCacheKey extends AbstractCommerceCacheKey<WcCredentials> {
  private String username = "preview";
  private String password = "passw0rd";
  private final LoginService loginService;

  public PreviewUserCacheKey(String username, String password, StoreContext storeContext, CommerceCache commerceCache, @Nonnull LoginService loginService) {
    super(username, storeContext, CONFIG_KEY_PREVIEW_USER_CREDENTIALS, commerceCache);
    this.username = username;
    this.password = password;
    this.loginService = loginService;
  }

  @Override
  public WcCredentials computeValue(Cache cache) {
    return loginService.loginIdentity(username, password);
  }

  @Override
  public void addExplicitDependency(WcCredentials wcCredentials) {
  }
}
