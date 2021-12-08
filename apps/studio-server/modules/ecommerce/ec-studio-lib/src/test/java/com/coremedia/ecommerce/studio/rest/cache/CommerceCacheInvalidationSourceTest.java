package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCacheInvalidationEvent;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
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
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.controller.EntityController;
import com.coremedia.rest.invalidations.InvalidationSource;
import com.coremedia.rest.linking.EntityControllerMappingImpl;
import com.coremedia.rest.linking.EntityLinker;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.TaskScheduler;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATALOG;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.MARKETING_SPOT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SEGMENT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommerceCacheInvalidationSourceTest {
  @Mock
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Mock
  private SitesService sitesService;

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  private ObjectProvider<TaskScheduler> taskScheduler;

  private CommerceCacheInvalidationSource testling;

  @BeforeEach
  void setUp() {
    SettingsServiceImpl settingsService = new SettingsServiceImpl();
    settingsService.addSettingsFinder(Map.class, new MapEntrySettingsFinder());

    // prepare the basic Studio REST linking infrastructure beans
    List<EntityController<?>> resourceClasses = List.of(
            new CatalogResource(catalogAliasTranslationService),
            new CategoryResource(catalogAliasTranslationService),
            new MarketingSpotResource(catalogAliasTranslationService),
            new MarketingResource(catalogAliasTranslationService),
            new ProductResource(catalogAliasTranslationService),
            new ProductVariantResource(catalogAliasTranslationService),
            new SegmentResource(catalogAliasTranslationService),
            new SegmentsResource(catalogAliasTranslationService, sitesService, settingsService));
    var entityControllerMapping = EntityControllerMappingImpl.create(resourceClasses);

    var linker = new EntityLinker(entityControllerMapping);
    var commerceBeanClassResolver = new CommerceBeanClassResolver();

    testling = new CommerceCacheInvalidationSource(taskScheduler, linker, settingsService, commerceBeanClassResolver);
    testling.setCapacity(10);
    testling.afterPropertiesSet();
  }

  @SuppressWarnings({"DuplicateStringLiteralInspection", "unused"})
  static Stream<Arguments> testInvalidate() {
    return Stream.of(
            createTestInvalidateArgs("mySite", null, "test:///catalog/category/42",
                    List.of("livecontext/category/mySite/{catalogAlias:.*}/42")),
            createTestInvalidateArgs("mySite", null, "test:///catalog/product/42",
                    List.of("livecontext/product/mySite/{catalogAlias:.*}/42",
                            "livecontext/sku/mySite/{catalogAlias:.*}/42")),
            createTestInvalidateArgs("mySite", null, "test:///catalog/sku/42",
                    List.of("livecontext/sku/mySite/{catalogAlias:.*}/42")),
            createTestInvalidateArgs("mySite", CATEGORY, null,
                    List.of("livecontext/category/mySite/{catalogAlias:.*}/{id:.*}")),
            createTestInvalidateArgs("site", PRODUCT, "ibm:///catalog/product/12345",
                    List.of("livecontext/product/site/{catalogAlias:.*}/12345",
                            "livecontext/sku/site/{catalogAlias:.*}/12345")),
            createTestInvalidateArgs("site", PRODUCT, "ibm:///catalog/product/67890",
                    List.of("livecontext/product/site/{catalogAlias:.*}/67890",
                            "livecontext/sku/site/{catalogAlias:.*}/67890")),
            createTestInvalidateArgs("mySite", PRODUCT, null,
                    List.of("livecontext/sku/mySite/{catalogAlias:.*}/{id:.*}",
                            "livecontext/product/mySite/{catalogAlias:.*}/{id:.*}")),
            createTestInvalidateArgs("mySite", SKU, null,
                    List.of("livecontext/sku/mySite/{catalogAlias:.*}/{id:.*}")),
            createTestInvalidateArgs("mySite", CATALOG, null,
                    List.of("livecontext/catalog/mySite/{id:.*}")),
            createTestInvalidateArgs("site", MARKETING_SPOT, "ibm:///catalog/marketingspot/abcde",
                    List.of("livecontext/marketingspot/site/abcde")),
            createTestInvalidateArgs("mySite", MARKETING_SPOT, null,
                    List.of("livecontext/marketingspot/mySite/{id:.*}")),
            createTestInvalidateArgs("site", SEGMENT, "ibm:///catalog/segment/fghi",
                    List.of("livecontext/segment/site/fghi")),
            createTestInvalidateArgs("mySite", SEGMENT, null,
                    List.of("livecontext/segment/mySite/{id:.*}")),
            createTestInvalidateArgs("mySite", null, null,
                    List.of("livecontext/{type:.*}/mySite/{id:.*}",
                            "livecontext/{type:.*}/mySite/{catalogAlias:.*}/{id:.*}",
                            "livecontext/{type:.*}/mySite"))
    );
  }

  @ParameterizedTest
  @MethodSource
  void testInvalidate(String siteId,
                      @Nullable CommerceBeanType commerceBeanType,
                      @Nullable String commerceIdString,
                      Iterable<String> expectedInvalidations) throws InterruptedException {
    StoreContext storeContext = mock(StoreContext.class);
    when(storeContext.getSiteId()).thenReturn(siteId);

    var beanType = commerceBeanType;
    String externalId = null;
    if (commerceIdString != null) {
      var commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(commerceIdString);
      beanType = commerceId.getCommerceBeanType();
      externalId = commerceId.getExternalId().get();
    }

    CommerceCacheInvalidationEvent commerceCacheInvalidationEvent =
            new CommerceCacheInvalidationEvent(storeContext, "insignificant", beanType, externalId);

    testling.invalidate(commerceCacheInvalidationEvent);

    InvalidationSource.Invalidations invalidations = testling.getInvalidations("0");

    assertThat(invalidations.getInvalidations()).containsExactlyInAnyOrderElementsOf(expectedInvalidations);
  }

  @Test
  void toCommerceBeanUriWithPartnumber() {
    assertThat(testling.toCommerceBeanUri(BaseCommerceBeanType.CATEGORY, "partNumber", null))
            .contains("livecontext/category/{siteId:.*}/{catalogAlias:.*}/partNumber");
  }

  @Test
  void toCommerceBeanUriWithTechId() {
    assertThat(testling.toCommerceBeanUri(BaseCommerceBeanType.CATEGORY, null, null))
            .contains("livecontext/category/{siteId:.*}/{catalogAlias:.*}/{id:.*}");
  }

  @SuppressWarnings("SameParameterValue")
  private static Arguments createTestInvalidateArgs(String siteId,
                                                    @Nullable CommerceBeanType commerceBeanType,
                                                    @Nullable String qualifiedCommerceId,
                                                    List<String> expectedInvalidations) {
    return Arguments.of(siteId, commerceBeanType, qualifiedCommerceId, expectedInvalidations);
  }
}
