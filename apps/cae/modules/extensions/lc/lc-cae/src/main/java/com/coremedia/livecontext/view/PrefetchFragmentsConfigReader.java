package com.coremedia.livecontext.view;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentObjectSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.dispatch.Types;
import com.coremedia.objectserver.beans.ContentBean;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.invoke.MethodHandles.lookup;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;

@DefaultAnnotation(NonNull.class)
@Named
public class PrefetchFragmentsConfigReader {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private static final String LIVECONTEXT_FRAGMENTS = "livecontext-fragments";

  private final SettingsService settingsService;
  private final SitesService sitesService;
  private final Cache cache;

  public PrefetchFragmentsConfigReader(SettingsService settingsService, SitesService sitesService, Cache cache) {
    this.settingsService = settingsService;
    this.sitesService = sitesService;
    this.cache = cache;
  }

  Optional<String> getPlacementView(Page page, String placementName) {
    Optional<String> placementViewForLayout = getPlacementViewForLayout(page, placementName);
    if (placementViewForLayout.isPresent()) {
      return placementViewForLayout;
    }

    return getPlacementDefaultView(page, placementName);
  }

  Optional<String> getPlacementDefaultView(Page page, String placementName) {
    return getLivecontextFragments(page)
            .map(LiveContextPrefetchFragments::getPlacementViews)
            .map(placementViews -> LiveContextPlacementViews.defaults(placementViews, settingsService))
            .flatMap(list -> readPlacementDefaultView(list, placementName));
  }

  private Optional<String> readPlacementDefaultView(List<LiveContextPlacementView> placementViews, String placementName) {
    return placementViews.stream()
            .map(placementView -> getMatchingViewForPlacementName(placementView, placementName))
            .flatMap(Optional::stream)
            .findFirst();
  }

  private Optional<Site> siteOf(Page page) {
    CMContext context = page.getContext();
    if (context == null) {
      LOG.warn("Could not read Prefetch-Config, since page \"{}\" has no context", page.getTitle());
      return empty();
    }
    Content content = context.getContent();
    if (content == null) {
      LOG.warn("Could not read Prefetch-Config, since page \"{}\" has no content", page.getTitle());
      return empty();
    }
    return Optional.of(content)
            .map(sitesService::getContentSiteAspect)
            .map(ContentObjectSiteAspect::getSite);
  }

  private Optional<LiveContextPrefetchFragments> getLivecontextFragments(Page page) {
    return siteOf(page)
            .map(site -> getLiveContextFragments(site, settingsService))
            .map(struct -> getLiveContextFragments(struct, settingsService));
  }

  static LiveContextPrefetchFragments getLiveContextFragments(Struct struct, SettingsService settingsService) {
    return settingsService.createProxy(LiveContextPrefetchFragments.class, struct);
  }

  static Struct getLiveContextFragments(Site site, SettingsService settingsService) {
    return settingsService.setting(LIVECONTEXT_FRAGMENTS, Struct.class, site);
  }

  Optional<String> getPlacementViewForLayout(Page page, String placementName) {
    PageGrid pageGrid = page.getPageGrid();
    if (pageGrid == null) {
      return empty();
    }
    Content layout = pageGrid.getLayout();
    if (layout == null) {
      return empty();
    }

    return getLivecontextFragments(page)
            .map(LiveContextPrefetchFragments::getPlacementViews)
            .map(placementViews -> LiveContextPlacementViews.layouts(placementViews, settingsService))
            .flatMap(layouts -> readPlacementViewForLayout(layouts, layout, placementName));
  }

  private Optional<String> readPlacementViewForLayout(List<LiveContextPlacementViewLayout> layouts, Content layout, String placementName) {
    return layouts.stream()
            .flatMap(placementViewLayout -> getMatchingPlacementViewsForLayout(placementViewLayout, layout))
            .map(placementView -> getMatchingViewForPlacementName(placementView, placementName))
            .flatMap(Optional::stream)
            .findFirst();
  }

  private Stream<LiveContextPlacementView> getMatchingPlacementViewsForLayout(LiveContextPlacementViewLayout placementViewLayout, Content layout) {
    if (!layout.equals(placementViewLayout.getLayout())) {
      return Stream.empty();
    }
    return LiveContextPlacementViewLayout.placementViews(placementViewLayout, settingsService).stream();
  }

  private static Optional<String> getMatchingViewForPlacementName(LiveContextPlacementView placementView, String placementName) {
    if (!isSectionConfigMatching(placementView, placementName)) {
      return empty();
    }

    return Optional.ofNullable(placementView.getView());
  }

  private static boolean isSectionConfigMatching(LiveContextPlacementView placementView, String placementName) {
    Content section = placementView.getSection();
    if (section != null) {
      return placementName.equals(section.getName());
    }
    return false;
  }

  List<String> getPredefinedViews(@Nullable Object bean, Page page) {
    if (bean instanceof ContentBean) {
      Content content = ((ContentBean) bean).getContent();
      List<String> predefinedViewsForContent = getPredefinedViewsForContent(content, page);
      if (!predefinedViewsForContent.isEmpty()) {
        return predefinedViewsForContent;
      }
    }

    List<String> predefinedViewsForLayout = getPredefinedViewsForLayout(page);
    if (!predefinedViewsForLayout.isEmpty()) {
      return predefinedViewsForLayout;
    }

    return getPredefinedDefaultViews(page);
  }

  List<String> getPredefinedDefaultViews(Page page) {
    return getLivecontextFragments(page)
            .map(LiveContextPrefetchFragments::getPrefetchedViews)
            .map(LiveContextPrefetchedViews::getDefaults)
            .orElseGet(List::of);
  }

  List<String> getPredefinedViewsForContent(Content content, Page page) {
    return siteOf(page)
            .map(this::getPrefetchConfigContentTypeDispatcher)
            .map(dispatcher -> dispatcher.lookupPredefinedViews(Types.getTypeOf(content)))
            .orElseGet(List::of);
  }

  private PrefetchConfigContentTypeDispatcher getPrefetchConfigContentTypeDispatcher(Site site) {
    return cache.get(new ConfiguredContentTypesCacheKey(site, settingsService));
  }

  List<String> getPredefinedViewsForLayout(Page page) {
    Content layoutContent = page.getPageGrid().getLayout();
    if (layoutContent == null) {
      return emptyList();
    }

    List<LiveContextPrefetchedViewLayout> liveContextPrefetchedViewLayouts = getLivecontextFragments(page)
            .map(LiveContextPrefetchFragments::getPrefetchedViews)
            .map(liveContextPrefetchedViews -> LiveContextPrefetchedViews.layouts(liveContextPrefetchedViews, settingsService))
            .orElseGet(List::of);

    return liveContextPrefetchedViewLayouts.stream()
            .filter(layout -> layoutContent.equals(layout.getLayout()))
            .map(LiveContextPrefetchedViewLayout::getPrefetchedViews)
            .findFirst()
            .orElseGet(Collections::emptyList);
  }

}
