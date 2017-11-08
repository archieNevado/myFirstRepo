package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LiveContextUserSessionSynchronizerImplTest {

  private MockCommerceEnvBuilder envBuilder;

  @Test
  public void synchronizeUserSessionWithCommerceLogin() throws GeneralSecurityException {
    when(commerceConnection.getUserSessionService().isLoggedIn()).thenReturn(true);

    when(commerceConnection.getUserService().findCurrentUser()).thenReturn(commerceUser);
    when(commerceUser.getLogonId()).thenReturn("shopper");
    when(commerceUser.getEmail1()).thenReturn("shopper@mail.com");

    when(communityUserService.getUserByName("shopper")).thenReturn(communityUser);

    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);

    verifyIsLoggedIn();
  }

  @Test
  public void synchronizeUserSessionWithCommerceLoginAndUserCreation() throws GeneralSecurityException {
    when(commerceConnection.getUserSessionService().isLoggedIn()).thenReturn(true);
    when(communityUserService.getUserByName("shopper")).thenReturn(null);

    when(commerceUser.getLogonId()).thenReturn("shopper");
    when(commerceUser.getEmail1()).thenReturn("shopper@mail.com");
    when(commerceConnection.getUserService().findCurrentUser()).thenReturn(commerceUser);

    when(communityUserService.createUser("shopper", null, "shopper@mail.com")).thenReturn(communityUser);

    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);

    verify(communityUserService).createUser("shopper", null, "shopper@mail.com");
    assertEquals(UserContext.getUser(), communityUser);

    verifyIsLoggedIn();
  }

  @Test
  public void synchronizeUserSessionNoCommerceLogin() throws GeneralSecurityException {
    when(commerceConnection.getUserSessionService().isLoggedIn()).thenReturn(false);
    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);

    verifyNoUserContext();
  }

  private void verifyIsLoggedIn() throws GeneralSecurityException {
    verify(commerceConnection.getUserSessionService()).isLoggedIn();
    assertNotNull(UserContext.getUser());
  }

  private void verifyNoUserContext(){
    assertNull(UserContext.getUser());
  }

  // The default setup simulates a correctly logged in user. That is a user who is logged into commerce as well
  // as into elastic.
  @Before
  public void defaultSetup() throws CredentialExpiredException {

    testling = spy(new LiveContextUserSessionSynchronizerImpl());
    testling.setCommunityUserService(communityUserService);
//    when(testling.getCommerceUserSessionService()).thenReturn(commerceUserSessionService);

    envBuilder = MockCommerceEnvBuilder.create();
    commerceConnection = envBuilder.setupEnv();

    UserContext.clear();
  }

  @After
  public void cleanUp(){
    com.coremedia.blueprint.elastic.social.cae.user.UserContext.clear();
    envBuilder.tearDownEnv();
  }

  private LiveContextUserSessionSynchronizerImpl testling;

  @Mock
  private User commerceUser;

  @Mock
  private CommunityUser communityUser;

  @Mock
  private UserSessionService commerceUserSessionService;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private HttpServletResponse httpServletResponse;

  private CommerceConnection commerceConnection;

}
