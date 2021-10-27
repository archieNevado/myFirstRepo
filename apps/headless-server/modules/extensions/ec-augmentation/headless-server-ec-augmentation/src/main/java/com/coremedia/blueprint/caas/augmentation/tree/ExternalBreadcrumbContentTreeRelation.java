package com.coremedia.blueprint.caas.augmentation.tree;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;

public class ExternalBreadcrumbContentTreeRelation implements TreeRelation<Content> {
  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  //local variables to avoid contentbean dependency
  private static final String EXTERNAL_ID = "externalId";
  private static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";

  private final AugmentationService augmentationService;
  private final ObjectProvider<ExternalBreadcrumbTreeRelation> breadcrumbTreeRelationProvider;
  private final SitesService sitesService;

  public ExternalBreadcrumbContentTreeRelation(AugmentationService augmentationService,
                                               ObjectProvider<ExternalBreadcrumbTreeRelation> breadcrumbTreeRelationProvider,
                                               SitesService sitesService) {
    this.augmentationService = augmentationService;
    this.breadcrumbTreeRelationProvider = breadcrumbTreeRelationProvider;
    this.sitesService = sitesService;
  }

  @Override
  public Collection<Content> getChildrenOf(Content parent) {
    throw new UnsupportedOperationException(
            ExternalBreadcrumbContentTreeRelation.class.getName() + " only supports bottum up lookups.");
  }

  @Override
  public Content getParentOf(Content child) {
    if (!isApplicable(child)) {
      return null;
    }

    Site site = sitesService.getContentSiteAspect(child).getSite();
    if (site == null) {
      LOG.warn("Content '{}' has no site, cannot determine parent content.", child.getPath());
      return null;
    }

    Optional<CommerceId> childCommerceId = getCommerceIdFrom(child);
    if (childCommerceId.isEmpty()) {
      return null;
    }

    return getParentOf(childCommerceId.get(), child, site);
  }

  @Nullable
  private Content getParentOf(@NonNull CommerceId childCategoryId, @NonNull Content child, @NonNull Site site) {
    String parentCategoryId = breadcrumbTreeRelationProvider.getObject().getParentOf(CommerceIdFormatterHelper.format(childCategoryId));
    if (parentCategoryId != null) {
      return getParentContent(CommerceIdParserHelper.parseCommerceIdOrThrow(parentCategoryId), site);
    }

    return null;
  }

  @Nullable
  private Content getParentContent(@NonNull CommerceId parentCategory, @NonNull Site site) {
    Content parentContent = getNearestContentForCategory(parentCategory, site);
    if (parentContent == null) {
      return site.getSiteRootDocument();
    }
    return parentContent;
  }

  @Nullable
  public Content getNearestContentForCategory(@Nullable CommerceId categoryId, @Nullable Site site) {
    if (categoryId == null || site == null) {
      return null;
    }

    Content augmentingContent = null;
    if (augmentationService != null) {
      augmentingContent = augmentationService.getContentByExternalId(CommerceIdFormatterHelper.format(categoryId), site);
    }

    if (null != augmentingContent) {
      return augmentingContent;
    }

    String parentCategoryId = breadcrumbTreeRelationProvider.getObject().getParentOf(CommerceIdFormatterHelper.format(categoryId));
    if (null != parentCategoryId) {
      return getNearestContentForCategory(CommerceIdParserHelper.parseCommerceIdOrThrow(parentCategoryId), site);
    }

    return null;
  }

  @Override
  public Content getParentUnchecked(Content child) {
    return getParentOf(child);
  }

  @Override
  public List<Content> pathToRoot(Content child) {
    List<Content> path = new ArrayList<>();
    Content parent = child;
    while (parent != null) {
      path.add(parent);
      parent = getParentOf(parent);
    }
    Collections.reverse(path);
    LOG.trace("path to root for {}: {}", child, path);
    return path;
  }

  @Override
  public boolean isRoot(Content item) {
    return getParentOf(item) == null;
  }

  @Override
  public boolean isApplicable(Content item) {
    return item != null && item.getType().isSubtypeOf(CM_EXTERNAL_CHANNEL) && isLinkedCategoryValid(item);
  }

  private static boolean isLinkedCategoryValid(@NonNull Content item) {
    return getCommerceIdFrom(item).isPresent();
  }

  @NonNull
  private static Optional<CommerceId> getCommerceIdFrom(@NonNull Content content) {
    String reference = content.getString(EXTERNAL_ID);
    return CommerceIdParserHelper.parseCommerceId(reference);
  }

}
