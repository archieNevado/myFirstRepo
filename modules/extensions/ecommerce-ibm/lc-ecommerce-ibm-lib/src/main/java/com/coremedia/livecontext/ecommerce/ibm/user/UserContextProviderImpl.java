package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Provides access to the current {@link UserContext}. The handling itself is delegated to the {@link UserContextHelper}.
 */
public class UserContextProviderImpl implements UserContextProvider {

  private static final String STUDIO_PREVIEW_TEST_PARAM = "p13n_test";

  @Nullable
  private UserSessionService userSessionService;

  @Override
  @NonNull
  public UserContext getCurrentContext() {
    return UserContextHelper.getCurrentContext();
  }

  @Override
  public void setCurrentContext(UserContext userContext) {
    UserContextHelper.setCurrentContext(userContext);
  }

  @Override
  @NonNull
  public UserContext createContext(@NonNull HttpServletRequest request) {
    UserContext.Builder builder = UserContext.builder();

    findUserId(request).ifPresent(builder::withUserId);
    findCookieHeader(request).ifPresent(builder::withCookieHeader);

    return builder.build();
  }

  @Override
  public void clearCurrentContext() {
  }

  @NonNull
  private Optional<String> findUserId(@NonNull HttpServletRequest request) {
    if (userSessionService == null) {
      return Optional.empty();
    }

    StoreContext storeContext = StoreContextHelper.getCurrentContextOrThrow();
    StoreContextHelper.validateContext(storeContext);

    String userId = userSessionService.resolveUserId(request, storeContext.getStoreId(), false);
    return Optional.ofNullable(userId);
  }

  @NonNull
  private static Optional<String> findCookieHeader(@NonNull HttpServletRequest request) {
    if (isStudioPreviewRequest(request)) {
      return Optional.empty();
    }

    String header = request.getHeader("Cookie");
    String rewrittenHeader = WcCookieHelper.rewritePreviewCookies(header);
    return Optional.ofNullable(rewrittenHeader);
  }

  private static boolean isStudioPreviewRequest(@NonNull HttpServletRequest request) {
    return "true".equals(request.getParameter(STUDIO_PREVIEW_TEST_PARAM));
  }

  // ------------ Config -----------------------

  @Autowired(required = false)
  public void setUserSessionService(UserSessionService userSessionService) {
    this.userSessionService = userSessionService;
  }
}
