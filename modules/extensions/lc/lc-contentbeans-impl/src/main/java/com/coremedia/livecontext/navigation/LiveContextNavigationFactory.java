package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannel;
import com.coremedia.livecontext.context.CategoryInSite;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.springframework.util.Assert.notNull;

public class LiveContextNavigationFactory {

  private LiveContextNavigationTreeRelation treeRelation;
  private SitesService sitesService;
  private ContentBeanFactory contentBeanFactory;
  private AugmentationService augmentationService;
  private ValidationService<LiveContextExternalChannel> validationService;

  /**
   * Creates a new live context navigation from the given category.
   * Since the category and therefore the corresponding store is already resolved,
   * we don't need to pass the channel document here.
   *
   * @return If the category is augmented a {@link LiveContextExternalChannel} is returned, otherwise a {@link LiveContextCategoryNavigation}.
   *
   *
   * @param category The category the navigation should be build for.
   */
  @Nonnull
  public LiveContextNavigation createNavigation(@Nonnull Category category, @Nonnull Site site) {
    if (augmentationService != null) {
      Content externalChannelContent = augmentationService.getContent(category);
      LiveContextExternalChannel externalChannel = (LiveContextExternalChannel) contentBeanFactory.createBeanFor(externalChannelContent);
      if (null != externalChannel && validationService.validate(externalChannel)) {
        return externalChannel;
      }
    }
    return new LiveContextCategoryNavigation(category, site, treeRelation);
  }

  /**
   * Creates a new LiveContextNavigation by searching a category in the catalog by the given seo segment.
   * The context of the catalog (which shop should contain the seo segment) is resolved from a channel.
   *
   * @param parentChannel the channel to resolve the store context for
   * @param seoSegment the seo segment of the category which should be wrapped in a LiveContextNavigation
   * @return category the category found for given seo segment
   */
  @Nonnull
  public LiveContextNavigation createNavigationBySeoSegment(@Nonnull Content parentChannel, @Nonnull String seoSegment) {
    StoreContext storeContext = getStoreContextProvider().findContextByContent(parentChannel);
    notNull(storeContext, "No StoreContext found for "+parentChannel.getName());
    Category category = getCatalogService().findCategoryBySeoSegment(seoSegment);
    notNull(category, "No category found for seo segment: "+seoSegment);
    Site site = sitesService.getContentSiteAspect(parentChannel).getSite();
    notNull(site, "No site found for " + parentChannel);

    return createNavigation(category, site);
  }

  @Nullable
  public CategoryInSite createCategoryInSite(@Nonnull Category category, @Nonnull String siteId) {
    Site site = sitesService.getSite(siteId);
    if(null != site) {
      return createCategoryInSite(category, site);
    }
    return null;
  }

  @Nullable
  public ProductInSite createProductInSite(@Nonnull Product product, String siteId) {
    Site site = sitesService.getSite(siteId);
    if(null != site) {
      return createProductInSite(product, site);
    }
    return null;
  }

  @Nonnull
  public CategoryInSite createCategoryInSite(@Nonnull Category category, @Nonnull Site site) {
    return new CategoryInSiteImpl(category, site);
  }

  @Nonnull
  public ProductInSite createProductInSite(@Nonnull Product product, @Nonnull Site site) {
    return new ProductInSiteImpl(product, site);
  }


  // --- configuration ----------------------------------------------

  @Required
  public void setTreeRelation(LiveContextNavigationTreeRelation treeRelation) {
    this.treeRelation = treeRelation;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  public CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Autowired(required = false)
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setValidationService(ValidationService<LiveContextExternalChannel> validationService) {
    this.validationService = validationService;
  }
}
