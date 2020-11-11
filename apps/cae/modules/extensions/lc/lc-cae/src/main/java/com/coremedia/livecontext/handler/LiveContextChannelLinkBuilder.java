package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.commercebeans.CategoryInSite;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import com.coremedia.objectserver.web.links.Link;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

/**
 * Link Builder for CMChannels that are supposed to link to the commerce system.
 *
 * @deprecated This link scheme is no longer needed and has been replaced by
 * {@link com.coremedia.livecontext.fragment.links.CommerceLinks#buildLinkForCategoryInSite(CategoryInSite, Map, HttpServletRequest)}
 */
@Link
@RequestMapping
@Deprecated
public class LiveContextChannelLinkBuilder extends LiveContextPageHandlerBase {

  private CommerceLedLinkBuilderHelper commerceLedLinkBuilderHelper;
  private SearchLandingPagesLinkBuilderHelper searchLandingPagesLinkBuilderHelper;
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Required
  public void setSearchLandingPagesLinkBuilderHelper(
          SearchLandingPagesLinkBuilderHelper searchLandingPagesLinkBuilderHelper) {
    this.searchLandingPagesLinkBuilderHelper = searchLandingPagesLinkBuilderHelper;
  }

  @Required
  public void setCommerceLedLinkBuilderHelper(CommerceLedLinkBuilderHelper commerceLedLinkBuilderHelper) {
    this.commerceLedLinkBuilderHelper = commerceLedLinkBuilderHelper;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }

  // There is no request mapping because micro site requests are handled by the commerce system.

  // --- LinkSchemes ---------------------------------------------------------------------------------------------------

  /**
   * @deprecated This link scheme is no longer needed and has been replaced by
   * {@link com.coremedia.livecontext.fragment.links.CommerceLinks#buildLinkForCMChannel(CMChannel, Map, HttpServletRequest)}
   */
  @SuppressWarnings("unused")
  @Link(type = CMChannel.class)
  @Nullable
  @Deprecated
  public UriComponents buildLinkForSearchLandingPage(@NonNull CMChannel channel) {
    Site site = findSite(channel).orElse(null);
    if (site == null) {
      return null;
    }

    // Search Landing Page case
    if (!commerceLedLinkBuilderHelper.isCommerceLedChannel(channel) &&
            searchLandingPagesLinkBuilderHelper.isSearchLandingPage(channel, site)) {

      CommerceConnection commerceConnection = commerceConnectionInitializer.findConnectionForSite(site).orElse(null);
      if (commerceConnection == null) {
        return null;
      }

      return searchLandingPagesLinkBuilderHelper
              .createSearchLandingPageURLFor(channel, commerceConnection)
              .orElse(null);
    }

    return null;
  }

  @NonNull
  private Optional<Site> findSite(@NonNull CMChannel channel) {
    return getSitesService().getContentSiteAspect(channel.getContent()).findSite();
  }
}
