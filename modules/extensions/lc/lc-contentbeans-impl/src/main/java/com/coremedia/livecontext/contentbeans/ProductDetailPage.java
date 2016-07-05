package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.context.LiveContextNavigation;

/**
 * Custom page that renders the PDP page grid instead of the regular page grid.
 * <p>
 * Does not work with arbitrary Navigations, but only with LiveContextExternalChannels.
 */
public class ProductDetailPage extends PageImpl {
  public ProductDetailPage(boolean developerMode, SitesService sitesService, Cache cache) {
    super(developerMode, sitesService, cache);
  }

  /**
   * Better use setLiveContextNavigation for type safety.
   *
   * @param navigation must be a LiveContextNavigation
   */
  @Override
  public final void setNavigation(Navigation navigation) {
    if (!(navigation instanceof LiveContextNavigation)) {
      throw new IllegalArgumentException("Navigation " + navigation + " is no LiveContextNavigation.  Use setLiveContextNavigation in order to avoid this kind of mismatch.");
    }
    setLiveContextNavigation((LiveContextNavigation)navigation);
  }

  public void setLiveContextNavigation(LiveContextNavigation navigation) {
    super.setNavigation(navigation);
  }

  @Override
  public PageGrid getPageGrid() {
    LiveContextExternalChannel externalChannel = getExternalChannel();
    if (externalChannel==null) {
      throw new IllegalStateException("Must set an external channel before accessing the pagegrid.");
    }
    return externalChannel.getPdpPagegrid();
  }

  protected LiveContextExternalChannel getExternalChannel() {
    // We ensured the type in the setter.  No need to check here.
    return (LiveContextExternalChannel)getNavigation();
  }
}
