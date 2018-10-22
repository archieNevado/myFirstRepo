package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.ShopProductSearchResultDocument;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_CATEGORYID;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_FACET;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_OFFSET;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_ORDERBY;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_TOTAL;
import static com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.OCShopApiConnector.STORE_ID_PARAM;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service("ocapiShopProductSearchResource")
public class ShopProductSearchResource extends AbstractShopResource {

  private static final String PRODUCT_SEARCH_PATH = "/product_search";

  private static final String PARAM_LOCALE = "locale";
  private static final String PARAM_CURRENCY = "currency";
  private static final String PARAM_EXPAND = "expand";

  private static final String PARAM_REFINE_1 = "refine_1";
  private static final String PARAM_REFINE_2 = "refine_2";
  private static final String PARAM_REFINE_CATEGORY_ID = "cgid";

  private static final String PARAM_COUNT = "count";
  private static final String PARAM_START = "start";

  private static final String PARAM_SORT = "sort";

  //sfcc allows 200 as maximum number of instances per request
  private static final int MAX_COUNT = 200;

  //sfcc root category id
  private static final String ROOT_CATEGORY_ID = "root";

  /**
   * Returns a list of products that fits the given query string and params.
   *
   * @param query        the query string
   * @param params       the params
   * @param storeContext the effective store context
   * @return the list of product documents or an empty list if no product is found
   */
  public Optional<ShopProductSearchResultDocument> searchProducts(@NonNull String query,
                                                                  @NonNull Map<String, String> params,
                                                                  @NonNull StoreContext storeContext) {
    Map<String, String> pathParameters = ImmutableMap.of(STORE_ID_PARAM, storeContext.getStoreId());

    ListMultimap<String, String> queryParams = buildQueryParams(query, params, storeContext);

    return getConnector().getResource(PRODUCT_SEARCH_PATH, pathParameters, queryParams, ShopProductSearchResultDocument.class);
  }

  @NonNull
  private static ListMultimap<String, String> buildQueryParams(@NonNull String query,
                                                               @NonNull Map<String, String> params,
                                                               @NonNull StoreContext storeContext) {
    ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();

    //query is ignored as we are only interested in the facet search

    Locale locale = storeContext.getLocale();
    if (locale != null) {
      builder.put(PARAM_LOCALE, locale.toLanguageTag());
    }

    Currency currency = storeContext.getCurrency();
    if (currency != null) {
      builder.put(PARAM_CURRENCY, currency.getCurrencyCode());
    }

    String expand = params.get(PARAM_EXPAND);
    if (!isEmpty(expand)) {
      builder.put(PARAM_EXPAND, expand);
    }

    String categoryId = params.get(SEARCH_PARAM_CATEGORYID);
    if (isEmpty(categoryId)) {
      categoryId = ROOT_CATEGORY_ID;
    }
    builder.put(PARAM_REFINE_1, PARAM_REFINE_CATEGORY_ID + "=" + categoryId);

    String facet = params.get(SEARCH_PARAM_FACET);
    if (!isEmpty(facet)) {
      builder.put(PARAM_REFINE_2, facet);
    }

    String countString = params.get(SEARCH_PARAM_TOTAL);
    if (!isEmpty(countString)) {
      int count = Integer.parseInt(countString);
      if (count > MAX_COUNT) {
        count = MAX_COUNT;
      }
      builder.put(PARAM_COUNT, String.valueOf(count));
    }

    String offset = params.get(SEARCH_PARAM_OFFSET);
    if (!isEmpty(offset)) {
      builder.put(PARAM_START, offset);
    }

    String orderBy = params.get(SEARCH_PARAM_ORDERBY);
    if (!isEmpty(orderBy)) {
      String value = mapOrderByType(orderBy);
      if (value != null) {
        builder.put(PARAM_SORT, value);
      }
    }

    return builder.build();
  }

  private static String mapOrderByType(String orderByType) {
    try {
      return OrderByType.valueOf(orderByType).getValue();
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
