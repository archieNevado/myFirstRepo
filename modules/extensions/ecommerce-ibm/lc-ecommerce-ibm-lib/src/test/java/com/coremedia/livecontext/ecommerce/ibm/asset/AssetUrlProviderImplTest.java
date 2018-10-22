package com.coremedia.livecontext.ecommerce.ibm.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmStoreContextBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AssetUrlProviderImplTest {

  private static final String HTTP_ASSET_URL = "http://url/to/storefront";
  private static final String HTTPS_ASSET_URL = "https://url/to/storefront";
  private static final String SCHEMELESS_ASSET_URL = "//url/to/storefront";
  private static final String VALID_PREFIX_WITHOUT_SLASHES = "ExtendedCatalog/perfectchef";
  private static final String VALID_PREFIX_LEADING_SLASH = "/ExtendedCatalog/perfectchef";
  private static final String VALID_PREFIX_TRAILED_SLASH = "ExtendedCatalog/perfectchef/";
  private static final String VALID_PREFIX_TRAILED_AND_LEADING_SLASH = "/ExtendedCatalog/perfectchef/";

  private AssetUrlProviderImpl testling = new AssetUrlProviderImpl();

  private String productImageSegment = "product/url";
  private String productImageSegmentWithLeadingSlash = VALID_PREFIX_TRAILED_AND_LEADING_SLASH + productImageSegment; // happens when Search based REST handlers are used

  @Before
  public void setUp() {
    CommerceConnection commerceConnection = new BaseCommerceConnection();
    CurrentCommerceConnection.set(commerceConnection);

    StoreContextImpl storeContext = IbmStoreContextBuilder
            .from(newStoreContext())
            .withStoreId("10001")
            .withCatalogId(CatalogId.of("catalog"))
            .build();
    commerceConnection.setStoreContext(storeContext);

    testling.setCmsHost("localhost");
    testling.setCommercePreviewUrl("//preview/url");
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test(expected = IllegalStateException.class)
  public void whenHostIsEmpty_IllegalArgumentExceptionIsExpected() {
    testling.setCommerceProductionUrl("");
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    testling.getImageUrl(productImageSegment);
  }

  @Test(expected = IllegalStateException.class)
  public void whenHostIsBlank_IllegalArgumentExceptionIsExpected() {
    testling.setCommerceProductionUrl("    ");
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    testling.getImageUrl(productImageSegment);
  }

  @Test(expected = IllegalStateException.class)
  public void whenAPathPrefixShouldBeAppendedButIsNotGiven_AnIllegalStateExceptionMustBeThrown() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(null);

    testling.getImageUrl(productImageSegment, true);
  }

  //TEST CHECK LOGIC - Without path prefix
  @Test
  public void whenAValidHostAndImageUrlIsGiven_AValidURLMustBeBuild() {
    testling.setCommerceProductionUrl(HTTP_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);
    assertEquals("http://url/to/storefront/product/url", testling.getImageUrl(productImageSegment));
  }

  @Test
  public void whenAnInvalidProductImageUrlIsGiven_theUrlIsNull() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertNull(testling.getImageUrl("  "));
  }

  @Test
  public void whenAHttpAssetUrlIsGiven_AHttpUrlMustBeBuild() {
    testling.setCommerceProductionUrl(HTTP_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertEquals("http://url/to/storefront/product/url", testling.getImageUrl(productImageSegment));
  }

  @Test
  public void whenAHttpsAssetUrlIsGiven_AHttpsUrlMustBeBuild() {
    testling.setCommerceProductionUrl(HTTPS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertEquals("https://url/to/storefront/product/url", testling.getImageUrl(productImageSegment));
  }

  @Test
  public void whenASchemelessAssetUrlIsGiven_ASchemelessUrlMustBeBuild() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertEquals("//url/to/storefront/product/url", testling.getImageUrl(productImageSegment));
  }

  @Test
  public void whenAnAbsoluteAssetUrlIsGiven_ASchemelessURLMustBeReturned() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    assertEquals("//url/to/storefront/product/url", testling.getImageUrl("http://url/to/storefront/product/url"));
  }

  //TEST CHECK LOGIC - With path prefix
  @Test
  public void whenAPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegment, true));
  }

  @Test
  public void whenALeadingSlashPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_LEADING_SLASH);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegment, true));
  }

  @Test
  public void whenAServerRelativePathIsGiven_TheURLMustNotContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_LEADING_SLASH);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegmentWithLeadingSlash, true));
  }

  @Test
  public void whenATrailedSlashPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_TRAILED_SLASH);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegment, true));
  }

  @Test
  public void whenASlashesPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_TRAILED_AND_LEADING_SLASH);

    assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegment, true));
  }

  @Test
  public void testReplaceToken() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_TRAILED_AND_LEADING_SLASH);

    StoreContext storeContext = CurrentCommerceConnection.get().getStoreContext();
    String storeId = storeContext.getStoreId();
    String catalogId = storeContext.getCatalogId().get().value();

    assertEquals("//localhost/" + storeId + "/" + catalogId, testling.getImageUrl("//[cmsHost]/[storeId]/[catalogId]", true));
  }
}
