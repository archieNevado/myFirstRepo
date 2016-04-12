package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.blueprint.base.livecontext.util.CatalogRootHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.impl.SitesServiceImpl;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.xml.Markup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class CategoryImpl extends AbstractIbmCommerceBean implements Category {

  private static final Logger LOG = LoggerFactory.getLogger(CategoryImpl.class);

  private Map<String, Object> delegate;
  private WcCatalogWrapperService catalogWrapperService;
  private SitesServiceImpl sitesService;

  protected Map<String, Object> getDelegate() {
    if (delegate == null) {
      if (isRoot()) {
        return Collections.emptyMap();
      }
      delegate = (Map<String, Object>) getCommerceCache().get(
        new CategoryCacheKey(getId(), getContext(), getCatalogWrapperService(), getCommerceCache()));
      if (delegate == null) {
        throw new NotFoundException(getId() + " (category not found in catalog)");
      }
    }
    return delegate;
  }

  @Override
  public void load() throws CommerceException {
    getDelegate();
  }

  @Override
  public void setDelegate(Object delegate) {
    this.delegate = (Map<String, Object>) delegate;
  }

  public WcCatalogWrapperService getCatalogWrapperService() {
    return catalogWrapperService;
  }

  @Required
  public void setCatalogWrapperService(WcCatalogWrapperService catalogWrapperService) {
    this.catalogWrapperService = catalogWrapperService;
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatCategoryId(getExternalId());
  }

  @Override
  public String getExternalId() {
    if (isRoot()) {
      return EXTERNAL_ID_ROOT_CATEGORY;
    }
    return DataMapHelper.getValueForKey(getDelegate(), "identifier", String.class);
  }

  @Override
  public String getExternalTechId() {
    return DataMapHelper.getValueForKey(getDelegate(), "uniqueID", String.class);
  }

  @Override
  public String getName() {
    if (isRoot()) {
      return EXTERNAL_ID_ROOT_CATEGORY;
    }
    return DataMapHelper.getValueForKey(getDelegate(), "name", String.class);
  }

  @Override
  public Markup getShortDescription() {
    String shortDescription = DataMapHelper.getValueForKey(getDelegate(), "shortDescription", String.class);
    return toRichtext(shortDescription);
  }

  @Override
  public Markup getLongDescription() {
    String longDescription = DataMapHelper.getValueForKey(getDelegate(), "longDescription", String.class);
    return toRichtext(longDescription);
  }

  @Override
  public String getThumbnailUrl() {
    return getAssetUrlProvider().getImageUrl(DataMapHelper.getValueForKey(getDelegate(), "thumbnail", String.class), true);
  }

  @Override
  public String getDefaultImageUrl() {
    return getAssetUrlProvider().getImageUrl(DataMapHelper.getValueForKey(getDelegate(), "fullImage", String.class));
  }

  @Override
  @Nonnull
  public List<Category> getChildren() throws CommerceException {
    if (isRoot()) {
      return getCatalogService().findTopCategories(sitesService.getSite(getContext().getSiteId()));
    }
    return getCatalogService().findSubCategories(this);
  }

  @Override
  @Nonnull
  public List<Product> getProducts() throws CommerceException {
    return getCatalogService().findProductsByCategory(this);
  }

  @Override
  @Nullable
  public Category getParent() throws CommerceException {
    if (isRoot()) {
      return null;
    } else {
      String parentCatalogGroupID = DataMapHelper.getValueForKey(getDelegate(), "parentCatalogGroupID[0]", String.class);
      if (parentCatalogGroupID != null && !parentCatalogGroupID.isEmpty() && !parentCatalogGroupID.equals("-1")) {
        return (Category) getCommerceBeanFactory().createBeanFor(
                CommerceIdHelper.formatCategoryTechId(parentCatalogGroupID), getContext());
      } else {
        return (Category) getCommerceBeanFactory().createBeanFor(CommerceIdHelper.formatCategoryId(EXTERNAL_ID_ROOT_CATEGORY), getContext());
      }
    }
  }

  @Override
  @Nonnull
  public List<Category> getBreadcrumb() throws CommerceException {
    List<Category> result = new ArrayList<>();
    Category parent = getParent();
    if (parent != null) {
      result.addAll(parent.getBreadcrumb());
    }
    if (!isRoot()) {
      result.add(this);
    }
    return result;
  }

  @Override
  @Nonnull
  public String getSeoSegment() {
    String localizedSeoSegment = DataMapHelper.getValueForKey(getDelegate(), "seo_token_ntk", String.class);
    if (isBlank(localizedSeoSegment)) {
      if (getDefaultLocale() == null) {
        LOG.warn("Default locale does not set for commerce beans.");
      }
      if (!getLocale().equals(getDefaultLocale())) {
        LOG.info("Category {} does not have a seo segment for locale {}. Return the seo segment of the category for the default locale {}.",
                getName(), getLocale(), getDefaultLocale());
        StoreContext newStoreContext = StoreContextHelper.getCurrentContextFor(getDefaultLocale());
          Category master = getCatalogService().withStoreContext(newStoreContext).findCategoryById(CommerceIdHelper.formatCategoryId(getExternalId()));
          if (master!=null && !equals(master)) {
            localizedSeoSegment = master.getSeoSegment();
        }
      }
    }

    if (localizedSeoSegment == null) {
      localizedSeoSegment = "";
    }

    return localizedSeoSegment;
  }

  @Override
  public String getMetaDescription() {
    return DataMapHelper.getValueForKey(getDelegate(), "metaDescription", String.class);
  }

  @Override
  public String getMetaKeywords() {
    return DataMapHelper.getValueForKey(getDelegate(), "metaKeyword", String.class);
  }

  @Override
  public String getTitle() {
    return DataMapHelper.getValueForKey(getDelegate(), "title", String.class);
  }

  @Override
  public String getDisplayName() {
    return getExternalId();
  }

  @Override
  public CatalogPicture getCatalogPicture(){
    AssetService assetService = getAssetService();
    if(null != assetService) {
      return assetService.getCatalogPicture(getDefaultImageUrl());
    }
    return new CatalogPicture("#", null);
  }

  @Override
  public Content getPicture() {
    List<Content> pictures = getPictures();
    return pictures != null && !pictures.isEmpty() ? pictures.get(0) : null;
  }

  @Override
  public List<Content> getPictures() {
    AssetService assetService = getAssetService();
    if(assetService != null) {
      return assetService.findPictures(getReference());
    }
    return Collections.emptyList();
  }

  @Override
  public List<Content> getVisuals() {
      AssetService assetService = getAssetService();
    if(null != assetService) {
      return assetService.findVisuals(getReference());
    }
    return Collections.emptyList();
  }

  @Override
  public List<Content> getDownloads() {
    AssetService assetService = getAssetService();
    if(null != assetService) {
      return assetService.findDownloads(getReference());
    }
    return Collections.emptyList();
  }


  public boolean isRoot(){
    return CatalogRootHelper.isCatalogRoot(this);
  }

  @Required
  public void setSitesService(SitesServiceImpl sitesService) {
    this.sitesService = sitesService;
  }

}
