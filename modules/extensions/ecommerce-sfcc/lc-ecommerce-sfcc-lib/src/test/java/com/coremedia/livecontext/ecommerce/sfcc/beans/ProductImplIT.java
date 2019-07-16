package com.coremedia.livecontext.ecommerce.sfcc.beans;

import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.DataApiResourceTestBase;
import org.junit.Test;

import javax.inject.Inject;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductImplIT extends DataApiResourceTestBase {

  private static final CommerceId WOMENS_JEWLERY_BRACELETS = SfccCommerceIdProvider
          .commerceId(BaseCommerceBeanType.CATEGORY)
          .withExternalId("womens-jewlery-bracelets")
          .withCatalogAlias(DEFAULT_CATALOG_ALIAS)
          .build();

  @Inject
  private CommerceBeanFactory commerceBeanFactory;

  @Test
  public void testPrimaryCategoryOnProductVariant() {
    if (useBetamaxTapes()) {
      return;
    }

    CommerceId productVariantId = SfccCommerceIdProvider
            .commerceId(BaseCommerceBeanType.SKU)
            .withExternalId("013742335538")
            .withCatalogAlias(DEFAULT_CATALOG_ALIAS)
            .build();

    CommerceBean productVariant = commerceBeanFactory.loadBeanFor(productVariantId, storeContext);
    assertThat(productVariant).isInstanceOf(ProductVariant.class);

    //noinspection ConstantConditions
    Category primaryCategory = ((ProductVariant) productVariant).getCategory();

    assertThat(primaryCategory).isNotNull().hasFieldOrPropertyWithValue("id", WOMENS_JEWLERY_BRACELETS);
  }

  @Test
  public void testPrimaryCategoryOnMasterProduct() {
    if (useBetamaxTapes()) {
      return;
    }

    CommerceId productId = SfccCommerceIdProvider
            .commerceId(BaseCommerceBeanType.PRODUCT)
            .withExternalId("25534903")
            .withCatalogAlias(DEFAULT_CATALOG_ALIAS)
            .build();

    CommerceBean product = commerceBeanFactory.loadBeanFor(productId, storeContext);
    assertThat(product).isInstanceOf(Product.class);

    //noinspection ConstantConditions
    Category primaryCategory = ((Product) product).getCategory();

    assertThat(primaryCategory).isNotNull().hasFieldOrPropertyWithValue("id", WOMENS_JEWLERY_BRACELETS);
  }
}
