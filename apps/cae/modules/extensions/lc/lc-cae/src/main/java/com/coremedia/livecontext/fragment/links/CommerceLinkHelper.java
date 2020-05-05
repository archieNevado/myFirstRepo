package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SiteHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Named;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.livecontext.handler.ExternalNavigationHandler.LIVECONTEXT_POLICY_COMMERCE_CATEGORY_LINKS;
import static com.coremedia.livecontext.product.ProductPageHandler.LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS;

@Named
@DefaultAnnotation(NonNull.class)
public class CommerceLinkHelper {

  private static final String LIVECONTEXT_CONTENT_LED = "livecontext.contentLed";

  private final CommerceLedLinkBuilderHelper commerceLedPageExtension;
  private final SettingsService settingsService;
  private final CommerceConnectionSupplier commerceConnectionSupplier;

  public CommerceLinkHelper(CommerceLedLinkBuilderHelper commerceLedPageExtension, SettingsService settingsService,
                            CommerceConnectionSupplier commerceConnectionSupplier) {
    this.commerceLedPageExtension = commerceLedPageExtension;
    this.settingsService = settingsService;
    this.commerceConnectionSupplier = commerceConnectionSupplier;
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

  static boolean isFragmentOrDynamicRequest(@NonNull HttpServletRequest request) {
    return isFragmentRequest(request) || isDynamicRequest(request);
  }

  public static boolean isFragmentRequest(@NonNull HttpServletRequest request) {
    return FragmentContextProvider.findFragmentContext(request)
            .map(FragmentContext::isFragmentRequest)
            .orElse(false);
  }

  private static boolean isDynamicRequest(@NonNull HttpServletRequest request) {
    try {
      return request.getRequestURI().contains("/" + PREFIX_DYNAMIC + "/");
    } catch (UnsupportedOperationException ignored) {
      // we may end up here in case of elastic social registration which uses dummy requests internally :(
      return false;
    }
  }

  boolean isSiteContentLed(ServletRequest request){
    return findSiteSetting(request, LIVECONTEXT_CONTENT_LED).orElse(false);
  }

  Optional<CommerceConnection> findCommerceConnection(CMChannel channel) {
    return findCommerceConnection(channel.getContent());
  }

  private Optional<CommerceConnection> findCommerceConnection(Content content) {
    return commerceConnectionSupplier.findConnection(content);
  }

  static UriComponents toUriComponents(String uri) {
    return UriComponentsBuilder
            .fromUriString(uri)
            .build();
  }

}
