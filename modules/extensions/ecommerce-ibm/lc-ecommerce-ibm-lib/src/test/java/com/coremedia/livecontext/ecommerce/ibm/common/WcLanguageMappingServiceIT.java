package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.catalog.WcCatalogWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {IbmServiceTestBase.LocalConfig.class, AbstractWrapperServiceTestCase.LocalConfig.class})
@ActiveProfiles({IbmServiceTestBase.LocalConfig.PROFILE})
public class WcLanguageMappingServiceIT extends AbstractWrapperServiceTestCase {

  @Inject
  private WcCatalogWrapperService testling;

  @Inject
  protected Commerce commerce;
  protected CommerceConnection connection;

  @Inject
  protected StoreInfoService storeInfoService;

  @MockBean
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Before
  public void setup() {
    doAnswer(invocationOnMock -> Optional.of(CurrentCommerceConnection.get())).when(commerceConnectionInitializer).findConnectionForSite(any(Site.class));
    connection = commerce.findConnection("wcs1")
            .orElseThrow(() -> new IllegalStateException("Could not obtain commerce connection."));

    storeInfoService.getWcsVersion().ifPresent(testConfig::setWcsVersion);
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
    assertThat(testling.getLanguageMapping()).isNotNull();

    StoreContext storeContext = connection.getStoreContext();
    CatalogAlias catalogAlias = CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
    CatalogId catalogId = testling.findCatalogId(catalogAlias, storeContext)
            .orElseThrow(() -> new IllegalStateException("Catalog alias must map to a catalog ID."));

    Currency currency = Currency.getInstance("USD");
    String userName = "forUser";

    Map<String, String[]> parametersMap;

    parametersMap = testling.buildParameterMap()
            .withCatalogId(catalogId)
            .withCurrency(currency)
            .withLanguageId(new Locale("en", "EN"))
            .withUserName(userName)
            .build();
    assertThat(parametersMap.get("langId")[0]).isEqualTo("-1");

    parametersMap = testling.buildParameterMap()
            .withCatalogId(catalogId)
            .withCurrency(currency)
            .withLanguageId(new Locale("en"))
            .withUserName(userName)
            .build();
    assertThat(parametersMap.get("langId")[0]).isEqualTo("-1");

    parametersMap = testling.buildParameterMap()
            .withCatalogId(catalogId)
            .withCurrency(currency)
            .withLanguageId(new Locale("de"))
            .withUserName(userName)
            .build();
    assertThat(parametersMap.get("langId")[0]).isEqualTo("-3");

    parametersMap = testling.buildParameterMap()
            .withCatalogId(catalogId)
            .withCurrency(currency)
            .withLanguageId(new Locale("xx"))
            .withUserName(userName)
            .build();
    assertThat(parametersMap.get("langId")[0]).isEqualTo("-1");
  }
}
