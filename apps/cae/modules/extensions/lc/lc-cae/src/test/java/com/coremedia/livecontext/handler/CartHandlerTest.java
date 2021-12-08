package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentUserContext;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.ecommerce.order.CartService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.objectserver.web.HttpError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartHandlerTest {

  private static final String CONTEXT_NAME = "anyChannelName";

  @InjectMocks
  private CartHandler testling;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private CartService cartService;

  @Mock
  private StoreContext storeContext;

  @Mock
  private UserContext userContext;

  private final MockHttpServletRequest request = new MockHttpServletRequest();
  private final MockHttpServletResponse response = new MockHttpServletResponse();

  @Before
  public void beforeEachTest() {
    CommerceConnection commerceConnection = mock(CommerceConnection.class);
    when(storeContext.getConnection()).thenReturn(commerceConnection);
    when(commerceConnection.getCartService()).thenReturn(Optional.of(cartService));
    CurrentStoreContext.set(storeContext, request);
    CurrentUserContext.set(userContext, request);
  }

  @After
  public void tearDown() {
    request.clearAttributes();
  }

  @Test
  public void testHandleFragmentRequest() {
    Navigation context = mock(Navigation.class);

    configureContext(context);

    Cart resolvedCart = mock(Cart.class);
    configureResolveCart(resolvedCart);

    String viewName = "viewName";

    ModelAndView modelAndView = testling.handleFragmentRequest(CONTEXT_NAME, viewName, request);

    checkCartServiceIsUsedCorrectly();

    checkModelContainsCartAndNavigation(resolvedCart, context, modelAndView);

    checkViewName(viewName, modelAndView);
  }

  @Test
  public void testHandleFragmentRequestNoContext() {
    configureContext(null);
    String viewName = "viewName";
    ModelAndView modelAndView = testling.handleFragmentRequest(CONTEXT_NAME, viewName, request);
    checkSelfIsHttpError(modelAndView);
  }

  @Test
  public void testHandleAjaxRequestDeleteOrderItem() {
    when(cartService.deleteCartOrderItem(any(), any(), any(StoreContext.class), any(UserContext.class))).thenReturn(UserContext.builder().build());

    String orderItemId = "12";
    request.setParameter("orderItemId", orderItemId);
    Cart cart = mock(Cart.class);
    configureResolveCart(cart);
    Cart.OrderItem orderItem = mock(Cart.OrderItem.class);
    configureCartFindOrderItem(cart, orderItemId, orderItem);

    Object result = testling.handleAjaxRequest(CartHandler.ACTION_REMOVE_ORDER_ITEM, request, response);

    checkCartServiceIsUsedCorrectly();
    verifyCartDeleteOrderItem(orderItemId);
    assertEquals(Collections.emptyMap(), result);
  }

  //Is this really the expected behavior?!
  //Action was found but has invalid parameters...
  @Test(expected = CartHandler.NotFoundException.class)
  public void testHandleAjaxRequestNoOrderItemId() {
    String orderItemId = null;
    request.setParameter("orderItemId", orderItemId);
    Cart cart = mock(Cart.class);
    configureResolveCart(cart);
    Cart.OrderItem orderItem = mock(Cart.OrderItem.class);
    configureCartFindOrderItem(cart, orderItemId, orderItem);

    testling.handleAjaxRequest(CartHandler.ACTION_REMOVE_ORDER_ITEM, request, response);

    checkCartServiceIsUsedCorrectly();
  }

  //Is this really the expected behavior?!
  //Action was found but has invalid parameters...
  @Test(expected = CartHandler.NotFoundException.class)
  public void testHandleAjaxRequestNoOrderItem() {
    String orderItemId = "12";
    request.setParameter("orderItemId", orderItemId);
    Cart cart = mock(Cart.class);
    configureResolveCart(cart);
    Cart.OrderItem orderItem = null;
    configureCartFindOrderItem(cart, orderItemId, orderItem);

    testling.handleAjaxRequest(CartHandler.ACTION_REMOVE_ORDER_ITEM, request, response);

    checkCartServiceIsUsedCorrectly();
  }

  @Test(expected = CartHandler.NotFoundException.class)
  public void testHandleAjaxRequestUnsupportedAction() {
    testling.handleAjaxRequest("AnyInvalidAction", mock(HttpServletRequest.class), response);
    checkCartServiceIsUsedCorrectly();
  }

  //Mock Configurations

  private void configureCartFindOrderItem(Cart cart, String orderItemId, Cart.OrderItem orderItem) {
    when(cart.findOrderItemById(orderItemId)).thenReturn(orderItem);
  }

  private void configureResolveCart(Cart cart) {
    when(cartService.getCart(any(StoreContext.class), any(UserContext.class))).thenReturn(cart);
  }

  private void configureContext(Navigation navigation) {
    when(navigationSegmentsUriHelper.parsePath(ArgumentMatchers.eq(Collections.singletonList(CONTEXT_NAME))))
            .thenReturn(navigation);
  }

  //Checks and Verifies...

  private void checkViewName(String viewName, ModelAndView modelAndView) {
    String actualViewName = modelAndView.getViewName();
    assertEquals(viewName, actualViewName);
  }

  private void checkModelContainsCartAndNavigation(Cart expectedCart, Navigation context, ModelAndView modelAndView) {
    Map<String, Object> model = modelAndView.getModel();
    Object self = model.get("self");
    assertTrue(self instanceof Cart);
    assertSame(expectedCart, self);
    Object navigation = model.get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION);
    assertSame(context, navigation);
  }

  private void checkCartServiceIsUsedCorrectly() {
    verify(cartService, times(1)).getCart(storeContext, userContext);
  }

  private void checkSelfIsHttpError(ModelAndView modelAndView) {
    assertTrue(modelAndView.getModel().get("self") instanceof HttpError);
  }

  private void verifyCartDeleteOrderItem(String orderItemId) {
    verify(cartService, times(1)).deleteCartOrderItem(orderItemId, null, storeContext, userContext);
  }
}
