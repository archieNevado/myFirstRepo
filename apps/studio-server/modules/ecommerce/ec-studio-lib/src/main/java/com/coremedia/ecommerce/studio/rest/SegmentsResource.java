package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.client.settings.CommerceSettings;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.studio.rest.model.Segments;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.base.livecontext.client.settings.SettingsUtils.getCommerceSettingsProvider;

/**
 * The resource handles the top level store node "Segments".
 * It is not showed in the catalog tree but used to invalidate the list of available commerce segments
 */
@RestController
@RequestMapping(value = "livecontext/segments/{siteId}", produces = MediaType.APPLICATION_JSON_VALUE)
public class SegmentsResource extends AbstractCatalogResource<Segments> {

  private final SitesService sitesService;
  private final SettingsService settingsService;

  @Autowired
  public SegmentsResource(CatalogAliasTranslationService catalogAliasTranslationService, SitesService sitesService, SettingsService settingsService) {
    super(catalogAliasTranslationService);
    this.sitesService = sitesService;
    this.settingsService = settingsService;
  }

  @Override
  protected SegmentsRepresentation getRepresentation(@NonNull Map<String, String> params) {
    SegmentsRepresentation representation = new SegmentsRepresentation();
    fillRepresentation(params, representation);
    return representation;
  }

  private void fillRepresentation(@NonNull Map<String, String> params, SegmentsRepresentation representation) {
    Segments segments = getEntity(params);
    StoreContext storeContext = segments.getContext();

    representation.setId(segments.getId());

    storeContext.getConnection()
            .getSegmentService()
            .map(segmentService -> getSegments(segmentService, storeContext))
            .ifPresent(representation::setSegments);
  }

  @VisibleForTesting
  List<Segment> getSegments(SegmentService segmentService, StoreContext storeContext) {
    Site site = sitesService.getSite(storeContext.getSiteId());
    CommerceSettings commerceSettings = getCommerceSettingsProvider(site, settingsService).getCommerce();
    return readSegmentsFromSettings(segmentService, storeContext, commerceSettings);
  }

  private List<Segment> readSegmentsFromSettings(SegmentService segmentService, StoreContext storeContext, CommerceSettings commerceSettings) {
    if (commerceSettings == null || commerceSettings.getConfiguredSegmentIds() == null) {
      return segmentService.findAllSegments(storeContext);
    }
    List<String> configuredIds = commerceSettings.getConfiguredSegmentIds();
    return configuredIds.stream()
            .map(CommerceIdParserHelper::parseCommerceId)
            .flatMap(Optional::stream)
            .map(segmentId -> segmentService.findSegmentById(segmentId, storeContext))
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableList());
  }

  @Override
  protected Segments doGetEntity(@NonNull Map<String, String> params) {
    return getStoreContext(params).map(Segments::new).orElse(null);
  }

  @NonNull
  @Override
  public Map<String, String> getPathVariables(@NonNull Segments segments) {
    Map<String, String> params = new HashMap<>();
    String segmentsId = segments.getId();
    params.put(PATH_ID, segmentsId);

    StoreContext context = segments.getContext();
    params.put(PATH_SITE_ID, context.getSiteId());
    return params;
  }
}
