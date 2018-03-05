package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.ProductsResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration
public class ProductsResourceIT extends ShopApiResourceTestBase {

  private static final Currency CURRENCY_EUR = Currency.getInstance("EUR");
  private static final Currency CURRENCY_JPY = Currency.getInstance("JPY");
  private static final Currency CURRENCY_CNY = Currency.getInstance("CNY");

  private static final String STORE_ID = "SiteGenesisGlobal";
  private static final String PRODUCT_ID = "25791388";

  @Autowired
  private ProductsResource resource;

  @Test
  public void testGetProductById() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<ProductDocument> doc = resource.getProductById(PRODUCT_ID, STORE_ID);
    assertThat(doc).isPresent();

    assertThat(doc).map(ProductDocument::getCurrency).hasValue("GBP");
    assertThat(doc).map(ProductDocument::getPrice).hasValue(63.36);
  }

  @Test
  public void testGetProductByIdFrench() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<ProductDocument> doc = resource.getProductById(PRODUCT_ID, STORE_ID, Locale.FRANCE, CURRENCY_EUR);
    assertThat(doc).isPresent();

    assertThat(doc).map(ProductDocument::getCurrency).hasValue("EUR");
    assertThat(doc).map(ProductDocument::getPrice).hasValue(71.0);
  }

  @Test
  public void testGetProductByIdItalian() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<ProductDocument> doc = resource.getProductById(PRODUCT_ID, STORE_ID, Locale.ITALY, CURRENCY_EUR);
    assertThat(doc).isPresent();

    assertThat(doc).map(ProductDocument::getCurrency).hasValue("EUR");
    assertThat(doc).map(ProductDocument::getPrice).hasValue(71.0);
  }

  @Test
  public void testGetProductByIdChinese() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<ProductDocument> doc = resource.getProductById(PRODUCT_ID, STORE_ID, Locale.CHINA, CURRENCY_CNY);
    assertThat(doc).isPresent();

    assertThat(doc).map(ProductDocument::getCurrency).hasValue("CNY");
    assertThat(doc).map(ProductDocument::getPrice).hasValue(634.0);
  }

  @Test
  public void testGetProductByIdJapanese() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<ProductDocument> doc = resource.getProductById(PRODUCT_ID, STORE_ID, Locale.JAPAN, CURRENCY_JPY);
    assertThat(doc).isPresent();

    assertThat(doc).map(ProductDocument::getCurrency).hasValue("JPY");
    assertThat(doc).map(ProductDocument::getPrice).hasValue(11937.0);
  }

  @Test
  public void testGetProductPrices() {
  }
}
