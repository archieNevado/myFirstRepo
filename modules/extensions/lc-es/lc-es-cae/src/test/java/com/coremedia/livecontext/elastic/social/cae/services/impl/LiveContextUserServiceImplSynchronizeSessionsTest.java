package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.UserPrincipal;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class, UserContext.class})
public class LiveContextUserServiceImplSynchronizeSessionsTest {

  @Test
  public void synchronizeUserSessionNoLocalButCommerceLogin() throws GeneralSecurityException {
    when(securityContext.getAuthentication()).thenReturn(null);
    when(commerceConnection.getUserSessionService().isLoggedIn()).thenReturn(true);

    when(commerceConnection.getUserService().findCurrentUser()).thenReturn(commerceUser);
    when(commerceUser.getLogonId()).thenReturn("shopper");
    when(commerceUser.getEmail1()).thenReturn("shopper@mail.com");

    when(communityUserService.getUserByName("shopper")).thenReturn(communityUser);

    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);

    verifyIsLoggedIn(true);
    verifyLogoutIsCalled(false);
  }

  @Test
  public void synchronizeUserSessionNoLocalButCommerceLoginWithUserCreation() throws GeneralSecurityException {
    when(securityContext.getAuthentication()).thenReturn(null);
    when(commerceConnection.getUserSessionService().isLoggedIn()).thenReturn(true);
    when(communityUserService.getUserByName("shopper")).thenReturn(null);

    when(commerceUser.getLogonId()).thenReturn("shopper");
    when(commerceUser.getEmail1()).thenReturn("shopper@mail.com");
    when(commerceConnection.getUserService().findCurrentUser()).thenReturn(commerceUser);

    when(communityUserService.createUser("shopper", null, "shopper@mail.com")).thenReturn(communityUser);

    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);

    verify(communityUserService).createUser("shopper", null, "shopper@mail.com");
    assertEquals(UserContext.getUser(), communityUser);

    verifyIsLoggedIn(true);
    verifyLogoutIsCalled(false);
  }

  @Test
  public void synchronizeUserSessionNoLocalAndNoCommerceLogin() throws GeneralSecurityException {
    when(securityContext.getAuthentication()).thenReturn(null);
    when(commerceConnection.getUserSessionService().isLoggedIn()).thenReturn(false);
    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);

    verifyIsLoggedIn(false);
    verifyLogoutIsCalled(false);
  }

  @Test
  public void synchronizeUserSessionLocalLoginButNoCommerceLogin() throws GeneralSecurityException {
    when(commerceConnection.getUserSessionService().isLoggedIn()).thenReturn(false);
    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);
    verifyIsLoggedIn(false);
    verifyLogoutIsCalled(true);
  }

  @Test
  public void synchronizeUserSessionLocalLoginAndCommerceLogin() throws GeneralSecurityException {
    UserContext.setUser(communityUser);
    when(commerceConnection.getUserSessionService().isLoggedIn()).thenReturn(true);
    when(commerceConnection.getUserService().findCurrentUser()).thenReturn(commerceUser);
    when(commerceUser.getLogonId()).thenReturn("shopper");
    when(commerceUser.getEmail1()).thenReturn("shopper@mail.com");
    when(communityUserService.createUser("shopper", null, "shopper@mail.com")).thenReturn(communityUser);
    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);
    verifyIsLoggedIn(true);
    verifyLogoutIsCalled(false);
  }

  private void verifyIsLoggedIn(boolean localLogin) throws GeneralSecurityException {
    verify(commerceConnection.getUserSessionService()).isLoggedIn();
    if (localLogin) {
      assertNotNull(UserContext.getUser());
    } else {
      assertNull(UserContext.getUser());
    }
  }

  private void verifyLogoutIsCalled(boolean localLogout) throws GeneralSecurityException {
    VerificationMode localLogoutVM = localLogout ? times(1) : never();
    verify(securityContextLogoutHandler, localLogoutVM).logout(any(HttpServletRequest.class), any(HttpServletResponse.class), eq(authentication));
  }

  // The default setup simulates a correctly logged in user. That is a user who is logged into commerce as well
  // as into elastic.
  @Before
  public void defaultSetup() throws CredentialExpiredException {

    testling = new LiveContextUserServiceImpl();
    testling.setSecurityContextLogoutHandler(securityContextLogoutHandler);
    testling.setCommunityUserService(communityUserService);

    mockStatic(SecurityContextHolder.class);
    when(SecurityContextHolder.getContext()).thenReturn(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userPrincipal);

    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
/*
    Commerce.setCurrentConnection(commerceConnection);
    when(commerceConnection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(commerceConnection.getUserContextProvider()).thenReturn(userContextProvider);
    when(commerceConnection.getIdProvider()).thenReturn(new BaseCommerceIdProvider("vendor"));
    when(commerceConnection.getStoreContext()).thenReturn(getStoreContext());
    when(commerceConnection.getUserSessionService()).thenReturn(commerceUserSessionService);
    when(commerceConnection.getUserService()).thenReturn(commerceUserService);

    when(commerceUserSessionService.isLoggedIn()).thenReturn(true);
    when(storeContextProvider.getCurrentContext()).thenReturn(getStoreContext());
    when(userContextProvider.getCurrentContext()).thenReturn(getUserContext());
*/

    UserContext.clear();
  }

  private LiveContextUserServiceImpl testling;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private SecurityContextLogoutHandler securityContextLogoutHandler;

  @Mock
  private Authentication authentication;

  @Mock
  private UserPrincipal userPrincipal;

  @Mock
  private User commerceUser;

  @Mock
  private CommunityUser communityUser;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private HttpServletResponse httpServletResponse;

  private CommerceConnection commerceConnection;

}
