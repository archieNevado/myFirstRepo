package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.ibm.SystemProperties;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWrapperServiceTestCase;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WcPersonWrapperServiceTest extends AbstractWrapperServiceTestCase {

  @Inject
  private WcPersonWrapperService testling;
  @Inject
  protected Commerce commerce;
  protected CommerceConnection connection;
  @Inject
  protected StoreInfoService storeInfoService;

  @Before
  public void setup() {
    testling.clearLanguageMapping();
    connection = commerce.getConnection("wcs1");
    testConfig.setWcsVersion(storeInfoService.getWcsVersion());
    connection.setStoreContext(testConfig.getStoreContext());
    Commerce.setCurrentConnection(connection);
  }

  @Test
  public void testRegisterUser() throws Exception {
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) {
      return;
    }

    String testUser = "testuser" + (System.currentTimeMillis() + "").hashCode();

    Map<String, Object> personMap = testling.registerPerson(testUser, "passw0rd", testUser + "@coremedia.com", testConfig.getStoreContext());
    assertNotNull(personMap);
    assertEquals("logonId should be identical", testUser, DataMapHelper.getValueForKey(personMap, "logonId", String.class));
  }

  @Test
  public void testRegisterUserAsAnonymous() throws Exception {
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) {
      return;
    }

    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(null);
    userContext.setUserId("-1002");

    UserContextHelper.setCurrentContext(userContext);
    String testUser = "testuser" + (System.currentTimeMillis() + "").hashCode();

    Map<String, Object> personMap = testling.registerPerson(testUser, "passw0rd", testUser + "@coremedia.com", testConfig.getStoreContext());
    assertNotNull(personMap);
    assertEquals("logonId should be identical", testUser, DataMapHelper.getValueForKey(personMap, "logonId", String.class));
  }
}
