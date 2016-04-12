package com.coremedia.livecontext.ecommerce.ibm.user;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.user.UserContextBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@ContextConfiguration(classes = AbstractServiceTest.LocalConfig.class)
@ActiveProfiles(AbstractServiceTest.LocalConfig.PROFILE)
public class UserContextProviderImplIT extends AbstractServiceTest {

  @Inject
  UserContextProviderImpl testling;

  @Before
  public void setup() {
    super.setup();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @After
  public void tearDown() throws Exception {
    ((LoginServiceImpl)loginService).destroy();
  }

  @Test
  @Betamax(tape = "ucpi_testCreateUserContextFor", match = {MatchRule.path, MatchRule.query})
  public void testCreateUserContextFor() {
    UserContext userContext = testling.createContext("testUser");
    assertEquals(UserContextHelper.getForUserName(userContext), "testUser");
  }

  @Test
  public void testCurrentUserContext() {

    HashMap userContextContent = new HashMap();
    userContextContent.put(UserContextHelper.FOR_USER_NAME, "currentUser");
    UserContext userContext = (UserContext) UserContextBuilder.create().withValues(userContextContent).build();

    testling.setCurrentContext(userContext);
    assertNotNull(testling.getCurrentContext());
    assertEquals("currentUser", UserContextHelper.getForUserName(testling.getCurrentContext()));
  }


}
