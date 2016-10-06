package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.user.UserContextHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;
import static com.google.common.collect.Maps.newHashMap;

public class CatalogServiceImpl implements CatalogService {
  private static final Logger LOG = LoggerFactory.getLogger(CatalogServiceImpl.class);

  static final String EXTERNAL_ID_ROOT_CATEGORY = "ROOT";

  private WcCatalogWrapperService catalogWrapperService;
  private StoreContextProvider storeContextProvider;
  private CommerceBeanFactory commerceBeanFactory;
  private CommerceIdProvider commerceIdProvider;
  private CommerceCache commerceCache;

  private String wcsUrl;
  private String wcsStoreUrl;
  private String wcsAssetsUrl;

  private boolean useExternalIdForBeanCreation;

  public WcCatalogWrapperService getCatalogWrapperService() {
    return catalogWrapperService;
  }

  @Required
  public void setCatalogWrapperService(WcCatalogWrapperService catalogWrapperService) {
    this.catalogWrapperService = catalogWrapperService;
  }

  public CommerceBeanFactory getCommerceBeanFactory() {
    return commerceBeanFactory;
  }

  @Required
  public void setStoreContextProvider(StoreContextProvider storeContextProvider) {
    this.storeContextProvider = storeContextProvider;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  @Required
  public void setCommerceIdProvider(CommerceIdProvider commerceIdProvider) {
    this.commerceIdProvider = commerceIdProvider;
  }

  public CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Required
  public void setWcsUrl(String wcsUrl) {
    this.wcsUrl = wcsUrl;
  }

  /**
   * The base url to the commerce system as can bee seen by an end user. This must not be any kind
   * of internal url that may be used as part of the hidden backend communication between CAE and
   * commerce system.
   *
   * @return the publicly known base url to the commerce system
   */
  public String getWcsUrl() {
    return CommercePropertyHelper.replaceTokens(wcsUrl, StoreContextHelper.getCurrentContext());
  }

  @Required
  public void setWcsStoreUrl(String wcsStoreUrl) {
    this.wcsStoreUrl = wcsStoreUrl;
  }

  public String getWcsStoreUrl() {
    return CommercePropertyHelper.replaceTokens(wcsStoreUrl, StoreContextHelper.getCurrentContext());
  }

  @Required
  public void setWcsAssetsUrl(String wcsAssetsUrl) {
    this.wcsAssetsUrl = wcsAssetsUrl;
  }

  public String getWcsAssetsUrl() {
    return CommercePropertyHelper.replaceTokens(wcsAssetsUrl, StoreContextHelper.getCurrentContext());
  }

  public void setUseExternalIdForBeanCreation(boolean useExternalIdForBeanCreation) {
    this.useExternalIdForBeanCreation = useExternalIdForBeanCreation;
  }

  @PostConstruct
  void initialize(){
    if(null != wcsAssetsUrl) {
      if (!wcsAssetsUrl.endsWith("/")) {
        this.wcsAssetsUrl = wcsAssetsUrl + "/";
      }
      validateUrlString(this.wcsAssetsUrl, "wcsAssetsUrl");
    }

    if(null != wcsStoreUrl) {
      if (!wcsStoreUrl.endsWith("/")) {
        this.wcsStoreUrl = wcsStoreUrl + "/";
      }
      validateUrlString(wcsStoreUrl, "wcsStoreUrl");
    }
  }

  private static void validateUrlString(String string, String urlPropertyName) {
    // validate format of url, e.g. a path part starting with two slashes can lead to broken
    // images in the store front
    try {
      URL url = new URL(string);
      String path = url.getPath();
      if (path.startsWith("//")) {
        LOG.warn("Invalid format of " + urlPropertyName + ": URL's path part starts with '//'. URL is {}", url.toExternalForm());
      }
    } catch (MalformedURLException e) {
      throw new IllegalStateException(urlPropertyName + " is invalid.", e);
    }
  }

  static boolean isCatalogRootId(String categoryId) {
    return categoryId != null && categoryId.endsWith("/" + EXTERNAL_ID_ROOT_CATEGORY);
  }

  @Override
  @Nullable
  public Product findProductById(@Nonnull final String id) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    if (id.isEmpty()) {
      return null;
    }
    Map wcProductMap = (Map) commerceCache.get(
            new ProductCacheKey(id, currentContext, UserContextHelper.getCurrentContext(), catalogWrapperService, commerceCache));
    return createProductBeanFor(wcProductMap, currentContext, false);
  }

  @Nullable
  Product findProductByExternalId(@Nonnull final String externalId) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map wcProductMap = (Map) commerceCache.get(
            new ProductCacheKey(CommerceIdHelper.formatProductId(externalId), currentContext, UserContextHelper.getCurrentContext(), catalogWrapperService, commerceCache));
    return createProductBeanFor(wcProductMap, currentContext, false);
  }

  @Nullable
  public Product findProductByExternalTechId(@Nonnull final String externalTechId) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map wcProductMap = (Map) commerceCache.get(
            new ProductCacheKey(CommerceIdHelper.formatProductTechId(externalTechId), currentContext, UserContextHelper.getCurrentContext(), catalogWrapperService, commerceCache));
    return createProductBeanFor(wcProductMap, currentContext, false);
  }

  @Override
  @Nullable
  public Product findProductBySeoSegment(@Nonnull final String seoSegment) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map wcProductMap = (Map) commerceCache.get(
            new ProductCacheKey(CommerceIdHelper.formatProductSeoId(seoSegment), currentContext, UserContextHelper.getCurrentContext(), catalogWrapperService, commerceCache));
    return createProductBeanFor(wcProductMap, currentContext, false);
  }

  @Override
  @Nullable
  public ProductVariant findProductVariantById(@Nonnull final String id) throws CommerceException {
    return (ProductVariant) findProductById(id);
  }

  @Override
  @Nonnull
  public List<Product> findProductsByCategory(@Nonnull final Category category) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    if (category.isRoot()) {
      // the wcs has no root category thus asking would lead to an error
      return Collections.emptyList();
    }
    List<Map<String, Object>> wcProductsMap = (List<Map<String, Object>>) commerceCache.get(
            new ProductsByCategoryCacheKey(category.getExternalTechId(), currentContext, UserContextHelper.getCurrentContext(), catalogWrapperService, commerceCache));
    return createProductBeansFor(wcProductsMap, currentContext);
  }

  @Override
  @Nullable
  public Category findCategoryById(@Nonnull final String id) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    if (isCatalogRootId(id)){
      return (Category) getCommerceBeanFactory()
              .createBeanFor(CommerceIdHelper.formatCategoryId(EXTERNAL_ID_ROOT_CATEGORY), currentContext);
    }
    Map<String, Object> wcCategory = (Map<String, Object>) commerceCache.get(
            new CategoryCacheKey(id, currentContext, UserContextHelper.getCurrentContext(), catalogWrapperService, commerceCache));
    return createCategoryBeanFor(wcCategory, currentContext, false);
  }

  @Override
  @Nullable
  public Category findCategoryBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map<String, Object> wcCategory = (Map<String, Object>) commerceCache.get(
            new CategoryCacheKey(CommerceIdHelper.formatCategorySeoId(seoSegment), currentContext, UserContextHelper.getCurrentContext(), catalogWrapperService, commerceCache));
    return createCategoryBeanFor(wcCategory, currentContext, false);
  }

  @Override
  @Nonnull
  public Category findRootCategory() throws CommerceException {
    String rootCategoryId = commerceIdProvider.formatCategoryId(EXTERNAL_ID_ROOT_CATEGORY);
    return (Category) getCommerceBeanFactory().createBeanFor(rootCategoryId, storeContextProvider.getCurrentContext());
  }

  /**
   * Implementation uses current {@see StoreContext} to resolve some necessary properties
   * for rest call (e.g. site, currency, locale ...)
   * //TODO remove site param from interface
   */
  @Override
  @Nonnull
  public List<Category> findTopCategories(Site site) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    List<Map<String, Object>> wcCategories = (List<Map<String, Object>>) commerceCache.get(
            new TopCategoriesCacheKey(currentContext, UserContextHelper.getCurrentContext(), catalogWrapperService, commerceCache));
    return createCategoryBeansFor(wcCategories, currentContext);
  }

  @Override
  @Nonnull
  public List<Category> findSubCategories(@Nonnull final Category parentCategory) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    if (parentCategory.isRoot()) {
      return findTopCategories(null);
    }
    List<Map<String, Object>> wcCategories = (List<Map<String, Object>>) commerceCache.get(
            new SubCategoriesCacheKey(parentCategory.getExternalTechId(), currentContext, UserContextHelper.getCurrentContext(), catalogWrapperService, commerceCache));
    return createCategoryBeansFor(wcCategories, currentContext);
  }

  /**
   * Search for Products
   * @param searchTerm   search keywords
   * @param searchParams map of search params:
   *                     <ul>
   *                     <li>pageNumber {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_PAGENUMBER}</li>
   *                     <li>pageSize {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_PAGESIZE}</li>
   *                     <li>categoryId {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_CATEGORYID}</li>
   *                     </ul>
   *                     In addition ibm rest specific parameters may be passed. See ibm rest api of the ProductViewHandler
   *                     for more details. See {@link WcCatalogWrapperService#validSearchParams} for the complete list of allowed parameters
   *                     to pass to the IBM ProductViewHandler.
   * @return SearchResult containing the list of products
   * @throws CommerceException
   */
  @Override
  @Nonnull
  public SearchResult<Product> searchProducts(@Nonnull final String searchTerm,
                                              @Nullable Map<String, String> searchParams) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    SearchResult<Map<String, Object>> wcSearchResult = getCatalogWrapperService().searchProducts(
            searchTerm, searchParams, currentContext, SearchType.SEARCH_TYPE_PRODUCTS, UserContextHelper.getCurrentContext());
    SearchResult<Product> result = new SearchResult<>();
    result.setSearchResult(createProductBeansFor(wcSearchResult.getSearchResult(), currentContext));
    result.setTotalCount(wcSearchResult.getTotalCount());
    result.setPageNumber(wcSearchResult.getPageNumber());
    result.setPageSize(wcSearchResult.getPageSize());
    return result;
  }

  /**
   * Search for ProductVariants
   * @param searchTerm   search keywords
   * @param searchParams map of search params:
   *                     <ul>
   *                     <li>pageNumber {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_PAGENUMBER}</li>
   *                     <li>pageSize {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_PAGESIZE}</li>
   *                     <li>categoryId {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_CATEGORYID}</li>
   *                     </ul>
   *                     In addition ibm rest specific parameters may be passed. See ibm rest api of the ProductViewHandler
   *                     for more details. See {@link WcCatalogWrapperService#validSearchParams} for the complete list of allowed parameters
   *                     to pass to the IBM ProductViewHandler.
   * @return SearchResult containing the list of product variants
   * @throws CommerceException
   */
  @Override
  @Nonnull
  public SearchResult<ProductVariant> searchProductVariants(@Nonnull final String searchTerm,
                                                            @Nullable Map<String, String> searchParams) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    SearchResult<Map<String, Object>> wcSearchResult = getCatalogWrapperService().searchProducts(
            searchTerm, searchParams, currentContext, SearchType.SEARCH_TYPE_PRODUCT_VARIANTS, UserContextHelper.getCurrentContext());
    SearchResult<ProductVariant> result = new SearchResult<>();
    List<ProductVariant> productBeansFor = createProductBeansFor(wcSearchResult.getSearchResult(), currentContext);
    result.setSearchResult(productBeansFor);
    result.setTotalCount(wcSearchResult.getTotalCount());
    result.setPageNumber(wcSearchResult.getPageNumber());
    result.setPageSize(wcSearchResult.getPageSize());
    return result;
  }

  public String getLanguageId(Locale locale) {
    return getCatalogWrapperService().getLanguageId(locale);
  }

  protected <T extends Product> T createProductBeanFor(Map<String, Object> productWrapper, StoreContext context, boolean reloadById) {
    if (productWrapper != null) {
      String id;
      if ("ItemBean".equals(DataMapHelper.getValueForKey(productWrapper, "catalogEntryTypeCode", String.class))) {
        id = useExternalIdForBeanCreation ?
                CommerceIdHelper.formatProductVariantId(DataMapHelper.getValueForKey(productWrapper, "partNumber", String.class)) :
                CommerceIdHelper.formatProductVariantTechId(DataMapHelper.getValueForKey(productWrapper, "uniqueID", String.class));
      } else {
        id = useExternalIdForBeanCreation ?
                CommerceIdHelper.formatProductId(DataMapHelper.getValueForKey(productWrapper, "partNumber", String.class)) :
                CommerceIdHelper.formatProductTechId(DataMapHelper.getValueForKey(productWrapper, "uniqueID", String.class));
      }
      if (CommerceIdHelper.isProductId(id) || CommerceIdHelper.isProductVariantId(id)) {
        final ProductBase product = (ProductBase) commerceBeanFactory.createBeanFor(id, context);
        // register the product wrapper with the cache, it will optimize later accesses (there are good
        // chances that the beans will be called immediately after this call
        // Todo: currently we use it only for studio calls, but check if we can do it for a cae webapp as well
        // (it probably requires that we are able to reload beans dynamically if someone tries to read a property that is not available)
        Transformer transformer = null;
        if (reloadById) {
          transformer = new ProductDelegateLoader(product);
        }
        product.setDelegate(asLazyMap(productWrapper, transformer));
        return (T) product;
      }
    }
    return null;
  }

  protected <T extends Product> List<T> createProductBeansFor(List<Map<String, Object>> productWrappers, StoreContext context) {
    if (productWrappers == null || productWrappers.isEmpty()) {
      return Collections.emptyList();
    }
    List<T> result = new ArrayList<>(productWrappers.size());
    for (Map<String, Object> productWrapper : productWrappers) {
      T productBeanFor = createProductBeanFor(productWrapper, context, true);
      result.add(productBeanFor);
    }
    return Collections.unmodifiableList(result);
  }

  protected Category createCategoryBeanFor(@Nullable Map<String, Object> categoryWrapper, @Nonnull StoreContext context, boolean reloadById) {
    if (categoryWrapper != null) {
      String id = useExternalIdForBeanCreation ?
              CommerceIdHelper.formatCategoryId(DataMapHelper.getValueForKey(categoryWrapper, "identifier", String.class)) :
              CommerceIdHelper.formatCategoryTechId(DataMapHelper.getValueForKey(categoryWrapper, "uniqueID", String.class));
      if (CommerceIdHelper.isCategoryId(id)) {
        final CategoryImpl category = (CategoryImpl) commerceBeanFactory.createBeanFor(id, context);
        // register the category wrapper with the cache, it will optimize later accesses (there are good
        // chances that the beans will be called immediately after this call
        // Todo: currently we use it only for studio calls, but check if we can do it for a cae webapp as well
        // (it probably requires that we are able to reload beans dynamically if someone tries to read a property that is not available)
        Transformer transformer = null;
        if (reloadById) {
          transformer = new CategoryDelegateLoader(category);
        }
        category.setDelegate(asLazyMap(categoryWrapper, transformer));
        return category;
      }
    }
    return null;
  }

  @Nullable
  public static Map<String, Object> asLazyMap(@Nullable Map<String, Object> map, @Nullable Transformer transformer) {
    if(null != map && null != transformer) {
      //noinspection unchecked
      return LazyMap.decorate(newHashMap(map), transformer);
    }
    return map;
  }

  protected List<Category> createCategoryBeansFor(List<Map<String, Object>> categoryWrappers, StoreContext context) {
    if (categoryWrappers == null || categoryWrappers.isEmpty()) {
      return Collections.emptyList();
    }
    List<Category> result = new ArrayList<>(categoryWrappers.size());
    for (Map<String, Object> categoryWrapper : categoryWrappers) {
      result.add(createCategoryBeanFor(categoryWrapper, context, true));
    }
    return Collections.unmodifiableList(result);
  }

  @Nonnull
  @Override
  public CatalogService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, CatalogService.class);
  }
}
