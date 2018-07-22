package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.AbstractOCSearchResultDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.BoolQueryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.Operator;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductSearchResultDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.SearchRequestDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.TermQueryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.TextQueryDocument;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_OFFSET;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_ORDERBY;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_PAGENUMBER;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_PAGESIZE;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_TOTAL;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyMap;

/**
 * ProductSearch resource.
 */
@Service("ocapiProductSearchResource")
public class ProductSearchResource extends AbstractDataResource {

  private static final String PRODUCT_SEARCH_PATH = "/product_search";

  /**
   * Returns a list of products that fits the given query string and params.
   *
   * @param query        the query string
   * @param params       the params
   * @param storeContext the effective store context
   * @return the list of product documents or an empty list if no product is found
   */
  @NonNull
  public Optional<ProductSearchResultDocument> searchProducts(@NonNull String query,
                                                              @NonNull Map<String, String> params,
                                                              @NonNull Set<String> categoryIds,
                                                              @NonNull StoreContext storeContext) {
    ListMultimap<String, String> queryParams = ImmutableListMultimap
            .of("site_id", storeContext.getStoreId());
    String searchPhrase = StringUtils.isBlank(query) ? "*" : query;

    SearchRequestDocument searchRequest = new SearchRequestDocument();
    searchRequest.setExpand("all");

    // Add pagination if params given.
    if (!params.isEmpty()) {
      // result count
      if (params.containsKey(SEARCH_PARAM_TOTAL)) {
        searchRequest.setCount(Integer.parseInt(params.get(SEARCH_PARAM_TOTAL)));
      } else if (params.containsKey(SEARCH_PARAM_PAGESIZE)) {
        searchRequest.setCount(Integer.parseInt(params.get(SEARCH_PARAM_PAGESIZE)));
      }
      // offset
      if (params.containsKey(SEARCH_PARAM_OFFSET)) {
        searchRequest.setStart(Integer.parseInt(params.get(SEARCH_PARAM_OFFSET)) - 1);
      } else if (params.containsKey(SEARCH_PARAM_PAGENUMBER) && params.containsKey(SEARCH_PARAM_PAGESIZE)) {
        int pageSize = Integer.parseInt(params.get(SEARCH_PARAM_PAGESIZE));
        int pageNumber = Integer.parseInt(params.get(SEARCH_PARAM_PAGENUMBER));
        searchRequest.setStart(pageSize * (pageNumber - 1));
      }

      // Sort.
      if (params.containsKey(SEARCH_PARAM_ORDERBY)) {
        // sorting not needed yet
        //searchRequest.setSorts(Collections.singletonList(new SortDocument("name", SortDocument.SortOrder.asc)));
      }
    }

    //default result size: 200 is the maximum result size given by sfcc.
    if (!params.containsKey(SEARCH_PARAM_TOTAL)) {
      searchRequest.setCount(200);
    }

    if (!categoryIds.isEmpty()) {
      TextQueryDocument textQuery = new TextQueryDocument(Arrays.asList("id", "name"), searchPhrase);
      TermQueryDocument categoryQuery = new TermQueryDocument("category_id", Operator.one_of, newArrayList(categoryIds));
      searchRequest.setQuery(new BoolQueryDocument().mustMatch(textQuery).mustMatch(categoryQuery));
    } else {
      searchRequest.setQuery(new TextQueryDocument(Arrays.asList("id", "name"), searchPhrase));
    }
    String requestBody = searchRequest.toJSONString();

    return getConnector().postResource(PRODUCT_SEARCH_PATH, emptyMap(), queryParams, requestBody,
            ProductSearchResultDocument.class);
  }

  @NonNull
  public List<ProductDocument> getProductsById(@NonNull List<String> productIds, @NonNull StoreContext storeContext) {
    ListMultimap<String, String> queryParams = ImmutableListMultimap
            .of("site_id", storeContext.getStoreId());

    SearchRequestDocument searchRequest = new SearchRequestDocument();
    searchRequest.setSelect(null);
    searchRequest.setQuery(new TermQueryDocument("id", Operator.one_of, productIds));
    String requestBody = searchRequest.toJSONString();

    Optional<ProductSearchResultDocument> doc = getConnector().postResource(PRODUCT_SEARCH_PATH, emptyMap(), queryParams,
            requestBody, ProductSearchResultDocument.class);

    return doc
            .map(AbstractOCSearchResultDocument::getHits)
            .orElseGet(Collections::emptyList);
  }
}
