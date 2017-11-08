package com.coremedia.livecontext.logictypes;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.ExternalSeoSegmentBuilder;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.seo.SeoSegmentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;

/**
 * Link Builder for CMChannels that are supposed to link to the commerce system.
 */
public class CommerceLedPageExtension extends ExtensionBase {

  private String contentURLKeyword = DEFAULT_CM_CONTENT_URL_KEYWORD;

  // Logic
  public static final String DEFAULT_CM_CONTENT_URL_KEYWORD = "cm";
  private static final String LIVECONTEXT_POLICY_COMMERCE_PAGE_LINKS = "livecontext.policy.commerce-page-links";
  private static final String LIVECONTEXT_POLICY_COMMERCE_MICROSITE_LINKS = "livecontext.policy.commerce-microsite-links";

  private SeoSegmentBuilder seoSegmentBuilder;

  public boolean isCommerceLedChannel(CMObject cmObject) {
    return (cmObject instanceof CMChannel) && (!(cmObject instanceof CMExternalChannel)) && isCommerceLed((CMChannel) cmObject);
  }

  // Link Builder

  /**
   * Create a link to the Channel, as used by the Studio preview.
   *
   * @param channel given channel
   * @return link
   */
  public Object createLinkForChannelInShop(CMChannel channel) {
    if (!isCommerceLedChannel(channel)) {
      return null;
    }

    try {
      if (isCommerceLed(channel)) {
        Site site = getSitesService().getContentSiteAspect(channel.getContent()).getSite();
        if (site == null) {
          return null;
        }

        StoreContext storeContext = findStoreContextForSite(site).orElse(null);
        if (storeContext == null) {
          return null;
        }

        String seoSegment = seoSegmentBuilder.asSeoSegment(channel, channel);
        if (StringUtils.isNotEmpty(seoSegment)) {
          seoSegment = seoSegment.replaceAll("--", "/");
        }

        return buildCommerceLinkFor(null, getContentURLKeyword() + "/" + seoSegment,
                Collections.emptyMap(), CurrentCommerceConnection.get().getStoreContext());
      }
    } catch (Exception e) {
      LOG.error("Error determining URL for Channel", e);
    }

    return null;
  }

  @Nonnull
  private static Optional<StoreContext> findStoreContextForSite(@Nonnull Site site) {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getStoreContextProvider)
            .map(storeContextProvider -> storeContextProvider.findContextBySite(site));
  }

  /**
   * Return true if the given channel link shall be rendered as a WCS/Commerce link.
   *
   * @param channel given channel
   * @return true if the given channel link shall be rendered as Commerce link.
   */
  protected boolean isCommerceLed(CMChannel channel) {
    return getSettingsService().settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_PAGE_LINKS, Boolean.class, false, channel)
            || getSettingsService().settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_MICROSITE_LINKS, Boolean.class, false, channel);
  }

  public String getSeoSegmentForChannel(CMChannel channel) {
    return seoSegmentBuilder.asSeoSegment(channel, channel);
  }

  @Required
  public void setSeoSegmentBuilder(ExternalSeoSegmentBuilder seoSegmentBuilder) {
    this.seoSegmentBuilder = seoSegmentBuilder;
  }

  public String getContentURLKeyword() {
    return contentURLKeyword;
  }

  public void setContentURLKeyword(String contentURLKeyword) {
    this.contentURLKeyword = contentURLKeyword;
  }
}
