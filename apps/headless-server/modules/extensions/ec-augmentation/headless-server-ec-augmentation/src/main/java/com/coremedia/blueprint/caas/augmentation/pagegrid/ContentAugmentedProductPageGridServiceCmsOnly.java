package com.coremedia.blueprint.caas.augmentation.pagegrid;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbContentTreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.pagegrid.ContentAugmentedProductPageGridServiceImpl;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * This ContentBackedPageGridService merges content backed pageGrids for augmented products.
 * In contrast to {@link ContentAugmentedProductPageGridServiceImpl} no underlying commerce connection is used.
 * Instead the lookup is done along the external breadcrumb hierarchy.
 */
@DefaultAnnotation(NonNull.class)
public class ContentAugmentedProductPageGridServiceCmsOnly extends ContentAugmentedProductPageGridServiceImpl {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  @Nullable
  @Override
  protected Content getParentOf(@Nullable Content content) {
    if (content == null || !content.getType().isSubtypeOf(CM_EXTERNAL_PRODUCT)) {
      return null;
    }

    return getParentExternalChannelContent(content);
  }

  @Nullable
  private Content getParentExternalChannelContent(Content content) {
    Site site = getSitesService().getContentSiteAspect(content).getSite();
    if (site == null) {
      LOG.warn("Content '{}' has no site, cannot determine parent content.", content.getPath());
      return null;
    }

    TreeRelation<Content> treeRelation = getTreeRelation();
    if (!(treeRelation instanceof ExternalBreadcrumbContentTreeRelation)) {
      throw new IllegalStateException(
              String.format("Tree relation is supposed to be of type %s, but is %s", ExternalBreadcrumbContentTreeRelation.class.getName(), getTreeRelation().getClass().getName()));
    }

    return ((ExternalBreadcrumbContentTreeRelation) treeRelation).getNearestContentForLeafCategory(site);
  }

}
