package com.coremedia.blueprint.elastic.base;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.common.CapObjectDestroyedException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteDestroyedException;
import com.coremedia.cap.multisite.SitesService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.join;

/**
 * Helper bean to compute the configured tenants (from root navigation settings)
 */
@Component
public class TenantHelper {

  private static final Logger LOG = LoggerFactory.getLogger(TenantHelper.class);

  public static final String SETTINGS_STRUCT = "elasticSocial";

  private final SitesService sitesService;
  private final SettingsService settingsService;

  public TenantHelper(SitesService sitesService, SettingsService settingsService) {
    this.sitesService = sitesService;
    this.settingsService = settingsService;
  }

  public Collection<String> readTenantsFromContent() {
    Collection<String> tenants = new HashSet<>();
    for (Site site : sitesService.getSites()) {
      try {
        Map<String, Object> settingsMap = getSettingsAsMap(site.getSiteRootDocument());
        String tenant = join(settingsMap.get("tenant"));
        if (!StringUtils.isEmpty(tenant)) {
          tenants.add(tenant);
        }
      } catch (CapObjectDestroyedException | SiteDestroyedException e) {
        LOG.debug("ignoring destroyed site '{}'", site.getId(), e);
      }
    }
    return Collections.unmodifiableCollection(tenants);
  }

  private Map<String, Object> getSettingsAsMap(final Content rootNavigation) {
    return settingsService.settingAsMap(SETTINGS_STRUCT, String.class, Object.class, rootNavigation);
  }

}
