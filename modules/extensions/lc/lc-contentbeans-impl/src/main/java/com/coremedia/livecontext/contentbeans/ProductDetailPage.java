package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.SitesService;

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
   * Better use setExternalChannel for type safety.
   *
   * @param navigation must be a LiveContextExternalChannel
   */
  @Override
  public final void setNavigation(Navigation navigation) {
    if (!(navigation instanceof LiveContextExternalChannel)) {
      throw new IllegalArgumentException("Navigation " + navigation + " is no LiveContextExternalChannel.  Use setExternalChannel in order to avoid this kind of mismatch.");
    }
    setExternalChannel((LiveContextExternalChannel)navigation);
  }

  public void setExternalChannel(LiveContextExternalChannel navigation) {
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
