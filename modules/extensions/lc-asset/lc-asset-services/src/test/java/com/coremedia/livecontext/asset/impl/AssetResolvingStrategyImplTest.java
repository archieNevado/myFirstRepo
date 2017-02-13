package com.coremedia.livecontext.asset.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.asset.AssetSearchService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetResolvingStrategyImplTest {

  private static final String EXTERNAL_ID = "externalId1";
  private static final String EXTERNAL_ID_SKU = "externalIdSKU";
  private static final String COMMERCE_ID = "vendor:///catalog/product/" + EXTERNAL_ID;
  private static final String COMMERCE_ID_SKU = "vendor:///catalog/sku/" + EXTERNAL_ID_SKU;

  private static final String CMPICTURE_DOCTYPE_NAME = "CMPicture";

  @Spy
  @InjectMocks
  private AssetResolvingStrategyImpl testling;

  @Mock
  private AssetSearchService assetSearchService;

  @Mock
  private AssetChanges assetChanges;

  @Mock
  private Site site;

  private BaseCommerceConnection commerceConnection;

  @Before
  public void setUp() throws Exception {
    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
  }

  @Test
  public void noCacheOneIndexedInSolrButNotUpToDate() {
    Content picture = createPictureMock("picture");

    List<Content> indexedAssets = of(picture);
    List<Content> cachedAssets = of();

    returnIndexedAssets(EXTERNAL_ID, indexedAssets);
    returnCachedAssets(COMMERCE_ID, site, cachedAssets);
    isUpToDateInCache(picture, COMMERCE_ID, site, false);

    List<?> assets = testling.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site);
    assertTrue(assets.isEmpty());
  }

  @Test
  public void twoCachedAndIndexedOneNotUptodate() {
    String upToDatePictureName = "picture up to date in cache";
    Content pictureUpToDate = createPictureMock(upToDatePictureName);
    Content otherPicture = createPictureMock("not up to date in cache");

    List<Content> indexedAssets = of(otherPicture, pictureUpToDate);
    List<Content> cachedAssets = of(otherPicture, pictureUpToDate);
    List<String> referencedOnContent = of(COMMERCE_ID);

    returnIndexedAssets(EXTERNAL_ID, indexedAssets);
    returnCachedAssets(COMMERCE_ID, site, cachedAssets);
    isUpToDateInCache(otherPicture, COMMERCE_ID, site, false);
    isUpToDateInCache(pictureUpToDate, COMMERCE_ID, site, true);
    isReferencedInContent(pictureUpToDate, referencedOnContent);

    List<?> assets = testling.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site);
    assertEquals(1, assets.size());
    assertEquals(upToDatePictureName, ((Content) assets.get(0)).getName());
  }

  @Test
  public void oneCachedAndNotIndexed() {
    String pictureName = "picture";
    Content picture = createPictureMock(pictureName);

    List<Content> indexedAssets = of();
    List<Content> cachedAssets = of(picture);
    List<String> referencedOnContent = of(COMMERCE_ID);

    returnIndexedAssets(EXTERNAL_ID, indexedAssets);
    returnCachedAssets(COMMERCE_ID, site, cachedAssets);
    isUpToDateInCache(picture, COMMERCE_ID, site, true);
    isReferencedInContent(picture, referencedOnContent);

    List<?> assets = testling.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site);

    assertEquals(1, assets.size());
    assertEquals(pictureName, ((Content) assets.get(0)).getName());
  }

  @Test
  public void twoCachedOneIndexedOnlyOneOnContent() {
    String aPicturesName = "picture one";
    String anotherPicturesName = "picture two";
    Content aPicture = createPictureMock(aPicturesName);
    Content anotherPicture = createPictureMock(anotherPicturesName);

    List<Content> indexedAssets = of(aPicture);
    List<Content> cachedAssets = of(aPicture, anotherPicture);
    List<String> referencedOnContent = of(COMMERCE_ID);

    returnIndexedAssets(EXTERNAL_ID, indexedAssets);
    returnCachedAssets(COMMERCE_ID, site, cachedAssets);

    isUpToDateInCache(aPicture, COMMERCE_ID, site, true);
    isReferencedInContent(aPicture, referencedOnContent);

    isUpToDateInCache(anotherPicture, COMMERCE_ID, site, true);
    isReferencedInContent(anotherPicture, referencedOnContent);

    List<?> assets = testling.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site);

    assertEquals(2, assets.size());
    assertEquals(aPicturesName, ((Content) assets.get(0)).getName());
    assertEquals(anotherPicturesName, ((Content) assets.get(1)).getName());
  }

  @Test
  public void findProductAssetsOneIndexedAndCachedAndReferenced() throws Exception {
    Content picture = createPictureMock("picture");
    List<Content> indexedAssets = of(picture);
    List<Content> cachedAssets = of(picture);
    List<String> referencedOnContent = of(COMMERCE_ID);

    returnIndexedAssets(EXTERNAL_ID, indexedAssets);
    returnCachedAssets(COMMERCE_ID, site, cachedAssets);
    isUpToDateInCache(picture, COMMERCE_ID, site, true);
    isReferencedInContent(picture, referencedOnContent);

    Collection<?> pictures = testling.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site);
    assertEquals(1, pictures.size());
  }

  @Test
  public void findProductAssetsNonInCacheOrIndexed() {
    List<Content> indexedAssets = of();
    List<Content> cachedAssets = of();

    returnIndexedAssets(EXTERNAL_ID, indexedAssets);
    returnCachedAssets(COMMERCE_ID, site, cachedAssets);

    Collection<?> pictures = testling.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site);
    assertTrue(pictures.isEmpty());
  }

  @Test
  public void testFindAssetsForVariants() throws Exception {
    Content variantPicture = createPictureMock("variant picture");
    Content productPicture = createPictureMock("product picture");

    List<Content> indexedVariantAssets = of(variantPicture);
    List<Content> indexedProductAssets = of(productPicture);
    List<Content> cachedProductAssets = of(productPicture);
    List<String> referencedOnProductContent = of(COMMERCE_ID);

    returnProductVariantWithProduct(COMMERCE_ID_SKU, EXTERNAL_ID_SKU, COMMERCE_ID);
    returnIndexedAssets(EXTERNAL_ID_SKU, indexedVariantAssets);

    returnIndexedAssets(EXTERNAL_ID, indexedProductAssets);
    returnCachedAssets(COMMERCE_ID, site, cachedProductAssets);
    isUpToDateInCache(productPicture, COMMERCE_ID, site, true);
    isReferencedInContent(productPicture, referencedOnProductContent);

    Collection<?> pictures = testling.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID_SKU, site);
    assertEquals(1, pictures.size());
  }

  @Test
  public void findAssetsForSKUsWithFallbackToParent() throws Exception {
    Content variantPicture = createPictureMock("variant picture");
    Content productPicture = createPictureMock("product picture");

    List<Content> indexedVariantAssets = of(variantPicture);
    List<Content> cachedProductAssets = of(productPicture);
    List<Content> indexedProductAssets = of(productPicture);
    List<String> referencedOnContent = of(COMMERCE_ID);

    returnProductVariantWithProduct(COMMERCE_ID_SKU, EXTERNAL_ID_SKU, COMMERCE_ID);
    returnIndexedAssets(EXTERNAL_ID_SKU, indexedVariantAssets);

    returnCachedAssets(COMMERCE_ID, site, cachedProductAssets);
    returnIndexedAssets(EXTERNAL_ID, indexedProductAssets);
    isUpToDateInCache(productPicture, COMMERCE_ID, site, true);
    isReferencedInContent(productPicture, referencedOnContent);

    Collection<?> pictures = testling.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID_SKU, site);
    assertEquals(1, pictures.size());
  }

  private void isUpToDateInCache(Content asset, String fullId, Site site, boolean isUpToDate) {
    when(assetChanges.isUpToDate(asset, fullId, site)).thenReturn(isUpToDate);
  }

  private void returnCachedAssets(String fullId, Site site, List<Content> assets) {
    when(assetChanges.get(fullId, site)).thenReturn(assets);
  }

  private void isReferencedInContent(Content picture, List<String> referencesFromCommerceStruct) {
    doReturn(referencesFromCommerceStruct).when(testling).getExternalReferences(picture);
  }

  private void returnIndexedAssets(String externalId, List<Content> picturesInSolrForExternalId) {
    when(assetSearchService.searchAssets(CMPICTURE_DOCTYPE_NAME, externalId, site))
            .thenReturn(picturesInSolrForExternalId);
  }

  private void returnProductVariantWithProduct(String productVariantId,
                                               String productVariantExternalId,
                                               String productId) {
    ProductVariant variant = mock(ProductVariant.class);
    when(commerceConnection.getCatalogService().findProductById(productVariantId)).thenReturn(variant);
    when(variant.getExternalId()).thenReturn(productVariantExternalId);
    when(variant.getReference()).thenReturn(productVariantId);
    markAsSKU(productVariantId);

    Product product = mock(Product.class);
    when(product.getReference()).thenReturn(productId);

    when(variant.getParent()).thenReturn(product);
  }

  private Content createPictureMock(String pictureName) {
    Content picture = mock(Content.class);
    ContentType contentType = mock(ContentType.class);
    when(contentType.isSubtypeOf(CMPICTURE_DOCTYPE_NAME)).thenReturn(true);
    when(picture.getName()).thenReturn(pictureName);
    when(picture.getType()).thenReturn(contentType);
    return picture;
  }

  private void markAsSKU(String id) {
    doReturn(true).when(testling).isSkuId(id);
  }
}