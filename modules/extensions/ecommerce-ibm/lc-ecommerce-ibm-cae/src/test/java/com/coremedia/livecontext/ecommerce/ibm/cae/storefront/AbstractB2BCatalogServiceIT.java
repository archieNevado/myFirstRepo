package com.coremedia.livecontext.ecommerce.ibm.cae.storefront;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.ibm.catalog.IbmCatalogServiceBaseTest;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_8;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for BOD REST interface.
 */
abstract class AbstractB2BCatalogServiceIT extends IbmCatalogServiceBaseTest {
  @Inject
  private UserSessionService userSessionService;

  private List<String> getContractIdsForUser(String user, String password) {
    HttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();

    UserContext userContextBeforeLogin = UserContext.builder().withUserId(user).withUserName(user).build();
    UserContextHelper.setCurrentContext(userContextBeforeLogin);

    boolean loginSuccess = userSessionService.loginUser(request, response, user, password);
    assertTrue(loginSuccess);

    CommerceConnection commerceConnection = CurrentCommerceConnection.get();

    UserContext userContextAfterLogin = commerceConnection.getUserContext();
    assertNotNull(userContextAfterLogin.getCookieHeader());

    Collection<Contract> contractIdsForUser = contractService.findContractIdsForUser(userContextAfterLogin,
            commerceConnection.getStoreContext());
    return contractIdsForUser.stream()
            .map(contract -> {
              assert contract != null;
              return contract.getExternalTechId();
            })
            .collect(toList());
  }

  protected void testFindSubCategoriesWithContract() throws Exception {
    if (useBetamaxTapes() || StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_8)) {
      return;
    }

    StoreContext storeContext = testConfig.getB2BStoreContext();
    StoreContextHelper.setCurrentContext(storeContext);

    Category category = findAndAssertCategory("Lighting", null, storeContext);
    assertNotNull(category);

    category = findAndAssertCategory("Fasteners", null, storeContext);
    assertNotNull(category);

    List<Category> subCategoriesNoContract = testling.findSubCategories(category);
    assertTrue(subCategoriesNoContract.size() >= 3);

    //test b2b categories with contract
    List<String> contractIds = getContractIdsForUser("bmiller", "passw0rd");

    storeContext.setContractIds(contractIds);
    category = findAndAssertCategory("Fasteners", null, storeContext);

    List<Category> subCategoriesWithContract = testling.findSubCategories(category);
    assertEquals(2, subCategoriesWithContract.size());
    assertEquals(BaseCommerceBeanType.CATEGORY, subCategoriesWithContract.get(0).getId().getCommerceBeanType());
    assertEquals("Bolts", subCategoriesWithContract.get(0).getName());
    assertEquals("Screws", subCategoriesWithContract.get(1).getName());
  }

}
