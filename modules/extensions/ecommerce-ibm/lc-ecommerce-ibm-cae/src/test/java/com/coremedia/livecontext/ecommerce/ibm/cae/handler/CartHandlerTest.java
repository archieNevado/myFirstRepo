package com.coremedia.livecontext.ecommerce.ibm.cae.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.cae.WcsUrlProvider;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.objectserver.web.HttpError;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CartHandlerTest {

  private static final String CONTEXT_NAME = "anyChannelName";

  @InjectMocks
  private CartHandler testling;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private WcsUrlProvider checkoutRedirectPropertyProvider;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @SuppressWarnings("unused") // required by cart handler
  @Spy
  private LinkFormatter linkFormatter;

  private BaseCommerceConnection commerceConnection;
  private MockCommerceEnvBuilder envBuilder;


  @Before
  public void beforeEachTest() {
    envBuilder = MockCommerceEnvBuilder.create();
    commerceConnection = envBuilder.setupEnv();
  }

  @After
  public void tearDown() throws Exception {
    envBuilder.tearDownEnv();
  }

  @Test
  public void testHandleRequestNoContextFound() {
    UriComponentsBuilder testUrlBuilder = UriComponentsBuilder.fromUriString("checkoutUrl");
    UriComponents testUrl = testUrlBuilder.build();
    Mockito.when(checkoutRedirectPropertyProvider.provideValue(any(Map.class), any(), any())).thenReturn(testUrl);

    View modelAndView = testling.handleRequest(CONTEXT_NAME, Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class));
    Assert.assertTrue(modelAndView instanceof RedirectView);
    Assert.assertTrue(((RedirectView) modelAndView).getUrl().equals(testUrl.toString()));
  }

  @Test
  public void testHandleFragmentRequest() {
    Navigation context = Mockito.mock(Navigation.class);

    configureContext(context);

    Cart resolvedCart = Mockito.mock(Cart.class);
    configureResolveCart(resolvedCart);

    String viewName = "viewName";

    ModelAndView modelAndView = testling.handleFragmentRequest(CONTEXT_NAME, viewName);

    checkCartServiceIsUsedCorrectly();

    checkModelContainsCartAndNavigation(resolvedCart, context, modelAndView);

    checkViewName(viewName, modelAndView);
  }

  @Test
  public void testHandleFragmentRequestNoContext() {
    configureContext(null);
    String viewName = "viewName";
    ModelAndView modelAndView = testling.handleFragmentRequest(CONTEXT_NAME, viewName);
    checkSelfIsHttpError(modelAndView);
  }

  @Test
  public void testHandleAjaxRequestDeleteOrderItem() {
    String orderItemId = "12";
    configureRequestParameter(request, "orderItemId", orderItemId);
    Cart cart = Mockito.mock(Cart.class);
    configureResolveCart(cart);
    Cart.OrderItem orderItem = Mockito.mock(Cart.OrderItem.class);
    configureCartFindOrderItem(cart, orderItemId, orderItem);

    Object result = testling.handleAjaxRequest(CartHandler.ACTION_REMOVE_ORDER_ITEM, request, response);

    checkCartServiceIsUsedCorrectly();
    verifyCartDeleteOrderItem(orderItemId);
    Assert.assertEquals(Collections.emptyMap(), result);
  }

  //Is this really the expected behavior?!
  //Action was found but has invalid parameters...
  @Test(expected = CartHandler.NotFoundException.class)
  public void testHandleAjaxRequestNoOrderItemId() {
    String orderItemId = null;
    configureRequestParameter(request, "orderItemId", orderItemId);
    Cart cart = Mockito.mock(Cart.class);
    configureResolveCart(cart);
    Cart.OrderItem orderItem = Mockito.mock(Cart.OrderItem.class);
    configureCartFindOrderItem(cart, orderItemId, orderItem);

    testling.handleAjaxRequest(CartHandler.ACTION_REMOVE_ORDER_ITEM, request, response);

    checkCartServiceIsUsedCorrectly();
  }


  //Is this really the expected behavior?!
  //Action was found but has invalid parameters...
  @Test(expected = CartHandler.NotFoundException.class)
  public void testHandleAjaxRequestNoOrderItem() {
    String orderItemId = "12";
    configureRequestParameter(request, "orderItemId", orderItemId);
    Cart cart = Mockito.mock(Cart.class);
    configureResolveCart(cart);
    Cart.OrderItem orderItem = null;
    configureCartFindOrderItem(cart, orderItemId, orderItem);

    testling.handleAjaxRequest(CartHandler.ACTION_REMOVE_ORDER_ITEM, request, response);

    checkCartServiceIsUsedCorrectly();
  }

  @Test(expected = CartHandler.NotFoundException.class)
  public void testHandleAjaxRequestUnsupportedAction() {
    testling.handleAjaxRequest("AnyInvalidAction", Mockito.mock(HttpServletRequest.class), response);
    checkCartServiceIsUsedCorrectly();
  }

  //Mock Configurations

  private void configureCartFindOrderItem(Cart cart, String orderItemId, Cart.OrderItem orderItem) {
    Mockito.when(cart.findOrderItemById(orderItemId)).thenReturn(orderItem);
  }

  private void configureRequestParameter(HttpServletRequest request, String parameterKey, String parameterValue) {
    Mockito.when(request.getParameter(parameterKey)).thenReturn(parameterValue);
  }

  private void configureResolveCart(Cart cart) {
    Mockito.when(commerceConnection.getCartService().getCart(any(StoreContext.class))).thenReturn(cart);
  }

  private void configureContext(Navigation navigation) {
    Mockito.when(navigationSegmentsUriHelper.parsePath(ArgumentMatchers.eq(Collections.singletonList(CONTEXT_NAME)))).thenReturn(navigation);
  }

  //Checks and Verifies...

  private void checkViewName(String viewName, ModelAndView modelAndView) {
    String actualViewName = modelAndView.getViewName();
    Assert.assertEquals(viewName, actualViewName);
  }

  private void checkModelContainsCartAndNavigation(Cart expectedCart, Navigation context, ModelAndView modelAndView) {
    Map<String, Object> model = modelAndView.getModel();
    Object self = model.get("self");
    Assert.assertTrue(self instanceof Cart);
    Assert.assertSame(expectedCart, self);
    Object navigation = model.get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION);
    Assert.assertSame(context, navigation);
  }

  private void checkCartServiceIsUsedCorrectly() {
    Mockito.verify(commerceConnection.getCartService(), Mockito.times(1)).getCart(CurrentCommerceConnection.get().getStoreContext());
  }

  private void checkSelfIsHttpError(ModelAndView modelAndView) {
    Assert.assertTrue(modelAndView.getModel().get("self") instanceof HttpError);
  }

  private void verifyCartDeleteOrderItem(String orderItemId) {
    Mockito.verify(commerceConnection.getCartService(), Mockito.times(1)).deleteCartOrderItem(orderItemId, CurrentCommerceConnection.get().getStoreContext());
  }
}
