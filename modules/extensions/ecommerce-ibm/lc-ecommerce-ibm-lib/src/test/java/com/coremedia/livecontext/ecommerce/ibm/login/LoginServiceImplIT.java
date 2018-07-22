package com.coremedia.livecontext.ecommerce.ibm.login;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.common.InvalidLoginException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class LoginServiceImplIT extends IbmServiceTestBase {

  @Inject
  private LoginServiceImpl testling;

  private String origServiceUser;
  private String origServicePassword;

  @Before
  @Override
  public void setup() {
    super.setup();
    origServiceUser = testling.getServiceUser();
    origServicePassword = testling.getServicePassword();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @After
  public void tearDown() throws Exception {
    testling.destroy();
    testling.setServiceUser(origServiceUser);
    testling.setServicePassword(origServicePassword);
  }

  @Test
  @Betamax(tape = "lsi_testLoginSuccess", match = {MatchRule.path, MatchRule.query})
  public void testLoginSuccess() throws Exception {
    WcCredentials credentials = testling.loginServiceIdentity(StoreContextHelper.getCurrentContextOrThrow());
    assertThat(credentials).isNotNull();
    assertThat(credentials.getSession()).isNotNull();
  }

  /**
   * Attention: this test does not work with betamax tapes. It only works against the wcs system.
   * The reason is: the existence of a workspace id induces the request of a preview token that can only
   * be done by https. https calls are currently not supported by betamax. The test independently detects a
   * "ignoreHosts" configuration and runs only if such a java property is found.
   * <p>
   * The other restriction is, the wcs system has to support workspaces.
   * permanently.
   */
  @Test
  public void testLoginSuccessWithWorkspaces() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    StoreContext storeContext = testConfig.getStoreContextWithWorkspace();
    StoreContextHelper.setCurrentContext(storeContext);
    WcCredentials credentials = testling.loginServiceIdentity(storeContext);
    assertThat(credentials).isNotNull();
    assertThat(credentials.getSession()).isNotNull();
  }

  @Test(expected = InvalidLoginException.class)
  @Betamax(tape = "lsi_testLoginFailure", match = {MatchRule.path, MatchRule.query, MatchRule.body})
  public void testLoginFailure() throws Exception {
    testling.setServicePassword("wrong password");
    testling.loginServiceIdentity(StoreContextHelper.getCurrentContextOrThrow());
  }

  @Test
  @Betamax(tape = "lsi_testLogout", match = {MatchRule.path, MatchRule.query})
  public void testLogout() throws Exception {
    WcCredentials credentials = testling.loginServiceIdentity(StoreContextHelper.getCurrentContextOrThrow());
    assertThat(credentials).isNotNull();
    boolean success = testling.logoutServiceIdentity(StoreContextHelper.getCurrentContextOrThrow());
    assertThat(success).isTrue();
  }

  @Test
  public void testGetPreviewToken() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    WcPreviewToken previewToken = testling.getPreviewToken(StoreContextHelper.getCurrentContextOrThrow());
    assertThat(previewToken).isNotNull();
    assertThat(previewToken.getPreviewToken()).isNotEmpty();
  }

}
