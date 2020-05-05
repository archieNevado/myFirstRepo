package com.coremedia.livecontext.fragment.links;

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

import static com.coremedia.livecontext.fragment.links.CommerceLinkHelper.isFragmentOrDynamicRequest;
import static com.coremedia.livecontext.fragment.links.CommerceLinkHelper.isFragmentRequest;

/**
 * Link building for commerce led integration (fragment requests and ajax includes via shop).
 * <p/>
 * In the scope of fragment requests instead of links the CAE renders parametrized link templates see
 * {@link com.coremedia.livecontext.fragment.links.transformers.LiveContextLinkTransformer}
 * and {@link com.coremedia.livecontext.fragment.links.transformers.resolvers.CommerceLinkResolver}.
 * The commerce system finally processes these link templates and generates real urls.
 *
 * This is why this scheme simply returns a dummy urls to simply trigger the
 * {@link com.coremedia.livecontext.fragment.links.transformers.LiveContextLinkTransformer}.
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
public class CommerceLedLinks {

  // dummy URL meant to be replaced by the commerce link resolver later on
  public static final String DUMMY_URI_STRING = "http://lc-generic-live.vm";
  private static final UriComponents DUMMY_URI_TO_BE_REPLACED = toUriComponents(DUMMY_URI_STRING);

  private final CommerceLinkHelper commerceLinkHelper;

  public CommerceLedLinks(CommerceLinkHelper commerceLinkHelper) {
    this.commerceLinkHelper = commerceLinkHelper;
  }

  @Link(type = Category.class, order = 1)
  @Nullable
  public UriComponents buildLinkForCategory(Category category, Map<String, Object> linkParameters,
                                            HttpServletRequest request) {

    if (!isFragmentOrDynamicRequest(request) || !commerceLinkHelper.useCommerceCategoryLinks(request)) {
      return null;
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = CategoryInSite.class)
  @Nullable
  public UriComponents buildLinkForCategoryInSite(CategoryInSite categoryInSite, Map<String, Object> linkParameters,
                                                  HttpServletRequest request) {

    Category category = categoryInSite.getCategory();

    return category != null ? buildLinkForCategory(category, linkParameters, request) : null;
  }

  @Link(type = LiveContextCategoryNavigation.class, order = 1)
  @Nullable
  public UriComponents buildLinkForLiveContextCategoryNavigation(LiveContextCategoryNavigation categoryNavigation,
                                                                 Map<String, Object> linkParameters,
                                                                 HttpServletRequest request) {

    Category category = categoryNavigation.getCategory();

    return buildLinkForCategory(category, linkParameters, request);
  }

  @Link(type = LiveContextExternalChannel.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalChannel(LiveContextExternalChannel augmentedCategory,
                                                   Map<String, Object> linkParameters, HttpServletRequest request) {

    Category category = augmentedCategory.getCategory();

    return category != null ? buildLinkForCategory(category, linkParameters, request) : null;
  }

  @Link(type = Product.class, order = 1)
  @Nullable
  public UriComponents buildLinkForProduct(Product product, Map<String, Object> linkParameters,
                                           HttpServletRequest request) {

    if (!isFragmentRequest(request) || !commerceLinkHelper.useCommerceProductLinks(request)) {
      return null;
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = ProductInSite.class, order = 1)
  @Nullable
  public UriComponents buildLinkForProductInSite(ProductInSite productInSite, Map<String, Object> linkParameters,
                                                 HttpServletRequest request) {

    return buildLinkForProduct(productInSite.getProduct(), linkParameters, request);
  }

  @Link(type = CMProductTeaser.class, view = HandlerHelper.VIEWNAME_DEFAULT, order = 1)
  @Nullable
  public UriComponents buildLinkForProductTeaser(CMProductTeaser productTeaser, Map<String, Object> linkParameters,
                                                 HttpServletRequest request) {

    Product product = productTeaser.getProduct();

    return product != null ? buildLinkForProduct(product, linkParameters, request) : null;
  }

  @Link(type = LiveContextExternalProduct.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalProduct(LiveContextExternalProduct externalProduct,
                                                   Map<String, Object> linkParameters, HttpServletRequest request) {

    Product product = externalProduct.getProduct();

    return product != null ? buildLinkForProduct(product, linkParameters, request) : null;
  }

  @Link(type = CMExternalPage.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalPage(CMExternalPage externalPage, Map<String, Object> linkParameters,
                                                HttpServletRequest request) {

    if (!isFragmentOrDynamicRequest(request)) {
      return null;
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = CMChannel.class, order = 1)
  @Nullable
  public UriComponents buildLinkForCMChannel(CMChannel channel, Map<String, Object> linkParameters,
                                             HttpServletRequest request) {
    if (!isFragmentOrDynamicRequest(request) || !commerceLinkHelper.useCommerceLinkForChannel(channel)) {
      return null;
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = CMLinkable.class, order = 1)
  @Nullable
  public UriComponents buildLinkForCMLinkable(CMLinkable linkable, Map<String, Object> linkParameters,
                                             HttpServletRequest request) {
    if (!isFragmentOrDynamicRequest(request) || !commerceLinkHelper.useCommerceLinkForLinkable(linkable)) {
      return null;
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  private static UriComponents toUriComponents(String uri) {
    return UriComponentsBuilder
            .fromUriString(uri)
            .build();
  }
}
