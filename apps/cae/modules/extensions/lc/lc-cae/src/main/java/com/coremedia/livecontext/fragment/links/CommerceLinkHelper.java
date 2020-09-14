package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.cap.multisite.SiteHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import com.coremedia.objectserver.beans.ContentBean;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.VIEW_PARAMETER;
import static com.coremedia.blueprint.cae.handlers.HandlerBase.FRAGMENT_PREVIEW;
import static com.coremedia.blueprint.cae.handlers.PreviewHandler.isStudioPreviewRequest;
import static com.coremedia.livecontext.handler.ExternalNavigationHandler.LIVECONTEXT_POLICY_COMMERCE_CATEGORY_LINKS;
import static com.coremedia.livecontext.product.ProductPageHandler.LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS;

@DefaultAnnotation(NonNull.class)
class CommerceLinkHelper {

  private final CommerceLedLinkBuilderHelper commerceLedPageExtension;
  private final SettingsService settingsService;
  private final CommerceConnectionSupplier commerceConnectionSupplier;

  CommerceLinkHelper(CommerceLedLinkBuilderHelper commerceLedPageExtension,
                            SettingsService settingsService,
                            CommerceConnectionSupplier commerceConnectionSupplier) {
    this.commerceLedPageExtension = commerceLedPageExtension;
    this.settingsService = settingsService;
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  CommerceLinkDispatcher createCategoryLinkDispatcher(HttpServletRequest request) {
    boolean useCommerceLinks = useCommerceCategoryLinks(request);
    return createCommerceLinkDispatcher(request, useCommerceLinks);
  }

  CommerceLinkDispatcher createProductLinkDispatcher(HttpServletRequest request) {
    boolean useCommerceLinks = useCommerceProductLinks(request);
    return createCommerceLinkDispatcher(request, useCommerceLinks);
  }

  CommerceLinkDispatcher createCommerceLinkDispatcher(HttpServletRequest request, boolean useCommerceLinks) {
    boolean fragmentRequest = CommerceLinkUtils.isFragmentRequest(request);
    boolean studioPreviewRequest = isStudioPreviewRequest(request);
    boolean studioFragmentPreviewRequest = FRAGMENT_PREVIEW.equals(request.getParameter(VIEW_PARAMETER));
    return new CommerceLinkDispatcher(fragmentRequest, useCommerceLinks, studioPreviewRequest, studioFragmentPreviewRequest);
  }

  boolean useCommerceProductLinks(ServletRequest request) {
    return findSiteSetting(request, LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS).orElse(true);
  }

  boolean useCommerceCategoryLinks(ServletRequest request) {
    return findSiteSetting(request, LIVECONTEXT_POLICY_COMMERCE_CATEGORY_LINKS).orElse(false);
  }

  private Optional<Boolean> findSiteSetting(ServletRequest request, String settingName) {
    return SiteHelper.findSite(request)
            .flatMap(site -> settingsService.getSetting(settingName, Boolean.class, site));
  }

  boolean useCommerceLinkForChannel(CMChannel channel) {
    return commerceLedPageExtension.isCommerceLedChannel(channel);
  }

  boolean useCommerceLinkForLinkable(CMLinkable linkable) {
    return commerceLedPageExtension.isCommerceLedLinkable(linkable);
  }

  Optional<CommerceConnection> findCommerceConnection(ContentBean contentBean) {
    return commerceConnectionSupplier.findConnection(contentBean.getContent());
  }

}
