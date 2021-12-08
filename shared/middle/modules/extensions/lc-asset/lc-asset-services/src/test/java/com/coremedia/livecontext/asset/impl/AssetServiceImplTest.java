package com.coremedia.livecontext.asset.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceIdOrThrow;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetServiceImplTest {

  private static final String EXTERNAL_ID = "externalId1";

  private static final CommerceId COMMERCE_ID
          = parseCommerceIdOrThrow("vendor:///catalog/product/" + EXTERNAL_ID);

  private static final String CMPICTURE_DOCTYPE_NAME = "CMPicture";
  private static final String CMVISUAL_DOCTYPE_NAME = "CMVisual";
  private static final String CMDOWNLOAD_DOCTYPE_NAME = "CMDownload";

  @InjectMocks
  @Spy
  private AssetServiceImpl testling;

  @Mock
  private SitesService sitesService;

  @Mock
  private SettingsService settingsService;

  @Mock
  private AssetResolvingStrategy assetResolvingStrategy;

  @Mock
  private Site site1;

  @Before
  public void setUp() {
    CommerceConnection commerceConnection = mock(CommerceConnection.class);

    StoreContextImpl storeContext = StoreContextBuilderImpl.from(commerceConnection, "site-1").build();

    testling.setAssetResolvingStrategy(assetResolvingStrategy);
  }

  @After
  public void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  public void testDefaultPicture() {
    Content defaultPicture = mock(Content.class);

    when(settingsService.getSetting(anyString(), eq(Content.class), nullable(Site.class)))
            .thenReturn(Optional.of(defaultPicture));
    when(sitesService.getSite(anyString())).thenReturn(site1);

    assertEquals(defaultPicture, testling.findPictures(COMMERCE_ID, true, "site-1").iterator().next());
  }

  @Test
  public void testFindPictures() {
    Content picture = mock(Content.class);
    when(assetResolvingStrategy.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site1)).thenReturn(List.of(picture));
    when(sitesService.getSite(anyString())).thenReturn(site1);
    assertEquals(picture, testling.findPictures(COMMERCE_ID, false, "site-1").iterator().next());
  }

  @Test
  public void testFindVisuals() {
    Content visual = mock(Content.class);
    ContentType type = mock(ContentType.class);
    when(assetResolvingStrategy.findAssets(CMVISUAL_DOCTYPE_NAME, COMMERCE_ID, site1)).thenReturn(List.of(visual));
    when(sitesService.getSite(anyString())).thenReturn(site1);
    when(visual.getType()).thenReturn(type);
    when(type.isSubtypeOf(anyString())).thenReturn(false);
    assertEquals(visual, testling.findVisuals(COMMERCE_ID, false, "site-1").iterator().next());
  }

  @Test
  public void testFindDownloads() {
    Content download = mock(Content.class);
    when(assetResolvingStrategy.findAssets(CMDOWNLOAD_DOCTYPE_NAME, COMMERCE_ID, site1)).thenReturn(List.of(download));
    when(sitesService.findSite(anyString())).thenReturn(Optional.of(site1));
    assertEquals(download, testling.findDownloads(COMMERCE_ID, "site-1").iterator().next());
  }

  @Test
  public void testFindPicturesWithSiteId() {
    Content picture = mock(Content.class);
    when(assetResolvingStrategy.findAssets(CMPICTURE_DOCTYPE_NAME, COMMERCE_ID, site1)).thenReturn(List.of(picture));
    when(sitesService.getSite(anyString())).thenReturn(site1);

    List<Content> pictures = testling.findPictures(COMMERCE_ID, false, "site-1");
    assertNotNull(pictures);
  }
}
