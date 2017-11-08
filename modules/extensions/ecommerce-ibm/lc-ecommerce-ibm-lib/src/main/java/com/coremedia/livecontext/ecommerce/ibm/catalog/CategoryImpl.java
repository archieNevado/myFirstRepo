package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogName;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class CategoryImpl extends AbstractIbmCommerceBean implements Category, CommerceObject {

  private static final Logger LOG = LoggerFactory.getLogger(CategoryImpl.class);

  static final String ROOT_CATEGORY_ROLE_ID = "ROOT";

  private Map<String, Object> delegate;
  private WcCatalogWrapperService catalogWrapperService;

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
    CategoryCacheKey cacheKey = new CategoryCacheKey(getId(), getContext(), UserContextHelper.getCurrentContext(),
            getCatalogWrapperService(), getCommerceCache());

    return getCommerceCache().get(cacheKey);
  }

  @Override
  public void load() {
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
  public String getExternalId() {
    if (isRoot()) {
      return ROOT_CATEGORY_ROLE_ID;
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
      return ROOT_CATEGORY_ROLE_ID;
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
    return DataMapHelper.findValueForKey(getDelegate(), "thumbnail", String.class)
            .map(thumbnailUri -> getAssetUrlProvider().getImageUrl(thumbnailUri, true))
            .orElse(null);
  }

  @Override
  public String getDefaultImageUrl() {
    return DataMapHelper.findValueForKey(getDelegate(), "fullImage", String.class)
            .map(defaultImageUri -> getAssetUrlProvider().getImageUrl(defaultImageUri))
            .orElse(null);
  }

  @Override
  @Nonnull
  public List<Category> getChildren() {
    if (isRoot()) {
      CatalogAlias catalogAlias = getId().getCatalogAlias();
      return getCatalogService().findTopCategories(catalogAlias, getContext());
    }
    return getCatalogService().findSubCategories(this);
  }

  @Override
  @Nonnull
  public List<Product> getProducts() {
    return getCatalogService().findProductsByCategory(this);
  }

  @Override
  @Nullable
  public Category getParent() {
    if (isRoot()) {
      return null;
    }

    StoreContext context = getContext();
    CatalogAlias catalogAlias = getCatalogAlias();

    String catalogId = getCatalog().map(Catalog::getExternalId).orElse(null);
    List<String> parentCategoryIds = DataMapTransformationHelper.getParentCatGroupIdForSingleWrapper(getDelegate(),
            catalogId);

    if (!parentCategoryIds.isEmpty()) {
      String parentCatalogGroupID = parentCategoryIds.get(0);
      if (!isNullOrEmpty(parentCatalogGroupID) && !parentCatalogGroupID.equals("-1")) {
        CommerceId commerceId = getCommerceIdProvider().formatCategoryTechId(catalogAlias, parentCatalogGroupID);
        return (Category) getCommerceBeanFactory().createBeanFor(commerceId, context);
      }
    }

    CommerceId commerceId = getCommerceIdProvider().formatCategoryId(catalogAlias, ROOT_CATEGORY_ROLE_ID);
    return (Category) getCommerceBeanFactory().createBeanFor(commerceId, context);
  }

  @Override
  @Nonnull
  public List<Category> getBreadcrumb() {
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

    if (cmLocalizedSeoSegment == null) {
      return null;
    }

    String[] localizedSeoSegments = cmLocalizedSeoSegment.split(";");
    List<String> localizedSeoSegmentList = Arrays.asList(localizedSeoSegments);
    if (localizedSeoSegmentList.size() <= 1) {
      return cmLocalizedSeoSegment.substring(cmLocalizedSeoSegment.indexOf("_") + 1);
    }

    String storeId = getStoreId();
    for (String seoSegment : localizedSeoSegmentList) {
      if (seoSegment.startsWith(storeId)) {
        return seoSegment.substring(storeId.length() + 1);
      }
    }

    return localizedSeoSegmentList.get(0).substring(cmLocalizedSeoSegment.indexOf("_") + 1);
  }

  private String processCmLocalizedSeoSegment(String cmLocalizedSeoSegment) {
    if (!isBlank(cmLocalizedSeoSegment)) {
      return cmLocalizedSeoSegment;
    }

    Locale defaultLocale = getDefaultLocale();
    if (defaultLocale == null) {
      LOG.warn("Default locale does not set for commerce beans.");
    }

    if (getLocale().equals(defaultLocale)) {
      return cmLocalizedSeoSegment;
    }

    LOG.info("Category {} does not have a cm seo segment for locale {}. Return the cm seo segment of the category for the default locale {}.",
            getName(), getLocale(), defaultLocale);

    StoreContext newStoreContext = StoreContextHelper.getCurrentContextFor(defaultLocale);
    CommerceId categoryId = getCommerceIdProvider().formatCategoryId(getCatalogAlias(), getExternalId());

    CategoryImpl master = (CategoryImpl) getCatalogService().withStoreContext(newStoreContext)
            .findCategoryById(categoryId, newStoreContext);

    if (master != null && !equals(master)) {
      cmLocalizedSeoSegment = master.getCmSeoSegment();
    }

    return cmLocalizedSeoSegment;
  }

  @Nullable
  @Override
  public String getSeoSegment() {
    String localizedSeoSegment = getCmSeoSegment();

    if (!isBlank(localizedSeoSegment)) {
      return localizedSeoSegment;
    }

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

    return localizedSeoSegment;
  }

  private String processLocalizedSeoSegment(String localizedSeoSegment) {
    if (!isBlank(localizedSeoSegment)) {
      return localizedSeoSegment;
    }

    Locale defaultLocale = getDefaultLocale();
    if (defaultLocale == null) {
      LOG.warn("Default locale does not set for commerce beans.");
    }

    if (!getLocale().equals(defaultLocale)) {
      LOG.info("Category {} does not have a seo segment for locale {}. Return the seo segment of the category for the default locale {}.",
              getName(), getLocale(), defaultLocale);

      StoreContext newStoreContext = StoreContextHelper.getCurrentContextFor(defaultLocale);
      CommerceId commerceId = getCommerceIdProvider().formatCategoryId(getCatalogAlias(), getExternalId());
      Category master = getCatalogService().withStoreContext(newStoreContext).findCategoryById(commerceId, newStoreContext);

      if (master != null && !equals(master)) {
        localizedSeoSegment = master.getSeoSegment();
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

  @Nonnull
  @Override
  public String getDisplayName() {
    if (!isRoot()) {
      return getExternalId();
    }

    Optional<Catalog> catalog = getCatalog();
    Optional<String> catalogName = catalog.map(Catalog::getName).map(CatalogName::value);

    return withDefaultCatalogName(catalogName.orElseGet(this::getExternalId));
  }

  @Nonnull
  private String withDefaultCatalogName(@Nonnull String catalogName) {
    return DEFAULT_CATALOG_ALIAS.equals(getCatalogAlias()) ? catalogName + " (Default)" : catalogName;
  }

  @Override
  public CatalogPicture getCatalogPicture() {
    AssetService assetService = getAssetService();

    if (assetService == null) {
      return new CatalogPicture("#", null);
    }

    return assetService.getCatalogPicture(getDefaultImageUrl(), getReference());
  }

  @Override
  public Content getPicture() {
    List<Content> pictures = getPictures();
    return pictures != null && !pictures.isEmpty() ? pictures.get(0) : null;
  }

  @Override
  public List<Content> getPictures() {
    AssetService assetService = getAssetService();

    if (assetService == null) {
      return emptyList();
    }

    return assetService.findPictures(getReference());
  }

  @Override
  public List<Content> getVisuals() {
    AssetService assetService = getAssetService();

    if (assetService == null) {
      return emptyList();
    }

    return assetService.findVisuals(getReference());
  }

  @Override
  public List<Content> getDownloads() {
    AssetService assetService = getAssetService();

    if (assetService == null) {
      return emptyList();
    }

    return assetService.findDownloads(getReference());
  }

  public boolean isRoot() {
    return isRootCategoryId(getId());
  }

  static boolean isRootCategoryId(@Nonnull CommerceId id) {
    return id.getExternalId().map(ROOT_CATEGORY_ROLE_ID::equals).orElse(false);
  }

}
