package com.coremedia.livecontext.fragment.links;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
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

@DefaultAnnotation(NonNull.class)
public class CommerceLinkUtils {

  private CommerceLinkUtils() {
  }

  public static boolean isFragmentRequest(@NonNull HttpServletRequest request) {
    return FragmentContextProvider.findFragmentContext(request)
            .map(FragmentContext::isFragmentRequest)
            .orElse(false);
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
