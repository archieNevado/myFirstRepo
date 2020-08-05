package com.coremedia.livecontext.fragment.links;

import com.coremedia.livecontext.fragment.links.transformers.LiveContextLinkTransformer;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.function.Supplier;

@DefaultAnnotation(NonNull.class)
class CommerceLinkDispatcher {

  private static final UriComponents DUMMY_URI_TO_BE_REPLACED = UriComponentsBuilder
          .fromUriString(LiveContextLinkTransformer.DUMMY_URI_STRING).build();

  private final boolean fragmentRequest;
  private final boolean useCommerceLinks;
  private final boolean studioPreviewRequest;

  CommerceLinkDispatcher(boolean fragmentRequest, boolean useCommerceLinks, boolean studioPreviewRequest) {
    this.fragmentRequest = fragmentRequest;
    this.useCommerceLinks = useCommerceLinks;
    this.studioPreviewRequest = studioPreviewRequest;
  }

  @Nullable
  UriComponents dispatch(Supplier<Optional<UriComponents>> studioLinkSupplier,
                         Supplier<Optional<UriComponents>> commerceLedLinkSupplier) {
    // commerce led
    if (fragmentRequest && useCommerceLinks) {
      return DUMMY_URI_TO_BE_REPLACED;
    }

    // studio
    Optional<UriComponents> uriComponents = Optional.empty();
    if (studioPreviewRequest && useCommerceLinks) {
      uriComponents = studioLinkSupplier.get();
    }

    return uriComponents.or(() -> {
      // content led
      if (!fragmentRequest && useCommerceLinks) {
        return commerceLedLinkSupplier.get();
      }
      return Optional.empty();
    }).orElse(null);
  }

}
