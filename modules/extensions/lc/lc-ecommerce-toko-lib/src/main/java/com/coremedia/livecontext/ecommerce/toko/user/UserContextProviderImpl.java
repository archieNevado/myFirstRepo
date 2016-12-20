package com.coremedia.livecontext.ecommerce.toko.user;

import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import static com.coremedia.blueprint.base.livecontext.ecommerce.user.UserContextImpl.newUserContext;

public class UserContextProviderImpl implements UserContextProvider {

  private UserContext userContext;

  @Nonnull
  @Override
  public UserContext getCurrentContext() {
    return userContext;
  }

  @Override
  public void setCurrentContext(@Nullable UserContext userContext) {
    this.userContext = userContext;
  }

  @Nonnull
  @Override
  public UserContext createContext(@Nullable HttpServletRequest request, @Nullable String loginName) {
    return newUserContext();
  }

  @Nonnull
  @Override
  public UserContext createContext(@Nullable String loginName) {
    return newUserContext();
  }

  @Override
  public void clearCurrentContext() {
    this.userContext = null;
  }
}
