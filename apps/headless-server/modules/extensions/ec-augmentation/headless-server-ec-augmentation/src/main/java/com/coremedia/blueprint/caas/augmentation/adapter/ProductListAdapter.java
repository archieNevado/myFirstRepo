package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.base.caas.model.adapter.AbstractDynamicListAdapter;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.querylist.PaginationHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.caas.augmentation.CommerceEntityHelper;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.caas.model.adapter.ExtendedLinkListAdapterFactory;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.FACETS_DELIMITER;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_CATALOG_ALIAS;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_CATEGORYID;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_FACETS;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_FACET_SUPPORT;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_OFFSET;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_ORDERBY;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_TOTAL;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;

@DefaultAnnotation(NonNull.class)
public class ProductListAdapter extends AbstractDynamicListAdapter<Object> {

  static final String STRUCT_KEY_EXTERNAL_ID = "externalId";
  static final String STRUCT_KEY_PRODUCTLIST = "productList";
  static final String STRUCT_KEY_PRODUCTLIST_OFFSET = "offset";
  static final String STRUCT_KEY_PRODUCTLIST_MAX_LENGTH = "maxLength";
  static final String STRUCT_KEY_PRODUCTLIST_ORDER_BY = "orderBy";
  static final String STRUCT_KEY_PRODUCTLIST_FILTER_FACET_QUERIES = "filterFacets";

  static final String ORDER_BY_DEFAULT = "";
  static final Map<String, Map<String, List<String>>> FILTER_FACET_DEFAULT = Map.of();
  static final int MAX_LENGTH_DEFAULT = 10;
  static final int OFFSET_DEFAULT = 0;
  static final String ALL_QUERY = "*";

  private final SettingsService settingsService;
  private final CommerceEntityHelper commerceEntityHelper;
  private final CommerceSearchFacade commerceSearchFacade;
  private final Site site;
  private final Integer offset;

  public ProductListAdapter(ExtendedLinkListAdapterFactory extendedLinkListAdapterFactory,
                            Content content,
                            SettingsService settingsService,
                            CommerceEntityHelper commerceEntityHelper,
                            CommerceSearchFacade commerceSearchFacade,
                            Site site,
                            Integer offset) {
    super(extendedLinkListAdapterFactory, content);
    this.settingsService = settingsService;
    this.commerceEntityHelper = commerceEntityHelper;
    this.commerceSearchFacade = commerceSearchFacade;
    this.site = site;
    this.offset = offset;
  }

  @Override
  public int getLimit() {
    Object value = getProductListSettings().get(STRUCT_KEY_PRODUCTLIST_MAX_LENGTH);
    int intValue = value instanceof Integer ? (Integer) value : MAX_LENGTH_DEFAULT;
    return intValue != 0 ? intValue : MAX_LENGTH_DEFAULT;
  }

  @Override
  public int getStart() {
    return (offset != null && offset > 0) ? offset : 0;
  }

  public String getOrderBy() {
    Object value = getProductListSettings().get(STRUCT_KEY_PRODUCTLIST_ORDER_BY);
    return value instanceof String ? value.toString() : ORDER_BY_DEFAULT;
  }

  public List<String> getFacets() {
    return getFilterFacetQueries().stream()
            .filter(facet -> !facet.isBlank())
            .collect(toList());
  }

  public int getProductOffset() {
    Object value = getProductListSettings().get(STRUCT_KEY_PRODUCTLIST_OFFSET);
    // The UI (and thus productList settings) work with an offset based
    // on 1, whereas the API works with a technical offset based on 0.
    int initialOffset = value instanceof Integer ? (Integer) value - 1 : OFFSET_DEFAULT;

    int productOffset = initialOffset;
    if (Optional.ofNullable(offset).isPresent()) {
      productOffset = initialOffset + PaginationHelper.dynamicOffset(getFixedItemsStructList(), offset, ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME);
    }
    return productOffset;
  }

  /**
   * In real life scenario the search should be executed on the commerce system and not in the CoreMedia headless server.
   * @return commerce references, which need to be resolved externally
   */
  @Deprecated
  public List<CommerceRef> getProductRefs() {
    return commerceSearchFacade.searchProducts(ALL_QUERY, getSearchParams(site.getId()), site);
  }

  @SuppressWarnings("removal")
  private Map<String, String> getSearchParams(String siteId) {
    Map<String, String> params = new HashMap<>();
    String commerceIdStr = getContent().getString("externalId");
    CommerceIdParserHelper.parseCommerceId(commerceIdStr)
            .ifPresent(commerceId -> {
              params.put(SEARCH_PARAM_CATALOG_ALIAS, commerceId.getCatalogAlias().value());
              CommerceBean categoryBean = commerceEntityHelper.getCommerceBean(commerceId, siteId);
              if (categoryBean instanceof Category && !((Category)categoryBean).isRoot()) {
                var techId = categoryBean.getExternalTechId();
                if (!isNullOrEmpty(techId)) {
                  params.put(SEARCH_PARAM_CATEGORYID, techId);
                }
              }
            });

    String orderBy = getOrderBy();
    int limit = getLimit();
    int productOffset = getProductOffset();
    List<String> filterFacetQueries = getFilterFacetQueries();

    //if necessary use the api which supports the facet search
    params.put(SEARCH_PARAM_FACET_SUPPORT, "true");

    if (!isNullOrEmpty(orderBy)) {
      params.put(SEARCH_PARAM_ORDERBY, orderBy);
    }

    if (limit >= 0) {
      params.put(SEARCH_PARAM_TOTAL, String.valueOf(limit));
    }

    if (productOffset > 0) {
      params.put(SEARCH_PARAM_OFFSET, String.valueOf(productOffset));
    }

    if (!filterFacetQueries.isEmpty()) {
      params.put(SEARCH_PARAM_FACETS, String.join(FACETS_DELIMITER, filterFacetQueries));
    }

    return params;
  }

  private List<String> getFilterFacetQueries() {
    // return raw filter queries ignoring the override category
    return getFilterFacets().values().stream()
            .flatMap(m -> m.values().stream())
            .flatMap(List::stream)
            .filter(s -> !s.isBlank())
            .collect(toList());
  }

  @VisibleForTesting
  Map<String, Map<String, List<String>>> getFilterFacets() {
    Object o = getProductListSettings().get(STRUCT_KEY_PRODUCTLIST_FILTER_FACET_QUERIES);
    //noinspection unchecked
    return o instanceof Map ? (Map<String, Map<String, List<String>>>) o : FILTER_FACET_DEFAULT;
  }

  @VisibleForTesting
  Map<String, Object> getProductListSettings() {
    try {
      Struct productList = settingsService.setting(STRUCT_KEY_PRODUCTLIST, Struct.class, getContent());
      if (productList == null) {
        return Map.of();
      }
      //copy struct because it may be cached and the cache MUST NEVER be modified.
      return new HashMap<>(productList.toNestedMaps());
    } catch (NoSuchPropertyDescriptorException e) {
      //no struct configured for current content, empty map will be returned.
    }
    return Map.of();
  }

  @Override
  public List<Object> getDynamicItems() {
    // The cast to Object is necessary, because the dynamic products need to be the same class as or a superclass/interface
    // of Content.class (the fixed items are content items).
    return getProductRefs().stream().map(product -> (Object) product).collect(toList());
  }

  @Override
  public Class<Object> getItemClass() {
    return Object.class;
  }
}
