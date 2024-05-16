package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.CommerceRefHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetFacadeTest {

  private static final String CATEGORY_ID = "acme:///catalog/category/dogs";
  private static final CommerceId CATEGORY_COMMERCE_ID = CommerceIdParserHelper.parseCommerceId(CATEGORY_ID).orElseThrow();

  private static final String SITE_ID = "mySiteId";

  @Mock
  private AssetService assetService;

  @Mock
  private CommerceRefHelper commerceRefHelper;

  @Mock
  private Content asset1;

  private AssetFacade testling;

  private CommerceRef commerceRef;

  @BeforeEach
  public void init() {
    commerceRef = CommerceRefFactory.from(
            CommerceIdParserHelper.parseCommerceId(CATEGORY_ID).orElseThrow(),
            CatalogId.of("catalogId"),
            "storeId",
            Locale.US,
            SITE_ID,
            List.of());
    testling = new AssetFacade(assetService, commerceRefHelper);
  }

  @Test
  void testGetPictureHit() {
    when(commerceRefHelper.getCommerceId(commerceRef)).thenReturn(Optional.of(CATEGORY_COMMERCE_ID));
    when(assetService.findPictures(CATEGORY_COMMERCE_ID, false, SITE_ID)).thenReturn(Collections.singletonList(asset1));
    Content picture = testling.getPicture(commerceRef);
    assertThat(picture).isEqualTo(asset1);
  }

  @Test
  void testGetPictureMiss() {
    when(commerceRefHelper.getCommerceId(commerceRef)).thenReturn(Optional.of(CATEGORY_COMMERCE_ID));
    when(assetService.findPictures(CATEGORY_COMMERCE_ID, false, SITE_ID)).thenReturn(Collections.emptyList());
    Content picture = testling.getPicture(commerceRef);
    assertThat(picture).isNull();
  }

  @Test
  void testGetPicturesHit() {
    when(commerceRefHelper.getCommerceId(commerceRef)).thenReturn(Optional.of(CATEGORY_COMMERCE_ID));
    when(assetService.findPictures(CATEGORY_COMMERCE_ID, false, SITE_ID)).thenReturn(Collections.singletonList(asset1));
    List<Content> pictures = testling.getPictures(commerceRef);
    assertThat(pictures).isNotNull();
    assertThat(pictures.size()).isEqualTo(1);
    assertThat(pictures.get(0)).isEqualTo(asset1);
  }

  @Test
  void testGetPicturesMiss() {
    when(commerceRefHelper.getCommerceId(commerceRef)).thenReturn(Optional.of(CATEGORY_COMMERCE_ID));
    when(assetService.findPictures(CATEGORY_COMMERCE_ID, false, SITE_ID)).thenReturn(Collections.emptyList());
    List<Content> pictures = testling.getPictures(commerceRef);
    assertThat(pictures).isNotNull();
    assertThat(pictures.size()).isEqualTo(0);
  }

  @Test
  void testGetVisualsMiss() {
    when(commerceRefHelper.getCommerceId(commerceRef)).thenReturn(Optional.of(CATEGORY_COMMERCE_ID));
    List<Content> visuals = testling.getPictures(commerceRef);
    assertThat(visuals).isNotNull();
    assertThat(visuals.size()).isEqualTo(0);
  }

  @Test
  void testGetDownloadsMiss() {
    when(commerceRefHelper.getCommerceId(commerceRef)).thenReturn(Optional.of(CATEGORY_COMMERCE_ID));
    when(assetService.findDownloads(CATEGORY_COMMERCE_ID, SITE_ID)).thenReturn(Collections.emptyList());
    List<Content> downloads = testling.getDownloads(commerceRef);
    assertThat(downloads).isNotNull();
    assertThat(downloads.size()).isEqualTo(0);
  }
}
