package com.coremedia.livecontext.tree;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * The live context tree relation resolves content of type CMExternalChannel
 * and applies the matching category subtree and resolves matching contents of type CMExternalChannel
 * <p/>
 * The tree relation only support bottom up lookups.
 * Since CMExternalChannel does not reference any contents as children #getChildrenOf is not implemented.
 */
public class ExternalChannelContentTreeRelation implements TreeRelation<Content> {
  private static final Logger LOG = LoggerFactory.getLogger(ExternalChannelContentTreeRelation.class);

  //local variables to avoid contentbean dependency
  private static final String EXTERNAL_ID = "externalId";
  private static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";

  private AugmentationService augmentationService;
  private CommerceTreeRelation commerceTreeRelation;
  private SitesService sitesService;
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Override
  public Collection<Content> getChildrenOf(Content parent) {
    throw new UnsupportedOperationException(
            ExternalChannelContentTreeRelation.class.getName() + " only supports bottum up lookups.");
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

    return findCategoryFor(child, this::createCategoryFor)
            .map(commerceTreeRelation::getParentOf)
            .map(parent -> getNearestContentForCategory(parent, site))
            .orElseGet(site::getSiteRootDocument);
  }

  @Nullable
  public Content getNearestContentForCategory(@Nullable Category category, @Nullable Site site) {
    if (category == null || site == null) {
      return null;
    }

    Content augmentingContent = null;
    if (augmentationService != null) {
      augmentingContent = augmentationService.getContent(category);
    }

    if (null != augmentingContent) {
      return augmentingContent;
    }

    Category parentCategory = commerceTreeRelation.getParentOf(category);
    return getNearestContentForCategory(parentCategory, site);
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

  /**
   * A content is applicable if it is a subtype of CMExternalChannel and links to a category which can be loaded.
   * @param item The item to check the tree relation for.
   * @return if the content is an instance of CMExternalChannel and links to a category which can be loaded
   */
  @Override
  public boolean isApplicable(Content item) {
    return item != null && item.isInstanceOf(CM_EXTERNAL_CHANNEL) && isLinkedCategoryValid(item);
  }

  private boolean isLinkedCategoryValid(@NonNull Content item) {
    return findCategoryFor(item, this::findLoadableCategoryFor).isPresent();
  }

  @NonNull
  private static Optional<CommerceId> getCommerceIdFrom(@NonNull Content content) {
    String reference = content.getString(EXTERNAL_ID);
    return CommerceIdParserHelper.parseCommerceId(reference);
  }

  @NonNull
  private Optional<Category> findCategoryFor(@NonNull Content content,
                                             @NonNull BiFunction<CommerceConnection, CommerceId, Category> factory) {
    return getCommerceIdFrom(content)
            .flatMap(commerceId -> findCategoryFor(content, commerceId, factory));
  }

  @NonNull
  private Optional<Category> findCategoryFor(@NonNull Content content, @NonNull CommerceId commerceId,
                                             @NonNull BiFunction<CommerceConnection, CommerceId, Category> factory) {
    return sitesService.getContentSiteAspect(content).findSite()
            .flatMap(commerceConnectionSupplier::findConnection)
            .map(connection -> factory.apply(connection, commerceId));
  }

  @Nullable
  private Category findLoadableCategoryFor(@NonNull CommerceConnection connection, @NonNull CommerceId commerceId) {
    var storeContext = connection.getInitialStoreContext();
    CommerceBeanFactory commerceBeanFactory = connection.getCommerceBeanFactory();
    return (Category) commerceBeanFactory.loadBeanFor(commerceId, storeContext);
  }

  @NonNull
  private Category createCategoryFor(@NonNull CommerceConnection connection, @NonNull CommerceId commerceId) {
    var storeContext = connection.getInitialStoreContext();
    CommerceBeanFactory commerceBeanFactory = connection.getCommerceBeanFactory();
    return (Category) commerceBeanFactory.createBeanFor(commerceId, storeContext);
  }

  @Autowired(required = false)
  @Qualifier("categoryAugmentationService")
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Autowired
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Autowired
  public void setCommerceConnectionSupplier(CommerceConnectionSupplier commerceConnectionSupplier) {
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  @Autowired
  public void setCommerceTreeRelation(CommerceTreeRelation commerceTreeRelation) {
    this.commerceTreeRelation = commerceTreeRelation;
  }
}
