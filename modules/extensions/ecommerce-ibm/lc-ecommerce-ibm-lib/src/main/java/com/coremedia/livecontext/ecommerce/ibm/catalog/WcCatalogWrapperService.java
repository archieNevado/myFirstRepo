package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapTransformationHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrice;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPriceParam;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPriceV7_6;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPrices;
import com.coremedia.livecontext.ecommerce.ibm.pricing.WcPricesV7_6;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCatalogId;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getContractIds;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreNameInLowerCase;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_6;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static java.util.Arrays.asList;

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

  //for better performance we fetch only the first 100 results.
  //TODO: In near future the studio client should be able to fetch the next 100 and so fort.
  //TODO: why not SEARCH_QUERY_PARAM_PAGENUMBER simply?
  public static final String DEFAULT_SEARCH_PAGE_NUMBER = "pageNumber";
  public static final String DEFAULT_SEARCH_PAGE_NUMBER_VALUE = "1";
  public static final String DEFAULT_SEARCH_PAGESIZE = "pageSize";
  public static final String DEFAULT_SEARCH_PAGESIZE_VALUE = "100";

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
          FIND_PRODUCT_BY_EXTERNAL_ID_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/%20?partNumber={id}&profileName=CoreMedia_findProductByPartNumber_Details", false, false, true, true, true, Map.class),
          FIND_PRODUCT_BY_EXTERNAL_TECH_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/byId/{id}", false, false, true, true, null, Map.class),
          FIND_PRODUCT_BY_EXTERNAL_TECH_ID_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/byId/{id}?profileName=CoreMedia_findProductByIds_Details", false, false, true, true, true, Map.class),
          FIND_PRODUCTS_BY_CATEGORY = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/byCategory/{id}", false, false, true, true, null, Map.class),
          FIND_PRODUCTS_BY_CATEGORY_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/byCategory/{id}?profileName=CoreMedia_findProductsByCategory", false, false, true, true, true, Map.class),
          FIND_PRODUCT_BY_SEO_SEGMENT = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/bySeo/{language}/{storeName}/{seoSegment}", false, false, true, true, null, Map.class),
          FIND_PRODUCT_BY_SEO_SEGMENT_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/bySearchTerm/{term}?profileName=CoreMedia_findProductsBySeoSegment", false, false, true, true, true, Map.class),
          SEARCH_PRODUCTS = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/productview/bySearchTerm/{term}", false, false, Map.class),
          SEARCH_PRODUCTS_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/productview/bySearchTerm/{term}?profileName=CoreMedia_findProductsBySearchTerm", false, false, Map.class);

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
          FIND_CATEGORY_BY_EXTERNAL_TECH_ID_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/byId/{id}?profileName=CoreMedia_findCategoryByUniqueIds", false, false, true, true, true, Map.class),
          FIND_CATEGORY_BY_EXTERNAL_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/{id}", false, false, true, true, null, Map.class),
          FIND_CATEGORY_BY_EXTERNAL_ID_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/%20?categoryIdentifier={id}&profileName=CoreMedia_findCategoryByIdentifier", false, false, true, true, true, Map.class),
          FIND_CATEGORY_BY_SEO_SEGMENT = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/bySeo/{language}/{storeName}/{seoSegment}", false, false, true, true, null, Map.class),
          FIND_CATEGORY_BY_SEO_SEGMENT_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/{seoSegment}?profileName=CoreMedia_findCategoryBySeoSegment&locale={locale}", false, false, true, true, true, Map.class),
          FIND_TOP_CATEGORIES = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/@top", false, false, true, true, null, Map.class),
          FIND_TOP_CATEGORIES_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/@top?profileName=CoreMedia_findSubCategories", false, false, true, true, true, Map.class),
          FIND_SUB_CATEGORIES = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/byParentCategory/{parentCategoryId}", false, false, true, true, null, Map.class),
          FIND_SUB_CATEGORIES_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/byParentCategory/{parentCategoryId}?profileName=CoreMedia_findSubCategories", false, false, true, true, true, Map.class);

  private boolean useSearchRestHandlerProductIfAvailable = true;
  private boolean useSearchRestHandlerCategoryIfAvailable = true;

  /**
   * Gets a product map by a given id (in a coremedia internal format like "ibm:///catalog/product/4711").
   *
   * @param id           the coremedia internal id
   * @param storeContext the store context
   * @param userContext the current user context
   * @return the product map or null if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException  if something is wrong with the catalog connection
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidIdException if the id is in a wrong format
   */
  public Map<String, Object> findProductById(final String id, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    if (CommerceIdHelper.isProductTechId(id) || CommerceIdHelper.isProductVariantTechId(id)) {
      return findProductByExternalTechId(CommerceIdHelper.parseExternalTechIdFromId(id), storeContext, userContext);
    } else if (CommerceIdHelper.isProductSeoId(id) || CommerceIdHelper.isProductVariantSeoId(id)) {
      return findProductBySeoSegment(CommerceIdHelper.parseExternalSeoIdFromId(id), storeContext, userContext);
    }
    //noinspection unchecked
    return findProductByExternalId(CommerceIdHelper.parseExternalIdFromId(id), storeContext, userContext);
  }

  /**
   * Gets a product map by a given external id.
   *
   * @param externalId   the external id
   * @param storeContext the store context
   * @param userContext the current user context
   * @return the product map or null if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  public Map<String, Object> findProductByExternalId(final String externalId, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      //noinspection unchecked
      Map<String, Object> productsMap = getRestConnector().callService(
              useSearchRestHandlerProduct(storeContext) ? FIND_PRODUCT_BY_EXTERNAL_ID_SEARCH : FIND_PRODUCT_BY_EXTERNAL_ID, asList(getStoreId(storeContext), externalId),
              createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), StoreContextHelper.getContractIds(storeContext)), null, storeContext, userContext);

      return getFirstProductWrapper(productsMap);

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
   * @param storeContext   the store context
   * @param userContext the current user context
   * @return the product wrapper or null if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  public Map<String, Object> findProductByExternalTechId(final String externalTechId, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      //noinspection unchecked
      Map<String, Object> productsMap = getRestConnector().callService(
              useSearchRestHandlerProduct(storeContext) ? FIND_PRODUCT_BY_EXTERNAL_TECH_ID_SEARCH : FIND_PRODUCT_BY_EXTERNAL_TECH_ID, asList(getStoreId(storeContext), externalTechId),
              createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), getContractIds(storeContext)), null, storeContext, userContext);

      return getFirstProductWrapper(productsMap);

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
   * @param userContext  the user context. may be null for list price
   * @return the product prices wrapper of null if product or prices could not be found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something went wrong with the catalog connection
   */
  @SuppressWarnings("unchecked")
  public WcPrice findDynamicProductPriceByExternalId(final String externalId, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      if (WCS_VERSION_7_6 == StoreContextHelper.getWcsVersion(storeContext)) {
        return findDynamicProductPriceByExternalIdV76(externalId, storeContext, userContext);
      }

      Map<String, Object> data;
      if (StoreContextHelper.getWcsVersion(storeContext) == WCS_VERSION_7_7) {
        data = getRestConnector().callService(
                FIND_PERSONALIZED_PRODUCT_PRICE_BY_EXTERNAL_TECH_ID_V7_7, Collections.singletonList(getStoreId(storeContext)),
                createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null),
                new WcPriceParam(externalId, getCurrency(storeContext).getCurrencyCode(), getContractIds(storeContext)),
                storeContext, userContext);
      } else {
        data = getRestConnector().callService(
                FIND_PERSONALIZED_PRODUCT_PRICE_BY_EXTERNAL_TECH_ID, asList(getStoreId(storeContext), externalId),
                createParametersMap(null, null, null, UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null),
                null, storeContext, userContext);
      }

      WcPrice result = null;
      if (data != null) {
        result = new WcPrice();
        result.setDataMap(data);
      }

      return result;

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private WcPrice findDynamicProductPriceByExternalIdV76(String externalId, StoreContext storeContext, final UserContext userContext) {
    Map<String, Object> data = getRestConnector().callService(
        FIND_PERSONALIZED_PRODUCT_PRICE_BY_EXTERNAL_TECH_ID_V7_6, asList(getStoreId(storeContext), externalId),
        createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null),
        null, storeContext, userContext);

    WcPriceV7_6 result = null;
    if (data != null) {
      result = new WcPriceV7_6();
      result.setDataMap(data);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public WcPrices findStaticProductPricesByExternalId(final String externalId, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {

      if (WCS_VERSION_7_6 == StoreContextHelper.getWcsVersion(storeContext)) {
        Map<String, Object> data = getRestConnector().callService(
                FIND_STATIC_PRODUCT_PRICES_BY_EXTERNAL_TECH_ID_V7_6, asList(getStoreId(storeContext), externalId),
                createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), null), null, storeContext, null);

        WcPricesV7_6 result = null;
        if (data != null) {
          result = new WcPricesV7_6();
          result.setDataMap(data);
        }
        return result;
      }

      Map<String, Object> data = getRestConnector().callService(
          FIND_STATIC_PRODUCT_PRICES_BY_EXTERNAL_TECH_ID, asList(getStoreId(storeContext), externalId),
          createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), getContractIds(storeContext)), null, storeContext, userContext);

      WcPrices result = null;
      if (data != null) {
        result = new WcPrices();
        result.setDataMap(data);
      }
      return result;

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
   * @param storeContext the store context
   * @param userContext  the current user context
   * @return the product map or null if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  Map<String, Object> findProductBySeoSegment(final String seoSegment, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      return getProductBySeoSegmentMap(seoSegment, storeContext);

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getProductBySeoSegmentMap(String seoSegment, StoreContext storeContext) {
    String language = getLocale(storeContext).getLanguage();
    Map<String, Object> productsMap = getRestConnector().callService(
            useSearchRestHandlerProduct(storeContext) ? FIND_PRODUCT_BY_SEO_SEGMENT_SEARCH : FIND_PRODUCT_BY_SEO_SEGMENT,
            useSearchRestHandlerProduct(storeContext) ? asList(getStoreId(storeContext), seoSegment) :
                          asList(getStoreId(storeContext), language, getStoreNameInLowerCase(storeContext), seoSegment),
                  createParametersMap(getCatalogId(storeContext), null, getCurrency(storeContext), getContractIds(storeContext)), null, storeContext, null);
    return getFirstProductWrapper(productsMap);
  }

  /**
   * Gets a list of product maps by a given category id.
   *
   * @param categoryId   the category id
   * @param storeContext the store context
   * @param userContext the current user context
   * @return list of product maps or empty list if no product was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  public List<Map<String, Object>> findProductsByCategoryId(final String categoryId, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      //noinspection unchecked
      Map<String, Object> productsMap = getRestConnector().callService(
              useSearchRestHandlerProduct(storeContext) ? FIND_PRODUCTS_BY_CATEGORY_SEARCH : FIND_PRODUCTS_BY_CATEGORY, asList(getStoreId(storeContext), categoryId),
              createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), getContractIds(storeContext)), null, storeContext, userContext);

      return getProductWrapperList(productsMap);
    } catch (CommerceException e) {
      // if category could not be resolved an remote error is thrown
      // unknown category code results in http-result 400 (bad request)
      if (e.getResultCode() == 400) {
        return Collections.emptyList();
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
   * @param storeContext   the store context
   * @param userContext
   * @return the category map or null if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  public Map<String, Object> findCategoryByExternalTechId(final String externalTechId, final StoreContext storeContext, UserContext userContext) throws CommerceException {
    try {
      //noinspection unchecked
      Map<String, Object> categoriesMap = getRestConnector().callService(
              useSearchRestHandlerCategory(storeContext) ? FIND_CATEGORY_BY_EXTERNAL_TECH_ID_SEARCH : FIND_CATEGORY_BY_EXTERNAL_TECH_ID, asList(getStoreId(storeContext), externalTechId),
              createParametersMap(getCatalogId(storeContext), getLocale(storeContext), null, getContractIds(storeContext)), null, storeContext, userContext);

      return getFirstCategoryWrapper(categoriesMap);

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
   * @param userContext the current user context
   * @return the category map or null if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException  if something is wrong with the catalog connection
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidIdException if the id is in a wrong format
   */
  public Map<String, Object> findCategoryById(final String id, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    if (CommerceIdHelper.isCategoryTechId(id)) {
      return findCategoryByExternalTechId(CommerceIdHelper.parseExternalTechIdFromId(id), storeContext, userContext);
    } else if (CommerceIdHelper.isCategorySeoId(id)) {
      return findCategoryBySeoSegment(CommerceIdHelper.parseExternalSeoIdFromId(id), storeContext, userContext);
    }
    return findCategoryByExternalId(CommerceIdHelper.parseExternalIdFromId(id), storeContext, userContext);
  }

  /**
   * Gets a category map by a given external id.
   *
   * @param externalId   the external id
   * @param storeContext the store context
   * @param userContext the current user context
   * @return the category map or null if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  public Map<String, Object> findCategoryByExternalId(final String externalId, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      //noinspection unchecked
      Map<String, Object> categoriesWrapper = getRestConnector().callService(
              useSearchRestHandlerCategory(storeContext) ? FIND_CATEGORY_BY_EXTERNAL_ID_SEARCH : FIND_CATEGORY_BY_EXTERNAL_ID, asList(getStoreId(storeContext), externalId),
              createParametersMap(getCatalogId(storeContext), getLocale(storeContext), null, getContractIds(storeContext)), null, storeContext, userContext);
      return getFirstCategoryWrapper(categoriesWrapper);

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
   * @param storeContext the store context
   * @param userContext the current user context
   * @return the category map or null if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  public Map<String, Object> findCategoryBySeoSegment(String seoSegment, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      String language = getLocale(storeContext).getLanguage();
      //noinspection unchecked
      Map<String, Object> categoriesWrapper = getRestConnector().callService(
              useSearchRestHandlerCategory(storeContext) ? FIND_CATEGORY_BY_SEO_SEGMENT_SEARCH : FIND_CATEGORY_BY_SEO_SEGMENT,
              useSearchRestHandlerCategory(storeContext) ? asList(getStoreId(storeContext), seoSegment, getLocale(storeContext).toString()) :
                      asList(getStoreId(storeContext), language, getStoreNameInLowerCase(storeContext), seoSegment),
              Collections.<String, String[]>emptyMap(), null, storeContext, userContext);
      return getFirstCategoryWrapper(categoriesWrapper);

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a list of all top category maps (all categories below root).
   *
   * @param storeContext the store context
   * @param userContext the current user context
   * @return the list of category maps or empty list if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  public List<Map<String, Object>> findTopCategories(final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      //noinspection unchecked
      Map<String, Object> categoriesWrapper = getRestConnector().callService(
              useSearchRestHandlerCategory(storeContext) ? FIND_TOP_CATEGORIES_SEARCH : FIND_TOP_CATEGORIES, Collections.singletonList(getStoreId(storeContext)),
              createParametersMap(getCatalogId(storeContext), getLocale(storeContext), null, getContractIds(storeContext)), null, storeContext, userContext);
      return getCategoryWrapperList(categoriesWrapper);

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
   * @param storeContext     the store context
   * @param userContext  the current user context
   * @return the list of category maps or empty list if no category was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  public List<Map<String, Object>> findSubCategories(final String parentCategoryId, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {

      Map<String, String[]> params = createParametersMap(getCatalogId(storeContext), getLocale(storeContext), null, getContractIds(storeContext));
      if (useSearchRestHandlerCategory(storeContext)) {
        params.put(SEARCH_QUERY_PARAM_DEPTH_AND_LIMIT, new String[]{"-1"});
      }

      //noinspection unchecked
      Map<String, Object> categoriesWrapper = getRestConnector().callService(
              useSearchRestHandlerCategory(storeContext) ? FIND_SUB_CATEGORIES_SEARCH : FIND_SUB_CATEGORIES, asList(getStoreId(storeContext), parentCategoryId),
              params, null, storeContext, userContext);

      if (useSearchRestHandlerCategory(storeContext)) {
        return getCategoryWrapperListForSubCategories(categoriesWrapper, parentCategoryId);
      }else {
        return getCategoryWrapperList(categoriesWrapper);
      }

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  SearchResult<Map<String, Object>> searchProducts(String searchTerm, Map<String, String> searchParams, StoreContext storeContext, SearchType searchType, UserContext userContext) throws CommerceException {

    if (searchTerm == null || searchTerm.isEmpty()) {
      return null;
    }

    Map<String, String[]> params = new TreeMap<>();
    params.putAll(createSearchParametersMap(searchParams));
    params.putAll(createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), getContractIds(storeContext)));
    if (searchType == null) {
      params.put(SEARCH_QUERY_PARAM_SEARCHTYPE, new String[]{SearchType.SEARCH_TYPE_PRODUCTS.getValue()});
    } else {
      params.put(SEARCH_QUERY_PARAM_SEARCHTYPE, new String[]{searchType.getValue()});
    }

    try {

      return getMapSearchResult(searchTerm, storeContext, params, userContext);

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private SearchResult<Map<String, Object>> getMapSearchResult(String searchTerm, StoreContext storeContext, Map<String, String[]> params, UserContext userContext) {
    Map<String, Object> wcProducts = getRestConnector().callService(
            useSearchRestHandlerProduct(storeContext) ? SEARCH_PRODUCTS_SEARCH : SEARCH_PRODUCTS, asList(getStoreId(storeContext), searchTerm), params, null, storeContext, userContext);
    List<Map<String, Object>> productWrappers = getProductWrapperList(wcProducts);

    SearchResult<Map<String, Object>> result = new SearchResult<>();
    if (wcProducts != null) {
      result.setSearchResult(productWrappers);
      result.setTotalCount(DataMapHelper.getValueForKey(wcProducts, "recordSetTotal", 0));
      result.setPageSize(DataMapHelper.getValueForKey(wcProducts, "recordSetCount", 0));
      result.setPageNumber(DataMapHelper.getValueForKey(wcProducts, "recordSetStartNumber", 0));
    }

    return result;
  }

  protected Map<String, Object> getFirstProductWrapper(Map<String, Object> productsMap) {
    if (productsMap == null || productsMap.isEmpty()) {
      return null;
    }
    Map<String, Object> resultMap = productsMap;
    resultMap = DataMapTransformationHelper.transformProductBodMap(resultMap);

    //noinspection unchecked
    List<Map<String, Object>> products = getCatalogEntryView(resultMap);

    if (products == null || products.isEmpty()) {
      return null;
    }
    return products.get(0);
  }

  protected List<Map<String, Object>> getProductWrapperList(Map<String, Object> productsMap) {
    if (productsMap == null || productsMap.isEmpty()) {
      return Collections.emptyList();
    }
    Map<String, Object> resultMap = productsMap;
    resultMap = DataMapTransformationHelper.transformProductBodMap(resultMap);

    //noinspection unchecked
    List<Map<String, Object>> products = getCatalogEntryView(resultMap);
    if (products == null || products.isEmpty()) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(products);
  }

  protected Map<String, Object> getFirstCategoryWrapper(Map<String, Object> categoriesMap) {
    if (categoriesMap == null) {
      return null;
    }
    Map<String, Object> resultMap = categoriesMap;
    resultMap = DataMapTransformationHelper.transformCategoryBodMap(resultMap);

    //noinspection unchecked
    List<Map<String, Object>> categories = getCatalogGroupView(resultMap);
    if (categories == null || categories.isEmpty()) {
      return null;
    }
    return categories.get(0);
  }

  /**
   * Returns the catalog entry view section of a given map. EntryView</code>.
   *
   * @param productsMap The product map retrieved by the commerce server.
   * @return The sub map containing the catalog entry view section or null if not available.
   */
  private List getCatalogEntryView(Map<String, Object> productsMap) {
    return DataMapHelper.getValueForKey(productsMap, "catalogEntryView", List.class);
  }

  /**
   * Returns the catalog group view section of a given map.
   *
   * @param categoriesWrapper The categories map retrieved by the commerce server.
   * @return The sub map containing the catalog group view section or null if not available.
   */
  private List getCatalogGroupView(Map<String, Object> categoriesWrapper) {
    return DataMapHelper.getValueForKey(categoriesWrapper, "catalogGroupView", List.class);
  }

  protected List<Map<String, Object>> getCategoryWrapperList(Map<String, Object> categoriesMap) {
    if (categoriesMap == null) {
      return Collections.emptyList();
    }
    Map<String, Object> resultMap = categoriesMap;
    resultMap = DataMapTransformationHelper.transformCategoryBodMap(resultMap);

    //noinspection unchecked
    List<Map<String, Object>> categories = getCatalogGroupView(resultMap);
    if (categories == null || categories.isEmpty()) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(categories);
  }

  /**
   * Returns a list of category wrappers equivalent to {@link #getCategoryWrapperList(Map)}, but also considers
   * differences in the JSON-Response of IBM Fix-Pack IFJR55049.
   * @param categoriesMap The categories map retrieved by the commerce server.
   * @param parentCategoryId
   * @return The sub map containing the catalog group view section or null if not available.
   */
  protected List<Map<String, Object>> getCategoryWrapperListForSubCategories(Map<String, Object> categoriesMap, String parentCategoryId) {
    if (categoriesMap == null) {
      return Collections.emptyList();
    }
    String jsonCategoryId = DataMapHelper.getValueForPath(categoriesMap, "catalogGroupView[0].uniqueID", String.class);
    if (jsonCategoryId != null && Objects.equals(jsonCategoryId, parentCategoryId)) {
      //JSON handling for fix pack IFJR55049
      Map<String, Object> resultMap = DataMapHelper.getValueForPath(categoriesMap, "catalogGroupView[0]", Map.class);

      if (null != resultMap) {
        List<Map<String, Object>> categories = DataMapHelper.getValueForKey(resultMap, "catalogGroupView", List.class);
        if (null != categories) {
          resultMap = DataMapTransformationHelper.transformCategoryBodMap(resultMap);
          categories = DataMapHelper.getValueForKey(resultMap, "catalogGroupView", List.class);
          return Collections.unmodifiableList(categories);
        }
      }
    } else {
      return getCategoryWrapperList(categoriesMap);
    }
    return Collections.emptyList();
  }

  /**
   * Converts a search parameters map into a IBM-specific search parameters map.
   */
  protected Map<String, String[]> createSearchParametersMap(Map<String, String> searchParams) {

    Map<String, String[]> parameters = new TreeMap<>();

    if (searchParams == null || !searchParams.containsKey(SEARCH_QUERY_PARAM_SEARCHTYPE)) {
      //IBM reverse engineering: 0 means searchTypeAll
      parameters.put(SEARCH_QUERY_PARAM_SEARCHTYPE, new String[]{SearchType.SEARCH_TYPE_PRODUCTS.getValue()});
    }
    if (searchParams == null || !searchParams.containsKey(DEFAULT_SEARCH_PAGE_NUMBER)) {
      parameters.put(DEFAULT_SEARCH_PAGE_NUMBER, new String[]{DEFAULT_SEARCH_PAGE_NUMBER_VALUE});
    }
    if (searchParams == null || !searchParams.containsKey(DEFAULT_SEARCH_PAGESIZE)) {
      parameters.put(DEFAULT_SEARCH_PAGESIZE, new String[]{DEFAULT_SEARCH_PAGESIZE_VALUE});
    }

    if (searchParams != null) {
      for (Map.Entry<String, String> entry : searchParams.entrySet()) {
        if (validSearchParams.contains(entry.getKey())) {
          parameters.put(entry.getKey(), new String[]{entry.getValue()});
        }
      }
    }

    return parameters;
  }

  public void setUseSearchRestHandlerProductIfAvailable(boolean useSearchRestHandlerProductIfAvailable) {
    this.useSearchRestHandlerProductIfAvailable = useSearchRestHandlerProductIfAvailable;
  }

  boolean useSearchRestHandlerProduct(StoreContext storeContext) {
    return WCS_VERSION_7_6.lessThan(StoreContextHelper.getWcsVersion(storeContext))
            && useSearchRestHandlerProductIfAvailable;
  }

  public void setUseSearchRestHandlerCategoryIfAvailable(boolean useSearchRestHandlerCategoryIfAvailable) {
    this.useSearchRestHandlerCategoryIfAvailable = useSearchRestHandlerCategoryIfAvailable;
  }

  boolean useSearchRestHandlerCategory(StoreContext storeContext) {
    return WCS_VERSION_7_6.lessThan(StoreContextHelper.getWcsVersion(storeContext))
            && useSearchRestHandlerCategoryIfAvailable;
  }
}
