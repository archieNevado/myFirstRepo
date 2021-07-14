package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.base.caas.model.adapter.ByPathAdapter;
import com.coremedia.blueprint.base.caas.model.adapter.ByPathAdapterFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.CommerceSettingsHelper;
import com.coremedia.blueprint.caas.augmentation.tree.ExternalBreadcrumbTreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import graphql.execution.DataFetcherResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Arrays;
import java.util.List;

import static java.util.Locale.US;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AugmentationFacadeCmsOnlyTest {

  public static final String STORE_ID = "myStoreId";
  public static final String SITE_ID = "mySiteId";
  public static final String ROOT_SEGMENT = "mySiteSegment";

  public static final String EXTERNAL_PRODUCT_ID = "myExternalProductId";
  private static final String PRODUCT_ID = "vendor:///catalog/product/" + EXTERNAL_PRODUCT_ID;

  private static final String[] BREADCRUMB = {"externalCategoryRoot", "externalCategory1rst", "externalCategory2nd"};

  public static final String EXTERNAL_CATEGORY_ID = "myExternalCategoryId";
  private static final String CATEGORY_ID = "vendor:///catalog/category/" + EXTERNAL_CATEGORY_ID;
  public static final CommerceId CATEGORY_COMMERCE_ID = CommerceIdParserHelper.parseCommerceId(CATEGORY_ID).orElseThrow();
  public static final CatalogId CATALOG = CatalogId.of("catalogId");

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private SitesService sitesService;

  @Mock
  private Site aSite;

  @Mock
  private Content homepage;

  @Mock
  private Content augmentingContent;

  @Mock
  private AugmentationService categoryAugmentationService;

  @Mock
  private AugmentationService productAugmentationService;

  @Mock
  private CommerceSettingsHelper commerceSettingsHelper;

  @Mock
  private ByPathAdapterFactory byPathAdapterFactory;

  @Mock
  private ByPathAdapter byPathAdapter;

  @Mock
  private ObjectProvider<ExternalBreadcrumbTreeRelation> externalBreadcrumbTreeRelationProvider;
  private ExternalBreadcrumbTreeRelation externalBreadcrumbTreeRelation = new ExternalBreadcrumbTreeRelation(Arrays.asList(BREADCRUMB));

  @Mock
  private ObjectProvider<AugmentationContext> augmentationContextProvider;
  private AugmentationContext augmentationContext = new AugmentationContext();

  private AugmentationFacadeCmsOnly testling;

  @BeforeEach
  void setUp() {
    when(sitesService.getSite(SITE_ID)).thenReturn(aSite);
    when(byPathAdapterFactory.to()).thenReturn(byPathAdapter);
    when(sitesService.getContentSiteAspect(homepage).getSite()).thenReturn(aSite);

    lenient().when(augmentationContextProvider.getObject()).thenReturn(augmentationContext);
    lenient().when(externalBreadcrumbTreeRelationProvider.getObject()).thenReturn(externalBreadcrumbTreeRelation);

    testling = new AugmentationFacadeCmsOnly(categoryAugmentationService, productAugmentationService, sitesService,
            externalBreadcrumbTreeRelationProvider, commerceSettingsHelper, byPathAdapterFactory, augmentationContextProvider);
  }

  private void initCommerceSettingsHelper() {
    when(commerceSettingsHelper.getCatalogId(aSite)).thenReturn(CATALOG.value());
    when(commerceSettingsHelper.getStoreId(aSite)).thenReturn(STORE_ID);
    lenient().when(commerceSettingsHelper.getVendor(aSite)).thenReturn("vendor");
    when(commerceSettingsHelper.getLocale(aSite)).thenReturn(US);
  }

  @Test
  void getProductAugmentationBySite() {
    initCommerceSettingsHelper();
    when(productAugmentationService.getContentByExternalId(PRODUCT_ID, aSite)).thenReturn(augmentingContent);

    DataFetcherResult<ProductAugmentationCmsOnly> productAugmentationBySite =
            testling.getProductAugmentationBySite(EXTERNAL_PRODUCT_ID, BREADCRUMB, null, SITE_ID);

    assertThat(productAugmentationBySite).isNotNull();
    assertThat(productAugmentationBySite.getErrors()).isEmpty();
    assertThat(productAugmentationBySite.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void getProductAugmentationBySegment() {
    initCommerceSettingsHelper();
    when(byPathAdapter.getPageByPath("", ROOT_SEGMENT)).thenReturn(homepage);
    when(productAugmentationService.getContentByExternalId(PRODUCT_ID, aSite)).thenReturn(augmentingContent);

    DataFetcherResult<ProductAugmentationCmsOnly> productAugmentationBySite =
            testling.getProductAugmentationBySegment(EXTERNAL_PRODUCT_ID, BREADCRUMB, null, ROOT_SEGMENT);

    assertThat(productAugmentationBySite).isNotNull();
    assertThat(productAugmentationBySite.getErrors()).isEmpty();
    assertThat(productAugmentationBySite.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void categoryAugmentationBySite() {
    initCommerceSettingsHelper();
    when(categoryAugmentationService.getContentByExternalId(CATEGORY_ID, aSite)).thenReturn(augmentingContent);

    DataFetcherResult<CategoryAugmentationCmsOnly> categoryAugmentationBySite =
            testling.getCategoryAugmentationBySite(EXTERNAL_CATEGORY_ID, BREADCRUMB, null, SITE_ID);

    assertThat(categoryAugmentationBySite).isNotNull();
    assertThat(categoryAugmentationBySite.getErrors()).isEmpty();
    assertThat(categoryAugmentationBySite.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void getCategoryAugmentationBySegment() {
    initCommerceSettingsHelper();
    when(byPathAdapter.getPageByPath("", ROOT_SEGMENT)).thenReturn(homepage);
    when(categoryAugmentationService.getContentByExternalId(CATEGORY_ID, aSite)).thenReturn(augmentingContent);

    DataFetcherResult<CategoryAugmentationCmsOnly> categoryAugmentationBySegment =
            testling.getCategoryAugmentationBySegment(EXTERNAL_CATEGORY_ID, BREADCRUMB, null, ROOT_SEGMENT);

    assertThat(categoryAugmentationBySegment).isNotNull();
    assertThat(categoryAugmentationBySegment.getErrors()).isEmpty();
    assertThat(categoryAugmentationBySegment.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void initializeBreadcrumbTreeRelation() {
    testling.initializeBreadcrumbTreeRelation(new String[]{"a", "b", "c"}, Vendor.of("vendor"));
    List<String> breadcrumb = externalBreadcrumbTreeRelation.getBreadcrumb();
    assertThat(breadcrumb).hasSize(4);
    assertThat(breadcrumb).first().isEqualTo("vendor:///catalog/category/root");
    assertThat(breadcrumb).allMatch(s -> s.startsWith("vendor:///catalog/category/"));
  }

  @Test
  void getCommerceRef() {
    initCommerceSettingsHelper();
    CommerceRef commerceRef = testling.getCommerceRef(CATEGORY_COMMERCE_ID, List.of(BREADCRUMB), null, aSite);

    assertThat(commerceRef)
            .returns(EXTERNAL_CATEGORY_ID, CommerceRef::getExternalId)
            .returns(aSite.getId(), CommerceRef::getSiteId)
            .returns(US.toLanguageTag(), CommerceRef::getLocale)
            .returns(CATALOG.value(), CommerceRef::getCatalogId)
            .returns(STORE_ID, CommerceRef::getStoreId)
            .returns(BaseCommerceBeanType.CATEGORY, CommerceRef::getType)
            .returns("catalog", CommerceRef::getCatalogAlias);
    assertThat(commerceRef.getBreadcrumb()).containsExactly(BREADCRUMB);
  }
}
