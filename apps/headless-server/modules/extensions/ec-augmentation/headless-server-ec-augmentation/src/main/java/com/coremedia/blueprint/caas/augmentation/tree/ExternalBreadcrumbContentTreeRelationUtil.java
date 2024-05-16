package com.coremedia.blueprint.caas.augmentation.tree;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.google.common.collect.Iterables;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class ExternalBreadcrumbContentTreeRelationUtil {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private ExternalBreadcrumbContentTreeRelationUtil() {
  }

  @Nullable
  private static Content performLookup(ExternalBreadcrumbContentTreeRelation instance,
                                       Function<Site, Content> lookupFunction,
                                       Content content) {
    Site site = instance.getSitesService().getContentSiteAspect(content).getSite();
    if (site == null) {
      LOG.warn("Content '{}' has no site, cannot determine parent content.", content.getPath());
      return null;
    }

    return lookupFunction.apply(site);
  }

  private static ExternalBreadcrumbContentTreeRelation cast(TreeRelation<Content> treeRelation) {
    if (!(treeRelation instanceof ExternalBreadcrumbContentTreeRelation)) {
      throw new IllegalStateException(
              String.format("Tree relation is supposed to be of type %s, but is %s",
                      ExternalBreadcrumbContentTreeRelation.class, treeRelation.getClass()));
    }
    return (ExternalBreadcrumbContentTreeRelation) treeRelation;
  }

  @Nullable
  public static Content getNearestContentForLeafCategory(Content content, TreeRelation<Content> treeRelation) {
    var instance = cast(treeRelation);
    return performLookup(instance, site -> {
      var parentId = Iterables.getLast(instance.getBreadcrumbTreeRelation().getBreadcrumb());
      return instance.getNearestContentForCategory(parentId, site);
    }, content);
  }

  @Nullable
  public static Content getContentForRootCategory(Content content, TreeRelation<Content> treeRelation) {
    var instance = cast(treeRelation);
    return performLookup(instance, site -> {
      var parentId = Iterables.getFirst(instance.getBreadcrumbTreeRelation().getBreadcrumb(), null);
      return instance.getNearestContentForCategory(parentId, site);
    }, content);
  }
}
