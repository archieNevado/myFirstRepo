package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannel;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.link.StorefrontRefKey;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.handler.ExternalNavigationHandler;
import com.coremedia.livecontext.navigation.LiveContextCategoryNavigation;
import com.coremedia.livecontext.product.ProductAvailabilityHandler;
import com.coremedia.livecontext.product.ProductPageHandler;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.fragment.links.CommerceLinkTemplateTypes.CHECKOUT_REDIRECT;
import static com.coremedia.livecontext.fragment.links.CommerceLinkUtils.getUriComponents;
import static com.coremedia.livecontext.handler.CartHandler.URI_PATTERN;

@DefaultAnnotation(NonNull.class)
@Link
public class CommerceLinks {

  private final CommerceLinkHelper commerceLinkHelper;
  private final CommerceStudioLinks commerceStudioLinks;
  private final CommerceContentLedLinks commerceContentLedLinks;

  CommerceLinks(CommerceLinkHelper commerceLinkHelper,
                CommerceStudioLinks commerceStudioLinks,
                CommerceContentLedLinks commerceContentLedLinks) {
    this.commerceLinkHelper = commerceLinkHelper;
    this.commerceStudioLinks = commerceStudioLinks;
    this.commerceContentLedLinks = commerceContentLedLinks;
  }

  @Link(type = Category.class)
  @Nullable
  public UriComponents buildLinkForCategory(Category bean,
                                            Map<String, Object> linkParameters,
                                            HttpServletRequest request) {
    return commerceLinkHelper.createCategoryLinkDispatcher(request)
            .dispatch(
                    () -> commerceStudioLinks.buildLinkForCategory(bean, linkParameters, request),
                    () -> commerceContentLedLinks.buildLinkForCategory(bean)
            );
  }

  /**
   * Link scheme that takes precedence over
   * {@link ExternalNavigationHandler#buildLinkFor(com.coremedia.livecontext.commercebeans.CategoryInSite, java.lang.String, java.util.Map, javax.servlet.http.HttpServletRequest)}
   */
  @Link(type = CategoryInSite.class, order = 1)
  @Nullable
  public UriComponents buildLinkForCategoryInSite(CategoryInSite bean,
                                                  Map<String, Object> linkParameters,
                                                  HttpServletRequest request) {
    return commerceLinkHelper.createCategoryLinkDispatcher(request)
            .dispatch(
                    Optional::empty,
                    () -> commerceContentLedLinks.buildLinkForCategory(bean.getCategory())
            );
  }

  /**
   * Link scheme that takes precedence over
   * {@link ExternalNavigationHandler#buildLinkForCategoryImpl(com.coremedia.livecontext.navigation.LiveContextCategoryNavigation, java.lang.String, java.util.Map, javax.servlet.http.HttpServletRequest)}
   */
  @Link(type = LiveContextCategoryNavigation.class, order = 1)
  @Nullable
  public UriComponents buildLinkForLiveContextCategoryNavigation(LiveContextCategoryNavigation bean,
                                                                 Map<String, Object> linkParameters,
                                                                 HttpServletRequest request) {
    return commerceLinkHelper.createCategoryLinkDispatcher(request)
            .dispatch(
                    Optional::empty,
                    () -> commerceContentLedLinks.buildLinkForLiveContextCategoryNavigation(bean)
            );
  }

  /**
   * Link scheme that takes precedence over
   * {@link ExternalNavigationHandler#buildLinkForExternalChannel(com.coremedia.livecontext.contentbeans.LiveContextExternalChannel, java.lang.String, java.util.Map, javax.servlet.http.HttpServletRequest)}
   */
  @Link(type = LiveContextExternalChannel.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalChannel(LiveContextExternalChannel bean,
                                                   Map<String, Object> linkParameters,
                                                   HttpServletRequest request) {
    Category category = bean.getCategory();
    if (category == null) {
      return null;
    }
    return commerceLinkHelper.createCategoryLinkDispatcher(request)
            .dispatch(
                    () -> commerceStudioLinks.buildLinkForCategory(category, linkParameters, request),
                    () -> commerceContentLedLinks.buildLinkForCategory(category)
            );
  }

  @Link(type = Product.class)
  @Nullable
  public UriComponents buildLinkForProduct(Product bean,
                                           Map<String, Object> linkParameters,
                                           HttpServletRequest request) {
    return commerceLinkHelper.createProductLinkDispatcher(request)
            .dispatch(
                    () -> commerceStudioLinks.buildLinkForProduct(bean, linkParameters, request),
                    () -> commerceContentLedLinks.buildLinkForProduct(bean)
            );
  }

  /**
   * Link scheme that takes precedence over
   * {@link ProductPageHandler#buildLinkFor(com.coremedia.livecontext.commercebeans.ProductInSite, java.lang.String, java.util.Map, javax.servlet.http.HttpServletRequest)} and
   * {@link ProductAvailabilityHandler#buildLinkFor(com.coremedia.livecontext.commercebeans.ProductInSite, org.springframework.web.util.UriTemplate, java.util.Map)}
   */
  @Link(type = ProductInSite.class, order = 1)
  @Nullable
  public UriComponents buildLinkForProductInSite(ProductInSite bean,
                                                 Map<String, Object> linkParameters,
                                                 HttpServletRequest request) {
    return commerceLinkHelper.createProductLinkDispatcher(request)
            .dispatch(
                    Optional::empty,
                    () -> commerceContentLedLinks.buildLinkForProduct(bean.getProduct())
            );
  }

  /**
   * Link scheme that takes precedence over
   * {@link ProductPageHandler#buildLinkFor(com.coremedia.livecontext.contentbeans.CMProductTeaser, java.lang.String, java.util.Map, javax.servlet.http.HttpServletRequest)}
   */
  @Link(type = CMProductTeaser.class, view = HandlerHelper.VIEWNAME_DEFAULT, order = 1)
  @Nullable
  public UriComponents buildLinkForProductTeaser(CMProductTeaser bean,
                                                 Map<String, Object> linkParameters,
                                                 HttpServletRequest request) {
    ProductInSite productInSite = bean.getProductInSite();
    if (productInSite == null) {
      return null;
    }
    return commerceLinkHelper.createProductLinkDispatcher(request)
            .dispatch(
                    Optional::empty,
                    () -> commerceContentLedLinks.buildLinkForProduct(productInSite.getProduct())
            );
  }

  @Link(type = LiveContextExternalProduct.class)
  @Nullable
  public UriComponents buildLinkForExternalProduct(LiveContextExternalProduct bean,
                                                   Map<String, Object> linkParameters,
                                                   HttpServletRequest request) {
    Product product = bean.getProduct();
    if (product == null) {
      return null;
    }

    return commerceLinkHelper.createProductLinkDispatcher(request)
            .dispatch(
                    () -> commerceStudioLinks.buildLinkForProduct(product, linkParameters, request),
                    () -> commerceContentLedLinks.buildLinkForProduct(product)
            );
  }

  @Link(type = CMExternalPage.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalPage(CMExternalPage bean,
                                                Map<String, Object> linkParameters,
                                                HttpServletRequest request) {
    return commerceLinkHelper.createCommerceLinkDispatcher(request, true)
            .dispatch(
                    () -> commerceStudioLinks.buildLinkForExternalPage(bean, linkParameters, request),
                    () -> commerceContentLedLinks.buildLinkForExternalPage(bean)
            );
  }

  /**
   * Link scheme that takes precedence over
   * {@link com.coremedia.livecontext.handler.LiveContextChannelLinkBuilder#buildLinkForSearchLandingPage(com.coremedia.blueprint.common.contentbeans.CMChannel)}
   */
  @Link(type = CMChannel.class, order = 1)
  @Nullable
  public UriComponents buildLinkForCMChannel(CMChannel bean,
                                             Map<String, Object> linkParameters,
                                             HttpServletRequest request) {
    boolean useCommerceLinks = commerceLinkHelper.useCommerceLinkForChannel(bean);
    return commerceLinkHelper.createCommerceLinkDispatcher(request, useCommerceLinks).dispatch(
            () -> commerceStudioLinks.buildLinkForCMChannel(bean, linkParameters, request),
            Optional::empty
    );
  }

  @Link(type = CMLinkable.class, order = 1)
  @Nullable
  public UriComponents buildLinkForCMLinkable(CMLinkable bean,
                                              Map<String, Object> linkParameters,
                                              HttpServletRequest request) {
    boolean useCommerceLinks = commerceLinkHelper.useCommerceLinkForLinkable(bean);
    return commerceLinkHelper.createCommerceLinkDispatcher(request, useCommerceLinks).dispatch(
            Optional::empty,
            () -> commerceContentLedLinks.buildLinkForCMLinkable(bean)
    );
  }

  @Link(type = Cart.class, uri = URI_PATTERN)
  @Nullable
  public UriComponents buildGoToCartLink(Cart cart) {
    return getUriComponents(cart.getContext(), CHECKOUT_REDIRECT).orElse(null);
  }

  @Nullable
  @Link(type = StorefrontRefKey.class)
  public UriComponents buildContentLedUrl(StorefrontRefKey storefrontRefKey) {
    return CurrentStoreContext.find()
            .flatMap(context -> getUriComponents(context, storefrontRefKey))
            .orElse(null);
  }

}
