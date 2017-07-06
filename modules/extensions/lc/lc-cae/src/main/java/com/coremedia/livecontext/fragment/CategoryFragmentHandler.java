package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.user.User;
import com.coremedia.livecontext.context.ResolveContextStrategy;
import com.coremedia.livecontext.navigation.LiveContextCategoryNavigation;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.UserVariantHelper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Handles fragment request that depend on a category id.
 */
public class CategoryFragmentHandler extends FragmentHandler {

  private ResolveContextStrategy contextStrategy;
  private boolean useOriginalNavigationContext = false;

  // --- FragmentHandler --------------------------------------------

  /**
   * Renders the complete context (which is a CMChannel) of the given <code>category</code> using the given <code>view</code>.
   * If no context can be found for the category, the <code>view</code> of the root channel will be rendered. The site
   * is determined by the tuple <code>(storeId, locale)</code>, which must be unique across all sites. If the placement
   * value is passed as part of the fragment parameters, the model and view will be created for it.
   *
   * @param parameters All parameters that have been passed for the fragment call.
   * @return the {@link ModelAndView model and view} containing the {@link com.coremedia.blueprint.common.contentbeans.Page page}
   * as <code>self</code> object, that contains the context (CMChannel) that shall be rendered.
   */
  @Nullable
  @Override
  public ModelAndView createModelAndView(@Nonnull FragmentParameters parameters, @Nonnull HttpServletRequest request) {
    Site site = SiteHelper.getSiteFromRequest(request);
    if (site == null) {
      return HandlerHelper.notFound(getClass().getName() + " cannot derive a site from request "
              + request.getRequestURI());
    }

    Navigation navigation = contextStrategy.resolveContext(site, parameters.getCategoryId());
    if (navigation == null) {
      return HandlerHelper.notFound(getClass().getName() + " did not find a navigation for storeId \""
              + parameters.getStoreId() + "\", locale \"" + parameters.getLocale() + "\", category id \""
              + parameters.getCategoryId() + "\"");
    }

    String placement = parameters.getPlacement();
    String view = parameters.getView();
    Content siteRootDocument = site.getSiteRootDocument();
    CMChannel rootChannel = getContentBeanFactory().createBeanFor(siteRootDocument, CMChannel.class);
    User developer = UserVariantHelper.getUser(request);

    ModelAndView modelAndView = createBasicModelAndView(navigation, placement, view, rootChannel, developer);
    enhanceModelAndView(modelAndView, navigation);

    return modelAndView;
  }

  @Nonnull
  private ModelAndView createBasicModelAndView(@Nonnull Navigation navigation, @Nullable String placement,
                                               @Nullable String view, @Nonnull CMChannel rootChannel,
                                               @Nullable User developer) {
    if (isNullOrEmpty(placement)) {
      if (useOriginalNavigationContext) {
        return createModelAndView(navigation, view, developer);
      } else {
        return createFragmentModelAndView(navigation, view, rootChannel, developer);
      }
    }

    return createFragmentModelAndViewForPlacementAndView(navigation, placement, view, rootChannel, developer);
  }

  @Override
  public boolean include(@Nonnull FragmentParameters params) {
    String categoryId = params.getCategoryId();
    String externalRef = params.getExternalRef();

    return !isNullOrEmpty(categoryId) && (isNullOrEmpty(externalRef) || !externalRef.startsWith("cm-"));
  }

  // --- internal ---------------------------------------------------

  private void enhanceModelAndView(@Nonnull ModelAndView modelAndView, @Nonnull Navigation navigation) {
    if (navigation instanceof LiveContextCategoryNavigation) {
      modelAndView.addObject("lcNavigation", navigation);
    }
  }

  // ------------------- Config ---------------------------------

  @Required
  public void setContextStrategy(ResolveContextStrategy contextStrategy) {
    this.contextStrategy = contextStrategy;
  }

  /**
   * Enforce ModelAndViews with the actual Navigation, even if it is no
   * content backed bean.
   * <p>
   * Default is false (backward compatible).
   */
  public void setUseOriginalNavigationContext(boolean useOriginalNavigationContext) {
    this.useOriginalNavigationContext = useOriginalNavigationContext;
  }
}
