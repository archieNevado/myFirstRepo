package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.client.settings.CommerceSettings;
import com.coremedia.blueprint.base.livecontext.client.settings.CommerceSettingsProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class SegmentsResourceTest {

  private static final String SITE_ID = "theSiteId";

  private static final List<String> CONFIGURED_SEGMENT_IDS = List.of("vendor:///catalog/segment/externalId0", "vendor:///catalog/segment/externalId1");

  private SegmentsResource segmentsResource;

  @MockBean
  private SitesService sitesService;

  @MockBean
  private SettingsService settingsService;

  @MockBean
  CatalogAliasTranslationService catalogAliasTranslationService;

  @Mock
  Site site;

  @Mock
  StoreContext storeContext;

  @Mock
  SegmentService segmentService;

  @Mock
  CommerceSettingsProvider commerceSettingsProvider;

  @Mock
  CommerceSettings commerceSettings;

  @Mock
  Segment segment1;

  @Mock
  Segment segment2;

  @BeforeEach
  private void setup() {
    segmentsResource = new SegmentsResource(catalogAliasTranslationService, sitesService, settingsService);

    when(storeContext.getSiteId()).thenReturn(SITE_ID);
    when(sitesService.getSite(SITE_ID)).thenReturn(site);
    when(settingsService.createProxy(CommerceSettingsProvider.class, site)).thenReturn(commerceSettingsProvider);
    when(commerceSettingsProvider.getCommerce()).thenReturn(commerceSettings);
  }

  @Test
  void testFindConfiguredSegments() {
    when(commerceSettings.getConfiguredSegmentIds()).thenReturn(CONFIGURED_SEGMENT_IDS);
    when(segmentService.findSegmentById(parseCommerceId(CONFIGURED_SEGMENT_IDS.get(0)).get(), storeContext)).thenReturn(segment1);
    when(segmentService.findSegmentById(parseCommerceId(CONFIGURED_SEGMENT_IDS.get(1)).get(), storeContext)).thenReturn(segment2);

    List<Segment> segments = segmentsResource.getSegments(segmentService, storeContext);
    // check that we got the configured segments Ids
    Set<Segment> segmentIdSet = Set.copyOf(segments);
    Set<Segment> configuredSegmentIds = Set.of(segment1, segment2);
    assertThat(segmentIdSet).containsAll(configuredSegmentIds);
  }

  @Test
  void testFindAllSegments() {
    when(commerceSettings.getConfiguredSegmentIds()).thenReturn(null);
    List<Segment> segments = segmentsResource.getSegments(segmentService, storeContext);
    // check that findAll was called
    verify(segmentService).findAllSegments(storeContext);
  }
}
