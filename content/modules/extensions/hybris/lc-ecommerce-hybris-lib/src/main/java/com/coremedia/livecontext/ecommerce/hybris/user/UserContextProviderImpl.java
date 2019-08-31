package com.coremedia.livecontext.ecommerce.hybris.user;

import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.servlet.http.HttpServletRequest;

public class UserContextProviderImpl implements UserContextProvider {

  private UserContext userContext;

  @NonNull
  @Override
  public UserContext getCurrentContext() {
    return userContext;
  }

  @Override
  public void setCurrentContext(@Nullable UserContext userContext) {
    this.userContext = userContext;
  }

  @NonNull
  @Override
  public UserContext createContext(@NonNull HttpServletRequest request) {
    return UserContext.builder().build();
  }

  @Override
  public void clearCurrentContext() {
    this.userContext = null;
  }
}
