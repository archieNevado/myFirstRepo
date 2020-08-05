package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextHelper;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.PreviewUrlService;
import com.coremedia.livecontext.ecommerce.link.QueryParam;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.link.UrlUtil.convertToQueryParamList;
import static com.google.common.base.Strings.isNullOrEmpty;

@DefaultAnnotation(NonNull.class)
class CommerceStudioLinks {

  private final ExternalSeoSegmentBuilder seoSegmentBuilder;
  private final CommerceLinkHelper commerceLinkHelper;

  CommerceStudioLinks(ExternalSeoSegmentBuilder seoSegmentBuilder, CommerceLinkHelper commerceLinkHelper) {
    this.seoSegmentBuilder = seoSegmentBuilder;
    this.commerceLinkHelper = commerceLinkHelper;
  }

  Optional<UriComponents> buildLinkForCategory(Category category, Map<String, Object> linkParameters,
                                               HttpServletRequest request) {
    StoreContext storeContext = StoreContextHelper.findStoreContext(request).orElse(category.getContext());
    return CommerceLinkUtils.getPreviewUrlService(storeContext)
            .map(urlService -> urlService.getCategoryUrl(category, storeContext, convertToQueryParamList(linkParameters), request));
  }

  Optional<UriComponents> buildLinkForProduct(Product product, Map<String, Object> linkParameters,
                                              HttpServletRequest request) {
    return CommerceLinkUtils.getPreviewUrlService(product.getContext())
            .map(previewUrlService -> buildLinkForProduct(product, linkParameters, request, previewUrlService));
  }

  UriComponents buildLinkForProduct(Product product,
                                    Map<String, Object> linkParameters,
                                    HttpServletRequest request,
                                    PreviewUrlService previewUrlService) {
    Category category = product.getCategory();
    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);
    StoreContext storeContext = StoreContextHelper.findStoreContext(request).orElse(category.getContext());

    return previewUrlService.getProductUrl(product, category, storeContext, queryParamList, request);
  }

  Optional<UriComponents> buildLinkForExternalPage(CMExternalPage externalPage,
                                                   Map<String, Object> linkParameters,
                                                   HttpServletRequest request) {
    return commerceLinkHelper.findCommerceConnection(externalPage)
            .flatMap(connection -> buildLinkForExternalPage(externalPage, linkParameters, request, connection));
  }

  private Optional<UriComponents> buildLinkForExternalPage(CMExternalPage bean,
                                                           Map<String, Object> linkParameters,
                                                           HttpServletRequest request,
                                                           CommerceConnection connection) {
    return connection.getPreviewUrlService()
            .map(previewUrlService -> buildLinkForExternalPage(bean, linkParameters, request, connection, previewUrlService));
  }

  private UriComponents buildLinkForExternalPage(CMExternalPage bean,
                                                 Map<String, Object> linkParameters,
                                                 HttpServletRequest request,
                                                 CommerceConnection connection,
                                                 PreviewUrlService previewUrlService) {
    StoreContext storeContext = StoreContextHelper.findStoreContext(request).orElse(connection.getStoreContext());
    // build the studio preview link
    return buildPreviewLinkForExternalPage(bean, linkParameters, previewUrlService, storeContext, request);
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

  Optional<UriComponents> buildLinkForCMChannel(CMChannel channel,
                                                Map<String, Object> linkParameters,
                                                HttpServletRequest request) {

    return commerceLinkHelper.findCommerceConnection(channel)
            .flatMap(commerceConnection -> buildLinkForCMChannel(channel, linkParameters, request, commerceConnection));
  }

  Optional<UriComponents> buildLinkForCMChannel(CMChannel channel,
                                                Map<String, Object> linkParameters,
                                                HttpServletRequest request,
                                                CommerceConnection commerceConnection) {
    return commerceConnection.getPreviewUrlService()
            .map(service -> buildLinkForChannel(channel, linkParameters, request, commerceConnection, service));
  }

  private UriComponents buildLinkForChannel(CMChannel channel,
                                            Map<String, Object> linkParameters,
                                            HttpServletRequest request,
                                            CommerceConnection commerceConnection,
                                            PreviewUrlService previewUrlService) {
    String seoPath = seoSegmentBuilder.asSeoSegment(channel, channel);
    List<QueryParam> queryParamList = convertToQueryParamList(linkParameters);

    StoreContext storeContext = StoreContextHelper.findStoreContext(request).orElse(commerceConnection.getStoreContext());
    return previewUrlService.getContentUrl(seoPath, storeContext, queryParamList, request);
  }

}
