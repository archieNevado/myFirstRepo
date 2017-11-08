package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.UserContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Helper class to build an "IBM WCS conform" user context.
 * You do not have to know the exact keys if you use the helper method.
 * Use this class as static import.
 */
public class UserContextHelper {

  private UserContextHelper() {
  }

  /**
   * Set the given user context as default in the current request (thread).
   * Read the default context with #getCurrentContext().
   *
   * @param context the default context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException
   */
  public static void setCurrentContext(UserContext context) {
    CurrentCommerceConnection.find().ifPresent(connection -> connection.setUserContext(context));
  }

  /**
   * Gets the default user context within the current request (thread).
   * Set the default context with #setCurrentContext();
   *
   * @return the UserContext
   */
  @Nonnull
  public static UserContext getCurrentContext() {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getUserContext)
            .orElseGet(() -> UserContext.builder().build());
  }

  @Nullable
  public static String getForUserName(@Nullable UserContext context) {
    if (context == null) {
      return null;
    }

    Object value = context.getUserName();
    if (value == null) {
      return null;
    }

    return (String) value;
  }

  @Nullable
  public static Integer getForUserId(@Nullable UserContext context) {
    if (context == null) {
      return null;
    }

    Object value = context.getUserId();
    if (value == null) {
      return null;
    }

    try {
      return Integer.parseInt(String.valueOf(value));
    } catch (NumberFormatException ignored) {
      //ignore
    }

    return null;
  }

  public static boolean isAnonymousId(@Nullable Integer userId) {
    return userId == null || userId < 0;
  }
}
