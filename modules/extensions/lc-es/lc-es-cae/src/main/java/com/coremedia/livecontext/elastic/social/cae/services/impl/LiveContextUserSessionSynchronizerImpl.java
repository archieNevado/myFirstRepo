package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.elastic.core.api.models.UniqueConstraintViolationException;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.services.SessionSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;

/**
 * Implements an UserService to integrate with a commerce system.
 */
public class LiveContextUserSessionSynchronizerImpl implements SessionSynchronizer {

  private static final Logger LOG = LoggerFactory.getLogger(LiveContextUserSessionSynchronizerImpl.class);

  private CommunityUserService communityUserService;

  @Override
  public void synchronizeUserSession(HttpServletRequest request, HttpServletResponse response) throws GeneralSecurityException {
    UserSessionService commerceUserSessionService = getCommerceUserSessionService();
    boolean authenticatedOnCommerce = commerceUserSessionService != null && commerceUserSessionService.isLoggedIn();

    if (authenticatedOnCommerce) {
      synchronizeUserContext();
    }
  }

  private void synchronizeUserContext() {
    UserService userService = getCommerceUserService();
    com.coremedia.livecontext.ecommerce.user.User shopUser = userService!=null ? userService.findCurrentUser() : null;

    if (shopUser != null) {
      //get community user
      CommunityUser communityUser;
      try {
        communityUser = getOrCreateCommunityUser(shopUser);
      } catch (UniqueConstraintViolationException ex) {
        communityUser = getOrCreateCommunityUser(shopUser);
      }
      com.coremedia.blueprint.elastic.social.cae.user.UserContext.setUser(communityUser);
    } else {
      LOG.warn("Could not find current user {} in shop system", getUserContextProvider().getCurrentContext().getUserId());
    }
  }

  private synchronized CommunityUser getOrCreateCommunityUser(com.coremedia.livecontext.ecommerce.user.User shopUser) {
    CommunityUser communityUser = communityUserService.getUserByName(shopUser.getLogonId());
    if (communityUser == null) {
      //register communityUser if not existing
      communityUser = communityUserService.createUser(shopUser.getLogonId(), null, shopUser.getEmail1());
      communityUser.setProperty("state", CommunityUser.State.ACTIVATED);
      communityUser.save();
    }
    return communityUser;
  }

  @Required
  public void setCommunityUserService(CommunityUserService communityUserService) {
    this.communityUserService = communityUserService; //NOSONAR - the setter is called before the service is used.
  }

  public UserContextProvider getUserContextProvider() {
    CommerceConnection connection = Commerce.getCurrentConnection();
    return connection!=null ? connection.getUserContextProvider() : null;
  }

  public UserSessionService getCommerceUserSessionService() {
    CommerceConnection connection = Commerce.getCurrentConnection();
    return connection!=null ? connection.getUserSessionService() : null;
  }

  public UserService getCommerceUserService() {
    CommerceConnection connection = Commerce.getCurrentConnection();
    return connection!=null ? connection.getUserService() : null;
  }
}
