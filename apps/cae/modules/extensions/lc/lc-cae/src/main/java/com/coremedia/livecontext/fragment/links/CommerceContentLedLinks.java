package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannel;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.StorefrontRef;
import com.coremedia.livecontext.ecommerce.link.StorefrontRefKey;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.navigation.LiveContextCategoryNavigation;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.fragment.links.CommerceLinkHelper.isFragmentRequest;
import static com.coremedia.livecontext.fragment.links.CommerceLinkHelper.toUriComponents;
import static com.coremedia.livecontext.fragment.links.CommerceLinkTemplateTypes.CHECKOUT_REDIRECT;
import static com.coremedia.livecontext.fragment.links.CommerceLinkTemplateTypes.EXTERNAL_PAGE_NON_SEO;
import static com.coremedia.livecontext.fragment.links.CommerceLinkTemplateTypes.EXTERNAL_PAGE_SEO;
import static com.coremedia.livecontext.handler.CartHandler.URI_PATTERN;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Link building for content led integration (CAE renders links to shop pages directly).
 * <p/>
 * The commerce specific link building to shop pages takes place in the commerce specific commerce adapter implementation.
 * <p/>
 * There are competing commerce specific link schemes, which need to be executed in this specific order:
 * <ol>
 *   <li>{@link CommerceLedLinks}</li>
 *   <li>{@link CommerceStudioLinks}</li>
 *   <li>{@link CommerceContentLedLinks}</li>
 * </ol>
 */
@DefaultAnnotation(NonNull.class)
@Link
public class CommerceContentLedLinks {

  private final CommerceLinkHelper commerceLinkHelper;

  public CommerceContentLedLinks(CommerceLinkHelper commerceLinkHelper) {
    this.commerceLinkHelper = commerceLinkHelper;
  }

  @Link(type = Category.class, order = 3)
  @Nullable
  public UriComponents buildLinkForCategory(Category category,
                                            HttpServletRequest request) {

    if (isFragmentRequest(request) || !commerceLinkHelper.isSiteContentLed(request)
            || !commerceLinkHelper.useCommerceCategoryLinks(request)) {
      return null;
    }

    String storefrontUrl = category.getStorefrontUrl();
    if (isNullOrEmpty(storefrontUrl)) {
      return null;
    }

    return toUriComponents(storefrontUrl);
  }

  @Link(type = CategoryInSite.class, order = 3)
  @Nullable
  public UriComponents buildLinkForCategoryInSite(CategoryInSite categoryInSite,
                                                  HttpServletRequest request) {

    Category category = categoryInSite.getCategory();
    if (category == null) {
      return null;
    }
    return buildLinkForCategory(category, request);
  }

  @Link(type = LiveContextCategoryNavigation.class, order = 3)
  @Nullable
  public UriComponents buildLinkForLiveContextCategoryNavigation(LiveContextCategoryNavigation categoryNavigation,
                                                                 HttpServletRequest request) {

    Category category = categoryNavigation.getCategory();

    return buildLinkForCategory(category, request);
  }

  @Link(type = LiveContextExternalChannel.class, order = 3)
  @Nullable
  public UriComponents buildLinkForExternalChannel(LiveContextExternalChannel augmentedCategory,
                                                   HttpServletRequest request) {

    Category category = augmentedCategory.getCategory();
    if (category == null) {
      return null;
    }
    return buildLinkForCategory(category, request);
  }

  @Link(type = Product.class, order = 3)
  @Nullable
  public UriComponents buildLinkForProduct(Product product,
                                           HttpServletRequest request) {

    if (isFragmentRequest(request) || !commerceLinkHelper.isSiteContentLed(request)
            || !commerceLinkHelper.useCommerceProductLinks(request)) {
      return null;
    }

    String storefrontUrl = product.getStorefrontUrl();
    if (isNullOrEmpty(storefrontUrl)) {
      return null;
    }

    return toUriComponents(storefrontUrl);
  }

  @Link(type = ProductInSite.class, order = 3)
  @Nullable
  public UriComponents buildLinkForProductInSite(ProductInSite productInSite,
                                                 HttpServletRequest request) {

    return buildLinkForProduct(productInSite.getProduct(), request);
  }

  @Link(type = CMProductTeaser.class, view = HandlerHelper.VIEWNAME_DEFAULT, order = 3)
  @Nullable
  public UriComponents buildLinkForProductTeaser(CMProductTeaser productTeaser,
                                                 HttpServletRequest request) {

    Product product = productTeaser.getProduct();
    if (product == null) {
      return null;
    }
    return buildLinkForProduct(product, request);
  }

  @Link(type = LiveContextExternalProduct.class, order = 3)
  @Nullable
  public UriComponents buildLinkForExternalProduct(LiveContextExternalProduct externalProduct,
                                                   HttpServletRequest request) {

    Product product = externalProduct.getProduct();
    if (product == null) {
      return null;
    }
    return buildLinkForProduct(product, request);
  }

  @Link(type = CMExternalPage.class, order = 3)
  @Nullable
  public UriComponents buildLinkForExternalPage(CMExternalPage externalPage,
                                                HttpServletRequest request) {

    if (isFragmentRequest(request) || !commerceLinkHelper.isSiteContentLed(request)) {
      return null;
    }

    return commerceLinkHelper.findCommerceConnection(externalPage)
            .flatMap(commerceConnection -> buildContentLedLinkForExternalPage(externalPage, commerceConnection))
            .orElse(null);
  }

  @Link(type = Cart.class, uri = URI_PATTERN)
  @Nullable
  public UriComponents buildGoToCartLink(@NonNull HttpServletRequest request) {
    return CurrentStoreContext.find()
            // only perform this check, if the store context is available
            .filter(context -> commerceLinkHelper.isSiteContentLed(request))
            .flatMap(context -> getUriComponents(context, CHECKOUT_REDIRECT))
            .orElse(null);
  }

  @Nullable
  @Link(type = StorefrontRefKey.class)
  public UriComponents buildContentLedUrl(StorefrontRefKey storefrontRefKey) {
    return CurrentStoreContext.find()
            .flatMap(context -> getUriComponents(context, storefrontRefKey))
            .orElse(null);
  }

  public static Optional<UriComponents> getUriComponents(StoreContext storeContext, StorefrontRefKey storefrontRefKey) {
    return getStorefrontRef(storeContext, storefrontRefKey)
            .map(StorefrontRef::toLink)
            .map(UriComponentsBuilder::fromUriString)
            .map(UriComponentsBuilder::build);
  }

  private static Optional<StorefrontRef> getStorefrontRef(StoreContext storeContext, StorefrontRefKey templateKey) {
    return storeContext.getConnection().getLinkService()
            .flatMap(linkService -> linkService.getStorefrontRef(templateKey, storeContext));
  }

  private Optional<UriComponents> buildContentLedLinkForExternalPage(CMExternalPage externalPage,
                                                           CommerceConnection commerceConnection) {

    String externalUriPath = externalPage.getExternalUriPath();

    if (!isNullOrEmpty(externalUriPath)) {
      return buildLink(commerceConnection, EXTERNAL_PAGE_NON_SEO, Map.of("uriPath", externalUriPath));
    }

    String segment = externalPage.getSegment();
    return buildLink(commerceConnection, EXTERNAL_PAGE_SEO, Map.of("seoSegment", segment));
  }

  private static Optional<UriComponents> buildLink(CommerceConnection commerceConnection,
                                                   StorefrontRefKey templateKey,
                                                   Map<String, String> replacements) {
    StoreContext storeContext = commerceConnection.getStoreContext();
    return commerceConnection.getLinkService()
            .flatMap(linkService -> linkService.getStorefrontRef(templateKey, storeContext))
            .map(storefrontRef -> storefrontRef.replace(replacements))
            .map(StorefrontRef::toLink)
            .map(UriComponentsBuilder::fromUriString)
            .map(UriComponentsBuilder::build);
  }

}
