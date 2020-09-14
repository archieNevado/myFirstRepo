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
  private final boolean isFragmentPreview;

  CommerceLinkDispatcher(boolean fragmentRequest, boolean useCommerceLinks, boolean studioPreviewRequest, boolean isFragmentPreview) {
    this.fragmentRequest = fragmentRequest;
    this.useCommerceLinks = useCommerceLinks;
    this.studioPreviewRequest = studioPreviewRequest;
    this.isFragmentPreview = isFragmentPreview;
  }

  @Nullable
  UriComponents dispatch(Supplier<Optional<UriComponents>> studioLinkSupplier,
                         Supplier<Optional<UriComponents>> contentLedLinkSupplier) {
    // commerce led
    if (fragmentRequest && useCommerceLinks) {
      return DUMMY_URI_TO_BE_REPLACED;
    }

    // studio
    if (studioPreviewRequest && useCommerceLinks) {
      return studioLinkSupplier.get().orElse(null);
    }

    // content led
    if (!fragmentRequest && useCommerceLinks) {
      return contentLedLinkSupplier.get()
              .orElseGet(() -> {
                // fallback to studioLinkSupplier, if Studio fragment preview and no content-led link available
                if (isFragmentPreview) {
                  return studioLinkSupplier.get().orElse(null);
                }
                return null;
              });
    }

    return null;
  }

}
