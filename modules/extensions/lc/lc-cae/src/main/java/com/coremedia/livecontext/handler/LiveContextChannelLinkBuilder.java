package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.livecontext.logictypes.CommerceLedPageExtension;
import com.coremedia.livecontext.logictypes.SearchLandingPagesExtension;
import com.coremedia.objectserver.web.links.Link;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Link Builder for CMChannels that are supposed to link to the commerce system.
 */
@Link
@RequestMapping
public class LiveContextChannelLinkBuilder extends LiveContextPageHandlerBase {

  private CommerceLedPageExtension commerceLedPageExtension;
  private SearchLandingPagesExtension searchLandingPagesExtension;

  public static final String DEFAULT_CM_CONTENT_URL_KEYWORD = "cm";

  @Required
  public void setSearchLandingPagesExtension(SearchLandingPagesExtension searchLandingPagesExtension) {
    this.searchLandingPagesExtension = searchLandingPagesExtension;
  }

  @Required
  public void setCommerceLedPageExtension(CommerceLedPageExtension commerceLedPageExtension) {
    this.commerceLedPageExtension = commerceLedPageExtension;
  }

  // There is no request mapping, since micro site requests are handled by the commerce system

  // --- LinkSchemes ---------------------------------------------------------------------------------------------------

  @Link(type = CMChannel.class)
  @Nullable
  public Object buildLinkForChannel(
          @Nonnull CMChannel channel,
          @Nullable String viewName,
          @Nonnull Map<String, Object> linkParameters) {

    // Channel case
    if (commerceLedPageExtension.isCommerceLedChannel(channel)) {
      return commerceLedPageExtension.createLinkForChannelInShop(channel);
    }

    // Search Landing Page case
    if (searchLandingPagesExtension.isSearchLandingPage(channel)) {
      return searchLandingPagesExtension.createSearchLandingPageURLFor(channel);
    }

    return null;
  }
}
