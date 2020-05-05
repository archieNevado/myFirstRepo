package com.coremedia.livecontext.logictypes;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMDownload;
import com.coremedia.blueprint.common.contentbeans.CMExternalLink;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * Link Builder helper for CMChannels that are supposed to link to the commerce system.
 */
public class CommerceLedLinkBuilderHelper {

  private static final String LIVECONTEXT_POLICY_COMMERCE_PAGE_LINKS = "livecontext.policy.commerce-page-links";
  private static final String LIVECONTEXT_POLICY_COMMERCE_MICROSITE_LINKS = "livecontext.policy.commerce-microsite-links";

  private SettingsService settingsService;

  public boolean isCommerceLedChannel(CMChannel cmChannel) {
    return !(cmChannel instanceof CMExternalChannel) && isCommerceLedEnabledForChannel(cmChannel);
  }

  public boolean isCommerceLedLinkable(CMLinkable cmLinkable) {
    return !(cmLinkable instanceof CMDownload) &&
            !(cmLinkable instanceof CMExternalLink) &&
            isCommerceLedEnabledForLinkable(cmLinkable);
  }

  /**
   * Return true if the given channel link shall be rendered as a Commerce link.
   *
   * @param channel given channel
   * @return true if the given channel link shall be rendered as Commerce link.
   */
  private boolean isCommerceLedEnabledForChannel(@NonNull CMChannel channel) {
    //noinspection ConstantConditions
    return settingsService.getSetting(LIVECONTEXT_POLICY_COMMERCE_PAGE_LINKS, Boolean.class, channel).orElse(false)
            || settingsService.getSetting(LIVECONTEXT_POLICY_COMMERCE_MICROSITE_LINKS, Boolean.class, channel).orElse(false);
  }

  /**
   * Return true if the given linkable link shall be rendered as a Commerce link.
   *
   * @param linkable given linkable
   * @return true if the given linkable shall be rendered as Commerce link.
   */
  private boolean isCommerceLedEnabledForLinkable(@NonNull CMLinkable linkable) {
    // we are currently looking at the same setting as for pages, maybe we should split this in the feature
    List<CMContext> contexts = linkable.getContexts();
    if (contexts != null && !contexts.isEmpty()) {
      return settingsService.getSetting(
              LIVECONTEXT_POLICY_COMMERCE_PAGE_LINKS, Boolean.class, linkable, contexts.get(0)).orElse(false);
    }
    return settingsService.getSetting(
            LIVECONTEXT_POLICY_COMMERCE_PAGE_LINKS, Boolean.class, linkable).orElse(false);
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }
}
