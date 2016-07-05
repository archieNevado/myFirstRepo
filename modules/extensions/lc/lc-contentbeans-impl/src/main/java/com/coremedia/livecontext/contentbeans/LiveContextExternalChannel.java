package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridService;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper.getCurrentCommerceIdProvider;

/**
 * A LiveContextNavigation which is backed by a CMExternalChannel content
 * in the CMS repository.
 */
public class LiveContextExternalChannel extends CMExternalChannelBase implements LiveContextNavigation {
  
  private static final Logger LOG = LoggerFactory.getLogger(LiveContextExternalChannel.class);

  private LiveContextNavigationFactory liveContextNavigationFactory;
  private Site site;
  private PageGridService pdpPageGridService;

  @Override
  public Category getCategory() {
    Content content = this.getContent();
    String externalId = getExternalId();
    StoreContextProvider storeContextProvider = getStoreContextProvider();
    if(null != storeContextProvider) {
      StoreContext storeContext = storeContextProvider.findContextByContent(content);
      Category category = getCatalogService().withStoreContext(storeContext).findCategoryById(
              getCurrentCommerceIdProvider().formatCategoryId(externalId));
      if (category == null) {
        LOG.debug("Content #{}: No category found for externalId:{} - maybe the category only exists in a Workspace?",
                content, externalId);
      }
      return category;
    }
    return null;
  }

  @Nonnull
  @Override
  public Site getSite() {
    if(site == null) {
      site = getSitesService().getContentSiteAspect(getContent()).getSite();
      if (site == null) {
        throw new IllegalStateException("A " + LiveContextExternalChannel.class.getName() + " must belong to a site " +
                "but content[" + getContentId() + "] does not. ");
      }
    }
    return site;
  }
  
  @Nonnull
  public String getExternalId() {
    String externalId = getContent().getString(EXTERNAL_ID);
    return externalId==null ? "" : externalId.trim();
  }

  @Override
  protected List<Linkable> getExternalChildren(Site site) {
    if (isCommerceChildrenSelected()) {
      StoreContextProvider storeContextProvider = getStoreContextProvider();
      StoreContext storeContext = null != storeContextProvider ? storeContextProvider.findContextBySite(site) : null;
      if (storeContext != null) {
        CatalogService catalogService = getCatalogService().withStoreContext(storeContext);
        List<Category> subCategories = new ArrayList<>();
        List<String> commerceChildrenIds = getCommerceChildrenIds();
        for (String commerceChildrenId : commerceChildrenIds) {
          Category category = catalogService.findCategoryById(commerceChildrenId);
          if (category != null) {
            subCategories.add(category);
          }
        }
        List<Linkable> result = new ArrayList<>();
        for (Category subCategory : subCategories) {
          result.add(liveContextNavigationFactory.createNavigation(subCategory, site));
        }
        return result;
      }
    }
    // in all other cases (especially in automatic mode) we ask the treeRelation...
    return new ArrayList<>(treeRelation.getChildrenOf(this));
  }

  @Nullable
  private StoreContextProvider getStoreContextProvider() {
    CommerceConnection currentConnection = Commerce.getCurrentConnection();
    return null != currentConnection ? currentConnection.getStoreContextProvider() : null;
  }

  public boolean isCatalogRoot() {
    Category category = getCategory();
    return null != category && category.isRoot();
  }

  public PageGrid getPdpPagegrid() {
    return pdpPageGridService.getContentBackedPageGrid(this);
  }

  @Required
  public void setLiveContextNavigationFactory(LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  @Required
  public void setPdpPageGridService(PageGridService pdpPageGridService) {
    this.pdpPageGridService = pdpPageGridService;
  }

  @Override
  public String toString() {
    return "LiveContextExternalChannel{" +
            "content=" + getContent().getPath() +
            ", externalId=" + getExternalId() +
            '}';
  }
}
