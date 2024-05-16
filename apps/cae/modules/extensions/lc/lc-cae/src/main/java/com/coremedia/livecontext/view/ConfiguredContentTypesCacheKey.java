package com.coremedia.livecontext.view;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.multisite.Site;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.common.collect.ImmutableSortedMap.toImmutableSortedMap;
import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
class ConfiguredContentTypesCacheKey extends CacheKey<PrefetchConfigContentTypeDispatcher> {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private final Site site;
  private final SettingsService settingsService;

  public ConfiguredContentTypesCacheKey(Site site, SettingsService settingsService) {
    this.site = site;
    this.settingsService = settingsService;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfiguredContentTypesCacheKey that = (ConfiguredContentTypesCacheKey) o;
    return site.equals(that.site) &&
            settingsService.equals(that.settingsService);
  }

  @Override
  public int hashCode() {
    return Objects.hash(site, settingsService);
  }

  @Override
  public PrefetchConfigContentTypeDispatcher evaluate(Cache cache)  {
    Map<String, List<String>> viewsByType = getPrefetchedViews().stream()
            .filter(view -> Objects.nonNull(view.getType()) && Objects.nonNull(view.getPrefetchedViews()))
            .collect(toImmutableSortedMap(
                    String::compareTo,
                    LiveContextContentTypeView::getType,
                    LiveContextContentTypeView::getPrefetchedViews));
    LOG.info("Prefetch config for site '{}' is: {}", site.getName(), viewsByType);
    return new PrefetchConfigContentTypeDispatcher(viewsByType);
  }

  private List<LiveContextContentTypeView> getPrefetchedViews() {
    var struct = PrefetchFragmentsConfigReader.getLiveContextFragments(site, settingsService);
    if (struct == null) {
      return List.of();
    }
    var views = PrefetchFragmentsConfigReader.getLiveContextFragments(struct, settingsService).getPrefetchedViews();
    if (views == null) {
      return List.of();
    }
    return LiveContextPrefetchedViews.contentTypes(views, settingsService);
  }
}
