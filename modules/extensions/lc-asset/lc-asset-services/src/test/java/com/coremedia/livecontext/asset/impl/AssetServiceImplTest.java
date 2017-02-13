package com.coremedia.livecontext.asset.impl;


import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetServiceImplTest{

  private static final String EXTERNAL_ID = "externalId1";
  private static final String EXTERNAL_ID_SKU = "externalIdSKU";
  private static final String COMMERCE_ID = "vendor:///catalog/product/" + EXTERNAL_ID;
  private static final String COMMERCE_ID_SKU = "vendor:///catalog/sku/" + EXTERNAL_ID_SKU;
  private static final String COMMERCE_ID_SKU_AS_PRODUCT = "vendor:///catalog/product/" + EXTERNAL_ID_SKU;
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
  private AssetUrlProvider assetUrlProvider;
  @Mock
  private SitesService sitesService;
  @Mock
  private SettingsService settingsService;
  @Mock
  private AssetResolvingStrategy assetResolvingStrategy;

  @Mock
  private Site site1;

  private BaseCommerceConnection commerceConnection;

  @Before
  public void setUp() throws Exception {
    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();

    when(assetUrlProvider.getImageUrl(anyString())).thenReturn("http://an/asset/url.jpg");
  }

  @Test
  public void testGetCatalogPicture() throws Exception {
    when(sitesService.getSite(anyString())).thenReturn(site1);
    Content picture = mock(Content.class);
    when(assetResolvingStrategy.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site1)).thenReturn(of(picture));

    CatalogPicture catalogPicture = testling.getCatalogPicture(URL_WITH_EXTERNAL_ID_1);
    assertNotNull(catalogPicture);
    assertEquals(picture, catalogPicture.getPicture());
  }

  @Test
  public void testGetCatalogPictureSKU() throws Exception {
    when(sitesService.getSite(anyString())).thenReturn(site1);

    Content picture = mock(Content.class);

    returnProductVariantWithProduct(COMMERCE_ID_SKU_AS_PRODUCT, EXTERNAL_ID_SKU, COMMERCE_ID_SKU);
    when(assetResolvingStrategy.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID_SKU_AS_PRODUCT, site1)).thenReturn(of(picture));

    CatalogPicture catalogPicture = testling.getCatalogPicture(URL_WITH_EXTERNAL_ID_OF_SKU);
    assertNotNull(catalogPicture);
    assertEquals(picture, catalogPicture.getPicture());
  }

  @Test
  public void testGetCatalogPicture2() throws Exception {
    when(sitesService.getSite(anyString())).thenReturn(site1);
    CatalogPicture catalogPicture = testling.getCatalogPicture(COMMERCE_URL);
    assertNotNull(catalogPicture);
    assertNull(catalogPicture.getPicture());
    assertNotNull(catalogPicture.getUrl());
  }

  @Test
  public void testGetCatalogPictureSiteDefault() throws Exception {
    Content picture = mock(Content.class);
    when(sitesService.getSite(anyString())).thenReturn(site1);
    when(settingsService.setting(anyString(), eq(Content.class), any(Site.class))).thenReturn(picture);

    CatalogPicture catalogPicture = testling.getCatalogPicture(NOT_LINKED_URL);
    assertEquals(picture, catalogPicture.getPicture());
  }

  @Test
  public void testDefaultPicture() throws Exception {
    Content defaultPicture = mock(Content.class);

    when(settingsService.setting(anyString(), eq(Content.class), any(Site.class))).thenReturn(defaultPicture);
    when(sitesService.getSite(anyString())).thenReturn(site1);

    List<Content> pictures = testling.findPictures(COMMERCE_ID);

    assertEquals(defaultPicture, pictures.iterator().next());
  }

  private void returnProductVariantWithProduct(String productVariantId,
                                               String productVariantExternalId,
                                               String productId) {
    ProductVariant variant = mock(ProductVariant.class);
    when(commerceConnection.getCatalogService().findProductById(productVariantId)).thenReturn(variant);
    when(variant.getExternalId()).thenReturn(productVariantExternalId);
    when(variant.getReference()).thenReturn(productVariantId);

    Product product = mock(Product.class);
    when(product.getReference()).thenReturn(productId);

    when(variant.getParent()).thenReturn(product);
  }
}