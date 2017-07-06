package com.coremedia.livecontext.ecommerce.hybris.rest.resources;

import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.hybris.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductSearchDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupRefDocument;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.UserGroupsDocument;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CatalogResource extends AbstractHybrisResource {

  private final static String CATALOG_PATH = "/catalogs/{catalogId}/catalogversions/{catalogVersion}/";

  private final static String CATEGORIES_PATH = "/catalogs/{catalogId}/catalogversions/{catalogVersion}/categories/{categoryId}";

//  private final static String PRODUCTS_FOR_CATEGORY_PATH = "/{storeId}/cmocc/{catalogId}/{catalogVersion}/category/{categoryId}/products";

  private static final String PRICES_PATH= "/pricerows/{priceId}";

  private final static String PRODUCTS_PATH = "/catalogs/{catalogId}/catalogversions/{catalogVersion}/products/{productId}";
  //private final static String PRODUCTS_SKU_PATH = PRODUCTS_PATH + "/{sku}";

  private final static String PRODUCT_SEARCH_PATH = "/{storeId}/products/search";

  private final static String USER_GROUPS_PATH = "/usergroups/";
  private final static String USER_GROUP_BY_ID_PATH = "/usergroups/{groupId}";

  public CatalogDocument getCatalog(StoreContext storeContext) {
    String catalogId = StoreContextHelper.getCatalogId();
    String catalogVersion = StoreContextHelper.getCatalogVersion();
    List<String> uriTemplateParameters = new ArrayList<>(Arrays.asList(catalogId, catalogVersion));
    return getConnector().performGet(CATALOG_PATH, storeContext, CatalogDocument.class, uriTemplateParameters);
  }

  public List<UserGroupRefDocument> getAllUserGroups(StoreContext storeContext) {
    UserGroupsDocument userGroupDocuments = getConnector().performGet(USER_GROUPS_PATH, storeContext, UserGroupsDocument.class);
    return userGroupDocuments.getUserGroups();
  }

  public UserGroupDocument getUserGroup(String userGroupId, StoreContext storeContext){
    List<String> uriTemplateParameters = new ArrayList<>(Collections.singletonList(userGroupId));
    return getConnector().performGet(USER_GROUP_BY_ID_PATH, storeContext, UserGroupDocument.class, uriTemplateParameters);
  }

  public CategoryDocument getCategoryById(String categoryId, StoreContext storeContext) {
    String catalogId = StoreContextHelper.getCatalogId(storeContext);
    String catalogVersion = StoreContextHelper.getCatalogVersion(storeContext);
    List<String> uriTemplateParameters = new ArrayList<>(Arrays.asList(catalogId, catalogVersion, categoryId));
    return getConnector().performGet(CATEGORIES_PATH, storeContext, CategoryDocument.class, uriTemplateParameters);
  }

  public ProductDocument getProductById(String productId, StoreContext storeContext) {
    String catalogId = StoreContextHelper.getCatalogId();
    String catalogVersion = StoreContextHelper.getCatalogVersion();
    List<String> uriTemplateParameters = new ArrayList<>(Arrays.asList(catalogId, catalogVersion, productId));
    return getConnector().performGet(PRODUCTS_PATH, storeContext, ProductDocument.class, uriTemplateParameters);
  }

  public PriceDocument getPriceDocumentById(String priceId, StoreContext storeContext) {
    List<String> uriTemplateParameters = new ArrayList<>(Collections.singletonList(priceId));
    return getConnector().performGet(PRICES_PATH, storeContext, PriceDocument.class, uriTemplateParameters);
  }

  public ProductSearchDocument searchProducts(String searchTerm, @Nullable Map<String, String> searchParams, StoreContext storeContext) {
    if (searchTerm != null) {
      List<String> pathParams = new ArrayList<>(Collections.singletonList(StoreContextHelper.getStoreId()));
      MultiValueMap<String, String> queryParams = prepareQueryParams(searchParams);
      queryParams.add("query", prepareSearchTerm(searchTerm, searchParams));
      return getOccConnector().performGet(PRODUCT_SEARCH_PATH, storeContext, ProductSearchDocument.class, pathParams, queryParams, true);
    }
    return null;
  }

  private String prepareSearchTerm(String searchTerm, @Nullable Map<String, String> searchParams) {
    String term = "*".equals(searchTerm) ? "" : searchTerm;
    if (searchParams != null && searchParams.containsKey(CatalogServiceImpl.SEARCH_PARAM_CATEGORYID)) {
      StringBuilder search = new StringBuilder(term);
      search.append(":relevance");
      for (Map.Entry<String, String> searchParam : searchParams.entrySet()) {
        search.append(":");
        search.append(searchParam.getKey());
        search.append(":");
        search.append(searchParam.getValue());
      }
      return search.toString();
    }
    return term;
  }

  private MultiValueMap<String, String> prepareQueryParams(@Nullable Map<String, String> searchParams)  {
    if (searchParams == null) {
      return new LinkedMultiValueMap<>();
    }

    if (searchParams.containsKey(CatalogService.SEARCH_PARAM_PAGENUMBER)) {
      String pageNumberEntry = searchParams.get(CatalogService.SEARCH_PARAM_PAGENUMBER);
      searchParams.put(CatalogServiceImpl.SEARCH_PARAM_PAGENUMBER, pageNumberEntry);
      searchParams.remove(CatalogService.SEARCH_PARAM_PAGENUMBER);
    }

    if (searchParams.containsKey(CatalogService.SEARCH_PARAM_CATEGORYID)) {
      String category = searchParams.get(CatalogService.SEARCH_PARAM_CATEGORYID);
      searchParams.put(CatalogServiceImpl.SEARCH_PARAM_CATEGORYID, category);
      searchParams.remove(CatalogService.SEARCH_PARAM_CATEGORYID);
    }

    LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.setAll(searchParams);
    return queryParams;
  }
}
