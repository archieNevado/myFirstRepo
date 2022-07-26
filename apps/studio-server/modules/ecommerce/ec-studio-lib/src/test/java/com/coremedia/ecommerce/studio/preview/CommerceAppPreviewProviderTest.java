package com.coremedia.ecommerce.studio.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import com.coremedia.service.previewurl.Preview;
import com.coremedia.service.previewurl.PreviewSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CommerceAppPreviewProviderTest {

  @Mock
  private CommerceBean commerceBean;
  @Mock
  private SitesService sitesService;

  @Mock
  private Site site;

  @Mock
  private Content siteRootDocument;

  @Mock
  private StoreContext storeContext;

  private CommerceAppPreviewProvider commerceAppPreviewProvider;

  private PreviewSettings previewSettings;

  @Before
  public void setUp() {
    Mockito.when(commerceBean.getContext()).thenReturn(storeContext);
    Mockito.when(commerceBean.getExternalId()).thenReturn("externalId");
    CommerceId commerceId = CommerceIdBuilder
            .builder(Vendor.of("vendor"), "serviceType", BaseCommerceBeanType.CATEGORY)
            .withExternalId("externalId")
            .build();
    Mockito.when(commerceBean.getId()).thenReturn(commerceId);
    Mockito.when(storeContext.getSiteId()).thenReturn("siteId");

    Mockito.when(sitesService.findSite("siteId")).thenReturn(Optional.of(site));
    Mockito.when(site.getSiteRootDocument()).thenReturn(siteRootDocument);
    Mockito.when(siteRootDocument.getString("segment")).thenReturn("rootSegment");

    commerceAppPreviewProvider = new CommerceAppPreviewProvider(sitesService);
    commerceAppPreviewProvider.setEnvironmentFqdn("coremedia.com");

    Map<String, Object> config = new HashMap<>();
    config.put("uriTemplate", "https://headless-client-preview.{fqdn}/commercepreview/{rootSegment}/{type}/{externalId}/");
    previewSettings = new PreviewSettings("PREVIEW_ID", "PROVIDER_ID", null, false, config, null, null, null, null);

  }

  @Test
  public void testGetPreview() {
    assertTrue(commerceAppPreviewProvider.validate(previewSettings));
    Optional<Preview> preview = commerceAppPreviewProvider.getPreview(commerceBean, previewSettings, Map.of());
    assertTrue(preview.isPresent());
    assertEquals("https://headless-client-preview.coremedia.com/commercepreview/rootSegment/category/externalId/", preview.get().getUrl());
  }
}
