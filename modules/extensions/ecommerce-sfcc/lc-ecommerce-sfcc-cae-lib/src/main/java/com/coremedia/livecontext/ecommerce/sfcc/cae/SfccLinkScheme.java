package com.coremedia.livecontext.ecommerce.sfcc.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cap.multisite.Site;
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
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceConnection;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccCommerceIdProvider;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import com.coremedia.livecontext.navigation.LiveContextCategoryNavigation;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.handler.LiveContextPageHandlerBase.P13N_URI_PARAMETER;
import static com.coremedia.livecontext.product.ProductPageHandler.LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

/**
 * Custom link scheme for Salesforce Commerce Cloud LiveContext integration.
 */
@Link
public class SfccLinkScheme {

  private final SfccCommerceUrlProvider urlProvider;
  private final CommerceConnectionSupplier commerceConnectionSupplier;
  private final CommerceLedLinkBuilderHelper commerceLedPageExtension;
  private final SettingsService settingsService;

  private final String REDIRECT_SERVICE_URL_TEMPLATE_PREFIX = "/Sites-{storeId}-Site/{locale}/CM-RedirectUrl?link=";

  SfccLinkScheme(@NonNull SfccCommerceUrlProvider urlProvider,
                 @NonNull CommerceConnectionSupplier commerceConnectionSupplier,
                 @NonNull CommerceLedLinkBuilderHelper commerceLedPageExtension,
                 @NonNull SettingsService settingsService) {
    this.urlProvider = urlProvider;
    this.commerceConnectionSupplier = commerceConnectionSupplier;
    this.commerceLedPageExtension = commerceLedPageExtension;
    this.settingsService = settingsService;
  }


  @Link(type = Category.class, order = 2)
  public Object buildLinkForCategory(@NonNull Category category, String viewName, Map<String, Object> linkParameters,
                                     @NonNull HttpServletRequest request, HttpServletResponse response) {
    if (!isSfcc(category)) {
      return null;
    }
    return buildCategoryUri(category, request);
  }

  @Link(type = Product.class, order = 2)
  public Object buildLinkForProduct(@NonNull Product product, String viewName, Map<String, Object> linkParameters,
                                    @NonNull HttpServletRequest request, HttpServletResponse response) {
    if (!isSfcc(product)) {
      return null;
    }
    return buildProductUri(product, request);
  }

  @Link(type = ProductInSite.class, order = 2)
  public Object buildLinkForProductInSite(@NonNull ProductInSite productInSite, String viewName,
                                          Map<String, Object> linkParameters, @NonNull HttpServletRequest request,
                                          HttpServletResponse response) {
    if (!isSfcc(productInSite.getProduct())) {
      return null;
    }
    return buildProductUri(productInSite.getProduct(), request);
  }

  @Link(type = CMProductTeaser.class, view = HandlerHelper.VIEWNAME_DEFAULT, order = 2)
  public Object buildLinkFor(@NonNull CMProductTeaser productTeaser, String viewName, Map<String, Object> linkParameters,
                             @NonNull HttpServletRequest request, HttpServletResponse response) {
    if (!isSfcc(productTeaser.getExternalId())) {
      return null;
    }
    ProductInSite productInSite = productTeaser.getProductInSite();
    if (productInSite == null) {
      return null;
    }

    return buildLinkForProductInSite(productInSite, viewName, linkParameters, request, response);
  }

  @Link(type = LiveContextExternalProduct.class, order = 2)
  @Nullable
  public Object buildLinkForExternalProduct(@NonNull LiveContextExternalProduct externalProduct, String viewName,
                                            Map<String, Object> linkParameters, @NonNull HttpServletRequest request,
                                            HttpServletResponse response) {
    if (!isSfcc(externalProduct.getExternalId())) {
      return null;
    }

    Product product = externalProduct.getProduct();

    if (product == null || !useCommerceProductLinks(externalProduct.getSite())) {
      // not responsible
      return null;
    }

    return buildLinkForProduct(product, viewName, linkParameters, request, response);
  }

  @Link(type = CMExternalPage.class, order = 2)
  @Nullable
  public Object buildLinkForExternalPage(@NonNull CMExternalPage page, String viewName,
                                         Map<String, Object> linkParameters, @NonNull HttpServletRequest request,
                                         HttpServletResponse response) {
    Optional<CommerceConnection> sfccCommerceConnection = findSfccCommerceConnection(page);
    if (!sfccCommerceConnection.isPresent()) {
      return null;
    }
    StoreContext storeContext = sfccCommerceConnection.get().getStoreContext();

    //build url for homepage
    if (page.getExternalId() == null || page.getExternalId().isEmpty()){
      return buildLinkForCMChannel(page, viewName, linkParameters, request, response);
    }
    Map<String, Object> params = singletonMap("pageId", page.getExternalId());

    return buildUriFromTemplate(REDIRECT_SERVICE_URL_TEMPLATE_PREFIX +"Page-Show,cid,{pageId}",
            params, storeContext, request);
  }

  @Link(type = CMChannel.class, order = 2)
  @Nullable
  public Object buildLinkForCMChannel(@NonNull CMChannel channel, String viewName, Map<String, Object> linkParameters,
                                      @NonNull HttpServletRequest request, HttpServletResponse response) {
    Optional<CommerceConnection> sfccCommerceConnection = findSfccCommerceConnection(channel);
    if (!sfccCommerceConnection.isPresent() || !commerceLedPageExtension.isCommerceLedChannel(channel)) {
      return null;
    }
    StoreContext storeContext = sfccCommerceConnection.get().getStoreContext();

    Map<String, Object> params = emptyMap();

    String urlTemplate;
    if (channel.isRoot()) {
      // Special link building for root channel.
      urlTemplate = REDIRECT_SERVICE_URL_TEMPLATE_PREFIX+"Home-Show";
    } else {
      String pageId = channel.getSegment() + "--" + channel.getContentId();
      params = singletonMap("pageId", pageId);
      urlTemplate = REDIRECT_SERVICE_URL_TEMPLATE_PREFIX+"CM-Content,pageid,{pageId},view,asMicroSite";
    }
    return buildUriFromTemplate(urlTemplate, params, storeContext, request);
  }

  @Link(type = LiveContextCategoryNavigation.class, order = 2)
  @Nullable
  public Object buildLinkForLiveContextCategoryNavigation(@NonNull LiveContextCategoryNavigation categoryNavigation,
                                                          String viewName, Map<String, Object> linkParameters,
                                                          @NonNull HttpServletRequest request,
                                                          HttpServletResponse response) {
    if (!isSfcc(categoryNavigation.getCategory())) {
      return null;
    }
    return buildCategoryUri(categoryNavigation.getCategory(), request);
  }

  @Link(type = LiveContextExternalChannelImpl.class, order = 2)
  @Nullable
  public Object buildLinkForExternalChannel(@NonNull LiveContextExternalChannelImpl channel, String viewName,
                                            Map<String, Object> linkParameters, @NonNull HttpServletRequest request,
                                            HttpServletResponse response) {
    if (!isSfcc(channel.getExternalId())) {
      return null;
    }
    return buildCategoryUri(channel.getCategory(), request);
  }

  /**
   * Builds a category uri for the given category.
   *
   * @param category target category
   * @param request  current request
   * @return {@link UriComponents} for category link
   */
  @Nullable
  private UriComponents buildCategoryUri(@NonNull Category category, @NonNull HttpServletRequest request) {
    if (!isSfcc(category)) {
      return null;
    }
    Map<String, Object> params = singletonMap("categoryId", category.getExternalId());
    return buildUriFromTemplate(REDIRECT_SERVICE_URL_TEMPLATE_PREFIX+"Search-Show,cgid,{categoryId}",
            params, category.getContext(), request);
  }

  /**
   * Builds a product uri for the given product.
   *
   * @param product target product
   * @param request current request
   * @return {@link UriComponents} for product link
   */
  @Nullable
  private UriComponents buildProductUri(@NonNull Product product, @NonNull HttpServletRequest request) {
    if (!isSfcc(product)) {
      return null;
    }
    Map<String, Object> params = singletonMap("productId", product.getExternalId());
    return buildUriFromTemplate(REDIRECT_SERVICE_URL_TEMPLATE_PREFIX+"Product-Show,pid,{productId}",
            params, product.getContext(), request);
  }

  private UriComponents buildUriFromTemplate(@NonNull String urlTemplate, @NonNull Map<String, Object> params, @NonNull StoreContext storeContext, @NonNull HttpServletRequest request) {
    if (isStudioPreviewRequest(request)) {
      urlTemplate += ",preview,true";
    }
    UriComponentsBuilder uriBuilder = urlProvider.provideValue(urlTemplate, params, storeContext);
    return uriBuilder.build();
  }

  @SuppressWarnings("ConstantConditions")
  private boolean useCommerceProductLinks(@NonNull Site site) {
    return settingsService.settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS, Boolean.class, true, site);
  }

  private Optional<CommerceConnection> findSfccCommerceConnection(@NonNull CMChannel channel) {
    return commerceConnectionSupplier.findConnectionForContent(channel.getContent())
            .filter(SfccCommerceConnection.class::isInstance);
  }

  private static boolean isSfcc(@NonNull CommerceBean commerceBean) {
    return SfccCommerceIdProvider.isSfccId(commerceBean.getId());
  }

  private static boolean isSfcc(@NonNull String commerceId) {
    return CommerceIdParserHelper.parseCommerceId(commerceId)
            .map(SfccCommerceIdProvider::isSfccId)
            .orElse(false);
  }

  private static boolean isStudioPreviewRequest(@NonNull HttpServletRequest request) {
    return PreviewHandler.isStudioPreviewRequest(request) || "true".equals(request.getParameter(P13N_URI_PARAMETER));
  }

}
