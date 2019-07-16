package com.coremedia.livecontext.ecommerce.ibm.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmStoreContextBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssetUrlProviderImplTest {

  private static final String HTTP_ASSET_URL = "http://url/to/storefront";
  private static final String HTTPS_ASSET_URL = "https://url/to/storefront";
  private static final String SCHEMELESS_ASSET_URL = "//url/to/storefront";
  private static final String VALID_PREFIX_WITHOUT_SLASHES = "ExtendedCatalog/perfectchef";
  private static final String VALID_PREFIX_LEADING_SLASH = "/ExtendedCatalog/perfectchef";
  private static final String VALID_PREFIX_TRAILED_SLASH = "ExtendedCatalog/perfectchef/";
  private static final String VALID_PREFIX_TRAILED_AND_LEADING_SLASH = "/ExtendedCatalog/perfectchef/";

  private static final String PRODUCT_IMAGE_SEGMENT = "product/url";
  private static final String PRODUCT_IMAGE_SEGMENT_WITH_LEADING_SLASH
          = VALID_PREFIX_TRAILED_AND_LEADING_SLASH + PRODUCT_IMAGE_SEGMENT; // happens when search-based REST handlers are used

  private static final String STORE_ID = "10001";
  private static final CatalogId CATALOG_ID = CatalogId.of("catalog");

  private AssetUrlProviderImpl testling = new AssetUrlProviderImpl();

  @BeforeEach
  void setUp() {
    CommerceConnection commerceConnection = new BaseCommerceConnection();
    CurrentCommerceConnection.set(commerceConnection);

    StoreContextImpl storeContext = IbmStoreContextBuilder
            .from(commerceConnection, "any-site-id")
            .withStoreId(STORE_ID)
            .withCatalogId(CATALOG_ID)
            .build();
    commerceConnection.setStoreContext(storeContext);

    testling.setCmsHost("localhost");
    testling.setCommercePreviewUrl("//preview/url");
  }

  @AfterEach
  void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test
  void whenHostIsEmpty_IllegalArgumentExceptionIsExpected() {
    testling.setCommerceProductionUrl("");
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertThrows(IllegalStateException.class, () -> testling.getImageUrl(PRODUCT_IMAGE_SEGMENT));
  }

  @Test
  void whenHostIsBlank_IllegalArgumentExceptionIsExpected() {
    testling.setCommerceProductionUrl("    ");
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertThrows(IllegalStateException.class, () -> testling.getImageUrl(PRODUCT_IMAGE_SEGMENT));
  }

  @Test
  void whenAPathPrefixShouldBeAppendedButIsNotGiven_AnIllegalStateExceptionMustBeThrown() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(null);

    assertThrows(IllegalStateException.class, () -> testling.getImageUrl(PRODUCT_IMAGE_SEGMENT, true));
  }

  //TEST CHECK LOGIC - Without path prefix
  @Test
  void whenAValidHostAndImageUrlIsGiven_AValidURLMustBeBuild() {
    testling.setCommerceProductionUrl(HTTP_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);
    assertEquals("http://url/to/storefront/product/url", testling.getImageUrl(PRODUCT_IMAGE_SEGMENT));
  }

  @Test
  void whenAnInvalidProductImageUrlIsGiven_theUrlIsNull() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertNull(testling.getImageUrl("  "));
  }

  @Test
  void whenAHttpAssetUrlIsGiven_AHttpUrlMustBeBuild() {
    testling.setCommerceProductionUrl(HTTP_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertEquals("http://url/to/storefront/product/url", testling.getImageUrl(PRODUCT_IMAGE_SEGMENT));
  }

  @Test
  void whenAHttpsAssetUrlIsGiven_AHttpsUrlMustBeBuild() {
    testling.setCommerceProductionUrl(HTTPS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertEquals("https://url/to/storefront/product/url", testling.getImageUrl(PRODUCT_IMAGE_SEGMENT));
  }

  @Test
  void whenASchemelessAssetUrlIsGiven_ASchemelessUrlMustBeBuild() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertEquals("//url/to/storefront/product/url", testling.getImageUrl(PRODUCT_IMAGE_SEGMENT));
  }

  @Test
  void whenAnAbsoluteAssetUrlIsGiven_ASchemelessURLMustBeReturned() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    assertEquals("//url/to/storefront/product/url", testling.getImageUrl("http://url/to/storefront/product/url"));
  }

  //TEST CHECK LOGIC - With path prefix
  @Test
  void whenAPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(PRODUCT_IMAGE_SEGMENT, true));
  }

  @Test
  void whenALeadingSlashPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_LEADING_SLASH);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(PRODUCT_IMAGE_SEGMENT, true));
  }

  @Test
  void whenAServerRelativePathIsGiven_TheURLMustNotContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_LEADING_SLASH);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(PRODUCT_IMAGE_SEGMENT_WITH_LEADING_SLASH, true));
  }

  @Test
  void whenATrailedSlashPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_TRAILED_SLASH);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(PRODUCT_IMAGE_SEGMENT, true));
  }

  @Test
  void whenASlashesPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_TRAILED_AND_LEADING_SLASH);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(PRODUCT_IMAGE_SEGMENT, true));
  }

  @Test
  void testReplaceToken() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_TRAILED_AND_LEADING_SLASH);

    assertEquals("//localhost/" + STORE_ID + "/" + CATALOG_ID.value(), testling.getImageUrl("//[cmsHost]/[storeId]/[catalogId]", true));
  }
}
