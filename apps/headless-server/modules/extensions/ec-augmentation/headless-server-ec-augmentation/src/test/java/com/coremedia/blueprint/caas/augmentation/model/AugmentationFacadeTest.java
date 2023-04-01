package com.coremedia.blueprint.caas.augmentation.model;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceSiteFinder;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.CommerceConnectionHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import graphql.execution.DataFetcherResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;
import static java.util.Locale.US;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AugmentationFacadeTest {

  public static final String STORE_ID = "myStoreId";
  public static final String SITE_ID = "mySiteId";
  private static final CatalogAlias CATALOG_ALIAS = CatalogAlias.of("catalog");
  public static final String EN_US = "en-US";

  public static final String EXTERNAL_PRODUCT_ID = "myExternalProductId";
  private static final String PRODUCT_ID = "acme:///catalog/product/" + EXTERNAL_PRODUCT_ID;
  public static final CommerceId PRODUCT_COMMERCE_ID = CommerceIdParserHelper.parseCommerceId(PRODUCT_ID).orElseThrow();

  public static final String EXTERNAL_SKU_ID = "myExternalSkuId";
  private static final String SKU_ID = "acme:///catalog/sku/" + EXTERNAL_SKU_ID;
  public static final CommerceId SKU_COMMERCE_ID = CommerceIdParserHelper.parseCommerceId(SKU_ID).orElseThrow();

  public static final String EXTERNAL_CATEGORY_ID = "myExternalCategoryId";
  private static final String CATEGORY_ID = "acme:///catalog/category/" + EXTERNAL_CATEGORY_ID;
  public static final CommerceId CATEGORY_COMMERCE_ID = CommerceIdParserHelper.parseCommerceId(CATEGORY_ID).orElseThrow();
  public static final CatalogId CATALOG = CatalogId.of("catalog");

  @Mock
  private AugmentationService augmentationService;

  @Mock
  private CommerceConnectionHelper commerceConnectionHelper;

  @Mock
  private CommerceSiteFinder commerceSiteFinder;

  @Mock(answer = RETURNS_DEEP_STUBS)
  private SitesService sitesService;

  @Mock
  private Site aSite;

  @Mock
  private CommerceConnection aConnection;

  @Mock
  private StoreContext aStoreContext;

  @Mock
  private Content augmentingContent;

  @Mock
  private CommerceIdProvider commerceIdProvider;

  @Mock
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Mock
  private CommerceBeanFactory commerceBeanFactory;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock(answer = Answers.RETURNS_SELF)
  private StoreContextBuilder storeContextBuilder;

  @InjectMocks
  private AugmentationFacade testling;

  @BeforeEach
  public void init() {
    when(sitesService.getSite(SITE_ID)).thenReturn(aSite);
    when(commerceConnectionHelper.getCommerceConnection(aSite)).thenReturn(aConnection);
    when(aConnection.getInitialStoreContext()).thenReturn(aStoreContext);
    when(aStoreContext.getCatalogAlias()).thenReturn(CATALOG_ALIAS);
    when(aStoreContext.getLocale()).thenReturn(US);
    when(aStoreContext.getStoreId()).thenReturn(STORE_ID);
  }

  @Test
  void getProductAugmentationBySite() {
    when(aConnection.getIdProvider()).thenReturn(commerceIdProvider);
    when(commerceIdProvider.formatProductId(CATALOG_ALIAS, EXTERNAL_PRODUCT_ID))
            .thenReturn(PRODUCT_COMMERCE_ID);
    when(augmentationService.getContentByExternalId(PRODUCT_ID, aSite)).thenReturn(augmentingContent);
    when(commerceConnectionHelper.getCommerceConnection(aSite)).thenReturn(aConnection);

    DataFetcherResult<ProductAugmentation> productAugmentationByStore =
            testling.getProductAugmentationBySite(EXTERNAL_PRODUCT_ID, null, SITE_ID);

    assertThat(productAugmentationByStore).isNotNull();
    assertThat(productAugmentationByStore.getErrors()).isEmpty();
    assertThat(productAugmentationByStore.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void getProductAugmentationBySiteWithNoAugmentingContent() {
    when(aConnection.getIdProvider()).thenReturn(commerceIdProvider);
    when(commerceIdProvider.formatProductId(CATALOG_ALIAS, EXTERNAL_PRODUCT_ID))
            .thenReturn(PRODUCT_COMMERCE_ID);
    when(augmentationService.getContentByExternalId(PRODUCT_ID, aSite)).thenReturn(null);
    when(commerceConnectionHelper.getCommerceConnection(aSite)).thenReturn(aConnection);

    DataFetcherResult<ProductAugmentation> productAugmentationByStore =
            testling.getProductAugmentationBySite(EXTERNAL_PRODUCT_ID, null, SITE_ID);

    assertThat(productAugmentationByStore).isNotNull();
    assertThat(productAugmentationByStore.getErrors()).isEmpty();
    assertThat(productAugmentationByStore.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNull();
    });
  }

  @Test
  void getCategoryAugmentationByStore() {
    when(commerceSiteFinder.findSiteFor(STORE_ID, US)).thenReturn(Optional.of(aSite));
    when(aConnection.getIdProvider()).thenReturn(commerceIdProvider);
    when(commerceIdProvider.formatCategoryId(CATALOG_ALIAS, EXTERNAL_CATEGORY_ID))
            .thenReturn(CATEGORY_COMMERCE_ID);
    when(augmentationService.getContentByExternalId(CATEGORY_ID, aSite)).thenReturn(augmentingContent);
    when(commerceConnectionHelper.getCommerceConnection(aSite)).thenReturn(aConnection);

    DataFetcherResult<CategoryAugmentation> categoryAugmentationByStore =
            testling.getCategoryAugmentationByStore(EXTERNAL_CATEGORY_ID, null, STORE_ID, EN_US);

    assertThat(categoryAugmentationByStore).isNotNull();
    assertThat(categoryAugmentationByStore.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void getProductAugmentationByStore() {
    when(commerceSiteFinder.findSiteFor(STORE_ID, US)).thenReturn(Optional.of(aSite));
    when(aConnection.getIdProvider()).thenReturn(commerceIdProvider);
    when(commerceIdProvider.formatProductId(CATALOG_ALIAS, EXTERNAL_PRODUCT_ID))
            .thenReturn(PRODUCT_COMMERCE_ID);
    when(augmentationService.getContentByExternalId(PRODUCT_ID, aSite)).thenReturn(augmentingContent);
    when(commerceConnectionHelper.getCommerceConnection(aSite)).thenReturn(aConnection);

    DataFetcherResult<ProductAugmentation> productAugmentationByStore =
            testling.getProductAugmentationByStore(EXTERNAL_PRODUCT_ID, null, STORE_ID, EN_US);

    assertThat(productAugmentationByStore).isNotNull();
    assertThat(productAugmentationByStore.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void getAugmentationByStoreWithWrongStoreId() {
    when(commerceSiteFinder.findSiteFor(STORE_ID, US)).thenReturn(Optional.of(aSite));
    when(aConnection.getIdProvider()).thenReturn(commerceIdProvider);
    when(commerceIdProvider.formatCategoryId(CATALOG_ALIAS, EXTERNAL_CATEGORY_ID))
            .thenReturn(CATEGORY_COMMERCE_ID);
    when(augmentationService.getContentByExternalId(CATEGORY_ID, aSite)).thenReturn(augmentingContent);
    when(commerceConnectionHelper.getCommerceConnection(aSite)).thenReturn(aConnection);

    DataFetcherResult<CategoryAugmentation> categoryAugmentationByStore =
            testling.getCategoryAugmentationByStore(EXTERNAL_CATEGORY_ID, null, STORE_ID, EN_US);

    assertThat(categoryAugmentationByStore).isNotNull();
    assertThat(categoryAugmentationByStore.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void getAugmentationForCommerceIdProduct() {
    when(augmentationService.getContentByExternalId(PRODUCT_ID, aSite)).thenReturn(augmentingContent);
    when(aStoreContext.getCatalogId()).thenReturn(Optional.of(CATALOG));
    when(storeContextProvider.buildContext(aStoreContext)).thenReturn(storeContextBuilder);
    when(storeContextBuilder.build()).thenReturn(aStoreContext);
    when(aConnection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(catalogAliasTranslationService.getCatalogIdForAlias(any(CatalogAlias.class), any(StoreContext.class)))
            .thenReturn(Optional.of(CATALOG));

    DataFetcherResult<? extends Augmentation> productAugmentation = testling.getAugmentationBySite(PRODUCT_ID, SITE_ID);

    assertProductAugmentation(productAugmentation, PRODUCT);
  }

  @Test
  void getAugmentationForCommerceIdSku() {
    when(aConnection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(catalogAliasTranslationService.getCatalogIdForAlias(any(CatalogAlias.class), any(StoreContext.class)))
            .thenReturn(Optional.of(CATALOG));
    when(aConnection.getCommerceBeanFactory()).thenReturn(commerceBeanFactory);
    when(aStoreContext.getCatalogId()).thenReturn(Optional.of(CATALOG));
    when(storeContextProvider.buildContext(aStoreContext)).thenReturn(storeContextBuilder);
    when(storeContextBuilder.build()).thenReturn(aStoreContext);

    ProductVariant productVariant = mock(ProductVariant.class);
    when(commerceBeanFactory.createBeanFor(SKU_COMMERCE_ID, aStoreContext))
            .thenReturn(productVariant);

    Product parentProduct = mock(Product.class);
    when(productVariant.getParent()).thenReturn(parentProduct);
    when(parentProduct.getId()).thenReturn(PRODUCT_COMMERCE_ID);

    when(augmentationService.getContentByExternalId(PRODUCT_ID, aSite)).thenReturn(augmentingContent);

    DataFetcherResult<? extends Augmentation> productAugmentation = testling.getAugmentationBySite(SKU_ID, SITE_ID);

    assertProductAugmentation(productAugmentation, SKU);
  }

  private static void assertProductAugmentation(DataFetcherResult<? extends Augmentation> productAugmentation, CommerceBeanType type) {
    assertThat(productAugmentation).isNotNull();
    assertThat(productAugmentation.getData()).isInstanceOf(ProductAugmentation.class);
    assertThat(productAugmentation.getErrors()).isEmpty();
    assertThat(productAugmentation.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull()
              .returns(type, CommerceRef::getType);
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void getAugmentationForCommerceIdCategory() {
    when(aConnection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(catalogAliasTranslationService.getCatalogIdForAlias(any(CatalogAlias.class), any(StoreContext.class)))
            .thenReturn(Optional.of(CATALOG));
    when(aStoreContext.getCatalogId()).thenReturn(Optional.of(CATALOG));
    when(storeContextProvider.buildContext(aStoreContext)).thenReturn(storeContextBuilder);
    when(storeContextBuilder.build()).thenReturn(aStoreContext);
    when(augmentationService.getContentByExternalId(CATEGORY_ID, aSite)).thenReturn(augmentingContent);

    DataFetcherResult<? extends Augmentation> categoryAugmentation = testling.getAugmentationBySite(CATEGORY_ID, SITE_ID);

    assertThat(categoryAugmentation).isNotNull();
    assertThat(categoryAugmentation.getData()).isInstanceOf(CategoryAugmentation.class);
    assertThat(categoryAugmentation.getErrors()).isEmpty();
    assertThat(categoryAugmentation.getData()).satisfies(augmentation -> {
      assertThat(augmentation.getCommerceRef()).isNotNull();
      assertThat(augmentation.getContent()).isNotNull();
    });
  }

  @Test
  void getAugmentationWithMultiCatalog() {
    var catalogAlias = CatalogAlias.of("myCatalogAlias");
    String catalogId = "myCatalogId";

    StoreContext storeContext = mock(StoreContext.class);
    when(aStoreContext.getCatalogAlias()).thenReturn(catalogAlias);
    when(aStoreContext.getLocale()).thenReturn(US);
    when(aStoreContext.getStoreId()).thenReturn(STORE_ID);
    when(aStoreContext.getCatalogId()).thenReturn(Optional.of(CatalogId.of(catalogId)));
    when(storeContextProvider.buildContext(aStoreContext)).thenReturn(storeContextBuilder);
    when(storeContextBuilder.build()).thenReturn(aStoreContext);
    when(aConnection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(catalogAliasTranslationService.getCatalogIdForAlias(any(CatalogAlias.class), any(StoreContext.class)))
            .thenReturn(Optional.of(CATALOG));

    StoreContextBuilder builder = mock(StoreContextBuilder.class, RETURNS_DEEP_STUBS);
    when(builder.build()).thenReturn(storeContext);

    String commerceIdWithCatalogAlias = "acme:///catalog/product/" + "catalog:" + catalogAlias + ";" + EXTERNAL_PRODUCT_ID;

    DataFetcherResult<? extends Augmentation> productAugmentation = testling.getAugmentationBySite(commerceIdWithCatalogAlias, SITE_ID);

    assertThat(productAugmentation).isNotNull();
    assertThat(productAugmentation.getData().getCommerceRef()).satisfies(ref -> {
              assertThat(ref.getCatalogAlias()).isEqualTo(catalogAlias);
              assertThat(ref.getCatalogId()).isEqualTo(catalogId);
            }
    );
  }
}
