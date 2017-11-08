package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class CatalogServiceImplWithCachingIT extends IbmServiceTestBase {

  //Rest response values
  private static final String PRODUCT_CODE = "GFR033_3303";
  private static final String PRODUCT_SEO = "mangoes";
  private static final String SKU_CODE = "GFR033_330301";
  private static final String CATEGORY_CODE = "Fruit";
  private static final String CATEGORY_SEO = "medicine";

  @Inject
  WcRestConnector wcRestConnector;
  @Inject
  WcCatalogWrapperService catalogWrapperService;
  @Inject
  CatalogServiceImpl testling;

  @Inject
  private IbmCommerceIdProvider ibmCommerceIdProvider;

  @Before
  @Override
  public void setup() {
    super.setup();
    testling.getCommerceCache().setEnabled(true);
    commerceCache.setEnabled(true);
  }

  @Betamax(tape = "csiwc_testProductCachingIsActive", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductCachingIsActive() throws Exception {
    testling.getCommerceCache().clear();
    Product product1 = getTestProduct();
    assertEquals(PRODUCT_CODE, product1.getExternalId());
    Product product2 = getTestProduct();
    assertEquals(PRODUCT_CODE, product2.getExternalId());
    assertEquals("product beans must be equal", product1, product2);
    assertNotSame("product beans must not be the same instance", product1, product2);

    Map<String, Object> productWrapper1 = (Map<String, Object>) getNotAccessibleMethodValue(product1, "getDelegate");
    Map<String, Object> productWrapper2 = (Map<String, Object>) getNotAccessibleMethodValue(product2, "getDelegate");
    assertSame("product delegates should be equal because of they are cached", productWrapper1, productWrapper2);
  }

  /**
   * Attention: This test runs to long hence it is not intended to run in the betamax mode
   * (that is used form the master pipeline). We should only run it when we test against the
   * real backend system (because it takes longer anyway).
   * To achieve this we test if the "betamax.ignoreHost" Java property is set to "*".
   */
  @Test
  public void testProductCacheEntryIsCorrectlyTimedOut() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    CommerceCache commerceCache = testling.getCommerceCache();
    commerceCache.clear();
    //reduce cache duration of products for testing
    Map<String, Long> origCacheTimesInSeconds = commerceCache.getCacheTimesInSeconds();
    Map<String, Long> tempCacheTimesInSeconds = new HashMap<>(origCacheTimesInSeconds);
    tempCacheTimesInSeconds.put("Product", 3L);
    commerceCache.setCacheTimesInSeconds(tempCacheTimesInSeconds);
    try {
      Product product1 = getTestProduct();
      assertEquals(PRODUCT_CODE, product1.getExternalId());
      Map<String, Object> productWrapper1 = (Map<String, Object>) getNotAccessibleMethodValue(product1, "getDelegate");
      // we assume a cache duration time of 30 seconds or lesser
      sleepForSeconds(5);
      Product product2 = getTestProduct();
      Map<String, Object> productWrapper2 = (Map<String, Object>) getNotAccessibleMethodValue(product2, "getDelegate");
      assertEquals(PRODUCT_CODE, product2.getExternalId());
      assertNotSame("the first cache entry should be timed out in the meantime", productWrapper1, productWrapper2);
    }finally {
      commerceCache.setCacheTimesInSeconds(origCacheTimesInSeconds);
    }
  }

  @Betamax(tape = "csiwc_testProductIsCached1", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductIsCached1() throws Exception {
    testling.getCommerceCache().clear();
    accessProductByExternalId();
    WcRestConnector instrumentedConnector = instrumentConnector();
    accessProductByExternalId();
    verifyConnectorIsNotCalled(instrumentedConnector);
  }

  @Betamax(tape = "csiwc_testProductIsCached2", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductIsCached2() throws Exception {
    testling.getCommerceCache().clear();
    accessProductByExternalTechId();
    WcRestConnector instrumentedConnector = instrumentConnector();
    accessProductByExternalTechId();
    verifyConnectorIsNotCalled(instrumentedConnector);
  }

  @Betamax(tape = "csiwc_testProductIsCached3", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductIsCached3() throws Exception {
    testling.getCommerceCache().clear();
    accessProductBySeoSegment();
    WcRestConnector instrumentedConnector = instrumentConnector();
    accessProductBySeoSegment();
    verifyConnectorIsNotCalled(instrumentedConnector);
  }

  @Betamax(tape = "csiwc_testProductVariantIsCached1", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductVariantIsCached1() throws Exception {
    testling.getCommerceCache().clear();
    accessProductVariantByExternalId();
    WcRestConnector instrumentedConnector = instrumentConnector();
    accessProductVariantByExternalId();
    verifyConnectorIsNotCalled(instrumentedConnector);
  }

  @Betamax(tape = "csiwc_testProductVariantIsCached2", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductVariantIsCached2() throws Exception {
    testling.getCommerceCache().clear();
    accessProductVariantByExternalTechId();
    WcRestConnector instrumentedConnector = instrumentConnector();
    accessProductVariantByExternalTechId();
    verifyConnectorIsNotCalled(instrumentedConnector);
  }

  @Betamax(tape = "csiwc_testCategoryIsCached1", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testCategoryIsCached1() throws Exception {
    testling.getCommerceCache().clear();
    accessCategoryByExternalId();
    WcRestConnector instrumentedConnector = instrumentConnector();
    accessCategoryByExternalId();
    verifyConnectorIsNotCalled(instrumentedConnector);
  }

  @Betamax(tape = "csiwc_testCategoryIsCached2", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testCategoryIsCached2() throws Exception {
    testling.getCommerceCache().clear();
    accessCategoryByExternalTechId();
    WcRestConnector instrumentedConnector = instrumentConnector();
    accessCategoryByExternalTechId();
    verifyConnectorIsNotCalled(instrumentedConnector);
  }

  @Betamax(tape = "csiwc_testCategoryIsCached3", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testCategoryIsCached3() throws Exception {
    testling.getCommerceCache().clear();
    accessCategoryBySeoSegment();
    WcRestConnector instrumentedConnector = instrumentConnector();
    accessCategoryBySeoSegment();
    verifyConnectorIsNotCalled(instrumentedConnector);
  }

  private void verifyConnectorIsNotCalled(WcRestConnector connector) {
    verify(connector, times(0)).callServiceInternal(
            any(WcRestServiceMethod.class),
            any(List.class),
            any(Map.class),
            any(Object.class),
            any(StoreContext.class),
            any(UserContext.class)
    );
  }

  private void accessProductByExternalId() throws Exception {
    Product product = getTestProduct();
    accessProduct(product);
  }

  private Product getTestProduct() {
    return testling.findProductById(IbmCommerceIdProvider.commerceId(PRODUCT).withExternalId(PRODUCT_CODE).build(), getStoreContext());
  }

  private void accessProductByExternalTechId() throws Exception {
    Product product = getTestProduct();
    String techId = product.getExternalTechId();
    Product product2 = testling.findProductByExternalTechId(techId);
    accessProduct(product2);
  }

  private void accessProductBySeoSegment() throws Exception {
    Product product = testling.findProductBySeoSegment(PRODUCT_SEO, getStoreContext());
    accessProduct(product);
  }

  private void accessProductVariantByExternalId() throws Exception {
    CommerceId productVariantId = ibmCommerceIdProvider.formatProductVariantId(getStoreContext().getCatalogAlias(), SKU_CODE);
    ProductVariant sku = testling.findProductVariantById(productVariantId, getStoreContext());
    accessProductVariant(sku);
  }

  private void accessProductVariantByExternalTechId() throws Exception {
    StoreContext storeContext = getStoreContext();
    CommerceId productVariantId = ibmCommerceIdProvider.formatProductVariantId(storeContext.getCatalogAlias(), SKU_CODE);
    ProductVariant sku = testling.findProductVariantById(productVariantId, getStoreContext());
    String techId = sku.getExternalTechId();
    CommerceId productVariantTechId = ibmCommerceIdProvider.formatProductVariantTechId(storeContext.getCatalogAlias(), techId);
    ProductVariant sku2 = testling.findProductVariantById(productVariantTechId, getStoreContext());
    accessProductVariant(sku2);
  }

  private void accessProduct(Product product) throws Exception {
    product.getName();
    Category category = product.getCategory();
    category.getName();
    List<ProductVariant> variants = product.getVariants();
    for (ProductVariant variant : variants) {
      variant.getName();
//      variant.getOfferPrice();
      variant.getParent().getName();
    }
//    product.getOfferPrice();
  }

  private void accessProductVariant(ProductVariant sku) throws Exception {
    sku.getName();
    Category category = sku.getCategory();
    category.getName();
    List<ProductVariant> variants = sku.getVariants();
    for (ProductVariant variant : variants) {
      variant.getName();
//      variant.getOfferPrice();
      variant.getParent().getName();
    }
//    sku.getOfferPrice();
  }

  private void accessCategoryByExternalId() throws Exception {
    StoreContext storeContext = getStoreContext();
    CommerceId categoryId = ibmCommerceIdProvider.formatCategoryId(storeContext.getCatalogAlias(), CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, storeContext);
    accessCategory(category);
  }

  private void accessCategoryByExternalTechId() throws Exception {
    StoreContext storeContext = getStoreContext();
    CommerceId categoryId = ibmCommerceIdProvider.formatCategoryId(storeContext.getCatalogAlias(), CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, storeContext);
    String techId = category.getExternalTechId();
    CommerceId categoryTechId = ibmCommerceIdProvider.formatCategoryTechId(storeContext.getCatalogAlias(), techId);
    Category category2 = testling.findCategoryById(categoryTechId, storeContext);
    accessCategory(category2);
  }

  private void accessCategoryBySeoSegment() throws Exception {
    Category category = testling.findCategoryBySeoSegment(CATEGORY_SEO, getStoreContext());
    accessCategory(category);
  }

  private void accessCategory(Category category) throws Exception {
    category.getName();
    category.getBreadcrumb();
    List<Product> products = testling.findProductsByCategory(category);
    for (Product product : products) {
      product.getName();
    }
  }

  private WcRestConnector instrumentConnector() {
    WcRestConnector connector = spy(wcRestConnector);
    catalogWrapperService.setRestConnector(connector);
    return connector;
  }

  private Object getNotAccessibleMethodValue(Object object, String methodName) {
    try {
      Method method = object.getClass().getDeclaredMethod(methodName, new Class[]{});
      method.setAccessible(true);
      return method.invoke(object, new Object[]{});
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private void sleepForSeconds(int seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}
