package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.elastic.core.api.models.UniqueConstraintViolationException;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.services.SessionSynchronizer;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
import java.util.Optional;

import static com.coremedia.common.logging.BaseMarker.PERSONAL_DATA;

/**
 * Implements an UserService to integrate with a commerce system.
 */
public class LiveContextUserSessionSynchronizerImpl implements SessionSynchronizer {

  private static final Logger LOG = LoggerFactory.getLogger(LiveContextUserSessionSynchronizerImpl.class);

  private CommunityUserService communityUserService;

  @Override
  public void synchronizeUserSession(HttpServletRequest request, HttpServletResponse response)
          throws GeneralSecurityException {
    CommerceConnection commerceConnection = CurrentCommerceConnection.find().orElse(null);

    if (isAuthenticatedOnCommerce(commerceConnection)) {
      synchronizeUserContext(commerceConnection);
    }
  }

  private static boolean isAuthenticatedOnCommerce(@Nullable CommerceConnection commerceConnection)
          throws CredentialExpiredException {
    UserSessionService commerceUserSessionService = Optional.ofNullable(commerceConnection)  // Nasty!
            .map(CommerceConnection::getUserSessionService)
            .orElse(null);

    return commerceUserSessionService != null && commerceUserSessionService.isLoggedIn();
  }

  private void synchronizeUserContext(@Nullable CommerceConnection commerceConnection) {
    User shopUser = Optional.ofNullable(commerceConnection)  // Nasty!
            .flatMap(LiveContextUserSessionSynchronizerImpl::findCurrentUser)
            .orElse(null);

    if (shopUser == null) {
      String currentUserId = Optional.ofNullable(commerceConnection)  // Nasty!
              .flatMap(LiveContextUserSessionSynchronizerImpl::findCurrentUserId)
              .orElse(null);

      LOG.warn(PERSONAL_DATA, "Could not find current user '{}' in shop system", currentUserId);
      return;
    }

    CommunityUser communityUser = getOrCreateCommunityUserWithRetry(shopUser);
    com.coremedia.blueprint.elastic.social.cae.user.UserContext.setUser(communityUser);
  }

  @NonNull
  private static Optional<User> findCurrentUser(@NonNull CommerceConnection commerceConnection) {
    return Optional.of(commerceConnection)  // Nasty!
            .map(CommerceConnection::getUserService)
            .map(UserService::findCurrentUser);
  }

  @NonNull
  private static Optional<String> findCurrentUserId(@NonNull CommerceConnection commerceConnection) {
    return Optional.of(commerceConnection)  // Nasty!
            .map(CommerceConnection::getUserContextProvider)
            .map(UserContextProvider::getCurrentContext)
            .map(UserContext::getUserId);
  }

  @NonNull
  private CommunityUser getOrCreateCommunityUserWithRetry(@NonNull User shopUser) {
    try {
      return getOrCreateCommunityUser(shopUser);
    } catch (UniqueConstraintViolationException ex) {
      return getOrCreateCommunityUser(shopUser);
    }
  }

  @NonNull
  private synchronized CommunityUser getOrCreateCommunityUser(@NonNull User shopUser) {
    CommunityUser communityUser = communityUserService.getUserByName(shopUser.getLogonId());

    if (communityUser == null) {
      communityUser = registerCommunityUser(shopUser.getLogonId(), shopUser.getEmail1());
    }

    return communityUser;
  }

  @NonNull
  private CommunityUser registerCommunityUser(String name, String emailAddress) {
    CommunityUser communityUser = communityUserService.createUser(name, null, emailAddress);
    communityUser.setProperty("state", CommunityUser.State.ACTIVATED);
    communityUser.save();
    return communityUser;
  }

  @Required
  public void setCommunityUserService(CommunityUserService communityUserService) {
    this.communityUserService = communityUserService; //NOSONAR - the setter is called before the service is used.
  }
}
