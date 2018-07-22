package com.coremedia.livecontext.ecommerce.sfcc.pricing;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccStoreContextProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SfccTestConfig;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.SiteGenesisGlobalTestConfig;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.ShopApiResourceTestBase;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.ProductsResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PriceServiceImplIT.LocalConfig.class)
public class PriceServiceImplIT extends ShopApiResourceTestBase {

  private static final Currency CURRENCY_GBP = Currency.getInstance("GBP");
  private static final Currency CURRENCY_EUR = Currency.getInstance("EUR");
  private static final Currency CURRENCY_JPY = Currency.getInstance("JPY");
  private static final Currency CURRENCY_CNY = Currency.getInstance("CNY");

  private static final String STORE_ID = "SiteGenesisGlobal";
  private static final String PRODUCT_ID = "008884303989";

  @Autowired
  private PriceServiceImpl priceService;

  @Test
  public void testGetListPrices() {
    if (useBetamaxTapes()) {
      return;
    }

    assertThatPriceIsEqual(priceService.findListPriceForProduct(PRODUCT_ID, STORE_ID, CURRENCY_GBP), "48.00");
    assertThatPriceIsEqual(priceService.findListPriceForProduct(PRODUCT_ID, STORE_ID, CURRENCY_EUR), "54.00");
    assertThatPriceIsEqual(priceService.findListPriceForProduct(PRODUCT_ID, STORE_ID, CURRENCY_JPY), "9044.00");
    assertThatPriceIsEqual(priceService.findListPriceForProduct(PRODUCT_ID, STORE_ID, CURRENCY_CNY), "480.00");
  }

  @Test
  public void testGetOfferPrices() {
    if (useBetamaxTapes()) {
      return;
    }

    assertThatPriceIsEqual(priceService.findOfferPriceForProduct(PRODUCT_ID, STORE_ID, CURRENCY_GBP), "48.00");
    assertThatPriceIsEqual(priceService.findOfferPriceForProduct(PRODUCT_ID, STORE_ID, CURRENCY_EUR), "54.00");
    assertThatPriceIsEqual(priceService.findOfferPriceForProduct(PRODUCT_ID, STORE_ID, CURRENCY_JPY), "9044.00");
    assertThatPriceIsEqual(priceService.findOfferPriceForProduct(PRODUCT_ID, STORE_ID, CURRENCY_CNY), "480.00");
  }

  private static void assertThatPriceIsEqual(
          @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<BigDecimal> actual, String expected) {
    assertThat(actual).hasValueSatisfying(price -> assertThat(price).isEqualTo(expected));
  }

  @Configuration
  @Import(XmlRepoConfiguration.class)
  @ComponentScan(basePackageClasses = SfccStoreContextProperties.class)
  public static class LocalConfig {

    @Bean
    PriceServiceImpl sfccPriceService(@NonNull ProductsResource productsResource) {
      return new PriceServiceImpl(productsResource);
    }

    @Bean
    @Primary
    CatalogAliasTranslationService theCatalogAliasTranslationService() {
      CatalogAliasTranslationService catalogAliasTranslationService = mock(CatalogAliasTranslationService.class);

      Optional<CatalogId> catalogId = Optional.of(CatalogId.of("sitegenisis"));
      when(catalogAliasTranslationService.getCatalogIdForAlias(any(), any()))
              .thenReturn(catalogId);

      return catalogAliasTranslationService;
    }

    @Bean
    @Primary
    public SfccTestConfig testConfig() {
      return new SiteGenesisGlobalTestConfig();
    }
  }
}
