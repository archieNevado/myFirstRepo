package com.coremedia.livecontext.product;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListSubstitutionServiceTest {

  private ProductListSubstitutionService testling;

  @Mock
  private CommerceConnection connection;

  private List<Product> listOfTablets;

  @Mock
  private Page page;

  @Mock
  private Site site;

  @Mock
  private Navigation noLiveContextNavigation;

  @Mock
  private LiveContextNavigation liveContextNavigation;

  @Mock
  private Category tablets;

  @Mock
  private Product product1, product2, product3, product4;

  @Mock
  private CatalogService catalogService;

  @Before
  public void defaultSetup() {
    StoreContextImpl storeContext = StoreContextBuilderImpl.from(connection, "any-site-id").build();
    when(tablets.getContext()).thenReturn(storeContext);

    testling = new ProductListSubstitutionService();
    testling.setLiveContextNavigationFactory(new LiveContextNavigationFactory());

    listOfTablets = List.of(product1, product2, product3, product4);

    when(connection.getCatalogService()).thenReturn(catalogService);

    when(page.getNavigation()).thenReturn(liveContextNavigation);
    when(liveContextNavigation.getCategory()).thenReturn(tablets);
    when(liveContextNavigation.getChildren()).thenReturn(Collections.emptyList());
    when(liveContextNavigation.getSite()).thenReturn(site);
    when(connection.getCatalogService().findProductsByCategory(tablets)).thenReturn(listOfTablets);
  }

  @Test
  public void getProductListPagesNavigationIsNoNoLiveContextNavigation() {
    when(page.getNavigation()).thenReturn(noLiveContextNavigation);

    ProductList result = testling.getProductList(page);

    assertNull(result);
    verify(page, times(1)).getNavigation();
  }

  @Test
  public void getProductListNoLiveContextNavigation() {
    ProductList result = testling.getProductList(null, 0, 10);
    assertNull(result);
  }

  @Test
  public void getProductListNoCategoriesReceivedFromBackend() {
    when(connection.getCatalogService().findProductsByCategory(tablets)).thenReturn(Collections.<Product>emptyList());

    ProductList pagedCategory = testling.getProductList(liveContextNavigation, 0, 10);
    assertTrue(pagedCategory.getTotalProductCount() == 0);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeException.class)
  public void getProductListCatalogServiceThrowsException() {
    when(connection.getCatalogService().findProductsByCategory(tablets)).thenThrow(CommerceException.class);

    testling.getProductList(liveContextNavigation, 0, 10);
  }

  @Test
  public void getProductListTooMuchProductsForPage() {
    int start = 0;
    int steps = listOfTablets.size() - 2;
    ProductList result = testling.getProductList(liveContextNavigation, start, steps);

    assertProductList(result, listOfTablets.subList(start, steps), listOfTablets.size(), start, steps);
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void getProductListListOfProductsFitsIntoOnePage() {
    ProductList result = testling.getProductList(liveContextNavigation, listOfTablets.size() + 5, 10);
    List<ProductInSite> loadedProducts = result.getLoadedProducts();

    assertTrue(loadedProducts.isEmpty());
  }

  @Test
  public void getProductListListOfProductsFitsExactlyIntoOnePage() {
    int start = listOfTablets.size();
    int steps = 10;
    ProductList result = testling.getProductList(liveContextNavigation, listOfTablets.size(), steps);

    assertProductList(result, Collections.emptyList(), listOfTablets.size(), start, steps);
  }

  @Test
  public void getProductListNoPageProvided() {
    ProductList result = testling.getProductList(null);
    assertNull(result);
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void getProductListSuccessfully() {
    ProductList result = testling.getProductList(page);
    checkListItems(listOfTablets, result);
  }

  private void assertProductList(ProductList result, List<Product> expectedProducts, int expectedTotalProductCount, int expectedStart, int expectedSteps) {
    checkListItems(expectedProducts, result);
    assertEquals(liveContextNavigation, result.getNavigation());
    assertEquals(tablets, result.getCategory());
    assertEquals(expectedStart, result.getStart());
    assertEquals(expectedSteps, result.getSteps());
    assertTrue(result.isProductCategory());
    assertEquals(expectedTotalProductCount, result.getTotalProductCount());
    assertFalse(result.hasCategories());
  }

  private void checkListItems(List<Product> expectedProducts, ProductList result) {
    List<ProductInSite> loadedProducts = result.getLoadedProducts();
    assertEquals("wrong size", expectedProducts.size(), loadedProducts.size());

    for (int i = 0; i < expectedProducts.size(); ++i) {
      ProductInSite productInSite = loadedProducts.get(i);
      assertEquals("wrong product at " + i, expectedProducts.get(i), productInSite.getProduct());
    }
  }
}
