package com.coremedia.blueprint.workflow.boot;

import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.Version;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.translate.workflow.AutoMergePredicateFactory;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * An {@link AutoMergePredicateFactory} that excludes the root channel's segment property from auto-merge.
 *
 * @since 2107.8, 2301.4, 2304.1
 */
class ExcludeRootChannelSegmentAutoMergePredicateFactory implements AutoMergePredicateFactory {
  private final SitesService sitesService;

  ExcludeRootChannelSegmentAutoMergePredicateFactory(@NonNull SitesService sitesService) {
    this.sitesService = requireNonNull(sitesService);
  }

  @NonNull
  @Override
  public Predicate<? super List<CapPropertyDescriptor>> create(@NonNull Version masterVersion, @NonNull Content derivedContent) {
    Site site = sitesService.getContentSiteAspect(derivedContent).getSite();
    boolean isSiteRootChannel = site != null && derivedContent.equals(site.getSiteRootDocument());
    return descriptors -> !isSiteRootChannel || !"segment".equals(descriptors.get(0).getName());
  }
}
