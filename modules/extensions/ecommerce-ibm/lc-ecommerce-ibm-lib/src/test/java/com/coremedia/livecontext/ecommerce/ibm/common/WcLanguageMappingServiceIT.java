package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.catalog.WcCatalogWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {IbmServiceTestBase.LocalConfig.class, AbstractWrapperServiceTestCase.LocalConfig.class})
@ActiveProfiles({IbmServiceTestBase.LocalConfig.PROFILE})
public class WcLanguageMappingServiceIT extends AbstractWrapperServiceTestCase {

  private static final String CATALOG_ID = System.getProperty("lc.test.catalogId", "10001");

  @Inject
  private WcCatalogWrapperService testling;

  @Inject
  protected Commerce commerce;
  protected CommerceConnection connection;

  @Inject
  protected StoreInfoService storeInfoService;

  @Before
  public void setup() {
    connection = commerce.getConnection("wcs1");
    testConfig.setWcsVersion(storeInfoService.getWcsVersion());
    connection.setStoreContext(testConfig.getStoreContext());
    CurrentCommerceConnection.set(connection);
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Betamax(tape = "wcws_testLanguageMapping", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testLanguageMapping() throws IOException {
    assertNotNull(testling.getLanguageMapping());

    StoreContext storeContext = connection.getStoreContext();
    CatalogAlias catalogAlias = CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;

    Map<String, String[]> parametersMap = testling.createParametersMap(
            catalogAlias, new Locale("en", "EN"), Currency.getInstance("USD"), null, "forUser", null, storeContext);
    assertTrue(parametersMap.get("langId")[0].equals("-1"));
    parametersMap = testling.createParametersMap(
            catalogAlias, new Locale("en"), Currency.getInstance("USD"), null, "forUser", null, storeContext);
    assertTrue(parametersMap.get("langId")[0].equals("-1"));
    parametersMap = testling.createParametersMap(
            catalogAlias, new Locale("de"), Currency.getInstance("USD"), null, "forUser", null, storeContext);
    assertTrue(parametersMap.get("langId")[0].equals("-3"));
    parametersMap = testling.createParametersMap(
            catalogAlias, new Locale("xx"), Currency.getInstance("USD"), null, "forUser", null, storeContext);
    assertTrue(parametersMap.get("langId")[0].equals("-1"));
  }
}
