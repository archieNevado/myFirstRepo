package com.coremedia.livecontext.fragment.links;

import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.PreviewUrlService;
import com.coremedia.livecontext.ecommerce.link.StorefrontRef;
import com.coremedia.livecontext.ecommerce.link.StorefrontRefKey;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.VIEW_PARAMETER;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.cae.handlers.HandlerBase.FRAGMENT_PREVIEW;

@DefaultAnnotation(NonNull.class)
public class CommerceLinkUtils {

  private CommerceLinkUtils() {
  }

  static Optional<PreviewUrlService> getPreviewUrlService(StoreContext storeContext) {
    return storeContext.getConnection().getPreviewUrlService();
  }

  static boolean isStudioPreviewRequest(HttpServletRequest request) {
    return PreviewHandler.isStudioPreviewRequest(request) || FRAGMENT_PREVIEW.equals(request.getParameter(VIEW_PARAMETER));
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

  static Optional<UriComponents> getUriComponents(StoreContext storeContext, StorefrontRefKey storefrontRefKey) {
    return getStorefrontRef(storeContext, storefrontRefKey)
            .map(StorefrontRef::toLink)
            .map(UriComponentsBuilder::fromUriString)
            .map(UriComponentsBuilder::build);
  }

  static Optional<StorefrontRef> getStorefrontRef(StoreContext storeContext, StorefrontRefKey templateKey) {
    return storeContext.getConnection().getLinkService()
            .flatMap(linkService -> linkService.getStorefrontRef(templateKey, storeContext));
  }
}
