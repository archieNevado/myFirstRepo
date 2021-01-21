package com.coremedia.blueprint.caas.commerce.adapter;

import com.coremedia.blueprint.base.caas.model.adapter.AbstractDynamicListAdapter;
import com.coremedia.blueprint.base.querylist.PaginationHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.caas.commerce.model.CommerceFacade;
import com.coremedia.caas.model.adapter.ExtendedLinkListAdapterFactory;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.util.CapStructUtil;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @deprecated The adapter will change its behavior since commerce data won't be served by the CoreMedia Headless Server anymore in future AEPs.
 * Whenever there is commerce data provided by this adapter, it will be replaced by a reference to the corresponding commerce item in future.
 * These references then need to be resolved by the commerce system.
 */
@DefaultAnnotation(NonNull.class)
@Deprecated(since = "2101")
public class ProductListAdapter extends AbstractDynamicListAdapter<Object> {
  public static final String STRUCT_KEY_EXTERNAL_ID = "externalId";
  public static final String STRUCT_KEY_PRODUCTLIST = "productList";
  public static final String STRUCT_KEY_PRODUCTLIST_SELECT_FACET_VALUE = "selectedFacetValue";
  public static final String STRUCT_KEY_PRODUCTLIST_OFFSET = "offset";
  public static final String STRUCT_KEY_PRODUCTLIST_MAX_LENGTH = "maxLength";
  public static final String STRUCT_KEY_PRODUCTLIST_ORDER_BY = "orderBy";

  public static final int MAX_LENGTH_DEFAULT = 10;
  public static final int OFFSET_DEFAULT = 0;
  public static final String DIGIT_PATTERN = "[0-9]*";
  public static final String ALL_QUERY = "*";

  private final SettingsService settingsService;
  private final CommerceFacade commerceFacade;
  private final String siteId;
  private final Integer offset;

  public ProductListAdapter(ExtendedLinkListAdapterFactory extendedLinkListAdapterFactory, Content content, SettingsService settingsService, CommerceFacade commerceFacade, String siteId) {
    this(extendedLinkListAdapterFactory, content, settingsService, commerceFacade, siteId, OFFSET_DEFAULT);
  }

  public ProductListAdapter(ExtendedLinkListAdapterFactory extendedLinkListAdapterFactory, Content content, SettingsService settingsService, CommerceFacade commerceFacade, String siteId, Integer offset) {
    super(extendedLinkListAdapterFactory, content);
    this.settingsService = settingsService;
    this.commerceFacade = commerceFacade;
    this.siteId = siteId;
    this.offset = offset;
  }

  private int getMaxLength() {
    Integer value = CapStructUtil.getInteger(getSettings(), STRUCT_KEY_PRODUCTLIST_MAX_LENGTH);
    return !(value == null || value == 0) ? value : MAX_LENGTH_DEFAULT;
  }

  private String getOrderBy() {
    return CapStructUtil.getString(getSettings(), STRUCT_KEY_PRODUCTLIST_ORDER_BY);
  }

  private String getFacet() {
    String facetSettingValue = CapStructUtil.getString(getSettings(), STRUCT_KEY_PRODUCTLIST_SELECT_FACET_VALUE);
    String returnValue = "";
    if (facetSettingValue != null && !facetSettingValue.matches(DIGIT_PATTERN)) {
      returnValue = facetSettingValue;
    }
    return returnValue;
  }

  /**
   * @deprecated The headless server won't serve catalog data anymore in future AEP release.
   * Instead a list of reference ids is going to be returned instead.
   */
  @Override
  public List getItems() {
    return super.getItems();
  }

  /**
   * @deprecated The headless server won't serve catalog data in future AEP release.
   * Instead a list of reference ids is going to be returned instead.
   */
  @Deprecated(since = "2101")
  public List<Product> getProducts() {
    SearchResult<Product> productSearchResult = commerceFacade.searchProducts(ALL_QUERY, getSearchParams(), siteId);
    return productSearchResult != null ? productSearchResult.getSearchResult() : Collections.emptyList();
  }

  private Map<String, String> getSearchParams() {
    Category category = commerceFacade.getCategory(getExternalId(), siteId).getData();
    Map<String, String> params = new HashMap<>();
    CatalogAlias catalogAlias = category != null ? category.getReference().getCatalogAlias() : null;
    String orderBy = getOrderBy();
    int limit = getMaxLength();
    int productOffset = getProductOffset();
    Optional<String> overrideCategoryId = getOverrideCategoryId();
    String facet = getFacet();

    //if necessary use the api which supports the facet search
    params.put(CatalogService.SEARCH_PARAM_FACET_SUPPORT, "true");

    if (overrideCategoryId.isPresent()) {
      params.put(CatalogService.SEARCH_PARAM_CATEGORYID, overrideCategoryId.get());
    } else if (category != null && !category.isRoot()) {
      params.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    }

    if (catalogAlias != null && !StringUtils.isEmpty(catalogAlias.value())) {
      params.put(CatalogService.SEARCH_PARAM_CATALOG_ALIAS, catalogAlias.value());
    }

    if (!StringUtils.isEmpty(orderBy)) {
      params.put(CatalogService.SEARCH_PARAM_ORDERBY, orderBy);
    }

    if (limit >= 0) {
      params.put(CatalogService.SEARCH_PARAM_TOTAL, String.valueOf(limit));
    }

    if (productOffset > 0) {
      params.put(CatalogService.SEARCH_PARAM_OFFSET, String.valueOf(productOffset));
    }

    if (StringUtils.isNotEmpty(facet)) {
      params.put(CatalogService.SEARCH_PARAM_FACET, facet);
    }

    return params;
  }

  private int getProductOffset() {
    int initialOffset = Optional.ofNullable(CapStructUtil.getInteger(getSettings(), STRUCT_KEY_PRODUCTLIST_OFFSET)).orElse(0);
    int productOffset = initialOffset;
    if (Optional.ofNullable(offset).isPresent()) {
      productOffset = initialOffset + PaginationHelper.dynamicOffset(getFixedItemsStructList(), offset, ANNOTATED_LINK_STRUCT_INDEX_PROPERTY_NAME);
    }
    return productOffset;
  }

  private Optional<String> getOverrideCategoryId() {
    String value = CapStructUtil.getString(getSettings(), STRUCT_KEY_PRODUCTLIST_SELECT_FACET_VALUE);
    if (value != null && value.matches(DIGIT_PATTERN)) {
      return Optional.of(value);
    }
    return Optional.empty();
  }

  private Struct getSettings() {
    return settingsService.setting(STRUCT_KEY_PRODUCTLIST, Struct.class, getContent());
  }

  private String getExternalId() {
    return getContent().getString(STRUCT_KEY_EXTERNAL_ID);
  }

  @Override
  public int getLimit() {
    return getMaxLength();
  }

  @Override
  public int getStart() {
    return (offset != null && offset > 0) ? offset : 0;
  }

  @Override
  public List<Object> getDynamicItems() {
    // The cast to Object is necessary, because the dynamic products need to be the same class as or a superclass/interface
    // of Content.class (the fixed items are content items).
    return getProducts().stream().map(product -> (Object) product).collect(Collectors.toList());
  }

  @Override
  public Class<Object> getItemClass() {
    return Object.class;
  }
}
