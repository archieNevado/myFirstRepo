package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.logictypes.CommerceLedLinkBuilderHelper;
import com.coremedia.objectserver.web.links.Link;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.util.Collections.emptyMap;

/**
 * Link Builder for CMChannels that are supposed to link to the commerce system.
 */
@Link
@RequestMapping
public class LiveContextChannelLinkBuilder extends LiveContextPageHandlerBase {

  private CommerceLedLinkBuilderHelper commerceLedLinkBuilderHelper;
  private SearchLandingPagesLinkBuilderHelper searchLandingPagesLinkBuilderHelper;
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Required
  public void setSearchLandingPagesLinkBuilderHelper(SearchLandingPagesLinkBuilderHelper searchLandingPagesLinkBuilderHelper) {
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

  // There is no request mapping, since micro site requests are handled by the commerce system

  // --- LinkSchemes ---------------------------------------------------------------------------------------------------

  @Link(type = CMChannel.class)
  @Nullable
  public Object buildLinkForChannel(@NonNull CMChannel channel,
                                    @NonNull HttpServletRequest request) {
    Optional<Site> siteOptional = findSite(channel);
    CommerceConnection commerceConnection = siteOptional
            .flatMap(commerceConnectionInitializer::findConnectionForSite).orElse(null);
    if (commerceConnection == null) {
      return null;
    }

    StoreContext storeContext = commerceConnection.getStoreContext();

    // Channel case
    if (commerceLedLinkBuilderHelper.isCommerceLedChannel(channel)) {
      return createLinkForChannelInShop(channel, storeContext, request);
    }

    //noinspection ConstantConditions site must have been present to find the commerce connection
    Site site = siteOptional.get();
    // Search Landing Page case
    if (searchLandingPagesLinkBuilderHelper.isSearchLandingPage(channel, site)) {
      return searchLandingPagesLinkBuilderHelper.createSearchLandingPageURLFor(channel, commerceConnection, request, storeContext);
    }

    return null;
  }

  @NonNull
  private Optional<Site> findSite(@NonNull CMChannel channel) {
    return getSitesService().getContentSiteAspect(channel.getContent()).findSite();
  }

  /**
   * Create a link to the Channel, as used by the Studio preview.
   */
  private Object createLinkForChannelInShop(@NonNull CMChannel channel, @NonNull StoreContext storeContext, @NonNull HttpServletRequest request) {
    String seoSegment = commerceLedLinkBuilderHelper.getSeoSegmentForChannel(channel);
    if (StringUtils.isNotEmpty(seoSegment)) {
      seoSegment = seoSegment.replaceAll("--", "/");
    }

    String seoSegments = commerceLedLinkBuilderHelper.getContentURLKeyword() + "/" + seoSegment;

    return findCommercePropertyProvider()
            .map(urlProvider -> urlProvider.buildShopLink(seoSegments, emptyMap(), request, storeContext))
            .orElse(null);
  }
}
