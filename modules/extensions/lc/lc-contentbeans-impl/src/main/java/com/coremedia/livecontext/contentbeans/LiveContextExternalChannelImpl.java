package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridService;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * A LiveContextNavigation which is backed by a CMExternalChannel content
 * in the CMS repository.
 */
public class LiveContextExternalChannelImpl extends CMExternalChannelBase implements LiveContextExternalChannel {

  private static final Logger LOG = LoggerFactory.getLogger(LiveContextExternalChannelImpl.class);

  private LiveContextNavigationFactory liveContextNavigationFactory;
  private Site site;
  private PageGridService pdpPageGridService;

  @Override
  public Category getCategory() {
    CommerceConnection currentConnection = CurrentCommerceConnection.find().orElse(null);
    if (currentConnection == null) {
      return null;
    }

    StoreContextProvider storeContextProvider = currentConnection.getStoreContextProvider();
    if (storeContextProvider == null) {
      return null;
    }

    Content content = getContent();
    String externalId = getExternalId();

    StoreContext storeContext = requireNonNull(storeContextProvider.findContextByContent(content),
            "Store context not available.");

    CatalogService catalogService = getCatalogService(storeContext);

    Optional<CommerceId> commerceIdOptional = CommerceIdParserHelper.parseCommerceId(externalId);
    if (!commerceIdOptional.isPresent()) {
      logHintAboutCategoryInWorkspace(content, externalId);
      return null;
    }

    Category category = catalogService.findCategoryById(commerceIdOptional.get(), storeContext);

    if (category == null) {
      logHintAboutCategoryInWorkspace(content, externalId);
    }

    return category;
  }

  private static void logHintAboutCategoryInWorkspace(Content content, String externalId) {
    LOG.debug("Content #{}: No category found for externalId:{} - maybe the category only exists in a workspace?",
            content, externalId);
  }

  @Nonnull
  @Override
  public Site getSite() {
    if (site == null) {
      site = getSitesService().getContentSiteAspect(getContent()).findSite()
              .orElseThrow(() -> new IllegalStateException(
                      "A " + LiveContextExternalChannelImpl.class.getName() + " must belong to a site but content["
                              + getContentId() + "] does not. "));
    }

    return site;
  }

  @Override
  @Nonnull
  public String getExternalId() {
    String externalId = getContent().getString(EXTERNAL_ID);
    return externalId == null ? "" : externalId.trim();
  }

  @Override
  protected List<Linkable> getExternalChildren(Site site) {
    if (isCommerceChildrenSelected()) {
      StoreContext storeContext = findStoreContextForSite(site);

      if (storeContext != null) {
        CatalogService catalogService = getCatalogService(storeContext);

        List<Category> subCategories = new ArrayList<>();

        List<String> commerceChildrenIds = getCommerceChildrenIds();
        for (String commerceChildrenId : commerceChildrenIds) {
          CommerceId commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(commerceChildrenId);
          Category category = catalogService.findCategoryById(commerceId, storeContext);
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
  private static StoreContext findStoreContextForSite(@Nullable Site site) {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getStoreContextProvider)
            .map(storeContextProvider -> storeContextProvider.findContextBySite(site))
            .orElse(null);
  }

  @Nonnull
  private static CatalogService getCatalogService(StoreContext storeContext) {
    CommerceConnection commerceConnection = CurrentCommerceConnection.get();

    CatalogService catalogService = requireNonNull(commerceConnection.getCatalogService(),
            "No catalog service available.");

    return catalogService.withStoreContext(storeContext);
  }

  @Override
  public boolean isCatalogRoot() {
    Category category = getCategory();
    return category != null && category.isRoot();
  }

  @Override
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
    return MoreObjects.toStringHelper(LiveContextExternalChannel.class)
            .add("contentId", getContent().getId())
            .add("externalId", getExternalId())
            .toString();
  }
}
