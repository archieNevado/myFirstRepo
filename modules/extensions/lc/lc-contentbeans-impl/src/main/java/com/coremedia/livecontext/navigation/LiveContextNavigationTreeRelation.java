package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentObjectSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.tree.ExternalChannelContentTreeRelation;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Tree relation implementation of the live context.
 * The live context tree relation resolves content beans of type CMExternalChannel
 * and applies the matching category subtree as navigation tree to the given navigation.
 * <p/>
 * The live context categories are wrapped into LiveContextNavigation instances that includes
 * this tree relation for resolving additional sub children that are live context categories.
 */
public class LiveContextNavigationTreeRelation implements TreeRelation<Linkable> {
  private ExternalChannelContentTreeRelation delegate;
  private SitesService sitesService;
  private ContentBeanFactory contentBeanFactory;
  private LiveContextNavigationFactory navigationFactory;


  @Override
  @Nonnull
  public Collection<Linkable> getChildrenOf(Linkable parent) {
    List<Linkable> navigationList = new ArrayList<>();
    Site site = getSite(parent);
    if (parent != null && parent instanceof LiveContextNavigation && site != null) {
      Category parentCategory = ((LiveContextNavigation) parent).getCategory();
      if (parentCategory != null) {
        //we deal with a category already, so we simply resolve the categories children
        for (Category child : parentCategory.getChildren()) {
          Linkable navigation = navigationFactory.createNavigation(child, site);
          navigationList.add(navigation);
        }
      }
    }
    return navigationList;
  }

  @Override
  public Linkable getParentOf(Linkable child) {
    if (child == null || !(child instanceof LiveContextNavigation)) {
      return null;
    }
    Content parentOf = delegate.getParentOf(((LiveContextNavigation) child).getContext().getContent());
    return parentOf != null ? (Linkable) contentBeanFactory.createBeanFor(parentOf) : null;
  }

  public CMExternalChannel getNearestExternalChannelForCategory(@Nullable Category category, @Nullable Site site) {
    if (category != null && site != null) {
      Content nearestExternalChannelForCategory = delegate.getNearestContentForCategory(category, site);
      return nearestExternalChannelForCategory != null ?
              (CMExternalChannel) contentBeanFactory.createBeanFor(nearestExternalChannelForCategory) : null;
    }
    return null;
  }

  @Override
  public Linkable getParentUnchecked(Linkable child) {
    return getParentOf(child);
  }

  @Override
  public List<Linkable> pathToRoot(Linkable child) {
    if (child == null || !(child instanceof LiveContextNavigation)) {
      return null;
    }
    List<Linkable> result = new ArrayList<>();
    List<Content> pathToRoot = delegate.pathToRoot(((LiveContextNavigation) child).getContext().getContent());
    for (Content content : pathToRoot) {
      ContentBean bean = contentBeanFactory.createBeanFor(content);
      if (bean instanceof Linkable) {
        result.add((Linkable) bean);
      }
    }
    return result;
  }

  @Override
  public boolean isRoot(Linkable item) {
    return getParentOf(item) == null;
  }

  @Override
  public boolean isApplicable(Linkable item) {
    return item instanceof CMExternalChannel;
  }

  private Site getSite(Linkable linkable) {
    if (linkable instanceof LiveContextNavigation) {
      return ((LiveContextNavigation) linkable).getSite();
    } else if (linkable instanceof ContentBean) {
      CMExternalChannel extChannel = (CMExternalChannel) linkable;
      ContentObjectSiteAspect siteAspect = sitesService.getSiteAspect(extChannel.getContent());
      return siteAspect.getSite();
    }
    return null;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setNavigationFactory(LiveContextNavigationFactory navigationFactory) {
    this.navigationFactory = navigationFactory;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setDelegate(ExternalChannelContentTreeRelation delegate) {
    this.delegate = delegate;
  }
}
