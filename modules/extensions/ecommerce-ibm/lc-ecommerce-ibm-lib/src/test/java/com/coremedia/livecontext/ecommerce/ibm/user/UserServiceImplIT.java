package com.coremedia.livecontext.ecommerce.ibm.user;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.UUID;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class UserServiceImplIT extends IbmServiceTestBase {

  @Inject
  UserServiceImpl testling;

  @Betamax(tape = "psi_testFindPerson", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindPerson() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = UserContext.builder().build();
    UserContextHelper.setCurrentContext(userContext);
    User user = testling.findCurrentUser();
    assertNotNull(user);
    assertNotNull(user.getLogonId());
    assertNotNull(user.getLogonId().equals(UserContextHelper.getForUserName(userContext)));

    user.getChallengeAnswer();
    user.getChallengeQuestion();
    assertEquals("Hamburg", user.getCity());
    assertEquals("D", user.getCountry());
    assertNotNull(user.getEmail1());
    assertTrue(StringUtils.isEmpty(user.getEmail2()));
    assertTrue(StringUtils.isEmpty(user.getEmail3()));
    assertEquals("Cm", user.getFirstName());
    assertEquals("Admin", user.getLastName());
    assertEquals("cmadmin", user.getLogonId());
    assertTrue(StringUtils.isEmpty(user.getLogonPassword()));
    assertTrue(StringUtils.isEmpty(user.getLogonPasswordVerify()));
    assertEquals("2", user.getUserId());
  }
}
