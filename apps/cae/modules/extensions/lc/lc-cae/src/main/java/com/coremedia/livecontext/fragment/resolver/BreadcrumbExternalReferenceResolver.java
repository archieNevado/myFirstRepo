package com.coremedia.livecontext.fragment.resolver;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.resolver.ContentSeoSegmentExternalReferenceResolver.Ids;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Resolves the breadcrumb for full page layouts in the commerce led scenario.
 */
public class BreadcrumbExternalReferenceResolver extends ExternalReferenceResolverBase {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String PREFIX = "cm-breadcrumb";

  public BreadcrumbExternalReferenceResolver() {
    super(PREFIX);
  }

  @Override
  protected boolean include(@NonNull FragmentParameters fragmentParameters, @NonNull String referenceInfo) {
    return true;
  }

  @Nullable
  @Override
  protected LinkableAndNavigation resolveExternalRef(@NonNull FragmentParameters fragmentParameters,
                                                     @NonNull String referenceInfo,
                                                     @NonNull Site site) {
    //no SEO segment passed, so we only render the root channel which results in an empty breadcrumb
    if (fragmentParameters.getParameter() == null) {
      Content channel = site.getSiteRootDocument();
      return new LinkableAndNavigation(channel, channel);
    }

    //regular breadcrumb building using the SEO segment instead
    String parameter = fragmentParameters.getParameter();
    Ids ids = ContentSeoSegmentExternalReferenceResolver.parseExternalReferenceInfo(parameter);
    if (ids == null) {
      return null;
    }

    String contentId = IdHelper.formatContentId(ids.contentId);
    Content linkable = contentRepository.getContent(contentId);

    Content channel;
    if (ids.contextId != null) {
      String contextId = IdHelper.formatContentId(ids.contextId);
      channel = contentRepository.getContent(contextId);
    } else {
      channel = linkable;
    }

    LOG.debug("Resolved breadcrumb '{}' to linkable '{}' and channel '{}'.", parameter, linkable, channel);

    return new LinkableAndNavigation(linkable, channel);
  }
}
