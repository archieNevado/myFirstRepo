package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.blueprint.caas.augmentation.model.CommerceRefFactory;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static java.util.Locale.US;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommerceEntityHelperTest {

  private static final String SITE_ID = "mySiteID";
  public static final CatalogAlias CATALOG_ALIAS = CatalogAlias.of("catalog");

  @Mock
  private SitesService sitesService;

  @Mock
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Mock
  private Site aSite;

  @Mock
  private CommerceConnection aConnection;

  @Mock
  private StoreContext aStoreContext;

  @Mock
  private CommerceBeanFactory commerceBeanFactory;

  @Mock
  private CommerceIdProvider commerceIdProvider;

  @InjectMocks
  private CommerceEntityHelper testling;

  @BeforeEach
  void init() {
    when(sitesService.getSite(SITE_ID)).thenReturn(aSite);
    when(aSite.getId()).thenReturn(SITE_ID);
    when(aSite.getLocale()).thenReturn(US);
    when(commerceConnectionSupplier.findConnection(aSite)).thenReturn(Optional.of(aConnection));
    when(aConnection.getCommerceBeanFactory()).thenReturn(commerceBeanFactory);
    when(aConnection.getInitialStoreContext()).thenReturn(aStoreContext);
    when(aConnection.getIdProvider()).thenReturn(commerceIdProvider);
    when(aStoreContext.getCatalogAlias()).thenReturn(CATALOG_ALIAS);
    when(aStoreContext.getLocale()).thenReturn(US);
    when(aStoreContext.getStoreId()).thenReturn("storeId");
    when(aStoreContext.getSiteId()).thenReturn(SITE_ID);
    when(aStoreContext.getCatalogId()).thenReturn(Optional.of(CatalogId.of("catalogID")));
  }

  @Test
  void createCommerceBean() {
    CommerceId productId = CommerceIdParserHelper.parseCommerceId("acme:///catalog/product/4711").orElseThrow();
    when(commerceBeanFactory.createBeanFor(eq(productId), any(StoreContext.class))).thenReturn(mock(Product.class));

    CommerceId skuId = CommerceIdParserHelper.parseCommerceId("acme:///catalog/sku/4712").orElseThrow();
    when(commerceBeanFactory.createBeanFor(eq(skuId), any(StoreContext.class))).thenReturn(mock(ProductVariant.class));

    Product product = testling.createCommerceBean(productId, SITE_ID, Product.class);
    assertThat(product).isNotNull();

    ProductVariant sku = (ProductVariant) testling.createCommerceBean(skuId, SITE_ID, Product.class);
    assertThat(sku).isNotNull();

    Category category = testling.createCommerceBean(productId, SITE_ID, Category.class);
    assertThat(category).isNull();

    Product productInWrongSite = testling.createCommerceBean(productId, "wrongSiteId", Product.class);
    assertThat(productInWrongSite).isNull();
  }

  @Test
  void getCommerceConnection() {
    assertThat(testling.getCommerceConnection(SITE_ID)).isNotNull();
    assertThat(testling.getCommerceConnection("unkownSiteId")).isNull();
  }

  @Test
  void getCommerceId() {
    String productId = "productId";
    when(commerceIdProvider.formatProductId(eq(CATALOG_ALIAS), eq(productId)))
            .thenReturn(CommerceIdParserHelper.parseCommerceIdOrThrow("acme:///catalog/product/productId"));

    CommerceRef productRef = CommerceRefFactory.from(productId, PRODUCT, aStoreContext);

    CommerceId commerceId = testling.getCommerceId(productRef);

    assertThat(commerceId).satisfies(id -> {
      assertThat(id.getVendor().value()).isEqualTo("acme");
      assertThat(id.getServiceType()).isEqualTo("catalog");
      assertThat(id.getCatalogAlias().value()).isEqualTo("catalog");
      assertThat(id.getCommerceBeanType()).isEqualTo(PRODUCT);
      assertThat(id.getExternalId()).hasValue(productId);
    });
  }

}
