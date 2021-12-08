package com.coremedia.blueprint.caas.augmentation.adapter;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.caas.augmentation.CommerceEntityHelper;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRefFactory;
import com.coremedia.caas.model.adapter.ExtendedLinkListAdapter;
import com.coremedia.caas.model.adapter.ExtendedLinkListAdapterFactory;
import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.common.CapType;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_CATALOG_ALIAS;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_FACET_SUPPORT;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_ORDERBY;
import static com.coremedia.blueprint.caas.augmentation.adapter.CommerceSearchFacade.SEARCH_PARAM_TOTAL;
import static com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapter.FILTER_FACET_DEFAULT;
import static com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapter.MAX_LENGTH_DEFAULT;
import static com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapter.OFFSET_DEFAULT;
import static com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapter.ORDER_BY_DEFAULT;
import static com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapter.STRUCT_KEY_PRODUCTLIST;
import static com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapter.STRUCT_KEY_PRODUCTLIST_FILTER_FACET_QUERIES;
import static com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapter.STRUCT_KEY_PRODUCTLIST_MAX_LENGTH;
import static com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapter.STRUCT_KEY_PRODUCTLIST_OFFSET;
import static com.coremedia.blueprint.caas.augmentation.adapter.ProductListAdapter.STRUCT_KEY_PRODUCTLIST_ORDER_BY;
import static com.coremedia.caas.headless_server.plugin_support.PluginSupport.CONTEXT_PARAMETER_NAME_PREVIEW_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductListAdapterTest {
  private ProductListAdapter productListAdapter;
  private static final String EXTERNAL_ID_PROPERTY_VALUE = "ibm:///catalog/category/0815";

  @Mock
  private ExtendedLinkListAdapterFactory extendedLinkListAdapterFactory;

  @Mock
  private Content productList;

  @Mock
  private SettingsService settingsService;

  @Mock
  private CommerceEntityHelper commerceEntityHelper;

  @Mock
  private CommerceSearchFacade commerceSearchFacade;

  @Mock
  private ExtendedLinkListAdapter extendedLinkListAdapter;

  @Mock
  private Content fixedTarget1;

  @Mock
  private Content fixedTarget2;

  @Mock
  private Content fixedTarget3;

  @Mock
  private Struct struct;

  @Mock
  private CapType capType;

  @Mock
  private Category category;

  @Mock
  private CapPropertyDescriptor propertyDescriptor;

  @Mock
  private Site site;

  @Mock
  private DataFetchingEnvironment dataFetchingEnvironment;

  @Mock
  private GraphQLContext graphQLContext;

  private final Map<String, Object> PRODUCT_LIST_STRUCT_DEFAULTS = Map.of(
          STRUCT_KEY_PRODUCTLIST_ORDER_BY, "priceDesc");

  private CommerceRef dynamicTarget1;
  private CommerceRef dynamicTarget2;
  private CommerceRef dynamicTarget3;
  private CommerceRef dynamicTarget4;
  private List<CommerceRef> productSearchResult;

  @BeforeEach
  public void setup() {
    dynamicTarget1 = CommerceRefFactory.from(
            CommerceIdParserHelper.parseCommerceId("ibm:///catalog/product/4711").orElseThrow(),
            CatalogId.of("catalogId"),
            "storeId",
            Locale.US,
            "siteId");
    dynamicTarget2 = CommerceRefFactory.from(
            CommerceIdParserHelper.parseCommerceId("ibm:///catalog/product/4712").orElseThrow(),
            CatalogId.of("catalogId"),
            "storeId",
            Locale.US,
            "siteId");
    dynamicTarget3 = CommerceRefFactory.from(
            CommerceIdParserHelper.parseCommerceId("ibm:///catalog/product/4713").orElseThrow(),
            CatalogId.of("catalogId"),
            "storeId",
            Locale.US,
            "siteId");
    dynamicTarget4 = CommerceRefFactory.from(
            CommerceIdParserHelper.parseCommerceId("ibm:///catalog/product/4714").orElseThrow(),
            CatalogId.of("catalogId"),
            "storeId",
            Locale.US,
            "siteId");

    productSearchResult = Stream.of(dynamicTarget1, dynamicTarget2, dynamicTarget3).collect(Collectors.toList());
    lenient().when(commerceSearchFacade.searchProducts(eq(ProductListAdapter.ALL_QUERY), anyMap(), eq(site))).thenReturn(productSearchResult);
    lenient().when(site.getId()).thenReturn("sideId");
    lenient().when(graphQLContext.get(CONTEXT_PARAMETER_NAME_PREVIEW_DATE)).thenReturn(ZonedDateTime.now());

    productListAdapter = new ProductListAdapter(extendedLinkListAdapterFactory, productList, settingsService, commerceEntityHelper, commerceSearchFacade, site, 0);
    when(settingsService.setting(STRUCT_KEY_PRODUCTLIST, Struct.class, productList)).thenReturn(struct);
    when(struct.toNestedMaps()).thenReturn(PRODUCT_LIST_STRUCT_DEFAULTS);
    lenient().when(productListAdapter.getContent().getString(ProductListAdapter.STRUCT_KEY_EXTERNAL_ID)).thenReturn(EXTERNAL_ID_PROPERTY_VALUE);
    lenient().when(commerceEntityHelper.getCommerceBean(CommerceIdParserHelper.parseCommerceId(EXTERNAL_ID_PROPERTY_VALUE).orElseThrow(), "sideId")).thenReturn(category);
  }

  private static Map<String, Object> getFixedItemMap(Content target, int index) {
    Map<String, Object> map = new HashMap<>();
    map.put("target", target);
    map.put("index", index);
    return map;
  }

  @Test
  void setEmptyOffsetAndLimit() {
    //Given
    Map<String, Object> structMap = getDefaultStructMap();
    structMap.put(STRUCT_KEY_PRODUCTLIST_OFFSET, null);
    structMap.put(STRUCT_KEY_PRODUCTLIST_MAX_LENGTH, null);
    structMap.put(STRUCT_KEY_PRODUCTLIST_FILTER_FACET_QUERIES, null);
    structMap.put(STRUCT_KEY_PRODUCTLIST_ORDER_BY, null);
    when(struct.toNestedMaps()).thenReturn(structMap);
    when(extendedLinkListAdapterFactory.to(productList)).thenReturn(extendedLinkListAdapter);

    //When
    Map<String, Object> productListSettings = productListAdapter.getProductListSettings();

    //Then
    assertThat(productListSettings)
            .containsKey(STRUCT_KEY_PRODUCTLIST_OFFSET)
            .containsKey(STRUCT_KEY_PRODUCTLIST_MAX_LENGTH)
            .containsKey(STRUCT_KEY_PRODUCTLIST_FILTER_FACET_QUERIES)
            .containsKey(STRUCT_KEY_PRODUCTLIST_ORDER_BY);
    assertThat(productListAdapter)
            .returns(OFFSET_DEFAULT, ProductListAdapter::getProductOffset)
            .returns(MAX_LENGTH_DEFAULT, ProductListAdapter::getLimit)
            .returns(ORDER_BY_DEFAULT, ProductListAdapter::getOrderBy)
            .returns(FILTER_FACET_DEFAULT, ProductListAdapter::getFilterFacets);
  }

  @Test
  void get_mixedItems() {
    List<Map<String, Object>> fixedItems = new ArrayList<>();
    fixedItems.add(getFixedItemMap(fixedTarget1, 1));
    fixedItems.add(getFixedItemMap(fixedTarget2, 3));
    fixedItems.add(getFixedItemMap(fixedTarget3, 5));
    when(extendedLinkListAdapterFactory.to(productList)).thenReturn(extendedLinkListAdapter);
    when(extendedLinkListAdapter.getExtendedTargets()).thenReturn(fixedItems);

    List items = productListAdapter.getItems().getData();

    assertThat(items).isNotEmpty();
    assertThat(items.size()).isEqualTo(6);
    assertThat(fixedTarget1).isEqualTo(items.get(0));
    assertThat(dynamicTarget1).isEqualTo(items.get(1));
    assertThat(fixedTarget2).isEqualTo(items.get(2));
    assertThat(dynamicTarget2).isEqualTo(items.get(3));
    assertThat(fixedTarget3).isEqualTo(items.get(4));
    assertThat(dynamicTarget3).isEqualTo(items.get(5));
  }

  @Test
  void get_itemsLimitedOnlyFixed() {
    List<Map<String, Object>> fixedItems = new ArrayList<>();
    fixedItems.add(getFixedItemMap(fixedTarget1, 1));
    fixedItems.add(getFixedItemMap(fixedTarget2, 2));
    fixedItems.add(getFixedItemMap(fixedTarget3, 5));

    Map<String, Object> structMap = getDefaultStructMap();
    structMap.put(STRUCT_KEY_PRODUCTLIST_MAX_LENGTH, 2);
    when(struct.toNestedMaps()).thenReturn(structMap);

    when(extendedLinkListAdapterFactory.to(productList)).thenReturn(extendedLinkListAdapter);
    when(extendedLinkListAdapter.getExtendedTargets()).thenReturn(fixedItems);

    List items = productListAdapter.getItems().getData();

    assertThat(items).isNotEmpty();
    assertThat(items.size()).isEqualTo(2);
    assertThat(fixedTarget1).isEqualTo(items.get(0));
    assertThat(fixedTarget2).isEqualTo(items.get(1));
  }

  @Test
  void get_itemsLimitedMixed() {
    List<Map<String, Object>> fixedItems = new ArrayList<>();
    fixedItems.add(getFixedItemMap(fixedTarget1, 1));
    fixedItems.add(getFixedItemMap(fixedTarget2, 3));
    fixedItems.add(getFixedItemMap(fixedTarget3, 5));

    Map<String, Object> structMap = getDefaultStructMap();
    structMap.put(STRUCT_KEY_PRODUCTLIST_MAX_LENGTH, 3);
    when(struct.toNestedMaps()).thenReturn(structMap);

    when(extendedLinkListAdapterFactory.to(productList)).thenReturn(extendedLinkListAdapter);
    when(extendedLinkListAdapter.getExtendedTargets()).thenReturn(fixedItems);

    List items = productListAdapter.getItems().getData();

    assertThat(items).isNotEmpty();
    assertThat(items.size()).isEqualTo(3);
    assertThat(fixedTarget1).isEqualTo(items.get(0));
    assertThat(dynamicTarget1).isEqualTo(items.get(1));
    assertThat(fixedTarget2).isEqualTo(items.get(2));
  }

  @Test
  void get_itemsStructMatchingDIGITS() {
    List<Map<String, Object>> fixedItems = new ArrayList<>();
    fixedItems.add(getFixedItemMap(fixedTarget1, 1));
    fixedItems.add(getFixedItemMap(fixedTarget2, 3));
    fixedItems.add(getFixedItemMap(fixedTarget3, 5));
    Map<String, Object> structMap = getDefaultStructMap();
    when(struct.toNestedMaps()).thenReturn(structMap);

    when(extendedLinkListAdapterFactory.to(productList)).thenReturn(extendedLinkListAdapter);
    Map<String, String> searchParams = getSearchParamsMap();

    when(commerceSearchFacade.searchProducts(eq(ProductListAdapter.ALL_QUERY), eq(searchParams), eq(site))).thenReturn(productSearchResult);
    when(category.getExternalTechId()).thenReturn(searchParams.get("categoryId"));
    // expect the facet to be: 12TestSomethingf4cet
    // expect the overrideCategoryId to be: ""

    List<CommerceRef> productRefs = productListAdapter.getProductRefs();
    assertThat(productRefs).isNotNull();

    //    verify(augmentationFacade, times(1)).getCategory(eq(EXTERNAL_ID), eq(SITE_ID));
    verify(commerceSearchFacade, times(1)).searchProducts(eq(ProductListAdapter.ALL_QUERY), eq(searchParams), eq(site));
  }

  @Test
  void get_itemsStructMatchingFacet() {
    List<Map<String, Object>> fixedItems = new ArrayList<>();
    fixedItems.add(getFixedItemMap(fixedTarget1, 1));
    fixedItems.add(getFixedItemMap(fixedTarget2, 3));
    fixedItems.add(getFixedItemMap(fixedTarget3, 5));

    Map<String, Object> structMap = getDefaultStructMap();
    when(struct.toNestedMaps()).thenReturn(structMap);

    Map<String, String> searchParams = getSearchParamsMap();
    lenient().when(commerceSearchFacade.searchProducts(eq(ProductListAdapter.ALL_QUERY), eq(searchParams), eq(site))).thenReturn(productSearchResult);
    when(extendedLinkListAdapterFactory.to(productList)).thenReturn(extendedLinkListAdapter);
    List<CommerceRef> productRefs = productListAdapter.getProductRefs();
    assertThat(productRefs).isNotNull();

    //    verify(augmentationFacade, times(1)).getCategory(eq(EXTERNAL_ID), eq(SITE_ID));
    verify(commerceSearchFacade, times(1)).searchProducts(eq(ProductListAdapter.ALL_QUERY), eq(searchParams), eq(site));
  }

  @Test
  void getFacets() {
    Map<String, Object> filterFacetsMap = new HashMap<>();
    filterFacetsMap.put("price", Map.of("queries", List.of("price=(20..50)")));
    filterFacetsMap.put("color", Map.of("queries", List.of("color=brown", "color=white")));

    Map<String, Object> structMap = getDefaultStructMap();
    structMap.put(STRUCT_KEY_PRODUCTLIST_FILTER_FACET_QUERIES, filterFacetsMap);
    when(struct.toNestedMaps()).thenReturn(structMap);

    when(struct.toNestedMaps()).thenReturn(structMap);

    assertThat(productListAdapter.getFacets()).contains("price=(20..50)", "color=brown", "color=white");
  }

  @Test
  void useProductOffset() {
    List<Map<String, Object>> fixedItems = new ArrayList<>();
    fixedItems.add(getFixedItemMap(fixedTarget1, 1));
    fixedItems.add(getFixedItemMap(fixedTarget2, 3));
    fixedItems.add(getFixedItemMap(fixedTarget3, 5));
    List<CommerceRef> otherProductSearchResult = Stream.of(dynamicTarget2, dynamicTarget3, dynamicTarget4).collect(Collectors.toList());
    when(commerceSearchFacade.searchProducts(eq(ProductListAdapter.ALL_QUERY), anyMap(), eq(site))).thenReturn(otherProductSearchResult);

    Map<String, Object> structMap = getDefaultStructMap();
    structMap.put(STRUCT_KEY_PRODUCTLIST_OFFSET, 2);
    structMap.put(STRUCT_KEY_PRODUCTLIST_MAX_LENGTH, 5);
    when(struct.toNestedMaps()).thenReturn(structMap);

    when(extendedLinkListAdapterFactory.to(productList)).thenReturn(extendedLinkListAdapter);
    when(extendedLinkListAdapter.getExtendedTargets()).thenReturn(fixedItems);

    Map<String, String> searchParams = getSearchParamsMap();
    //searchParams.put(CatalogService.SEARCH_PARAM_OFFSET, String.valueOf(2));

    List items = productListAdapter.getItems().getData();

    assertThat(items).isNotEmpty();
    assertThat(items.size()).isEqualTo(5);
    assertThat(fixedTarget1).isEqualTo(items.get(0));
    assertThat(dynamicTarget2).isEqualTo(items.get(1));
    assertThat(fixedTarget2).isEqualTo(items.get(2));
    assertThat(dynamicTarget3).isEqualTo(items.get(3));
    assertThat(fixedTarget3).isEqualTo(items.get(4));
  }

  @Test
  void useTotalOffset() {
    productListAdapter = new ProductListAdapter(extendedLinkListAdapterFactory, productList, settingsService, commerceEntityHelper, commerceSearchFacade, site, 2);
    List<Map<String, Object>> fixedItems = new ArrayList<>();
    fixedItems.add(getFixedItemMap(fixedTarget1, 1));
    fixedItems.add(getFixedItemMap(fixedTarget2, 3));
    fixedItems.add(getFixedItemMap(fixedTarget3, 5));

    Map<String, Object> productListSettings = getDefaultStructMap();
    productListSettings.put(STRUCT_KEY_PRODUCTLIST_OFFSET, "1");
    productListSettings.put(STRUCT_KEY_PRODUCTLIST_MAX_LENGTH, "5");
    when(struct.toNestedMaps()).thenReturn(productListSettings);

    when(extendedLinkListAdapterFactory.to(productList)).thenReturn(extendedLinkListAdapter);
    when(extendedLinkListAdapter.getExtendedTargets()).thenReturn(fixedItems);

    List items = productListAdapter.getItems().getData();

    assertThat(items).isNotEmpty();
    assertThat(items.size()).isEqualTo(5);
    assertThat(fixedTarget2).isEqualTo(items.get(0));
    assertThat(dynamicTarget1).isEqualTo(items.get(1));
    assertThat(fixedTarget3).isEqualTo(items.get(2));
    assertThat(dynamicTarget2).isEqualTo(items.get(3));
    assertThat(dynamicTarget3).isEqualTo(items.get(4));
  }

  private Map<String, Object> getDefaultStructMap() {
    return new HashMap(PRODUCT_LIST_STRUCT_DEFAULTS);
  }

  private static Map<String, String> getSearchParamsMap() {
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(SEARCH_PARAM_CATALOG_ALIAS, "catalog");
    searchParams.put(SEARCH_PARAM_FACET_SUPPORT, "true");
    searchParams.put(SEARCH_PARAM_TOTAL, "10");
    searchParams.put(SEARCH_PARAM_ORDERBY, "priceDesc");
    return searchParams;
  }
}
