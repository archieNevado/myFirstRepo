package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextHelper;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannel;
import com.coremedia.livecontext.contentbeans.LiveContextExternalProduct;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.PreviewUrlService;
import com.coremedia.livecontext.ecommerce.link.QueryParam;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import com.coremedia.objectserver.web.links.Link;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.link.UrlUtil.convertToQueryParamList;
import static com.coremedia.livecontext.handler.LiveContextPageHandlerBase.isInitialStudioRequest;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Link building for the studio preview of commerce beans.
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
public class CommerceStudioLinks {

  private final ExternalSeoSegmentBuilder seoSegmentBuilder;
  private final CommerceLinkHelper commerceLinkHelper;

  public CommerceStudioLinks(ExternalSeoSegmentBuilder seoSegmentBuilder, CommerceLinkHelper commerceLinkHelper) {
    this.seoSegmentBuilder = seoSegmentBuilder;
    this.commerceLinkHelper = commerceLinkHelper;
  }

  @Link(type = Category.class, order = 2)
  @Nullable
  public UriComponents buildLinkForCategory(Category category, Map<String, Object> linkParameters,
                                            HttpServletRequest request) {
    if (!isInitialStudioRequest(request) || !commerceLinkHelper.useCommerceCategoryLinks(request)) {
      // not responsible
      return null;
    }

    Optional<PreviewUrlService> previewUrlService = getPreviewUrlService(category);
    StoreContext storeContext = StoreContextHelper.findStoreContext(request).orElse(category.getContext());

    return previewUrlService
            .map(urlService -> urlService.getCategoryUrl(category, storeContext, convertToQueryParamList(linkParameters), request))
            .orElse(null);
  }


  @Link(type = LiveContextExternalChannel.class, order = 2)
  @Nullable
  public UriComponents buildLinkForExternalChannel(LiveContextExternalChannel augmentedCategory,
                                                   Map<String, Object> linkParameters, HttpServletRequest request) {
    Category category = augmentedCategory.getCategory();
    if (category == null) {
      return null;
    }

    return buildLinkForCategory(category, linkParameters, request);
  }

  @Link(type = Product.class, order = 2)
  @Nullable
  public UriComponents buildLinkForProduct(Product product, Map<String, Object> linkParameters,
                                           HttpServletRequest request) {

    if (!isInitialStudioRequest(request) || !commerceLinkHelper.useCommerceProductLinks(request)) {
      // not responsible
      return null;
    }

    PreviewUrlService previewUrlService = getPreviewUrlService(product).orElse(null);
    if (previewUrlService == null) {
      return null;
    }
    Category category = product.getCategory();
    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);
    StoreContext storeContext = StoreContextHelper.findStoreContext(request).orElse(category.getContext());

    return previewUrlService.getProductUrl(product, category, storeContext, queryParamList, request);
  }

  @Link(type = LiveContextExternalProduct.class, order = 2)
  @Nullable
  public UriComponents buildLinkForExternalProduct(LiveContextExternalProduct externalProduct,
                                                   Map<String, Object> linkParameters, HttpServletRequest request) {
    Product product = externalProduct.getProduct();
    if (product == null) {
      return null;
    }

    return buildLinkForProduct(product, linkParameters, request);
  }

  @Link(type = CMExternalPage.class, order = 2)
  @Nullable
  public UriComponents buildLinkForExternalPage(CMExternalPage externalPage, Map<String, Object> linkParameters,
                                                HttpServletRequest request) {
    if (!isInitialStudioRequest(request)) {
      // not responsible
      return null;
    }

    CommerceConnection commerceConnection = commerceLinkHelper.findCommerceConnection(externalPage).orElse(null);
    if (commerceConnection == null) {
      return null;
    }

    PreviewUrlService previewUrlService = commerceConnection.getPreviewUrlService().orElse(null);
    if (previewUrlService == null) {
      return null;
    }

    StoreContext storeContext = StoreContextHelper.findStoreContext(request).orElse(commerceConnection.getStoreContext());
    // build the studio preview link
    return buildPreviewLinkForExternalPage(externalPage, linkParameters, previewUrlService, storeContext, request);
  }

  @Nullable
  private UriComponents buildPreviewLinkForExternalPage(CMExternalPage externalPage, Map<String, Object> linkParameters,
                                                        PreviewUrlService previewUrlService, StoreContext storeContext,
                                                        HttpServletRequest request) {
    String externalId = externalPage.getExternalId();
    String externalUriPath = externalPage.getExternalUriPath();

    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

    if (!isNullOrEmpty(externalUriPath)) {
      return previewUrlService.getExternalPageNonSeoUrl(externalUriPath, storeContext, queryParamList, request);
    }

    return previewUrlService.getExternalPageSeoUrl(externalId, storeContext, queryParamList, request);
  }

  @Link(type = CMChannel.class, order = 2)
  @Nullable
  public UriComponents buildLinkForCMChannel(CMChannel channel, Map<String, Object> linkParameters,
                                             HttpServletRequest request) {

    if (!isInitialStudioRequest(request) || !commerceLinkHelper.useCommerceLinkForChannel(channel)) {
      // not responsible
      return null;
    }

    CommerceConnection commerceConnection = commerceLinkHelper.findCommerceConnection(channel).orElse(null);
    if (commerceConnection == null) {
      return null;
    }

    PreviewUrlService previewUrlService = commerceConnection.getPreviewUrlService().orElse(null);
    if (previewUrlService == null) {
      return null;
    }

    String seoPath = seoSegmentBuilder.asSeoSegment(channel, channel);
    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

    StoreContext storeContext = StoreContextHelper.findStoreContext(request).orElse(commerceConnection.getStoreContext());
    return previewUrlService.getContentUrl(seoPath, storeContext, queryParamList, request);
  }

  private static Optional<PreviewUrlService> getPreviewUrlService(CommerceBean commerceBean) {
    return commerceBean.getContext().getConnection().getPreviewUrlService();
  }
}
