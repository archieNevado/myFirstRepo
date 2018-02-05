package com.coremedia.livecontext.ecommerce.ibm.cae.handler;

import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.cae.WcsUrlProvider;
import com.coremedia.livecontext.ecommerce.ibm.cae.storefront.StoreFrontConfiguration;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.order.CartServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.ecommerce.order.CartService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = {
        IbmServiceTestBase.LocalConfig.class,
        StoreFrontConfiguration.class,
})
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class CartServiceImplIT extends IbmServiceTestBase {

  private static final String SKU_CODE = "CLA022_220301";
  private static final String PASSWORD = "passw0rd";

  @Inject
  private CartServiceImpl testling;

  @Inject
  private CatalogServiceImpl catalogService;
  @Inject
  private UserSessionService userSessionService;

  @Inject
  private IbmCommerceIdProvider ibmCommerceIdProvider;

  @SuppressWarnings("unused")
  @MockBean
  private WcsUrlProvider wcsUrlProvider;

  @Test
  public void testGetCart() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_7)) {
      return;
    }

    prefillCart();

    testLoginUser();

    StoreContext context = StoreContextHelper.getCurrentContext();
    Cart cart = testling.getCart(context);
    assertNotNull(cart);
    assertEquals("1.0", cart.getOrderItems().get(0).getQuantity().toPlainString());

    //test update cart
    Cart.OrderItem orderItem = cart.getOrderItems().get(0);
    List<CartService.OrderItemParam> updateParams = new ArrayList<>();
    updateParams.add(new CartService.OrderItemParam(
            orderItem.getExternalId(),
            orderItem.getQuantity().add(BigDecimal.ONE)));
    testling.updateCart(updateParams, context);
    cart = testling.getCart(context);
    assertEquals("2.0", cart.getOrderItems().get(0).getQuantity().toPlainString());
    testling.cancelCart(context);
  }

  private void testLoginUser() {
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContextHelper.setCurrentContext(UserContextHelper.getCurrentContext());

    HttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();

    boolean loginUserSuccess = userSessionService.loginUser(request, response, testConfig.getUser1Name(), PASSWORD);
    assertTrue(loginUserSuccess);
  }

  @Test
  public void testUpdateCartWithAnonymousUser() throws Exception {
    if (useBetamaxTapes() || StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_7)) {
      return;
    }

    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContextHelper.setCurrentContext(UserContextHelper.getCurrentContext());

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean guestIdentitySuccess = userSessionService.ensureGuestIdentity(request, response);
    assertTrue(guestIdentitySuccess);

    StoreContext currentContext = testConfig.getStoreContext();
    testling.cancelCart(currentContext);
    StoreContext context = currentContext;
    Cart cart = testling.getCart(context);
    assertNotNull(cart);
    assertTrue(cart.getOrderItems().isEmpty());

    String orderItemId = getOrderItemId(SKU_CODE);
    assertNotNull(orderItemId);

    List<CartService.OrderItemParam> addToParams = new ArrayList<>();
    addToParams.add(new CartService.OrderItemParam(
            orderItemId,
            BigDecimal.ONE));
    testling.addToCart(addToParams, context);
    cart = testling.getCart(context);
    assertNotNull(cart);

    Cart.OrderItem orderItem = cart.getOrderItems().get(0);
    assertEquals("1.0", orderItem.getQuantity().toPlainString());

    List<CartService.OrderItemParam> updateParams = new ArrayList<>();
    updateParams.add(new CartService.OrderItemParam(
            orderItem.getExternalId(),
            orderItem.getQuantity().add(BigDecimal.ONE)));
    testling.updateCart(updateParams, context);
    cart = testling.getCart(context);
    orderItem = cart.getOrderItems().get(0);
    assertEquals("2.0", orderItem.getQuantity().toPlainString());

    testling.cancelCart(currentContext);
  }

  private void prefillCart() throws Exception {
    testLoginUser();

    StoreContext context = StoreContextHelper.getCurrentContext();
    Cart cart = testling.getCart(context);
    assertNotNull(cart);

    String orderItemId = getOrderItemId(SKU_CODE);
    assertNotNull(orderItemId);

    List<CartService.OrderItemParam> updateParams = new ArrayList<>();
    updateParams.add(new CartService.OrderItemParam(
            orderItemId,
            BigDecimal.ONE));
    testling.addToCart(updateParams, context);
    cart = testling.getCart(context);
    assertNotNull(cart);
    assertEquals("1.0", cart.getOrderItems().get(0).getQuantity().toPlainString());
  }

  private String getOrderItemId(String id) {
    CommerceId productVariantId = ibmCommerceIdProvider.formatProductVariantId(getStoreContext().getCatalogAlias(), id);
    Product product = catalogService.findProductById(productVariantId, getStoreContext());
    if (product != null) {
      return product.getExternalTechId();
    }
    return null;
  }
}
