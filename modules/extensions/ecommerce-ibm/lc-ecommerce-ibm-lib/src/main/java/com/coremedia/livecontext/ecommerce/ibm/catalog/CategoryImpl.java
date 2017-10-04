package com.coremedia.livecontext.ecommerce.ibm.catalog;

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
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapTransformationHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.xml.Markup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
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
      delegate = getDelegateFromCache();
      if (delegate == null) {
        throw new NotFoundException(getId() + " (category not found in catalog)");
      }
    }
    return delegate;
  }

  /**
   * Perform by-id-call to get detail data
   *
   * @return detail data map
   */
  Map<String, Object> getDelegateFromCache() {
    //noinspection unchecked
    return (Map<String, Object>) getCommerceCache().get(
            new CategoryCacheKey(getId(), getContext(), UserContextHelper.getCurrentContext(), getCatalogWrapperService(), getCommerceCache()));
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
      return CatalogServiceImpl.EXTERNAL_ID_ROOT_CATEGORY;
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
      return CatalogServiceImpl.EXTERNAL_ID_ROOT_CATEGORY;
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
    return toRichtext(longDescription, false);
  }

  @Override
  public String getThumbnailUrl() {
    String thumbnailUri = DataMapHelper.getValueForKey(getDelegate(), "thumbnail", String.class);
    return null == thumbnailUri ? null : getAssetUrlProvider().getImageUrl(thumbnailUri, true);
  }

  @Override
  public String getDefaultImageUrl() {
    String defaultImageUri = DataMapHelper.getValueForKey(getDelegate(), "fullImage", String.class);
    return null == defaultImageUri ? null : getAssetUrlProvider().getImageUrl(defaultImageUri);
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
    }

    StoreContext context = getContext();

    String catalogId = context.getCatalogId();
    List<String> parentCategoryIds = DataMapTransformationHelper.getParentCatGroupIdForSingleWrapper(getDelegate(),
            catalogId);

    if (!parentCategoryIds.isEmpty()) {
      String parentCatalogGroupID = parentCategoryIds.get(0);
      if (!isNullOrEmpty(parentCatalogGroupID) && !parentCatalogGroupID.equals("-1")) {
        String id = CommerceIdHelper.formatCategoryTechId(parentCatalogGroupID);
        return (Category) getCommerceBeanFactory().createBeanFor(id, context);
      }
    }

    String id = CommerceIdHelper.formatCategoryId(CatalogServiceImpl.EXTERNAL_ID_ROOT_CATEGORY);
    return (Category) getCommerceBeanFactory().createBeanFor(id, context);
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

  private String getCmSeoSegment() {
    String cmLocalizedSeoSegment = DataMapHelper.getValueForKey(getDelegate(), "cm_seo_token_ntk", String.class);
    cmLocalizedSeoSegment = processCmLocalizedSeoSegment(cmLocalizedSeoSegment);

    if (cmLocalizedSeoSegment != null) {
      String[] localizedSeoSegments = cmLocalizedSeoSegment.split(";");
      List<String> localizedSeoSegmentList = Arrays.asList(localizedSeoSegments);
      if (localizedSeoSegmentList.size() > 1) {
        String storeId = getStoreId();
        for (String seoSegment : localizedSeoSegmentList) {
          if (seoSegment.startsWith(storeId)) {
            return seoSegment.substring(storeId.length() + 1);
          }
        }
        return localizedSeoSegmentList.get(0).substring(cmLocalizedSeoSegment.indexOf("_") + 1);
      } else {
        return cmLocalizedSeoSegment.substring(cmLocalizedSeoSegment.indexOf("_") + 1);
      }
    } else {
      return null;
    }
  }

  private String processCmLocalizedSeoSegment(String cmLocalizedSeoSegment) {
    if (isBlank(cmLocalizedSeoSegment)) {
      if (getDefaultLocale() == null) {
        LOG.warn("Default locale does not set for commerce beans.");
      }
      if (!getLocale().equals(getDefaultLocale())) {
        LOG.info("Category {} does not have a cm seo segment for locale {}. Return the cm seo segment of the category for the default locale {}.",
                getName(), getLocale(), getDefaultLocale());
        StoreContext newStoreContext = StoreContextHelper.getCurrentContextFor(getDefaultLocale());
        CategoryImpl master = (CategoryImpl) getCatalogService().withStoreContext(newStoreContext).findCategoryById(CommerceIdHelper.formatCategoryId(getExternalId()));
        if (master!=null && !equals(master)) {
          cmLocalizedSeoSegment = master.getCmSeoSegment();
        }
      }
    }
    return cmLocalizedSeoSegment;
  }

  @Nullable
  @Override
  public String getSeoSegment() {
    String localizedSeoSegment = getCmSeoSegment();
    if (isBlank(localizedSeoSegment)) {
      localizedSeoSegment = DataMapHelper.getValueForKey(getDelegate(), "seo_token_ntk", String.class);
      localizedSeoSegment = processLocalizedSeoSegment(localizedSeoSegment);

      if (localizedSeoSegment == null) {
        localizedSeoSegment = "";
      } else {
        String[] localizedSeoSegments = localizedSeoSegment.split(";");
        List<String> localizedSeoSegmentList = Arrays.asList(localizedSeoSegments);
        if (localizedSeoSegmentList.size() > 1) {
          localizedSeoSegment = localizedSeoSegmentList.get(0);
        }
      }
    }

    return localizedSeoSegment;
  }

  private String processLocalizedSeoSegment(String localizedSeoSegment) {
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
  public CatalogPicture getCatalogPicture() {
    AssetService assetService = getAssetService();
    if (null != assetService) {
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
    if (assetService != null) {
      return assetService.findPictures(getReference());
    }
    return Collections.emptyList();
  }

  @Override
  public List<Content> getVisuals() {
    AssetService assetService = getAssetService();
    if (null != assetService) {
      return assetService.findVisuals(getReference());
    }
    return Collections.emptyList();
  }

  @Override
  public List<Content> getDownloads() {
    AssetService assetService = getAssetService();
    if (null != assetService) {
      return assetService.findDownloads(getReference());
    }
    return Collections.emptyList();
  }


  public boolean isRoot() {
    return CatalogServiceImpl.isCatalogRootId(getId());
  }

  @Required
  public void setSitesService(SitesServiceImpl sitesService) {
    this.sitesService = sitesService;
  }

}
