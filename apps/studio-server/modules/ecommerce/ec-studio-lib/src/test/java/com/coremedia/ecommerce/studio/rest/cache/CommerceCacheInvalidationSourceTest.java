package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCacheInvalidationEvent;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.settings.impl.MapEntrySettingsFinder;
import com.coremedia.blueprint.base.settings.impl.SettingsServiceImpl;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.studio.rest.CatalogResource;
import com.coremedia.ecommerce.studio.rest.CategoryResource;
import com.coremedia.ecommerce.studio.rest.MarketingResource;
import com.coremedia.ecommerce.studio.rest.MarketingSpotResource;
import com.coremedia.ecommerce.studio.rest.ProductResource;
import com.coremedia.ecommerce.studio.rest.ProductVariantResource;
import com.coremedia.ecommerce.studio.rest.SegmentResource;
import com.coremedia.ecommerce.studio.rest.SegmentsResource;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.coremedia.rest.invalidations.InvalidationSource;
import com.coremedia.rest.linking.EntityResourceLinker;
import com.coremedia.rest.linking.TypeBasedResourceClassFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommerceCacheInvalidationSourceTest {

  @Mock
  private ApplicationContext context;

  @Mock
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Mock
  private SitesService sitesService;

  @Mock
  private SettingsService settingsService;

  private CommerceCacheInvalidationSource testling;

  private static final String CATALOG_PREFIX = "ibm:///catalog/";

  private static final String ID1 = "12345";
  private static final String ID2 = "67890";
  private static final String ID3 = "abcde";
  private static final String ID4 = "fghi";

  @BeforeEach
  void setUp() {
    testling = new CommerceCacheInvalidationSource();
    // prepare the basic Studio REST linking infrastructure beans
    EntityResourceLinker linker = new EntityResourceLinker();
    TypeBasedResourceClassFinder resourceClassFinder = new TypeBasedResourceClassFinder();
    resourceClassFinder.setResourceClasses(newArrayList(
            CatalogResource.class,
            CategoryResource.class,
            MarketingSpotResource.class,
            MarketingResource.class,
            ProductResource.class,
            ProductVariantResource.class,
            SegmentResource.class,
            SegmentsResource.class
    ));
    linker.setResourceClassFinder(resourceClassFinder);

    CatalogResource catalogResource = new CatalogResource(catalogAliasTranslationService);
    when(context.getBean(CatalogResource.class)).thenReturn(catalogResource);

    CategoryResource categoryResource = new CategoryResource(catalogAliasTranslationService);
    when(context.getBean(CategoryResource.class)).thenReturn(categoryResource);

    MarketingSpotResource marketingSpotResource = new MarketingSpotResource(catalogAliasTranslationService);
    when(context.getBean(MarketingSpotResource.class)).thenReturn(marketingSpotResource);

    MarketingResource marketingResource = new MarketingResource(catalogAliasTranslationService);
    when(context.getBean(MarketingResource.class)).thenReturn(marketingResource);

    ProductResource productResource = new ProductResource(catalogAliasTranslationService);
    when(context.getBean(ProductResource.class)).thenReturn(productResource);

    ProductVariantResource productVariantResource = new ProductVariantResource(catalogAliasTranslationService);
    when(context.getBean(ProductVariantResource.class)).thenReturn(productVariantResource);

    SegmentResource segmentResource = new SegmentResource(catalogAliasTranslationService);
    when(context.getBean(SegmentResource.class)).thenReturn(segmentResource);

    SegmentsResource segmentsResource = new SegmentsResource(catalogAliasTranslationService, sitesService, settingsService);
    when(context.getBean(SegmentsResource.class)).thenReturn(segmentsResource);

    linker.setApplicationContext(context);
    testling.setLinker(linker);

    SettingsServiceImpl settingsService = new SettingsServiceImpl();
    settingsService.addSettingsFinder(Map.class, new MapEntrySettingsFinder());
    testling.setSettingsService(settingsService);
    testling.setCapacity(10);
    testling.afterPropertiesSet();
  }

  @SuppressWarnings({"DuplicateStringLiteralInspection", "unused"})
  static Stream<Arguments> testInvalidateCommerceCacheInvalidationEvent() {
    return Stream.of(
            Arguments.of("mySite", "myCatalogAlias", null, "test:///catalog/category/42",
                    List.of("livecontext/category/mySite/{catalogAlias:.*}/{workspaceId:.*}/42")),
            Arguments.of("mySite", "myCatalogAlias", null, "test:///catalog/product/42",
                    List.of("livecontext/product/mySite/{catalogAlias:.*}/{workspaceId:.*}/42",
                            "livecontext/sku/mySite/{catalogAlias:.*}/{workspaceId:.*}/42")),
            Arguments.of("mySite", "myCatalogAlias", null, "test:///catalog/sku/42",
                    List.of("livecontext/sku/mySite/{catalogAlias:.*}/{workspaceId:.*}/42")),
            Arguments.of("mySite", "myCatalogAlias", BaseCommerceBeanType.CATEGORY, null,
                    List.of("livecontext/category/mySite/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}")),
            Arguments.of("mySite", "myCatalogAlias", BaseCommerceBeanType.PRODUCT, null,
                    List.of("livecontext/product/mySite/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}")),
            Arguments.of("mySite", "myCatalogAlias", BaseCommerceBeanType.SKU, null,
                    List.of("livecontext/sku/mySite/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}")),
            Arguments.of("mySite", "myCatalogAlias", BaseCommerceBeanType.CATALOG, null,
                    List.of("livecontext/catalog/mySite/{id:.*}")),
            Arguments.of("mySite", "myCatalogAlias", BaseCommerceBeanType.MARKETING_SPOT, null,
                    List.of("livecontext/marketingspot/mySite/{workspaceId:.*}/{id:.*}")),
            Arguments.of("mySite", "myCatalogAlias", BaseCommerceBeanType.SEGMENT, null,
                    List.of("livecontext/segment/mySite/{workspaceId:.*}/{id:.*}")),
            Arguments.of("mySite", "myCatalogAlias", null, null,
                    List.of("livecontext/{type:.*}/mySite/{id:.*}",
                            "livecontext/{type:.*}/mySite/{workspaceId:.*}/{id:.*}",
                            "livecontext/{type:.*}/mySite/{workspaceId:.*}",
                            "livecontext/facets/mySite/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}",
                            "livecontext/searchfacets/mySite/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}",
                            "livecontext/workspaces/mySite"))
    );
  }

  @ParameterizedTest
  @MethodSource
  void testInvalidateCommerceCacheInvalidationEvent(String siteId,
                                                    String catalogAlias,
                                                    CommerceBeanType commerceBeanType,
                                                    String commerceIdString,
                                                    Iterable<String> expectedInvalidations) throws InterruptedException {
    StoreContext storeContext = mock(StoreContext.class);
    when(storeContext.getSiteId()).thenReturn(siteId);
    when(storeContext.getCatalogAlias()).thenReturn(CatalogAlias.of(catalogAlias));

    CommerceId commerceId = commerceIdString == null ? null :
            CommerceIdParserHelper.parseCommerceIdOrThrow(commerceIdString);
    CommerceCacheInvalidationEvent commerceCacheInvalidationEvent =
            new CommerceCacheInvalidationEvent(storeContext, "insignificant", null, commerceBeanType, commerceId);

    testling.invalidate(commerceCacheInvalidationEvent);

    InvalidationSource.Invalidations invalidations = testling.getInvalidations("0");

    assertThat(invalidations.getInvalidations()).containsExactlyInAnyOrderElementsOf(expectedInvalidations);
  }

  @Test
  void testInvalidate() throws InterruptedException {
    InvalidationEvent event1 = createEvent(CATALOG_PREFIX + "product/" + ID1, "product");
    InvalidationEvent event2 = createEvent(CATALOG_PREFIX + "product/" + ID2, "product");
    InvalidationEvent event3 = createEvent(CATALOG_PREFIX + "marketingspot/" + ID3, "marketingspot");
    InvalidationEvent event4 = createEvent(CATALOG_PREFIX + "segment/" + ID4, "segment");

    List<InvalidationEvent> cacheInvalidations = newArrayList(event1, event2, event3, event4);

    testling.invalidate(cacheInvalidations);

    InvalidationSource.Invalidations invalidations = testling.getInvalidations("0");

    Set<String> expected = newHashSet(
            "livecontext/product/{siteId:.*}/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}",
            "livecontext/marketing/{siteId:.*}/{workspaceId:.*}",
            "livecontext/marketingspot/{siteId:.*}/{workspaceId:.*}/{id:.*}",
            "livecontext/segment/{siteId:.*}/{workspaceId:.*}/{id:.*}",
            "livecontext/segments/{siteId:.*}/{workspaceId:.*}"
    );

    assertThat(invalidations.getInvalidations()).isEqualTo(expected);
  }

  private InvalidationEvent createEvent(String id, String contentType) {
    return new InvalidationEvent(id, contentType, 0L);
  }

  @Test
  void testInvalidateClearAll() throws InterruptedException {
    InvalidationEvent event1 = createEvent(CATALOG_PREFIX + "product/" + ID1, "product");
    InvalidationEvent event2 = createEvent(null, "clearall");

    List<InvalidationEvent> cacheInvalidations = newArrayList(event1, event2);

    testling.invalidate(cacheInvalidations);

    InvalidationSource.Invalidations invalidations = testling.getInvalidations("0");

    Set<String> expected = newHashSet("livecontext/{suffix:.*}");

    assertThat(invalidations.getInvalidations()).isEqualTo(expected);
  }

  @Test
  void testInvalidateInvalidEvents() throws InterruptedException {
    InvalidationEvent event1 = createEvent(CATALOG_PREFIX + "product/" + ID1, "product");
    InvalidationEvent event2 = createEvent("blub", "unknown");
    InvalidationEvent event3 = createEvent(null, null);

    List<InvalidationEvent> cacheInvalidations = newArrayList(event1, event2, event3);

    testling.invalidate(cacheInvalidations);

    InvalidationSource.Invalidations invalidations = testling.getInvalidations("0");

    Set<String> expected = singleton("livecontext/product/{siteId:.*}/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}");

    assertThat(invalidations.getInvalidations()).isEqualTo(expected);
  }

  @Test
  void toCommerceBeanUriWithPartnumber() {
    Optional<CommerceId> commerceIdOptional = CommerceIdParserHelper.parseCommerceId("test:///catalog/category/partNumber");
    assertThat(commerceIdOptional).flatMap(id -> testling.toCommerceBeanUri(id, null))
            .contains("livecontext/category/{siteId:.*}/{catalogAlias:.*}/{workspaceId:.*}/partNumber");
  }

  @Test
  void toCommerceBeanUriWithTechId() {
    Optional<CommerceId> commerceIdOptional = CommerceIdParserHelper.parseCommerceId("test:///catalog/category/techId:42");
    assertThat(commerceIdOptional).flatMap(id -> testling.toCommerceBeanUri(id, null))
            .contains("livecontext/category/{siteId:.*}/{catalogAlias:.*}/{workspaceId:.*}/{id:.*}");
  }
}
