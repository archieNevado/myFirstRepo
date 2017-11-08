package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginServiceImpl;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class UserContextProviderImplIT extends IbmServiceTestBase {

  @Inject
  UserContextProviderImpl testling;

  @Before
  @Override
  public void setup() {
    super.setup();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @After
  public void tearDown() throws Exception {
    ((LoginServiceImpl) loginService).destroy();
  }

  @Test
  public void testCurrentUserContext() {
    UserContext userContext = UserContext.builder()
            .withUserName("currentUser")
            .build();

    testling.setCurrentContext(userContext);
    assertNotNull(testling.getCurrentContext());
    assertEquals("currentUser", UserContextHelper.getForUserName(testling.getCurrentContext()));
  }
}
