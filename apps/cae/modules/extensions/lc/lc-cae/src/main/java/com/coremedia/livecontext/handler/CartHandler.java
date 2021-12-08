package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentUserContext;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.ecommerce.order.CartService;
import com.coremedia.livecontext.ecommerce.order.CartService.OrderItemParam;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.objectserver.view.substitution.Substitution;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.ContentTypes.CONTENT_TYPE_JSON;
import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.TARGETVIEW_PARAMETER;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ROOT;
import static com.coremedia.blueprint.base.links.UriConstants.Views.VIEW_FRAGMENT;
import static com.coremedia.blueprint.links.BlueprintUriConstants.Prefixes.PREFIX_SERVICE;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

/**
 * Handler for Commerce carts.
 */
@Link
@RequestMapping
public class CartHandler extends LiveContextPageHandlerBase {

  protected static final String URI_PREFIX = "cart";

  /**
   * URI pattern, for URIs like "/service/cart/shopName"
   */
  public static final String URI_PATTERN =
          '/' + PREFIX_SERVICE +
                  '/' + URI_PREFIX +
                  "/{" + SEGMENT_ROOT + '}';

  /**
   * URI pattern, for URIs like "/dynamic/fragment/cart/shopName"
   */
  public static final String DYNAMIC_URI_PATTERN =
          '/' + PREFIX_DYNAMIC +
                  '/' + SEGMENTS_FRAGMENT +
                  '/' + URI_PREFIX +
                  "/{" + SEGMENT_ROOT + '}';

  private static final String PARAM_ACTION = "action";

  @VisibleForTesting
  static final String ACTION_REMOVE_ORDER_ITEM = "removeOrderItem";
  private static final String ORDER_ITEM_ID = "orderItemId";

  private static final String ACTION_ADD_ORDER_ITEM = "addOrderItem";
  private static final String EXTERNAL_ID = "externalId";

  @Substitution("cart")
  public Cart getCart(@NonNull HttpServletRequest request) {
    return new LazyCart(request);
  }

  // --- Handlers ------------------------------------------------------------------------------------------------------

  @GetMapping(value = DYNAMIC_URI_PATTERN)
  public ModelAndView handleFragmentRequest(@PathVariable(SEGMENT_ROOT) String context,
                                            @RequestParam(value = TARGETVIEW_PARAMETER, required = false) String view,
                                            HttpServletRequest request) {
    // If no context is available: return "not found".

    Navigation navigation = getNavigation(context);
    if (navigation == null) {
      return HandlerHelper.notFound();
    }

    Cart cart = resolveCart(request);
    if (cart == null) {
      return HandlerHelper.notFound();
    }

    // Add navigationContext as navigationContext request param.
    ModelAndView modelWithView = HandlerHelper.createModelWithView(cart, view);
    NavigationLinkSupport.setNavigation(modelWithView, navigation);
    return modelWithView;
  }

  @PostMapping(value = DYNAMIC_URI_PATTERN, produces = {CONTENT_TYPE_JSON})
  @ResponseBody
  public Object handleAjaxRequest(@RequestParam(value = PARAM_ACTION, required = true) String action,
                                  HttpServletRequest request, HttpServletResponse response) {
    switch (action) {
      case ACTION_REMOVE_ORDER_ITEM:
        return handleRemoveOrderItem(request, response);
      case ACTION_ADD_ORDER_ITEM:
        return handleAddOrderItem(request, response);
      default:
        throw new NotFoundException("Unsupported action: " + action);
    }
  }

  @NonNull
  private Object handleRemoveOrderItem(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
    Cart cart = resolveCart(request);
    if (cart == null) {
      return emptyMap();
    }

    String orderItemId = request.getParameter(ORDER_ITEM_ID);

    if (!orderItemExist(cart, orderItemId)) {
      throw new NotFoundException("Cannot remove order item with ID '" + orderItemId + "' from cart.");
    }

    UserContext updatedUserContext = deleteCartOrderItem(orderItemId, request);
    updatedUserContext.getCookies().forEach(c -> addCookie(c, response));

    return emptyMap();
  }

  @NonNull
  private Object handleAddOrderItem(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
    var externalId = request.getParameter(EXTERNAL_ID);

    var updatedUserContext = addCartOrderItem(externalId, request);
    updatedUserContext.getCookies().forEach(c -> addCookie(c, response));

    return emptyMap();
  }

  private static void addCookie(Cookie cookie, HttpServletResponse response){
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  private UserContext deleteCartOrderItem(@NonNull String orderItemId, HttpServletRequest request) {
    StoreContext storeContext = CurrentStoreContext.get(request);
    CommerceConnection commerceConnection = storeContext.getConnection();

    CartService cartService = getCartService(commerceConnection);
    var userContext = get(request);

    return cartService.deleteCartOrderItem(orderItemId, null, storeContext, userContext);
  }

  private UserContext addCartOrderItem(String skuId, HttpServletRequest request) {
    BigDecimal quantity = BigDecimal.valueOf(1);
    //we do not know the order item id yet. we may simply pass the sku id instead
    OrderItemParam orderItem = new OrderItemParam(skuId, skuId, quantity);

    List<OrderItemParam> orderItems = singletonList(orderItem);

    var storeContext = CurrentStoreContext.get(request);
    var commerceConnection = storeContext.getConnection();
    var cartService = getCartService(commerceConnection);
    var userContext = get(request);

    return cartService.addToCart(orderItems, storeContext, userContext);
  }

  private static UserContext get(HttpServletRequest request) {
    return CurrentUserContext.find(request)
            .orElseThrow(() -> new IllegalStateException("UserContext not available."));
  }

  private static boolean orderItemExist(@NonNull Cart cart, @Nullable String orderItemId) {
    return orderItemId != null && cart.findOrderItemById(orderItemId) != null;
  }

  @NonNull
  private static CartService getCartService(@NonNull CommerceConnection connection) {
    return connection.getCartService()
            .orElseThrow(() -> new IllegalStateException("Cart service is not available."));
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  public static class NotFoundException extends RuntimeException {

    public NotFoundException(String msg) {
      super(msg);
    }
  }

  // --- LinkSchemes ---------------------------------------------------------------------------------------------------

  @Link(type = Cart.class, view = VIEW_FRAGMENT, uri = DYNAMIC_URI_PATTERN)
  @NonNull
  public UriComponents buildFragmentLink(@NonNull UriTemplate uriPattern,
                                         @NonNull Map<String, Object> linkParameters) {
    return buildLinkInternal(uriPattern, linkParameters);
  }

  @Link(type = Cart.class, view = "ajax", uri = DYNAMIC_URI_PATTERN)
  @NonNull
  public UriComponents buildDeleteCartOderItemLink(@NonNull UriTemplate uriPattern,
                                                   @NonNull Map<String, Object> linkParameters) {
    return buildLinkInternal(uriPattern, linkParameters);
  }

  @NonNull
  private UriComponents buildLinkInternal(@NonNull UriTemplate uriPattern,
                                          @NonNull Map<String, Object> linkParameters) {
    Navigation context = getContextHelper().currentSiteContext();
    String firstNavigationPathSegment = getPathSegments(context).get(0);

    Map<String, String> uriVariables = Map.of(SEGMENT_ROOT, firstNavigationPathSegment);

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(uriPattern.toString());
    uriBuilder = addLinkParametersAsQueryParameters(uriBuilder, linkParameters);
    return uriBuilder.buildAndExpand(uriVariables);
  }

  @Nullable
  private static Cart resolveCart(HttpServletRequest request) {
    var storeContext = CurrentStoreContext.find(request).orElse(null);
    if (storeContext == null) {
      return null;
    }

    var userContext = CurrentUserContext.find(request).orElse(null);
    if (userContext == null) {
      return null;
    }

    return storeContext.getConnection()
            .getCartService()
            .map(cartService -> cartService.getCart(storeContext, userContext))
            .orElse(null);
  }

  //====================================================================================================================

  /**
   * This class fetches the actual cart from the cart service only if some methods are actually used. This saves a cart
   * fetch round trip to the commerce backend if the cart is only needed for link building.
   */
  private class LazyCart implements Cart {

    private Cart delegate;
    private final HttpServletRequest request;

    public LazyCart(HttpServletRequest request) {
      this.request = request;
    }

    public Cart getDelegate() {
      if (delegate == null) {
        delegate = requireNonNull(resolveCart(request));
      }
      return delegate;
    }

    @Override
    @NonNull
    public CommerceId getId() {
      return getDelegate().getId();
    }

    @Override
    @NonNull
    public CommerceId getReference() {
      return getId();
    }

    @Override
    public StoreContext getContext() {
      return getDelegate().getContext();
    }

    @Override
    public Locale getLocale() {
      return getDelegate().getLocale();
    }

    @Override
    public List<OrderItem> getOrderItems() {
      return getDelegate().getOrderItems();
    }

    @Override
    public BigDecimal getTotalQuantity() {
      return getDelegate().getTotalQuantity();
    }

    @Override
    public OrderItem findOrderItemById(String orderItemId) {
      return getDelegate().findOrderItemById(orderItemId);
    }

    @Override
    public String getExternalId() {
      return getDelegate().getExternalId();
    }

    @Override
    public String getExternalTechId() {
      return getDelegate().getExternalTechId();
    }

    @NonNull
    @Override
    public Map<String, Object> getCustomAttributes() {
      return getDelegate().getCustomAttributes();
    }

    @Nullable
    @Override
    public <T> T getCustomAttribute(@NonNull String key, @NonNull Class<T> expectedType) {
      return getDelegate().getCustomAttribute(key, expectedType);
    }

    @Override
    public void load() {
      getDelegate();
    }
  }
}
