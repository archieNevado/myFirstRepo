package com.coremedia.livecontext.tree;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.tree.CommerceTreeRelation;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The live context tree relation resolves content of type CMExternalChannel
 * and applies the matching category subtree and resolves matching contents of type CMExternalChannel
 * <p/>
 * The tree relation only support bottom up lookups.
 * Since CMExternalChannel does not reference any contents as children #getChildrenOf is not implemented.
 */
public class ExternalChannelContentTreeRelation implements TreeRelation<Content> {
  //local variables to avoid contentbean dependency
  private static final String EXTERNAL_ID = "externalId";
  private static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";

  private AugmentationService augmentationService;
  private CommerceTreeRelation commerceTreeRelation;
  private SitesService sitesService;
  private CommerceConnectionInitializer commerceConnectionInitializer;


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
    String externalId = child.getString(EXTERNAL_ID);
    if (!StringUtils.isEmpty(externalId) && site != null) {
      commerceConnectionInitializer.init(site);
      CommerceBean childCategory = getCommerceBeanFactory().createBeanFor(
              Commerce.getCurrentConnection().getIdProvider().formatCategoryId(externalId),
              Commerce.getCurrentConnection().getStoreContext());
      if (childCategory instanceof Category) {
        Category parentCategory = commerceTreeRelation.getParentOf((Category) childCategory);
        if (parentCategory != null) {
          Content parentContent = getNearestContentForCategory(parentCategory, site);
          if (parentContent != null) {
            return parentContent;
          } else {
            return site.getSiteRootDocument();
          }
        } else if (((Category) childCategory).isRoot()) {
          // Had to change this one, since it can lead to an endless loop when method is called with site root document.
          Content siteRoot = site.getSiteRootDocument();
          return siteRoot.equals(child) ? null : siteRoot;
        }
      }
    }
    return null;
  }

  @Nullable
  public Content getNearestContentForCategory(@Nullable Category category, @Nullable Site site) {
    if (category != null && site != null) {
      Content augmentingContent = null;
      if(augmentationService != null) {
        augmentingContent = augmentationService.getContent(category);
      }
      if (null != augmentingContent) {
        return augmentingContent;
      } else if (!category.isRoot()) {
        Category parentCategory = commerceTreeRelation.getParentOf(category);
        return getNearestContentForCategory(parentCategory, site);
      }
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
    return path;
  }

  @Override
  public boolean isRoot(Content item) {
    return getParentOf(item) == null;
  }

  @Override
  public boolean isApplicable(Content item) {
    return item != null && item.getType().isSubtypeOf(CM_EXTERNAL_CHANNEL);
  }

  public CommerceBeanFactory getCommerceBeanFactory() {
    return Commerce.getCurrentConnection().getCommerceBeanFactory();
  }

  @Autowired (required = false)
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Required
  public void setCommerceTreeRelation(CommerceTreeRelation commerceTreeRelation) {
    this.commerceTreeRelation = commerceTreeRelation;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }
}
