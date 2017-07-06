package com.coremedia.livecontext.ecommerce.ibm.order;

import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.SystemProperties;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.ecommerce.order.CartService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.junit.Test;
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

import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class CartServiceImplIT extends IbmServiceTestBase {

  private static final String SKU_CODE = "CLA022_220301";
  private static final String PASSWORD = "passw0rd";

  @Inject
  CartServiceImpl testling;
  @Inject
  CatalogServiceImpl catalogService;
  @Inject
  UserSessionService userSessionService;

  @Test
  public void testGetCart() throws Exception {
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) {
      return;
    }

    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_7)) {
      return;
    }

    prefillCart();

    testLoginUser();

    Cart cart = testling.getCart();
    assertNotNull(cart);
    assertEquals("1.0", cart.getOrderItems().get(0).getQuantity().toPlainString());

    //test update cart
    Cart.OrderItem orderItem = cart.getOrderItems().get(0);
    List<CartService.OrderItemParam> updateParams = new ArrayList<>();
    updateParams.add(new CartService.OrderItemParam(
            orderItem.getExternalId(),
            orderItem.getQuantity().add(BigDecimal.ONE)));
    testling.updateCart(updateParams);
    cart = testling.getCart();
    assertEquals("2.0", cart.getOrderItems().get(0).getQuantity().toPlainString());
    testling.cancelCart();
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
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts()) ||
            StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_7)) {
      return;
    }

    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContextHelper.setCurrentContext(UserContextHelper.getCurrentContext());

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean guestIdentitySuccess = userSessionService.ensureGuestIdentity(request, response);
    assertTrue(guestIdentitySuccess);

    testling.cancelCart();
    Cart cart = testling.getCart();
    assertNotNull(cart);
    assertTrue(cart.getOrderItems().isEmpty());

    String orderItemId = getOrderItemId(SKU_CODE);
    assertNotNull(orderItemId);

    List<CartService.OrderItemParam> addToParams = new ArrayList<>();
    addToParams.add(new CartService.OrderItemParam(
            orderItemId,
            BigDecimal.ONE));
    testling.addToCart(addToParams);
    cart = testling.getCart();
    assertNotNull(cart);

    Cart.OrderItem orderItem = cart.getOrderItems().get(0);
    assertEquals("1.0", orderItem.getQuantity().toPlainString());

    List<CartService.OrderItemParam> updateParams = new ArrayList<>();
    updateParams.add(new CartService.OrderItemParam(
            orderItem.getExternalId(),
            orderItem.getQuantity().add(BigDecimal.ONE)));
    testling.updateCart(updateParams);
    cart = testling.getCart();
    orderItem = cart.getOrderItems().get(0);
    assertEquals("2.0", orderItem.getQuantity().toPlainString());

    testling.cancelCart();
  }

  private void prefillCart() throws Exception {
    testLoginUser();

    Cart cart = testling.getCart();
    assertNotNull(cart);

    String orderItemId = getOrderItemId(SKU_CODE);
    assertNotNull(orderItemId);

    List<CartService.OrderItemParam> updateParams = new ArrayList<>();
    updateParams.add(new CartService.OrderItemParam(
            orderItemId,
            BigDecimal.ONE));
    testling.addToCart(updateParams);
    cart = testling.getCart();
    assertNotNull(cart);
    assertEquals("1.0", cart.getOrderItems().get(0).getQuantity().toPlainString());
  }

  private String getOrderItemId(String productId) {
    Product product = catalogService.findProductById(CommerceIdHelper.formatProductVariantId(productId));
    if (product != null) {
      return product.getExternalTechId();
    }
    return null;
  }
}
