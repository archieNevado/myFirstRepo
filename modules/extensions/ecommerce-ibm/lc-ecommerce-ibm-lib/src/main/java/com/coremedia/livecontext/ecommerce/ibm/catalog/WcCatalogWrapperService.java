package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapTransformationHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrice;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPriceParam;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPriceV7_6;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrices;
import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.findContractIds;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreNameInLowerCase;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_6;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * A service that uses the catalog getRestConnector() to get catalog maps by certain search queries.
 * The catalog maps are {@link java.util.Map} or lists of them and contain the JSON response
 * of the REST services called.
 */
public class WcCatalogWrapperService extends AbstractWcWrapperService {

  private static final String SEARCH_QUERY_PARAM_PAGENUMBER = "pageNumber";
  private static final String SEARCH_QUERY_PARAM_PAGESIZE = "pageSize";
  private static final String SEARCH_QUERY_PARAM_INTENTSEARCHTERM = "intentSearchTerm";
  private static final String SEARCH_QUERY_PARAM_ORIGINALSEARCHTERM = "originalSearchTerm";
  private static final String SEARCH_QUERY_PARAM_CATEGORYID = "categoryId";
  private static final String SEARCH_QUERY_PARAM_SEARCHTYPE = "searchType";
  private static final String SEARCH_QUERY_PARAM_FILTERTERM = "filterTerm";
  private static final String SEARCH_QUERY_PARAM_FILTERTYPE = "filterType";
  private static final String SEARCH_QUERY_PARAM_MANUFACTURER = "manufacturer";
  private static final String SEARCH_QUERY_PARAM_MINPRICE = "minPrice";
  private static final String SEARCH_QUERY_PARAM_MAXPRICE = "maxPrice";
  private static final String SEARCH_QUERY_PARAM_FACET = "facet";
  private static final String SEARCH_QUERY_PARAM_ADVANCEDFACETLIST = "advancedFacetList";
  private static final String SEARCH_QUERY_PARAM_FILTERFACET = "filterFacet";
  private static final String SEARCH_QUERY_PARAM_ORDERBY = "orderBy";
  private static final String SEARCH_QUERY_PARAM_METADATA = "metaData";
  private static final String SEARCH_QUERY_PARAM_SEARCHSOURCE = "searchSource";
  private static final String SEARCH_QUERY_PARAM_DEPTH_AND_LIMIT = "depthAndLimit";
  private static final String DEFAUL_SEARCH_PROFILE_PREFIX = "CoreMedia";

  private String wcsSearchProfilePrefix;

  //for better performance we fetch only the first 100 results.
  private static final int DEFAULT_SEARCH_TOTAL_COUNT = 100;
  private static final int DEFAULT_SEARCH_OFFSET = 0;

  private static final String ERROR_CODE_INTERNAL_SERVER_ERROR = "CWXFR0230E";

  final Collection<String> validSearchParams = ImmutableSet.<String>builder().add(
          SEARCH_QUERY_PARAM_PAGENUMBER,
          SEARCH_QUERY_PARAM_PAGESIZE,
          SEARCH_QUERY_PARAM_INTENTSEARCHTERM,
          SEARCH_QUERY_PARAM_ORIGINALSEARCHTERM,
          SEARCH_QUERY_PARAM_CATEGORYID,
          SEARCH_QUERY_PARAM_SEARCHTYPE, SEARCH_QUERY_PARAM_FILTERTERM,
          SEARCH_QUERY_PARAM_FILTERTYPE,
          SEARCH_QUERY_PARAM_MANUFACTURER,
          SEARCH_QUERY_PARAM_MINPRICE,
          SEARCH_QUERY_PARAM_MAXPRICE,
          SEARCH_QUERY_PARAM_FACET,
          SEARCH_QUERY_PARAM_ADVANCEDFACETLIST,
          SEARCH_QUERY_PARAM_FILTERFACET,
          SEARCH_QUERY_PARAM_ORDERBY,
          SEARCH_QUERY_PARAM_METADATA,
          SEARCH_QUERY_PARAM_SEARCHSOURCE).build();

  private static final WcRestServiceMethod<Map, Void>
          FIND_PRODUCT_BY_EXTERNAL_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/{id}", false, false, true, true, null, Map.class),
          FIND_PRODUCT_BY_EXTERNAL_ID_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/%20?partNumber={id}&profileName={profilePrefix}_findProductByPartNumber_Details", false, false, true, true, true, Map.class),
          FIND_PRODUCT_BY_EXTERNAL_TECH_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/byId/{id}", false, false, true, true, null, Map.class),
          FIND_PRODUCT_BY_EXTERNAL_TECH_ID_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/byId/{id}?profileName={profilePrefix}_findProductByIds_Details", false, false, true, true, true, Map.class),
          FIND_PRODUCTS_BY_CATEGORY = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/byCategory/{id}", false, false, true, true, null, Map.class),
          FIND_PRODUCTS_BY_CATEGORY_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/byCategory/{id}?profileName={profilePrefix}_findProductsByCategory", false, false, true, true, true, Map.class),
          FIND_PRODUCT_BY_SEO_SEGMENT = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/bySeo/{language}/{storeName}/{seoSegment}", false, false, true, true, null, Map.class),
          FIND_PRODUCT_BY_SEO_SEGMENT_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/bySearchTerm/{term}?profileName={profilePrefix}_findProductsBySeoSegment", false, false, true, true, true, Map.class),
          SEARCH_PRODUCTS = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/bySearchTerm/{term}", false, false, Map.class),
          SEARCH_PRODUCTS_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/bySearchTerm/{term}?profileName={profilePrefix}_findProductsBySearchTerm", false, false, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_PERSONALIZED_PRODUCT_PRICE_BY_EXTERNAL_TECH_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/price?q=byPartNumbers&partNumber={partNumber}", false, true, true, true, Void.class, Map.class);

  private static final WcRestServiceMethod<Map, WcPriceParam>
          FIND_PERSONALIZED_PRODUCT_PRICE_BY_EXTERNAL_TECH_ID_V7_7 = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/price", false, true, true, true, true, WcPriceParam.class, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_PERSONALIZED_PRODUCT_PRICE_BY_EXTERNAL_TECH_ID_V7_6 = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/dynamic/{partNumber}?profile=CMPrice", false, true, true, true, Void.class, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_STATIC_PRODUCT_PRICES_BY_EXTERNAL_TECH_ID = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/{id}?profileName=IBM_findProductByPartNumber_Summary", false, false, true, false, true, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_STATIC_PRODUCT_PRICES_BY_EXTERNAL_TECH_ID_V7_6 = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/{partNumber}", false, false, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_CATEGORY_BY_EXTERNAL_TECH_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/byId/{id}", false, false, true, true, null, Map.class),
          FIND_CATEGORY_BY_EXTERNAL_TECH_ID_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/byId/{id}?profileName={profilePrefix}_findCategoryByUniqueIds", false, false, true, true, true, Map.class),
          FIND_CATEGORY_BY_SEO_SEGMENT = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/bySeo/{language}/{storeName}/{seoSegment}", false, false, true, true, null, Map.class),
          FIND_CATEGORY_BY_SEO_SEGMENT_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/{seoSegment}?profileName={profilePrefix}_findCategoryBySeoSegment" + "&locale={locale}", false, false, true, true, true, Map.class),
          FIND_TOP_CATEGORIES = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/@top", false, false, true, true, null, Map.class),
          FIND_TOP_CATEGORIES_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/@top?profileName={profilePrefix}_findSubCategories", false, false, true, true, true, Map.class),
          FIND_SUB_CATEGORIES = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/byParentCategory/{parentCategoryId}", false, false, true, true, null, Map.class),
          FIND_SUB_CATEGORIES_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/byParentCategory/{parentCategoryId}?profileName={profilePrefix}_findSubCategories", false, false, true, true, true, Map.class);

  private static final WcRestServiceMethod<Map, Void>
          FIND_CATALOGS_BY_STORE = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/catalog", false, true, false, false, Void.class, Map.class);

  @VisibleForTesting
  static final WcRestServiceMethod<Map, Void> FIND_CATEGORY_BY_EXTERNAL_ID_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/%20?categoryIdentifier={id}&profileName={profilePrefix}_findCategoryByIdentifier", false, false, true, true, true, Map.class);

  @VisibleForTesting
  static final WcRestServiceMethod<Map, Void> FIND_CATEGORY_BY_EXTERNAL_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/{id}", false, false, true, true, null, Map.class);

  private boolean useSearchRestHandlerProductIfAvailable = true;
  private boolean useSearchRestHandlerCategoryIfAvailable = true;

  /**
   * Gets a product map by a given id (in a coremedia internal format like "ibm:///catalog/product/4711").
   *
   * @param id           the coremedia internal id
   * @param storeContext the store context
   * @param userContext  the current user context
   * @return the product map or null if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException  if something is wrong with the catalog connection
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidIdException if the id is in a wrong format
   */
  @Nullable
  public Map<String, Object> findProductById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext,
                                             UserContext userContext) {
    CatalogAlias catalog = id.getCatalogAlias();

    Optional<String> techId = id.getTechId();
    Optional<String> seo = id.getSeo();

    if (techId.isPresent()) {
      return findProductByExternalTechId(techId.get(), catalog, storeContext, userContext);
    } else if (seo.isPresent()) {
      return findProductBySeoSegment(seo.get(), catalog, storeContext, userContext).orElse(null);
    }

    return findProductByExternalId(CommerceIdHelper.getExternalIdOrThrow(id), catalog, storeContext, userContext);
  }

  /**
   * Gets a product map by a given external id.
   *
   * @param externalId   the external id
   * @param catalog
   * @param storeContext the store context
   * @param userContext  the current user context   @return the product map or null if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @Nullable
  public Map<String, Object> findProductByExternalId(String externalId, CatalogAlias catalog,
                                                     @Nonnull StoreContext storeContext, UserContext userContext) {
    try {
      WcParameterMapBuilder builder = buildParameterMap(catalog, storeContext)
              .withCurrency(storeContext)
              .withLanguageId(storeContext);
      findContractIds(storeContext).ifPresent(builder::withContractIds);

      boolean useSearchRestHandlerProduct = useSearchRestHandlerProduct(storeContext);

      WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerProduct
              ? FIND_PRODUCT_BY_EXTERNAL_ID_SEARCH
              : FIND_PRODUCT_BY_EXTERNAL_ID;

      String storeId = getStoreId(storeContext);
      List<String> variableValues = useSearchRestHandlerProduct
              ? asList(storeId, externalId, getWcsSearchProfilePrefix())
              : asList(storeId, externalId);

      Map<String, String[]> parameters = builder.build();

      return callRestService(serviceMethod, variableValues, parameters, storeContext, userContext)
              .flatMap(this::getFirstProductWrapper)
              .orElse(null);
    } catch (CommerceRemoteException e) {
      // it's really bad, but the wcs sends a 500 when the product is not visible due to contract restrictions
      // then we interpret it as "not found" (to avoid errors in rendering)
      if (ERROR_CODE_INTERNAL_SERVER_ERROR.equals(e.getErrorCode())) {
        return null;
      }

      throw e;
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a product map by a given external tech id.
   *
   * @param externalTechId the external tech id
   * @param catalog
   * @param storeContext   the store context
   * @param userContext    the current user context   @return the product wrapper or null if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @Nullable
  public Map<String, Object> findProductByExternalTechId(String externalTechId, CatalogAlias catalog,
                                                         @Nonnull StoreContext storeContext, UserContext userContext) {
    try {
      boolean useSearchRestHandlerProduct = useSearchRestHandlerProduct(storeContext);

      WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerProduct
              ? FIND_PRODUCT_BY_EXTERNAL_TECH_ID_SEARCH
              : FIND_PRODUCT_BY_EXTERNAL_TECH_ID;

      String storeId = getStoreId(storeContext);
      List<String> variableValues = useSearchRestHandlerProduct
              ? asList(storeId, externalTechId, getWcsSearchProfilePrefix())
              : asList(storeId, externalTechId);

      WcParameterMapBuilder builder = buildParameterMap(catalog, storeContext)
              .withCurrency(storeContext)
              .withLanguageId(storeContext);
      findContractIds(storeContext).ifPresent(builder::withContractIds);
      Map<String, String[]> parameters = builder.build();

      return callRestService(serviceMethod, variableValues, parameters, storeContext, userContext)
              .flatMap(this::getFirstProductWrapper)
              .orElse(null);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets product price for a given product. This method returns list or offer price depending if there is an userContext or not.
   * No userContext: listPrice
   * With usercontext: offerPrice
   *
   * @param externalId   The externalId of the product to retrieve the prices for.
   * @param storeContext the store context
   * @param userContext  the user context. may be null for list price   @return the product prices wrapper of null if product or prices could not be found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something went wrong with the catalog connection
   */
  @Nullable
  @SuppressWarnings("unchecked")
  public WcPrice findDynamicProductPriceByExternalId(String externalId, @Nonnull StoreContext storeContext,
                                                     UserContext userContext) {
    try {
      if (WCS_VERSION_7_6 == StoreContextHelper.getWcsVersion(storeContext)) {
        return findDynamicProductPriceByExternalIdV76(externalId, storeContext, userContext).orElse(null);
      }

      String storeId = getStoreId(storeContext);

      Map<String, String[]> parameters = buildParameterMap()
              .withCurrency(storeContext)
              .withLanguageId(storeContext)
              .withUserIdOrName(userContext)
              .build();

      Optional<Map<String, Object>> data;
      if (StoreContextHelper.getWcsVersion(storeContext) == WCS_VERSION_7_7) {
        String currencyCode = getCurrency(storeContext).getCurrencyCode();
        String[] contractIds = findContractIds(storeContext).orElse(null);

        List<String> variableValues = singletonList(storeId);
        WcPriceParam priceParam = new WcPriceParam(externalId, currencyCode, contractIds);

        data = callRestService(FIND_PERSONALIZED_PRODUCT_PRICE_BY_EXTERNAL_TECH_ID_V7_7, variableValues, parameters,
                priceParam, storeContext, userContext);
      } else {
        List<String> variableValues = asList(storeId, externalId);

        data = callRestService(FIND_PERSONALIZED_PRODUCT_PRICE_BY_EXTERNAL_TECH_ID, variableValues, parameters,
                storeContext, userContext);
      }

      return data
              .map(priceData -> {
                WcPrice price = new WcPrice();
                price.setDataMap(priceData);
                return price;
              })
              .orElse(null);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  private Optional<WcPrice> findDynamicProductPriceByExternalIdV76(String externalId,
                                                                   @Nonnull StoreContext storeContext,
                                                                   UserContext userContext) {
    String storeId = getStoreId(storeContext);
    List<String> variableValues = asList(storeId, externalId);

    Map<String, String[]> parameters = buildParameterMap()
            .withCurrency(storeContext)
            .withLanguageId(storeContext)
            .withUserIdOrName(userContext)
            .build();

    return callRestService(FIND_PERSONALIZED_PRODUCT_PRICE_BY_EXTERNAL_TECH_ID_V7_6, variableValues, parameters,
            storeContext, userContext)
            .map(priceData -> {
              WcPriceV7_6 price = new WcPriceV7_6();
              price.setDataMap(priceData);
              return price;
            });
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public WcPrices findStaticProductPricesByExternalId(String externalId, CatalogAlias catalog,
                                                      @Nonnull StoreContext storeContext, UserContext userContext) {
    try {
      String storeId = getStoreId(storeContext);
      List<String> variableValues = asList(storeId, externalId);

      Optional<Map<String, Object>> data;
      if (WCS_VERSION_7_6 == StoreContextHelper.getWcsVersion(storeContext)) {
        Map<String, String[]> parameters = buildParameterMap(catalog, storeContext)
                .withCurrency(storeContext)
                .withLanguageId(storeContext)
                .build();

        data = callRestService(FIND_STATIC_PRODUCT_PRICES_BY_EXTERNAL_TECH_ID_V7_6, variableValues, parameters,
                storeContext, null);
      } else {
        WcParameterMapBuilder builder = buildParameterMap(catalog, storeContext)
                .withCurrency(storeContext)
                .withLanguageId(storeContext);
        findContractIds(storeContext).ifPresent(builder::withContractIds);
        Map<String, String[]> parameters = builder.build();

        data = callRestService(FIND_STATIC_PRODUCT_PRICES_BY_EXTERNAL_TECH_ID, variableValues, parameters, storeContext,
                userContext);
      }

      return data
              .map(pricesData -> {
                WcPrices prices = new WcPrices();
                prices.setDataMap(pricesData);
                return prices;
              })
              .orElse(null);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @Nullable
  Map<String, Object> getAvailableCatalogs(StoreContext storeContext) {
    try {
      String storeId = getStoreId(storeContext);
      List<String> variableValues = singletonList(storeId);

      return callRestService(FIND_CATALOGS_BY_STORE, variableValues, emptyMap(), storeContext, null)
              .orElse(null);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a product map by a given seo segment.
   *
   * @param seoSegment   the seo segment
   * @param catalog      the catalog alias
   * @param storeContext the store context
   * @param userContext  the current user context   @return the product map or null if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @Nonnull
  Optional<Map<String, Object>> findProductBySeoSegment(String seoSegment, CatalogAlias catalog,
                                                        @Nonnull StoreContext storeContext, UserContext userContext) {
    try {
      return getProductBySeoSegmentMap(seoSegment, catalog, storeContext);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  private Optional<Map<String, Object>> getProductBySeoSegmentMap(String seoSegment, CatalogAlias catalog,
                                                                  @Nonnull StoreContext storeContext) {
    boolean useSearchRestHandlerProduct = useSearchRestHandlerProduct(storeContext);

    WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerProduct
            ? FIND_PRODUCT_BY_SEO_SEGMENT_SEARCH
            : FIND_PRODUCT_BY_SEO_SEGMENT;

    String storeId = getStoreId(storeContext);
    String language = getLocale(storeContext).getLanguage();
    List<String> variableValues = useSearchRestHandlerProduct
            ? asList(storeId, seoSegment, getWcsSearchProfilePrefix())
            : asList(storeId, language, getStoreNameInLowerCase(storeContext), seoSegment);

    WcParameterMapBuilder builder = buildParameterMap(catalog, storeContext)
            .withCurrency(storeContext);
    findContractIds(storeContext).ifPresent(builder::withContractIds);
    Map<String, String[]> parameters = builder.build();

    return callRestService(serviceMethod, variableValues, parameters, storeContext, null)
            .flatMap(this::getFirstProductWrapper);
  }

  /**
   * Gets a list of product maps by a given category id.
   *
   * @param categoryId   the category id
   * @param catalog      the catalog alias
   * @param storeContext the store context
   * @param userContext  the current user context   @return list of product maps or empty list if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @Nonnull
  public List<Map<String, Object>> findProductsByCategoryId(String categoryId, CatalogAlias catalog,
                                                            @Nonnull StoreContext storeContext, UserContext userContext) {
    try {
      boolean useSearchRestHandlerProduct = useSearchRestHandlerProduct(storeContext);

      WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerProduct
              ? FIND_PRODUCTS_BY_CATEGORY_SEARCH
              : FIND_PRODUCTS_BY_CATEGORY;

      String storeId = getStoreId(storeContext);
      List<String> variableValues = useSearchRestHandlerProduct
              ? asList(storeId, categoryId, getWcsSearchProfilePrefix())
              : asList(storeId, categoryId);

      WcParameterMapBuilder builder = buildParameterMap(catalog, storeContext)
              .withCurrency(storeContext)
              .withLanguageId(storeContext);
      findContractIds(storeContext).ifPresent(builder::withContractIds);
      Map<String, String[]> parameters = builder.build();

      return callRestService(serviceMethod, variableValues, parameters, storeContext, userContext)
              .map(productsMap -> getProductWrapperList(productsMap, storeContext))
              .orElseGet(Collections::emptyList);
    } catch (CommerceException e) {
      // if category could not be resolved an remote error is thrown
      // unknown category code results in http-result 400 (bad request)
      if (e.getResultCode() == 400) {
        return emptyList();
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a category map by a given external tech id.
   *
   * @param externalTechId the external tech id
   * @param catalog        the catalog alias
   * @param storeContext   the store context
   * @param userContext    @return the category map or null if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @Nullable
  public Map<String, Object> findCategoryByExternalTechId(String externalTechId, CatalogAlias catalog,
                                                          @Nonnull StoreContext storeContext, UserContext userContext) {
    try {
      boolean useSearchRestHandlerCategory = useSearchRestHandlerCategory(storeContext);

      WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerCategory
              ? FIND_CATEGORY_BY_EXTERNAL_TECH_ID_SEARCH
              : FIND_CATEGORY_BY_EXTERNAL_TECH_ID;

      String storeId = getStoreId(storeContext);
      List<String> variableValues = useSearchRestHandlerCategory
              ? asList(storeId, externalTechId, getWcsSearchProfilePrefix())
              : asList(storeId, externalTechId);

      WcParameterMapBuilder builder = buildParameterMap(catalog, storeContext)
              .withLanguageId(storeContext);
      findContractIds(storeContext).ifPresent(builder::withContractIds);
      Map<String, String[]> parameters = builder.build();

      return callRestService(serviceMethod, variableValues, parameters, storeContext, userContext)
              .flatMap(categoriesMap -> getFirstCategoryWrapper(categoriesMap, storeContext))
              .orElse(null);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a category map by a given id (in a coremedia internal format like "ibm:///catalog/category/0815").
   *
   * @param id           the coremedia internal id
   * @param storeContext the store context
   * @param userContext  the current user context
   * @return the category map or null if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException  if something is wrong with the catalog connection
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidIdException if the id is in a wrong format
   */
  @Nullable
  public Map<String, Object> findCategoryById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext,
                                              UserContext userContext) {
    CatalogAlias catalog = id.getCatalogAlias();

    Optional<String> techId = id.getTechId();
    Optional<String> seo = id.getSeo();

    if (techId.isPresent()) {
      return findCategoryByExternalTechId(techId.get(), catalog, storeContext, userContext);
    } else if (seo.isPresent()) {
      return findCategoryBySeoSegment(seo.get(), catalog, storeContext, userContext);
    }

    String externalId = CommerceIdHelper.getExternalIdOrThrow(id);
    return findCategoryByExternalId(externalId, catalog, storeContext, userContext);
  }

  /**
   * Gets a category map by a given external id.
   *
   * @param externalId   the external id
   * @param catalog      the catalog alias
   * @param storeContext the store context
   * @param userContext  the current user context   @return the category map or null if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @Nullable
  public Map<String, Object> findCategoryByExternalId(String externalId, CatalogAlias catalog,
                                                      @Nonnull StoreContext storeContext, UserContext userContext) {
    try {
      boolean useSearchRestHandlerCategory = useSearchRestHandlerCategory(storeContext);

      WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerCategory
              ? FIND_CATEGORY_BY_EXTERNAL_ID_SEARCH
              : FIND_CATEGORY_BY_EXTERNAL_ID;

      String storeId = getStoreId(storeContext);
      List<String> variableValues = useSearchRestHandlerCategory
              ? asList(storeId, externalId, getWcsSearchProfilePrefix())
              : asList(storeId, externalId);

      WcParameterMapBuilder builder = buildParameterMap(catalog, storeContext)
              .withLanguageId(storeContext);
      findContractIds(storeContext).ifPresent(builder::withContractIds);
      Map<String, String[]> parameters = builder.build();

      return callRestService(serviceMethod, variableValues, parameters, storeContext, userContext)
              .flatMap(categoriesMap -> getFirstCategoryWrapper(categoriesMap, storeContext))
              .orElse(null);
    } catch (CommerceRemoteException e) {
      // it's really bad, but the wcs sends a 500 when the category is not visible due to contract restrictions
      // then we interpret it as "not found" (to avoid errors in rendering)
      if (ERROR_CODE_INTERNAL_SERVER_ERROR.equals(e.getErrorCode())) {
        return null;
      }

      throw e;
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a category map by a given seo segment.
   *
   * @param seoSegment   the seo segment
   * @param catalog      the catalog alias
   * @param storeContext the store context
   * @param userContext  the current user context   @return the category map or null if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @Nullable
  public Map<String, Object> findCategoryBySeoSegment(String seoSegment, CatalogAlias catalog,
                                                      @Nonnull StoreContext storeContext, UserContext userContext) {
    try {
      boolean useSearchRestHandlerCategory = useSearchRestHandlerCategory(storeContext);

      WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerCategory
              ? FIND_CATEGORY_BY_SEO_SEGMENT_SEARCH
              : FIND_CATEGORY_BY_SEO_SEGMENT;

      String storeId = getStoreId(storeContext);
      Locale locale = getLocale(storeContext);
      String language = locale.getLanguage();
      List<String> variableValues = useSearchRestHandlerCategory
              ? asList(storeId, seoSegment, getWcsSearchProfilePrefix(), locale.toString())
              : asList(storeId, language, getStoreNameInLowerCase(storeContext), seoSegment);

      return callRestService(serviceMethod, variableValues, emptyMap(), storeContext, userContext)
              .flatMap(categoriesMap -> getFirstCategoryWrapper(categoriesMap, storeContext))
              .orElse(null);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a list of all top category maps (all categories below root).
   *
   * @param catalog      the catalog alias
   * @param storeContext the store context
   * @param userContext  the current user context
   * @return the list of category maps or empty list if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @Nonnull
  public List<Map<String, Object>> findTopCategories(CatalogAlias catalog, @Nonnull StoreContext storeContext,
                                                     UserContext userContext) {
    try {
      boolean useSearchRestHandlerCategory = useSearchRestHandlerCategory(storeContext);

      WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerCategory
              ? FIND_TOP_CATEGORIES_SEARCH
              : FIND_TOP_CATEGORIES;

      String storeId = getStoreId(storeContext);
      List<String> variableValues = useSearchRestHandlerCategory
              ? asList(storeId, getWcsSearchProfilePrefix())
              : asList(storeId);

      WcParameterMapBuilder builder = buildParameterMap(catalog, storeContext)
              .withLanguageId(storeContext);
      findContractIds(storeContext).ifPresent(builder::withContractIds);
      Map<String, String[]> parameters = builder.build();

      return callRestService(serviceMethod, variableValues, parameters, storeContext, userContext)
              .map(this::getCategoryWrapperList)
              .orElseGet(Collections::emptyList);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a list of all sub category maps of a given category id (direct children).
   *
   * @param parentCategoryId the parent category id
   * @param catalog          the catalog alias
   * @param storeContext     the store context
   * @param userContext      the current user context   @return the list of category maps or empty list if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @Nonnull
  public List<Map<String, Object>> findSubCategories(String parentCategoryId, CatalogAlias catalog,
                                                     @Nonnull StoreContext storeContext, UserContext userContext) {
    try {
      boolean useSearchRestHandlerCategory = useSearchRestHandlerCategory(storeContext);

      WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerCategory
              ? FIND_SUB_CATEGORIES_SEARCH
              : FIND_SUB_CATEGORIES;

      String storeId = getStoreId(storeContext);
      List<String> variableValues = useSearchRestHandlerCategory
              ? asList(storeId, parentCategoryId, getWcsSearchProfilePrefix())
              : asList(storeId, parentCategoryId);

      WcParameterMapBuilder builder = buildParameterMap(catalog, storeContext)
              .withLanguageId(storeContext);
      findContractIds(storeContext).ifPresent(builder::withContractIds);
      Map<String, String[]> parameters = builder.build();

      if (useSearchRestHandlerCategory) {
        parameters.put(SEARCH_QUERY_PARAM_DEPTH_AND_LIMIT, new String[]{"-1"});
      }

      Optional<Map<String, Object>> categoriesWrapper = callRestService(serviceMethod, variableValues, parameters,
              storeContext, userContext);

      if (useSearchRestHandlerCategory) {
        return categoriesWrapper
                .map(categoriesMap -> getCategoryWrapperListForSubCategories(categoriesMap, parentCategoryId))
                .orElseGet(Collections::emptyList);
      } else {
        return categoriesWrapper
                .map(this::getCategoryWrapperList)
                .orElseGet(Collections::emptyList);
      }
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @Nonnull
  SearchResult<Map<String, Object>> searchProducts(@Nonnull String searchTerm, @Nonnull Map<String, String> searchParams,
                                                   @Nonnull StoreContext storeContext, @Nonnull SearchType searchType,
                                                   UserContext userContext) {
    checkArgument(!searchTerm.isEmpty(), "Search term must not be empty.");

    Map<String, String[]> params = new TreeMap<>();
    params.putAll(createSearchParametersMap(searchParams));

    String catalogAliasStr = searchParams.get(CatalogService.SEARCH_PARAM_CATALOG_ALIAS);
    CatalogAlias catalogAlias = CatalogAlias.ofNullable(catalogAliasStr).orElse(null);

    WcParameterMapBuilder builder = buildParameterMap(catalogAlias, storeContext)
            .withCurrency(storeContext)
            .withLanguageId(storeContext);
    findContractIds(storeContext).ifPresent(builder::withContractIds);
    Map<String, String[]> parametersMap = builder.build();
    params.putAll(parametersMap);

    params.put(SEARCH_QUERY_PARAM_SEARCHTYPE, new String[]{searchType.getValue()});

    try {
      return getMapSearchResult(searchTerm, storeContext, params, userContext);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  private SearchResult<Map<String, Object>> getMapSearchResult(String searchTerm, @Nonnull StoreContext storeContext,
                                                               Map<String, String[]> params, UserContext userContext) {
    boolean useSearchRestHandlerProduct = useSearchRestHandlerProduct(storeContext);

    WcRestServiceMethod<Map, Void> serviceMethod = useSearchRestHandlerProduct
            ? SEARCH_PRODUCTS_SEARCH
            : SEARCH_PRODUCTS;

    String storeId = getStoreId(storeContext);
    List<String> variableValues = useSearchRestHandlerProduct
            ? asList(storeId, searchTerm, getWcsSearchProfilePrefix())
            : asList(storeId, searchTerm);

    Optional<Map<String, Object>> wcProductsResponse = callRestService(serviceMethod, variableValues, params,
            storeContext, userContext);

    List<Map<String, Object>> productWrappers = wcProductsResponse
            .map(wcProducts -> getProductWrapperList(wcProducts, storeContext))
            .orElseGet(Collections::emptyList);

    SearchResult<Map<String, Object>> result = new SearchResult<>();
    wcProductsResponse.ifPresent(wcProducts -> {
      result.setSearchResult(productWrappers);
      result.setTotalCount(DataMapHelper.findValue(wcProducts, "recordSetTotal", Integer.class).orElse(0));
      result.setPageSize(DataMapHelper.findValue(wcProducts, "recordSetCount", Integer.class).orElse(0));
      result.setPageNumber(DataMapHelper.findValue(wcProducts, "recordSetStartNumber", Integer.class).orElse(0));
      result.setFacets(createSearchFacetsForSearchResult(wcProducts));
    });

    return result;
  }

  @Nonnull
  protected Optional<Map<String, Object>> getFirstProductWrapper(@Nonnull Map<String, Object> productsMap) {
    if (productsMap.isEmpty()) {
      return Optional.empty();
    }

    Map<String, Object> resultMap = productsMap;
    if (!useSearchRestHandlerProduct(StoreContextHelper.getCurrentContextOrThrow())) {
      resultMap = DataMapTransformationHelper.transformProductBodMap(resultMap);
    }

    //noinspection unchecked
    List<Map<String, Object>> products = getCatalogEntryView(resultMap);
    return products.stream().findFirst();
  }

  @Nonnull
  protected List<Map<String, Object>> getProductWrapperList(@Nonnull Map<String, Object> productsMap,
                                                            @Nonnull StoreContext storeContext) {
    if (productsMap.isEmpty()) {
      return emptyList();
    }

    Map<String, Object> resultMap = productsMap;
    if (!useSearchRestHandlerProduct(storeContext)) {
      resultMap = DataMapTransformationHelper.transformProductBodMap(resultMap);
    }

    //noinspection unchecked
    List<Map<String, Object>> products = getCatalogEntryView(resultMap);
    return unmodifiableList(products);
  }

  @Nonnull
  protected static List<SearchFacet> createSearchFacetsForSearchResult(@Nonnull Map<String, Object> searchResultMap) {
    if (searchResultMap.isEmpty()) {
      return emptyList();
    }

    List<Map<String, Object>> facetViewWrappers = DataMapHelper.getListValue(searchResultMap, "facetView");

    return facetViewWrappers.stream()
            .map(SearchFacetImpl::new)
            .collect(collectingAndThen(toList(), Collections::unmodifiableList));
  }

  @Nonnull
  protected Optional<Map<String, Object>> getFirstCategoryWrapper(@Nonnull Map<String, Object> categoriesMap,
                                                                  @Nonnull StoreContext storeContext) {
    Map<String, Object> resultMap = categoriesMap;
    if (!useSearchRestHandlerCategory(storeContext)) {
      resultMap = DataMapTransformationHelper.transformCategoryBodMap(resultMap);
    }

    //noinspection unchecked
    List<Map<String, Object>> categories = getCatalogGroupView(resultMap);
    return categories.stream().findFirst();
  }

  /**
   * Returns the catalog entry view section of a given map. EntryView</code>.
   *
   * @param productsMap The product map retrieved by the commerce server.
   * @return The sub map containing the catalog entry view section or null if not available.
   */
  @Nonnull
  private static List getCatalogEntryView(@Nonnull Map<String, Object> productsMap) {
    return DataMapHelper.getListValue(productsMap, "catalogEntryView");
  }

  /**
   * Returns the catalog group view section of a given map.
   *
   * @param categoriesWrapper The categories map retrieved by the commerce server.
   * @return The sub map containing the catalog group view section or null if not available.
   */
  @Nonnull
  private static List getCatalogGroupView(@Nonnull Map<String, Object> categoriesWrapper) {
    return DataMapHelper.getListValue(categoriesWrapper, "catalogGroupView");
  }

  @Nonnull
  private List<Map<String, Object>> getCategoryWrapperList(@Nonnull Map<String, Object> categoriesMap) {
    Map<String, Object> resultMap = categoriesMap;
    if (!useSearchRestHandlerCategory(StoreContextHelper.getCurrentContextOrThrow())) {
      resultMap = DataMapTransformationHelper.transformCategoryBodMap(resultMap);
    }

    //noinspection unchecked
    List<Map<String, Object>> categories = getCatalogGroupView(resultMap);
    return unmodifiableList(categories);
  }

  /**
   * Returns a list of category wrappers equivalent to {@link #getCategoryWrapperList(Map)}, but also considers
   * differences in the JSON-Response of IBM Fix-Pack IFJR55049.
   *
   * @param categoriesMap    The categories map retrieved by the commerce server.
   * @param parentCategoryId The parent category's ID.
   * @return The sub map containing the catalog group view section or null if not available.
   */
  @Nonnull
  protected List<Map<String, Object>> getCategoryWrapperListForSubCategories(
          @Nonnull Map<String, Object> categoriesMap, String parentCategoryId) {
    String jsonCategoryId = DataMapHelper.findStringValue(categoriesMap, "catalogGroupView[0].uniqueID").orElse(null);
    if (jsonCategoryId != null && Objects.equals(jsonCategoryId, parentCategoryId)) {
      //JSON handling for fix pack IFJR55049
      Map<String, Object> resultMap = DataMapHelper.getValueForPath(categoriesMap, "catalogGroupView[0]", Map.class);
      if (resultMap == null) {
        return emptyList();
      }

      List<Map<String, Object>> categories = DataMapHelper.findValue(resultMap, "catalogGroupView", List.class)
              .orElse(null);
      if (categories == null) {
        return emptyList();
      }

      if (!useSearchRestHandlerCategory(StoreContextHelper.getCurrentContextOrThrow())) {
        resultMap = DataMapTransformationHelper.transformCategoryBodMap(resultMap);
      }
      categories = DataMapHelper.findValue(resultMap, "catalogGroupView", List.class).orElse(null);
      return unmodifiableList(categories);
    } else {
      return getCategoryWrapperList(categoriesMap);
    }
  }

  /**
   * Converts a search parameters map into a IBM-specific search parameters map.
   */
  @Nonnull
  private Map<String, String[]> createSearchParametersMap(@Nonnull Map<String, String> searchParams) {
    Map<String, String[]> parameters = new TreeMap<>();

    if (!searchParams.containsKey(SEARCH_QUERY_PARAM_SEARCHTYPE)) {
      //IBM reverse engineering: 0 means searchTypeAll
      parameters.put(SEARCH_QUERY_PARAM_SEARCHTYPE, new String[]{SearchType.SEARCH_TYPE_PRODUCTS.getValue()});
    }

    findValueInMap(searchParams, CatalogService.SEARCH_PARAM_ORDERBY)
            .map(OrderByType::valueOf)
            .map(orderByType -> new String[]{orderByType.getValue()})
            .ifPresent(orderByValue -> parameters.put(SEARCH_QUERY_PARAM_ORDERBY, orderByValue));

    // from a given total and offset value
    if (!searchParams.containsKey(CatalogService.SEARCH_PARAM_PAGENUMBER)
            || !searchParams.containsKey(CatalogService.SEARCH_PARAM_PAGESIZE)) {
      int total = findValueInMap(searchParams, CatalogService.SEARCH_PARAM_TOTAL)
              .map(Integer::valueOf)
              .orElse(DEFAULT_SEARCH_TOTAL_COUNT);

      // A given "offset parameter" is actually meant as a "start position" within an array (a start position of 1
      // means an offset of 0 when accessing the array). The further processing will be done with the offset value.
      // That means we have to subtract 1 if such an value exists.
      int offset = findValueInMap(searchParams, CatalogService.SEARCH_PARAM_OFFSET)
              .map(Integer::valueOf)
              .map(value -> value - 1)
              .orElse(DEFAULT_SEARCH_OFFSET);

      computeAndSetPagingParameter(offset, total, parameters);
    }

    for (Map.Entry<String, String> entry : searchParams.entrySet()) {
      String key = entry.getKey();
      if (validSearchParams.contains(key) && !parameters.containsKey(key)) {
        parameters.put(key, new String[]{entry.getValue()});
      }
    }

    return parameters;
  }

  @Nonnull
  private static Optional<String> findValueInMap(@Nonnull Map<String, String> map, @Nonnull String key) {
    if (!map.containsKey(key)) {
      return Optional.empty();
    }

    String value = map.get(key);
    return Optional.of(value);
  }

  private static void computeAndSetPagingParameter(int offset, int total, @Nonnull Map<String, String[]> params) {
    int pageNumber = 1;
    int pageSize = offset + total;
    if (total > 0 && offset > total) {
      int counter = total;
      while (offset % counter != 0) {
        counter++;
      }
      pageNumber = offset / counter + 1;
      pageSize = counter;
    }

    params.put(CatalogService.SEARCH_PARAM_PAGESIZE, new String[]{Integer.toString(pageSize)});
    params.put(CatalogService.SEARCH_PARAM_PAGENUMBER, new String[]{Integer.toString(pageNumber)});
  }

  public void setUseSearchRestHandlerProductIfAvailable(boolean useSearchRestHandlerProductIfAvailable) {
    this.useSearchRestHandlerProductIfAvailable = useSearchRestHandlerProductIfAvailable;
  }

  boolean useSearchRestHandlerProduct(@Nonnull StoreContext storeContext) {
    return WCS_VERSION_7_6.lessThan(StoreContextHelper.getWcsVersion(storeContext))
            && useSearchRestHandlerProductIfAvailable;
  }

  public void setUseSearchRestHandlerCategoryIfAvailable(boolean useSearchRestHandlerCategoryIfAvailable) {
    this.useSearchRestHandlerCategoryIfAvailable = useSearchRestHandlerCategoryIfAvailable;
  }

  private boolean useSearchRestHandlerCategory(@Nonnull StoreContext storeContext) {
    return WCS_VERSION_7_6.lessThan(StoreContextHelper.getWcsVersion(storeContext))
            && useSearchRestHandlerCategoryIfAvailable;
  }

  @Value("${livecontext.ibm.wcs.search.profile.prefix:" + DEFAUL_SEARCH_PROFILE_PREFIX + "}")
  public void setWcsSearchProfilePrefix(String wcsSearchProfilePrefix) {
    this.wcsSearchProfilePrefix = wcsSearchProfilePrefix;
  }

  public String getWcsSearchProfilePrefix() {
    return wcsSearchProfilePrefix;
  }

  @Nonnull
  private WcParameterMapBuilder buildParameterMap(@Nullable CatalogAlias catalogAlias,
                                                  @Nonnull StoreContext storeContext) {
    WcParameterMapBuilder builder = buildParameterMap();

    if (catalogAlias != null) {
      Optional<CatalogId> catalogId = findCatalogId(catalogAlias, storeContext);
      catalogId.ifPresent(builder::withCatalogId);
    }

    return builder;
  }

  @Nonnull
  private Optional<Map<String, Object>> callRestService(@Nonnull WcRestServiceMethod<Map, Void> serviceMethod,
                                                        @Nonnull List<String> variableValues,
                                                        @Nonnull Map<String, String[]> optionalParameters,
                                                        @Nullable StoreContext storeContext,
                                                        @Nullable UserContext userContext) {
    return callRestService(serviceMethod, variableValues, optionalParameters, null, storeContext, userContext);
  }

  @Nonnull
  private <P> Optional<Map<String, Object>> callRestService(@Nonnull WcRestServiceMethod<Map, P> serviceMethod,
                                                            @Nonnull List<String> variableValues,
                                                            @Nonnull Map<String, String[]> optionalParameters,
                                                            @Nullable P bodyData,
                                                            @Nullable StoreContext storeContext,
                                                            @Nullable UserContext userContext) {
    //noinspection unchecked
    Map<String, Object> response = getRestConnector().callService(serviceMethod, variableValues, optionalParameters,
            bodyData, storeContext, userContext);
    return Optional.ofNullable(response);
  }
}
