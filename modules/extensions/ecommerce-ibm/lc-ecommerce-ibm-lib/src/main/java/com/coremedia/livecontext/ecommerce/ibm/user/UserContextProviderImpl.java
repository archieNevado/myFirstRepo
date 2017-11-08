package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Provides access to the current {@link UserContext}. The handling itself is delegated to the {@link UserContextHelper}.
 */
public class UserContextProviderImpl implements UserContextProvider {

  private static final String STUDIO_PREVIEW_TEST_PARAM = "p13n_test";

  private UserSessionService userSessionService;

  @Override
  @Nonnull
  public UserContext getCurrentContext() {
    return UserContextHelper.getCurrentContext();
  }

  @Override
  public void setCurrentContext(UserContext userContext) {
    UserContextHelper.setCurrentContext(userContext);
  }

  @Override
  @Nonnull
  public UserContext createContext(@Nonnull HttpServletRequest request) {
    UserContext.Builder builder = UserContext.builder();

    findUserId(request).ifPresent(builder::withUserId);
    findCookieHeader(request).ifPresent(builder::withCookieHeader);

    return builder.build();
  }

  @Override
  public void clearCurrentContext() {
  }

  @Nonnull
  private Optional<String> findUserId(@Nonnull HttpServletRequest request) {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    StoreContextHelper.validateContext(storeContext);

    String userId = userSessionService.resolveUserId(request, storeContext.getStoreId(), false);
    return Optional.ofNullable(userId);
  }

  @Nonnull
  private static Optional<String> findCookieHeader(@Nonnull HttpServletRequest request) {
    if (isStudioPreviewRequest(request)) {
      return Optional.empty();
    }

    String header = request.getHeader("Cookie");
    String rewrittenHeader = WcCookieHelper.rewritePreviewCookies(header);
    return Optional.ofNullable(rewrittenHeader);
  }

  private static boolean isStudioPreviewRequest(@Nonnull HttpServletRequest request) {
    return "true".equals(request.getParameter(STUDIO_PREVIEW_TEST_PARAM));
  }

  // ------------ Config -----------------------

  @Required
  public void setUserSessionService(UserSessionService userSessionService) {
    this.userSessionService = userSessionService;
  }
}
