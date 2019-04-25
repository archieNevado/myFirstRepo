package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.link.UrlUtil.convertToQueryParamList;
import static com.coremedia.livecontext.handler.ExternalNavigationHandler.LIVECONTEXT_POLICY_COMMERCE_CATEGORY_LINKS;
import static com.coremedia.livecontext.product.ProductPageHandler.LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS;
import static com.google.common.base.Strings.isNullOrEmpty;

@DefaultAnnotation(NonNull.class)
@Link
public class CommerceLinkScheme {

  // dummy URL meant to be replaced by the commerce link resolver later on
  private static final UriComponents DUMMY_URI_TO_BE_REPLACED = toUriComponents("http://lc-generic-live.vm");

  private final CommerceConnectionSupplier commerceConnectionSupplier;
  private final CommerceLedLinkBuilderHelper commerceLedPageExtension;
  private final SettingsService settingsService;
  private final ExternalSeoSegmentBuilder seoSegmentBuilder;

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
    CommerceConnection commerceConnection = getCommerceConnection(category);
    PreviewUrlService previewUrlService = getPreviewUrlService(commerceConnection);
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceCategoryLinks(request)) {
      return null;
    }

    if (isStudioPreviewRequest(request)) {
      List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);
      return previewUrlService.getCategoryUrl(category, queryParamList, request);
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = CategoryInSite.class)
  @Nullable
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
    CommerceConnection commerceConnection = getCommerceConnection(product);
    PreviewUrlService previewUrlService = getPreviewUrlService(commerceConnection);
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceProductLinks(request)) {
      return null;
    }

    if (isStudioPreviewRequest(request)) {
      return buildProductPreviewUri(previewUrlService, product, linkParameters, request);
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
  public UriComponents buildLinkFor(CMProductTeaser productTeaser, Map<String, Object> linkParameters,
                                    HttpServletRequest request) {
    CommerceConnection commerceConnection = findCommerceConnection(productTeaser.getContent()).orElse(null);
    if (commerceConnection == null) {
      return null;
    }

    PreviewUrlService previewUrlService = getPreviewUrlService(commerceConnection);
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

    if (isStudioPreviewRequest(request)) {
      return buildProductPreviewUri(previewUrlService, productInSite.getProduct(), linkParameters, request);
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = LiveContextExternalProduct.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalProduct(LiveContextExternalProduct externalProduct,
                                                   Map<String, Object> linkParameters, HttpServletRequest request) {
    CommerceConnection commerceConnection = findCommerceConnection(externalProduct.getContent()).orElse(null);
    if (commerceConnection == null) {
      return null;
    }

    PreviewUrlService previewUrlService = getPreviewUrlService(commerceConnection);
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

    if (isStudioPreviewRequest(request)) {
      return buildProductPreviewUri(previewUrlService, product, linkParameters, request);
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = CMExternalPage.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalPage(CMExternalPage externalPage, Map<String, Object> linkParameters,
                                                HttpServletRequest request) {
    CommerceConnection commerceConnection = findCommerceConnection(externalPage).orElse(null);
    if (commerceConnection == null) {
      return null;
    }

    PreviewUrlService previewUrlService = getPreviewUrlService(commerceConnection);
    if (previewUrlService == null) {
      return null;
    }

    if (isStudioPreviewRequest(request)) {
      String externalId = externalPage.getExternalId();
      String externalUriPath = externalPage.getExternalUriPath();
      StoreContext storeContext = commerceConnection.getStoreContext();
      List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

      if (!isNullOrEmpty(externalUriPath)) {
        Optional<UriComponents> nonSeoUrl = previewUrlService
                .getExternalPageNonSeoUrl(externalUriPath, storeContext, queryParamList, request);
        if (nonSeoUrl.isPresent()) {
          return nonSeoUrl.get();
        }
      }

      return previewUrlService.getExternalPageSeoUrl(externalId, storeContext, queryParamList, request);
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = CMChannel.class, order = 1)
  @Nullable
  public UriComponents buildLinkForCMChannel(CMChannel channel, Map<String, Object> linkParameters,
                                             HttpServletRequest request) {
    CommerceConnection commerceConnection = findCommerceConnection(channel).orElse(null);
    if (commerceConnection == null) {
      return null;
    }

    PreviewUrlService previewUrlService = getPreviewUrlService(commerceConnection);
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceLinkForChannel(channel)) {
      return null;
    }

    if (isStudioPreviewRequest(request)) {
      String seoPath = seoSegmentBuilder.asSeoSegment(channel, channel);
      StoreContext storeContext = commerceConnection.getStoreContext();
      List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

      return previewUrlService.getContentUrl(seoPath, storeContext, queryParamList, request);
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = LiveContextCategoryNavigation.class, order = 1)
  @Nullable
  public UriComponents buildLinkForLiveContextCategoryNavigation(LiveContextCategoryNavigation categoryNavigation,
                                                                 Map<String, Object> linkParameters,
                                                                 HttpServletRequest request) {
    CommerceConnection commerceConnection = commerceConnectionSupplier.findConnection(categoryNavigation.getSite())
            .orElse(null);
    if (commerceConnection == null) {
      return null;
    }

    PreviewUrlService previewUrlService = getPreviewUrlService(commerceConnection);
    if (previewUrlService == null) {
      return null;
    }

    if (!useCommerceCategoryLinks(request)) {
      return null;
    }

    if (isStudioPreviewRequest(request)) {
      Category category = categoryNavigation.getCategory();
      return buildCategoryPreviewUri(previewUrlService, category, linkParameters, request);
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  @Link(type = LiveContextExternalChannelImpl.class, order = 1)
  @Nullable
  public UriComponents buildLinkForExternalChannel(LiveContextExternalChannelImpl channel,
                                                   Map<String, Object> linkParameters, HttpServletRequest request) {
    CommerceConnection commerceConnection = findCommerceConnection(channel).orElse(null);
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

    if (isStudioPreviewRequest(request)) {
      Category category = channel.getCategory();
      return buildCategoryPreviewUri(previewUrlService, category, linkParameters, request);
    }

    return DUMMY_URI_TO_BE_REPLACED;
  }

  /**
   * Builds a category uri for the given category.
   *
   * @param category target category
   * @param request  current request
   * @return {@link UriComponents} for category link
   */
  private static UriComponents buildCategoryPreviewUri(PreviewUrlService previewUrlService, Category category,
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

  private boolean useCommerceProductLinks(ServletRequest request) {
    return findSiteSetting(request, LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS).orElse(true);
  }

  private boolean useCommerceCategoryLinks(ServletRequest request) {
    return findSiteSetting(request, LIVECONTEXT_POLICY_COMMERCE_CATEGORY_LINKS).orElse(false);
  }

  private Optional<Boolean> findSiteSetting(ServletRequest request, String settingName) {
    return SiteHelper.findSite(request)
            .flatMap(site -> settingsService.getSetting(settingName, Boolean.class, site));
  }

  private boolean useCommerceLinkForChannel(CMChannel channel) {
    return commerceLedPageExtension.isCommerceLedChannel(channel);
  }

  private Optional<CommerceConnection> findCommerceConnection(CMChannel channel) {
    return findCommerceConnection(channel.getContent());
  }

  /**
   * To evaluate if the newPreviewSession query parameter has to be applied to a
   * commerce URL, the evaluator has to know if it's the first request triggered
   * by a Studio action (e.g. open in tab) or if it's a follow-up trigger by an
   * author clicking in the preview.
   * <p>
   * If it's a request triggered by a Studio action, an author wants to have a
   * cleared session (no logged in user or p13n context). If he tests in Studio,
   * the preview the author want to stay logged in and use the same p13n context.
   *
   * @param request the current request
   * @return true if the request was triggered by a Studio action.
   */
  private static boolean isStudioPreviewRequest(HttpServletRequest request) {
    return PreviewHandler.isStudioPreviewRequest(request);
  }

  @Nullable
  private static PreviewUrlService getPreviewUrlService(CommerceConnection commerceConnection) {
    return commerceConnection.getPreviewUrlService().orElse(null);
  }

  private static CommerceConnection getCommerceConnection(CommerceBean commerceBean) {
    return commerceBean.getContext().getConnection();
  }

  private Optional<CommerceConnection> findCommerceConnection(Content content) {
    return commerceConnectionSupplier.findConnection(content);
  }

  private static UriComponents toUriComponents(String uri) {
    return UriComponentsBuilder
            .fromUriString(uri)
            .build();
  }
}
