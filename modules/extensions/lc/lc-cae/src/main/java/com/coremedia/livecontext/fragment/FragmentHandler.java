package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.cae.layout.ContentBeanBackedPageGridPlacement;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.common.util.Predicate;
import com.coremedia.livecontext.fragment.pagegrid.PageGridPlacementResolver;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import static com.coremedia.objectserver.web.HandlerHelper.notFound;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

/**
 * Common base class for fragment handler. Each handler implements the Predicate interface
 * to decide if the concrete instance is responsible for the call, depending on the parameters in the FragmentParameters
 * object.
 */
public abstract class FragmentHandler extends PageHandlerBase implements Predicate<FragmentParameters> {

  private PageGridPlacementResolver pageGridPlacementResolver;
  protected ValidationService<Linkable> validationService;

  /**
   * Creates the ModelAndView depending on the parameters passed.
   * <p>
   * The method may return null to indicate "not found".
   * However, it is recommended to return a {@link HandlerHelper#notFound}
   * object with a specific message instead.
   *
   * @param params  The FragmentParameters instance that contains all matrix parameters
   * @param request The servlet request
   * @return The ModelAndView to create for the given parameters.
   */
  abstract ModelAndView createModelAndView(FragmentParameters params, HttpServletRequest request);

  @Nonnull
  protected ModelAndView createModelAndView(
          @Nonnull Navigation navigation,
          @Nullable String view) {
    return createModelAndView(asPage(navigation, navigation), view);
  }

  @Nonnull
  protected ModelAndView createFragmentModelAndView(
          @Nonnull Navigation navigation,
          @Nullable String view,
          @Nonnull CMChannel rootChannel) {
    CMContext context = navigation.getContext();
    if (context != null) {
      return createModelAndView(asPage(context, context), view);
    }

    LOG.info("Could not find a content based context for category '{}'. Will use the root channel instead.", navigation.getTitle());
    return createModelAndView(asPage(rootChannel, rootChannel), view);
  }

  @Nonnull
  protected ModelAndView createFragmentModelAndViewForPlacementAndView(
          @Nonnull Navigation navigation,
          @Nonnull String placement,
          @Nullable String view,
          @Nonnull CMChannel rootChannel) {
    CMContext context = navigation.getContext();
    if (context instanceof CMChannel) {
      return createModelAndViewForPlacementAndView((CMChannel) context, placement, view);
    }

    LOG.info("Could not find a content based context for category '{}'. Will use the root channel instead.", navigation.getTitle());
    return createModelAndViewForPlacementAndView(rootChannel, placement, view);
  }

  @Nonnull
  protected ModelAndView createModelAndViewForPlacementAndView(
          @Nonnull CMChannel channel,
          @Nonnull String placementName,
          @Nullable String view) {

    //noinspection unchecked
    if (!validationService.validate(channel)) {
      return handleInvalidLinkable(channel);
    }
    PageGridPlacement placement =  pageGridPlacementResolver.resolvePageGridPlacement(channel, placementName);
    if (placement == null) {
      LOG.error("No placement named {} found for {}.", placementName, channel.getContent().getPath());
      return notFound("No placement found for name '" + placementName + "'");
    }
    CMNavigation context = channel;
    // Take the context  of the placement for building the page . In most cases, this is the given channel.
    // For PDPs a specific navigation can be defined which differs from the given channel.
    if (placement instanceof ContentBeanBackedPageGridPlacement) {
      ContentBeanBackedPageGridPlacement contentBeanBackedPageGridPlacement =
              (ContentBeanBackedPageGridPlacement) placement;
      if (contentBeanBackedPageGridPlacement.getNavigation() != null) {
        context = contentBeanBackedPageGridPlacement.getNavigation();
      }
    }

    Page page = asPage(context, context);
    ModelAndView modelAndView = HandlerHelper.createModelWithView(placement, view);
    RequestAttributeConstants.setPage(modelAndView, page);
    NavigationLinkSupport.setNavigation(modelAndView, channel);

    return modelAndView;
  }

  protected ModelAndView handleInvalidLinkable(Linkable linkable) {
    LOG.debug("Trying to render invalid content, returning {} ({}).", SC_NO_CONTENT, linkable.getSegment());
    return notFound("invalid content: " + linkable.getSegment());
  }

  //-------------- Config --------------------

  @Required
  public void setPageGridPlacementResolver(PageGridPlacementResolver pageGridPlacementResolver) {
    this.pageGridPlacementResolver = pageGridPlacementResolver;
  }

  public ValidationService<Linkable> getValidationService() {
    return validationService;
  }

  @Required
  public void setValidationService(ValidationService<Linkable> validationService) {
    this.validationService = validationService;
  }
}
