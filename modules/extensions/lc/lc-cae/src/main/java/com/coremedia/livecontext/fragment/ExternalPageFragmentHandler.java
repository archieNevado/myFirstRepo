package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public class ExternalPageFragmentHandler extends FragmentHandler {

  private ContextStrategy<String, Navigation> contextStrategy;
  private boolean fullPageInheritance = true;

  @Override
  ModelAndView createModelAndView(FragmentParameters params, HttpServletRequest request) {
    String pageId = params.getPageId();

    Site site = SiteHelper.getSiteFromRequest(request);
    if (site==null) {
      return HandlerHelper.notFound("Cannot derive a site from the request for page " + pageId);
    }
    CMChannel rootChannel = getContentBeanFactory().createBeanFor(site.getSiteRootDocument(), CMChannel.class);
    if (rootChannel==null) {
      return HandlerHelper.notFound("Site " + site.getName() + " for page " + pageId + " has no root channel");
    }

    // Find a suitable context for the page in question.
    // I.e. a corresponding external page or some parent context
    // determined by the contextStrategy.
    Navigation context = contextStrategy.findAndSelectContextFor(pageId, rootChannel);
    if (context==null) {
      // Fallback to rootChannel if the page cannot be found
      context = rootChannel;
    }

    String placement = params.getPlacement();
    if (StringUtils.isEmpty(placement)) {
      // No placement means that a complete page is requested.
      // Either the context is this page, or we have no such page.
      if (fullPageInheritance || isTheExternalPage(context, pageId)) {
        return createFragmentModelAndView(context, params.getView(), rootChannel);
      } else {
        return HandlerHelper.notFound("No explicit augmented page found for id " + pageId);
      }
    } else {
      // Only a particular fragment is requested.
      return createFragmentModelAndViewForPlacementAndView(context, placement, params.getView(), rootChannel);
    }
  }

  @Override
  public boolean include(FragmentParameters params) {
    return !StringUtils.isEmpty(params.getPageId()) &&
            StringUtils.isEmpty(params.getProductId()) &&
            StringUtils.isEmpty(params.getCategoryId()) &&
            (params.getExternalRef() == null || !params.getExternalRef().startsWith("cm-"));
  }

  // ------------ Config --------------------------------------------

  @Required
  public void setContextStrategy(ContextStrategy<String, Navigation> contextStrategy) {
    this.contextStrategy = contextStrategy;
  }

  /**
   * Control the inheritance for complete pages.
   * <p>
   * If you request a placement, it is eventually inherited from a parent page.
   * If you request a complete page however, there are different usecases.
   * In a content centric scenario, you want exactly the requested page or notFound.
   * In a view centric scenario (namely Aurora), parent pages are appropriate, if the
   * particular page does not exist.
   * <p>
   * Default is true.  (Backward compatible to older versions without this flag.)
   */
  public void setFullPageInheritance(boolean fullPageInheritance) {
    this.fullPageInheritance = fullPageInheritance;
  }


  // --- internal ---------------------------------------------------

  /**
   * Checks whether the navigation represents the external page of the given id.
   */
  private static boolean isTheExternalPage(Navigation navigation, String pageId) {
    return navigation instanceof CMExternalPage && pageId.equals(((CMExternalPage)navigation).getExternalId());
  }
}
