package com.coremedia.blueprint.personalization.include;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.layout.Container;
import com.coremedia.blueprint.common.layout.DynamicContainerStrategy;
import com.coremedia.blueprint.personalization.contentbeans.CMP13NSearch;
import com.coremedia.blueprint.personalization.contentbeans.CMSelectionRules;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBean;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Required;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class P13NDynamicContainerStrategy implements DynamicContainerStrategy {

  public static final String P13N_DYNAMIC_INCLUDES_SETTING = "p13n-dynamic-includes-enabled";

  private SettingsService settingsService;
  private SitesService sitesService;

  @Override
  public boolean isEnabled(@NonNull Object bean) {
    if (!(bean instanceof ContentBean)) {
      return false;
    }
    Site site = sitesService.getContentSiteAspect(((ContentBean)bean).getContent()).getSite();
    if (site == null) {
      return false;
    }
    return settingsService.getSetting(P13N_DYNAMIC_INCLUDES_SETTING, Boolean.class, site).orElse(false);
  }

  @VisibleForTesting
  public boolean isDynamic(@NonNull List items) {
    return containsP13NItemRecursively(items, new HashSet<>());
  }

  private static boolean containsP13NItemRecursively(List items, Collection<Container> visited) {
    for (Object item : items) {
      if (item instanceof CMSelectionRules || item instanceof CMP13NSearch) {
        return true;
      }
      if (item instanceof Container) {
        Container container = (Container) item;
        if (!visited.contains(container)) {
          List children = container.getItems();
          visited.add(container);
          if (containsP13NItemRecursively(children, visited)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }
}
