package com.coremedia.livecontext.asset.impl;


import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceIdOrThrow;
import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssetServiceImplTest {

  private static final String EXTERNAL_ID = "externalId1";
  private static final String EXTERNAL_ID_SKU = "externalIdSKU";
  private static final CommerceId COMMERCE_ID = parseCommerceIdOrThrow("vendor:///catalog/product/" + EXTERNAL_ID);
  private static final CommerceId COMMERCE_ID_SKU = parseCommerceIdOrThrow("vendor:///catalog/sku/" + EXTERNAL_ID_SKU);
  private static final CommerceId COMMERCE_ID_SKU_AS_PRODUCT = parseCommerceIdOrThrow("vendor:///catalog/product/" + EXTERNAL_ID_SKU);
  private static final String URL_WITH_EXTERNAL_ID_1 = "http://localhost:40081/blueprint/servlet/catalogimage/product/10202/en_US/full/" +
          EXTERNAL_ID + ".jpg";
  private static final String URL_WITH_EXTERNAL_ID_OF_SKU = "http://localhost:40081/blueprint/servlet/catalogimage/product/10202/en_US/full/" +
          EXTERNAL_ID_SKU + ".jpg";
  private static final String NOT_LINKED_URL = "http://localhost:40081/blueprint/servlet/catalogimage/product/10202/en_US/full/anyID.jpg";
  private static final String COMMERCE_URL =
          "http://shop-preview-production-helios.blueprint-box.vagrant/wcsstore/ExtendedSitesCatalogAssetStore/images/catalog/apparel/boys/bcl014_tops/646x1000/bcl014_1417.jpg";

  private static final String CMPICTURE_DOCTYPE_NAME = "CMPicture";

  @InjectMocks
  @Spy
  private AssetServiceImpl testling = new AssetServiceImpl();

  @Mock
  private SitesService sitesService;
  @Mock
  private SettingsService settingsService;
  @Mock
  private AssetResolvingStrategy assetResolvingStrategy;

  @Mock
  private Site site1;

  private BaseCommerceConnection commerceConnection;
  private MockCommerceEnvBuilder envBuilder;
  private StoreContext storeContext;

  @Before
  public void setUp() throws Exception {
    envBuilder = MockCommerceEnvBuilder.create();
    commerceConnection = envBuilder.setupEnv();
    storeContext = commerceConnection.getStoreContext();
    testling.setAssetResolvingStrategy(assetResolvingStrategy);
    when(commerceConnection.getAssetUrlProvider().getImageUrl(anyString())).thenReturn("http://an/asset/url.jpg");
  }

  @After
  public void tearDown() throws Exception {
    envBuilder.tearDownEnv();
  }

  @Test
  public void testGetCatalogPicture() throws Exception {
    when(sitesService.getSite(nullable(String.class))).thenReturn(site1);
    Content picture = mock(Content.class);
    when(assetResolvingStrategy.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site1)).thenReturn(of(picture));

    CatalogPicture catalogPicture = testling.getCatalogPicture(URL_WITH_EXTERNAL_ID_1);
    assertNotNull(catalogPicture);
    assertEquals(picture, catalogPicture.getPicture());
  }

  @Test
  public void testGetCatalogPictureFromCommerceId() throws Exception {
    when(sitesService.getSite(nullable(String.class))).thenReturn(site1);
    Content picture = mock(Content.class);
    when(assetResolvingStrategy.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site1)).thenReturn(of(picture));

    CatalogPicture catalogPicture = testling.getCatalogPicture(URL_WITH_EXTERNAL_ID_1, COMMERCE_ID);
    assertNotNull(catalogPicture);
    assertEquals(picture, catalogPicture.getPicture());
  }

  @Test
  public void testGetCatalogPictureSKU() throws Exception {
    when(sitesService.getSite(nullable(String.class))).thenReturn(site1);

    Content picture = mock(Content.class);

    returnProductVariantWithProduct(COMMERCE_ID_SKU_AS_PRODUCT, EXTERNAL_ID_SKU, COMMERCE_ID_SKU);
    when(assetResolvingStrategy.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID_SKU_AS_PRODUCT, site1)).thenReturn(of(picture));

    CatalogPicture catalogPicture = testling.getCatalogPicture(URL_WITH_EXTERNAL_ID_OF_SKU);
    assertNotNull(catalogPicture);
    assertEquals(picture, catalogPicture.getPicture());
  }

  @Test
  public void testGetCatalogPictureSKUFromCommerceId() throws Exception {
    when(sitesService.getSite(nullable(String.class))).thenReturn(site1);

    Content picture = mock(Content.class);

    returnProductVariantWithProduct(COMMERCE_ID_SKU_AS_PRODUCT, EXTERNAL_ID_SKU, COMMERCE_ID_SKU);
    when(assetResolvingStrategy.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID_SKU_AS_PRODUCT, site1)).thenReturn(of(picture));

    CatalogPicture catalogPicture = testling.getCatalogPicture(URL_WITH_EXTERNAL_ID_OF_SKU, COMMERCE_ID_SKU_AS_PRODUCT);
    assertNotNull(catalogPicture);
    assertEquals(picture, catalogPicture.getPicture());
  }

  @Test
  public void testGetCatalogPicture2() throws Exception {
    when(sitesService.getSite(nullable(String.class))).thenReturn(site1);
    CatalogPicture catalogPicture = testling.getCatalogPicture(COMMERCE_URL);
    assertNotNull(catalogPicture);
    assertNull(catalogPicture.getPicture());
    assertNotNull(catalogPicture.getUrl());
  }

  @Test
  public void testGetCatalogPictureSiteDefault() throws Exception {
    Content picture = mock(Content.class);
    when(sitesService.getSite(nullable(String.class))).thenReturn(site1);
    when(commerceConnection.getAssetUrlProvider().getImageUrl(NOT_LINKED_URL)).thenReturn(NOT_LINKED_URL);

    CatalogPicture catalogPicture = testling.getCatalogPicture(NOT_LINKED_URL);
    assertNull(catalogPicture.getPicture());
    assertEquals(NOT_LINKED_URL, catalogPicture.getUrl());
  }

  @Test
  public void testDefaultPicture() throws Exception {
    Content defaultPicture = mock(Content.class);

    when(settingsService.setting(anyString(), eq(Content.class), nullable(Content.class))).thenReturn(defaultPicture);
    when(sitesService.getSite(nullable(String.class))).thenReturn(site1);

    List<Content> pictures = testling.findPictures(COMMERCE_ID);

    assertEquals(defaultPicture, testling.findPictures(COMMERCE_ID).iterator().next());
    assertEquals(defaultPicture, testling.findPictures(COMMERCE_ID, true).iterator().next());
    assertTrue(testling.findPictures(COMMERCE_ID, false).isEmpty());
  }

  private void returnProductVariantWithProduct(CommerceId productVariantId,
                                               String productVariantExternalId,
                                               CommerceId productId) {
    ProductVariant variant = mock(ProductVariant.class);
    when(commerceConnection.getCatalogService().findProductById(productVariantId, storeContext)).thenReturn(variant);
    when(variant.getExternalId()).thenReturn(productVariantExternalId);
    when(variant.getId()).thenReturn(productVariantId);

    Product product = mock(Product.class);
    when(product.getId()).thenReturn(productId);

    when(variant.getParent()).thenReturn(product);
  }
}