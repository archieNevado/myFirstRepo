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
import com.coremedia.livecontext.ecommerce.user.UserContext;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

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
    StoreContext storeContext = getStoreContext();
    return CommercePropertyHelper.replaceTokens(wcsUrl, storeContext);
  }

  @Required
  public void setWcsStoreUrl(String wcsStoreUrl) {
    this.wcsStoreUrl = wcsStoreUrl;
  }

  public String getWcsStoreUrl() {
    StoreContext storeContext = getStoreContext();
    return CommercePropertyHelper.replaceTokens(wcsStoreUrl, storeContext);
  }

  @Required
  public void setWcsAssetsUrl(String wcsAssetsUrl) {
    this.wcsAssetsUrl = wcsAssetsUrl;
  }

  public String getWcsAssetsUrl() {
    StoreContext storeContext = getStoreContext();
    return CommercePropertyHelper.replaceTokens(wcsAssetsUrl, storeContext);
  }

  public void setUseExternalIdForBeanCreation(boolean useExternalIdForBeanCreation) {
    this.useExternalIdForBeanCreation = useExternalIdForBeanCreation;
  }

  @PostConstruct
  void initialize() {
    if (null != wcsAssetsUrl) {
      if (!wcsAssetsUrl.endsWith("/")) {
        this.wcsAssetsUrl = wcsAssetsUrl + "/";
      }
      validateUrlString(this.wcsAssetsUrl, "wcsAssetsUrl");
    }

    if (null != wcsStoreUrl) {
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
        LOG.warn("Invalid format of " + urlPropertyName + ": URL's path part starts with '//'. URL is {}",
                url.toExternalForm());
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
  public Product findProductById(@Nonnull String id) throws CommerceException {
    if (id.isEmpty()) {
      return null;
    }

    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    Map wcProductMap = (Map) commerceCache.get(
            new ProductCacheKey(id, storeContext, userContext, catalogWrapperService, commerceCache));

    return createProductBeanFor(wcProductMap, storeContext, false);
  }

  @Nullable
  Product findProductByExternalId(@Nonnull String externalId) throws CommerceException {
    String id = CommerceIdHelper.formatProductId(externalId);
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    Map wcProductMap = (Map) commerceCache.get(new ProductCacheKey(id, storeContext, userContext,
            catalogWrapperService, commerceCache));

    return createProductBeanFor(wcProductMap, storeContext, false);
  }

  @Nullable
  public Product findProductByExternalTechId(@Nonnull String externalTechId) throws CommerceException {
    String id = CommerceIdHelper.formatProductTechId(externalTechId);
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    Map wcProductMap = (Map) commerceCache.get(new ProductCacheKey(id, storeContext, userContext, catalogWrapperService,
            commerceCache));

    return createProductBeanFor(wcProductMap, storeContext, false);
  }

  @Override
  @Nullable
  public Product findProductBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    String id = CommerceIdHelper.formatProductSeoId(seoSegment);
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    Map wcProductMap = (Map) commerceCache.get(new ProductCacheKey(id, storeContext, userContext, catalogWrapperService,
            commerceCache));

    return createProductBeanFor(wcProductMap, storeContext, false);
  }

  @Override
  @Nullable
  public ProductVariant findProductVariantById(@Nonnull String id) throws CommerceException {
    return (ProductVariant) findProductById(id);
  }

  @Override
  @Nonnull
  public List<Product> findProductsByCategory(@Nonnull Category category) throws CommerceException {
    if (category.isRoot()) {
      // the wcs has no root category thus asking would lead to an error
      return emptyList();
    }

    String id = category.getExternalTechId();
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    List<Map<String, Object>> wcProductsMap = (List<Map<String, Object>>) commerceCache.get(
            new ProductsByCategoryCacheKey(id, storeContext, userContext, catalogWrapperService, commerceCache));

    return createProductBeansFor(wcProductsMap, storeContext);
  }

  @Override
  @Nullable
  public Category findCategoryById(@Nonnull String id) throws CommerceException {
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    if (isCatalogRootId(id)) {
      return (Category) getCommerceBeanFactory()
              .createBeanFor(CommerceIdHelper.formatCategoryId(EXTERNAL_ID_ROOT_CATEGORY), storeContext);
    }

    Map<String, Object> wcCategory = (Map<String, Object>) commerceCache.get(new CategoryCacheKey(id, storeContext,
            userContext, catalogWrapperService, commerceCache));

    return createCategoryBeanFor(wcCategory, storeContext, false);
  }

  @Override
  @Nullable
  public Category findCategoryBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    String id = CommerceIdHelper.formatCategorySeoId(seoSegment);
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    Map<String, Object> wcCategory = (Map<String, Object>) commerceCache.get(new CategoryCacheKey(id, storeContext,
            userContext, catalogWrapperService, commerceCache));

    return createCategoryBeanFor(wcCategory, storeContext, false);
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
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    List<Map<String, Object>> wcCategories = (List<Map<String, Object>>) commerceCache.get(new TopCategoriesCacheKey(
            storeContext, userContext, catalogWrapperService, commerceCache));

    return createCategoryBeansFor(wcCategories, storeContext);
  }

  @Override
  @Nonnull
  public List<Category> findSubCategories(@Nonnull Category parentCategory) throws CommerceException {
    if (parentCategory.isRoot()) {
      return findTopCategories(null);
    }

    String id = parentCategory.getExternalTechId();
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    List<Map<String, Object>> wcCategories = (List<Map<String, Object>>) commerceCache.get(new SubCategoriesCacheKey(
            id, storeContext, userContext, catalogWrapperService, commerceCache));

    return createCategoryBeansFor(wcCategories, storeContext);
  }

  /**
   * Search for Products
   *
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
  public SearchResult<Product> searchProducts(@Nonnull String searchTerm, @Nullable Map<String, String> searchParams)
          throws CommerceException {
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    SearchResult<Map<String, Object>> wcSearchResult = getCatalogWrapperService().searchProducts(
            searchTerm, searchParams, storeContext, SearchType.SEARCH_TYPE_PRODUCTS, userContext);

    SearchResult<Product> result = new SearchResult<>();
    result.setSearchResult(createProductBeansFor(wcSearchResult.getSearchResult(), storeContext));
    result.setTotalCount(wcSearchResult.getTotalCount());
    result.setPageNumber(wcSearchResult.getPageNumber());
    result.setPageSize(wcSearchResult.getPageSize());
    return result;
  }

  /**
   * Search for ProductVariants
   *
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
  public SearchResult<ProductVariant> searchProductVariants(@Nonnull String searchTerm,
                                                            @Nullable Map<String, String> searchParams)
          throws CommerceException {
    StoreContext storeContext = getStoreContext();
    UserContext userContext = getUserContext();

    SearchResult<Map<String, Object>> wcSearchResult = getCatalogWrapperService().searchProducts(
            searchTerm, searchParams, storeContext, SearchType.SEARCH_TYPE_PRODUCT_VARIANTS, userContext);

    SearchResult<ProductVariant> result = new SearchResult<>();
    List<ProductVariant> productBeansFor = createProductBeansFor(wcSearchResult.getSearchResult(), storeContext);
    result.setSearchResult(productBeansFor);
    result.setTotalCount(wcSearchResult.getTotalCount());
    result.setPageNumber(wcSearchResult.getPageNumber());
    result.setPageSize(wcSearchResult.getPageSize());
    return result;
  }

  public String getLanguageId(Locale locale) {
    return getCatalogWrapperService().getLanguageId(locale);
  }

  protected <T extends Product> T createProductBeanFor(Map<String, Object> productWrapper, StoreContext context,
                                                       boolean reloadById) {
    if (productWrapper != null) {
      String id;
      if ("ItemBean".equals(DataMapHelper.getValueForKey(productWrapper, "catalogEntryTypeCode", String.class))) {
        id = useExternalIdForBeanCreation ?
                CommerceIdHelper.formatProductVariantId(
                        DataMapHelper.getValueForKey(productWrapper, "partNumber", String.class)) :
                CommerceIdHelper.formatProductVariantTechId(
                        DataMapHelper.getValueForKey(productWrapper, "uniqueID", String.class));
      } else {
        id = useExternalIdForBeanCreation ?
                CommerceIdHelper.formatProductId(
                        DataMapHelper.getValueForKey(productWrapper, "partNumber", String.class)) :
                CommerceIdHelper.formatProductTechId(
                        DataMapHelper.getValueForKey(productWrapper, "uniqueID", String.class));
      }

      if (CommerceIdHelper.isProductId(id) || CommerceIdHelper.isProductVariantId(id)) {
        ProductBase product = (ProductBase) commerceBeanFactory.createBeanFor(id, context);

        // register the product wrapper with the cache, it will optimize later accesses (there are good
        // chances that the beans will be called immediately after this call
        // Todo: currently we use it only for studio calls, but check if we can do it for a cae webapp as well
        // (it probably requires that we are able to reload beans dynamically if someone tries to read a property
        // that is not available)
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

  protected <T extends Product> List<T> createProductBeansFor(List<Map<String, Object>> productWrappers,
                                                              StoreContext context) {
    if (productWrappers == null || productWrappers.isEmpty()) {
      return emptyList();
    }

    List<T> result = new ArrayList<>(productWrappers.size());
    for (Map<String, Object> productWrapper : productWrappers) {
      T productBeanFor = createProductBeanFor(productWrapper, context, true);
      result.add(productBeanFor);
    }

    return unmodifiableList(result);
  }

  protected Category createCategoryBeanFor(@Nullable Map<String, Object> categoryWrapper, @Nonnull StoreContext context,
                                           boolean reloadById) {
    if (categoryWrapper != null) {
      String id = useExternalIdForBeanCreation ?
              CommerceIdHelper.formatCategoryId(DataMapHelper.getValueForKey(categoryWrapper, "identifier", String.class)) :
              CommerceIdHelper.formatCategoryTechId(DataMapHelper.getValueForKey(categoryWrapper, "uniqueID", String.class));

      if (CommerceIdHelper.isCategoryId(id)) {
        CategoryImpl category = (CategoryImpl) commerceBeanFactory.createBeanFor(id, context);

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
    if (null != map && null != transformer) {
      //noinspection unchecked
      return LazyMap.decorate(newHashMap(map), transformer);
    }

    return map;
  }

  protected List<Category> createCategoryBeansFor(List<Map<String, Object>> categoryWrappers, StoreContext context) {
    if (categoryWrappers == null || categoryWrappers.isEmpty()) {
      return emptyList();
    }

    List<Category> result = new ArrayList<>(categoryWrappers.size());
    for (Map<String, Object> categoryWrapper : categoryWrappers) {
      result.add(createCategoryBeanFor(categoryWrapper, context, true));
    }

    return unmodifiableList(result);
  }

  @Nonnull
  @Override
  public CatalogService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, CatalogService.class);
  }

  private static StoreContext getStoreContext() {
    return StoreContextHelper.getCurrentContext();
  }

  private static UserContext getUserContext() {
    return UserContextHelper.getCurrentContext();
  }
}
