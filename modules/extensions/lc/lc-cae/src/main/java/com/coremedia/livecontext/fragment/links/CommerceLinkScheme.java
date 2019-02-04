package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.PreviewUrlService;
import com.coremedia.livecontext.ecommerce.link.QueryParam;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import com.coremedia.livecontext.navigation.LiveContextCategoryNavigation;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.ecommerce.link.UrlUtil.convertToQueryParamList;
import static com.coremedia.livecontext.handler.ExternalNavigationHandler.LIVECONTEXT_POLICY_COMMERCE_CATEGORY_LINKS;
import static com.coremedia.livecontext.product.ProductPageHandler.LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS;

@Component
@DefaultAnnotation(NonNull.class)
@Link
public class CommerceLinkScheme {

  private final CommerceConnectionSupplier commerceConnectionSupplier;
  private final CommerceLedLinkBuilderHelper commerceLedPageExtension;
  private final SettingsService settingsService;
  private final ExternalSeoSegmentBuilder seoSegmentBuilder;

  private static final UriComponents EMPTY_URI_COMPONENT_BUILDER = UriComponentsBuilder
          .fromUriString("http://lc-generic-live.vm")
          .build();

  CommerceLinkScheme(CommerceConnectionSupplier commerceConnectionSupplier,
                     CommerceLedLinkBuilderHelper commerceLedPageExtension,
                     SettingsService settingsService,
                     ExternalSeoSegmentBuilder seoSegmentBuilder) {
    this.commerceConnectionSupplier = commerceConnectionSupplier;
    this.commerceLedPageExtension = commerceLedPageExtension;
    this.settingsService = settingsService;
    this.seoSegmentBuilder = seoSegmentBuilder;
  }

  @Link(type = Category.class, order = 1)
  @Nullable
  public UriComponents buildLinkForCategory(Category category, Map<String, Object> linkParameters,
                                            HttpServletRequest request) {
    PreviewUrlService previewUrlService = getPreviewUrlService();
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceCategoryLinks(request)) {
      return null;
    }

    // if it's not studio preview build a dummy link that will be later replaced by the commerce link resolver
    if (!isStudioPreviewRequest(request)) {
      return EMPTY_URI_COMPONENT_BUILDER;
    }

    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

    return previewUrlService.getCategoryUrl(category, queryParamList, request);
  }

  @Nullable
  @Link(type = CategoryInSite.class)
  public UriComponents buildLinkForCategoryInSite(CategoryInSite categoryInSite, Map<String, Object> linkParameters,
                                                  HttpServletRequest request) {
    Category category = categoryInSite.getCategory();
    if (category == null) {
      // not responsible
      return null;
    }

    return buildLinkForCategory(category, linkParameters, request);
  }

  @Link(type = Product.class, order = 1)
  @Nullable
  public UriComponents buildLinkForProduct(Product product, Map<String, Object> linkParameters,
                                           HttpServletRequest request) {
    PreviewUrlService previewUrlService = getPreviewUrlService();
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceProductLinks(request)) {
      return null;
    }

    // if it's not studio preview build a dummy link that will be later replaced by the commerce link resolver
    if (!isStudioPreviewRequest(request)) {
      return EMPTY_URI_COMPONENT_BUILDER;
    }

    return buildProductPreviewUri(previewUrlService, product, linkParameters, request);
  }

  @Link(type = ProductInSite.class, order = 1)
  @Nullable
  public UriComponents buildLinkForProductInSite(ProductInSite productInSite, Map<String, Object> linkParameters,
                                                 HttpServletRequest request) {
    return buildLinkForProduct(productInSite.getProduct(), linkParameters, request);
  }

  @Link(type = CMProductTeaser.class, view = HandlerHelper.VIEWNAME_DEFAULT, order = 1)
  @Nullable
  public UriComponents buildLinkFor(CMProductTeaser productTeaser, Map<String, Object> linkParameters,
                                    HttpServletRequest request) {
    PreviewUrlService previewUrlService = getPreviewUrlService();
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceProductLinks(request)) {
      return null;
    }

    ProductInSite productInSite = productTeaser.getProductInSite();
    if (productInSite == null) {
      return null;
    }

    // if it's not studio preview build a dummy link that will be later replaced by the commerce link resolver
    if (!isStudioPreviewRequest(request)) {
      return EMPTY_URI_COMPONENT_BUILDER;
    }

    return buildProductPreviewUri(previewUrlService, productInSite.getProduct(), linkParameters, request);
  }

  @Link(type = LiveContextExternalProduct.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalProduct(LiveContextExternalProduct externalProduct,
                                                   Map<String, Object> linkParameters, HttpServletRequest request) {
    PreviewUrlService previewUrlService = getPreviewUrlService();
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceProductLinks(request)) {
      return null;
    }

    Product product = externalProduct.getProduct();
    if (product == null) {
      // not responsible
      return null;
    }

    //only build link for studio preview
    if (!isStudioPreviewRequest(request)) {
      return EMPTY_URI_COMPONENT_BUILDER;
    }

    return buildProductPreviewUri(previewUrlService, product, linkParameters, request);
  }

  @Link(type = CMExternalPage.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalPage(CMExternalPage externalPage, Map<String, Object> linkParameters,
                                                HttpServletRequest request) {
    CommerceConnection commerceConnection = findCommerceConnection(externalPage);
    if (commerceConnection == null) {
      return null;
    }

    PreviewUrlService previewUrlService = getPreviewUrlService();
    if (previewUrlService == null) {
      return null;
    }

    // if it's not studio preview build a dummy link that will be later replaced by the commerce link resolver
    if (!isStudioPreviewRequest(request)) {
      return EMPTY_URI_COMPONENT_BUILDER;
    }

    String externalId = externalPage.getExternalId();
    String externalUriPath = externalPage.getExternalUriPath();
    StoreContext storeContext = commerceConnection.getStoreContext();
    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

    return previewUrlService.getExternalPageUrl(externalId, externalUriPath, storeContext, queryParamList, request);
  }

  @Link(type = CMChannel.class, order = 1)
  @Nullable
  public UriComponents buildLinkForCMChannel(CMChannel channel, Map<String, Object> linkParameters,
                                             HttpServletRequest request) {
    CommerceConnection commerceConnection = findCommerceConnection(channel);
    if (commerceConnection == null) {
      return null;
    }

    PreviewUrlService previewUrlService = getPreviewUrlService();
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceLinkForChannel(channel)) {
      return null;
    }

    // if it's not studio preview build a dummy link that will be later replaced by the commerce link resolver
    if (!isStudioPreviewRequest(request)) {
      return EMPTY_URI_COMPONENT_BUILDER;
    }

    String seoPath = seoSegmentBuilder.asSeoSegment(channel, channel);
    StoreContext storeContext = commerceConnection.getStoreContext();
    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

    return previewUrlService.getContentUrl(seoPath, storeContext, queryParamList, request);
  }

  @Link(type = LiveContextCategoryNavigation.class, order = 1)
  @Nullable
  public UriComponents buildLinkForLiveContextCategoryNavigation(LiveContextCategoryNavigation categoryNavigation,
                                                                 Map<String, Object> linkParameters,
                                                                 HttpServletRequest request) {
    PreviewUrlService previewUrlService = getPreviewUrlService();
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceCategoryLinks(request)) {
      return null;
    }

    // if it's not studio preview build a dummy link that will be later replaced by the commerce link resolver
    if (!isStudioPreviewRequest(request)) {
      return EMPTY_URI_COMPONENT_BUILDER;
    }

    Category category = categoryNavigation.getCategory();

    return buildCategoryPreviewUri(previewUrlService, category, linkParameters, request);
  }

  @Link(type = LiveContextExternalChannelImpl.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalChannel(LiveContextExternalChannelImpl channel,
                                                   Map<String, Object> linkParameters, HttpServletRequest request) {
    CommerceConnection commerceConnection = findCommerceConnection(channel);
    if (commerceConnection == null) {
      return null;
    }

    if (!useCommerceCategoryLinks(request)) {
      return null;
    }

    PreviewUrlService previewUrlService = getPreviewUrlService(commerceConnection);
    if (previewUrlService == null) {
      return null;
    }

    // if it's not studio preview build a dummy link that will be later replaced by the commerce link resolver
    if (!isStudioPreviewRequest(request)) {
      return EMPTY_URI_COMPONENT_BUILDER;
    }

    Category category = channel.getCategory();

    return buildCategoryPreviewUri(previewUrlService, category, linkParameters, request);
  }

  /**
   * Builds a category uri for the given category.
   *
   * @param category target category
   * @param request  current request
   * @return {@link UriComponents} for category link
   */
  @Nullable
  private UriComponents buildCategoryPreviewUri(PreviewUrlService previewUrlService, Category category,
                                                Map<String, Object> linkParameters, HttpServletRequest request) {
    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

    return previewUrlService.getCategoryUrl(category, queryParamList, request);
  }

  /**
   * Builds a product uri for the given product.
   *
   * @param previewUrlService link service implementation
   * @param product           target product
   * @param linkParameters
   * @param request           current request
   * @return {@link UriComponents} for product link
   */
  private static UriComponents buildProductPreviewUri(PreviewUrlService previewUrlService, Product product,
                                                      Map<String, Object> linkParameters, HttpServletRequest request) {
    Category category = product.getCategory();
    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

    return previewUrlService.getProductUrl(product, category, queryParamList, request);
  }

  @SuppressWarnings("ConstantConditions")
  private boolean useCommerceProductLinks(HttpServletRequest request) {
    Site site = SiteHelper.findSite(request).orElse(null);
    return settingsService.settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS, Boolean.class, true, site);
  }

  private boolean useCommerceCategoryLinks(ServletRequest request) {
    Site site = SiteHelper.findSite(request).orElse(null);
    return settingsService.settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_CATEGORY_LINKS, Boolean.class, false, site);
  }

  private boolean useCommerceLinkForChannel(CMChannel channel) {
    return commerceLedPageExtension.isCommerceLedChannel(channel);
  }

  private CommerceConnection findCommerceConnection(CMChannel channel) {
    return commerceConnectionSupplier.findConnectionForContent(channel.getContent()).orElse(null);
  }

  /**
   * To evaluate if the newPreviewSession query parameter has to be applied to a commerce url, the evaluator has to know
   * if it's the first request triggered by a studio action (e.g. open in tab) or it's a follow up trigger by an author
   * clicking in the preview.
   * If it's a request triggered by a studio action an author want's to have a cleared session (no logged in user or
   * p13n context). If he tests in studio the preview the author want to stay logged in and use the same p13n context.
   *
   * @param request the current request
   * @return true if the request was triggered by a studio action.
   */
  private static boolean isStudioPreviewRequest(HttpServletRequest request) {
    return PreviewHandler.isStudioPreviewRequest(request);
  }

  @Nullable
  private static PreviewUrlService getPreviewUrlService() {
    return getPreviewUrlService(CurrentCommerceConnection.get());
  }

  @Nullable
  private static PreviewUrlService getPreviewUrlService(CommerceConnection commerceConnection) {
    return commerceConnection.getPreviewUrlService().orElse(null);
  }
}
