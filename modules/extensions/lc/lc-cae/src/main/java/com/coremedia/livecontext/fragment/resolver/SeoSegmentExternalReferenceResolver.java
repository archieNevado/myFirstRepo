package com.coremedia.livecontext.fragment.resolver;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * External Content resolver for 'externalRef' values "cm-seosegment:segment/path[/seotitle-<contentid>]"
 */
public class SeoSegmentExternalReferenceResolver extends ExternalReferenceResolverBase {

  private static final Logger LOG = LoggerFactory.getLogger(SeoSegmentExternalReferenceResolver.class);

  private static final String SEO_SEGMENT_PREFIX = "cm-seosegment:";
  private static final String ID_DELIMITER = "-";
  private static final String PATH_DELIMITER = "--";

  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;
  private UrlPathFormattingHelper urlPathFormattingHelper;

  public SeoSegmentExternalReferenceResolver() {
    super(SEO_SEGMENT_PREFIX);
  }

  @Nullable
  @Override
  protected LinkableAndNavigation resolveExternalRef(@Nonnull FragmentParameters fragmentParameters,
                                                     @Nonnull String referenceInfo,
                                                     @Nonnull Site site) {

    String decodedSegmentPath = decode(referenceInfo);

    Content siteRootDocument = site.getSiteRootDocument();
    if (siteRootDocument == null) {
      LOG.warn("No site root document found for site: {}", site);
      return null;
    }

    Content navigation;
    Content linkable = null;

    // try to resolve rest of path as vanity URL: this will be null, if there is no vanity mapping
    String rootSegment = urlPathFormattingHelper.getVanityName(siteRootDocument);
    CMChannel rootChannel = (CMChannel) navigationSegmentsUriHelper.parsePath(Collections.singletonList(rootSegment));
    //handle the case when the vanity consists of multiple paths. We have to translate the internal delimiter back to '/'
    String vanity = decodedSegmentPath.replaceAll(PATH_DELIMITER, "/");
    CMLinkable target = (CMLinkable) rootChannel.getVanityUrlMapper().forPattern(vanity);
    if (target != null) {
      // vanity URL found: determine the context for the target in the current site
      CMContext context = contextHelper.findAndSelectContextFor(rootChannel, target);
      navigation = context == null ? siteRootDocument : context.getContent();
      linkable = target.getContent();
      LOG.debug("vanity URL found for {} pointing to {}", vanity, linkable);
      return new LinkableAndNavigation(linkable, navigation);
    }

    List<String> segmentList = new ArrayList<>();
    segmentList.add(rootSegment);
    if (StringUtils.isNotBlank(decodedSegmentPath)) {
      segmentList.addAll(Arrays.asList(decodedSegmentPath.split(PATH_DELIMITER)));
    }
    navigation = resolveNavigation(segmentList);
    final int indexOfDelimiter = decodedSegmentPath.lastIndexOf(ID_DELIMITER);
    if (navigation == null && indexOfDelimiter > 0) {
      // last segment path might be a Linkable with concrete content ID
      // first try to get parent navigation (without potential linkable path segment)
      final List<String> parentSegmentList = segmentList.subList(0, segmentList.size() - 1);
      LOG.debug("trying to lookup parent navigation {}", parentSegmentList);
      navigation = resolveNavigation(parentSegmentList);
      String potentialContentIdStr = decodedSegmentPath.substring(indexOfDelimiter + 1);
      if (navigation != null && StringUtils.isNumeric(potentialContentIdStr)) {
        LOG.debug("trying to lookup content by numeric id {}", potentialContentIdStr);
        linkable = resolveLinkable(Integer.parseInt(potentialContentIdStr));
        LOG.debug("numeric id {} resolved to {}", potentialContentIdStr, linkable);
      }
    } else {
      LOG.debug("found navigation object {} pointing to {}", vanity, linkable);
      // URL denotes a Navigation item without numeric ID, also use it as linkable
      linkable = navigation;
    }

    if (linkable != null && linkable.getType().isSubtypeOf("CMContext")) {
      linkable = navigation;
    }
    return new LinkableAndNavigation(linkable, navigation);
  }

  private Content resolveNavigation(List<String> segmentList) {
    Navigation navigation = navigationSegmentsUriHelper.parsePath(segmentList);
    if (navigation != null) {
      LOG.debug("found navigation content {} for segment list {}", navigation, segmentList);
      return navigation.getContext().getContent();
    }
    LOG.debug("no navigation content found for segment list {}", segmentList);
    return null;
  }

  private Content resolveLinkable(int contentId) {
    String capId = IdHelper.formatContentId(contentId);
    return contentRepository.getContent(capId);
  }

  private static String decode(String str) {
    try {
      return URLDecoder.decode(str, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOG.trace("Cannot decode string {}", str, e);
    }
    return str;
  }

  @Required
  public void setNavigationSegmentsUriHelper(NavigationSegmentsUriHelper navigationSegmentsUriHelper) {
    this.navigationSegmentsUriHelper = navigationSegmentsUriHelper;
  }

  @Required
  public void setUrlPathFormattingHelper(UrlPathFormattingHelper urlPathFormattingHelper) {
    this.urlPathFormattingHelper = urlPathFormattingHelper;
  }
}
