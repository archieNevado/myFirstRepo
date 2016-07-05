package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannel;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.handler.ExternalNavigationHandler;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.VIEW_PARAMETER;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_NAME;

@Link
@RequestMapping
public class LiveContextExternalChannelPreviewHandler extends LiveContextPageHandlerBase {

  private static final String SEGMENT_CATEGORY_PREVIEW = "categoryPreview";

  // e.g. /categoryPreview/perfectchef/segment-1234
  public static final String PREVIEW_URI_PATTERN =
          "/" + SEGMENT_CATEGORY_PREVIEW +
                  "/{" + SHOP_NAME_VARIABLE + "}" +
                  "/{" + SEGMENT_NAME + "}" +
                  "-{" + SEGMENT_ID + ":" + PATTERN_NUMBER + "}";

  private ExternalNavigationHandler externalNavigationHandler;

  @RequestMapping({PREVIEW_URI_PATTERN})
  public ModelAndView handleRequest(@PathVariable(SEGMENT_ID) LiveContextExternalChannel liveContextExternalChannel,
                                    @PathVariable(SEGMENT_NAME) String vanityName,
                                    @PathVariable(SHOP_NAME_VARIABLE) String siteSegment,
                                    @RequestParam(value = VIEW_PARAMETER, required = false) final String view) {
    Navigation navigation = getNavigation(siteSegment);
    if (null == navigation || !vanityName.equals(getVanityName(liveContextExternalChannel))) {
      return HandlerHelper.notFound();
    }
    Page page = asPage(navigation, liveContextExternalChannel);
    return createModelAndView(page, view);
  }

  @SuppressWarnings("unused")
  @Link(type = LiveContextExternalChannel.class)
  public Object buildLinkForExternalChannel(
          final LiveContextExternalChannel navigation,
          final String viewName,
          final Map<String, Object> linkParameters) {
    StoreContextProvider storeContextProvider = getStoreContextProvider();
    if (storeContextProvider != null && storeContextProvider.getCurrentContext() != null) {

      Category category;
      try {
        category = navigation.getCategory();
        // in case of the root category another link scheme should build the link
        // ...and that should lead to a fragment preview of the page grid
        if (category != null && category.isRoot()) {
          return null;
        }
      } catch (NotFoundException e) {
        return null;
      } catch (InvalidIdException e) {
        return null;
      }

      if (useCommerceCategoryLinks(navigation.getSite()) && category != null) {
        String seoSegment = category.getSeoSegment();
        Map<String, Object> updateParameters = (Map<String, Object>) updateQueryParams(category, linkParameters, seoSegment);
        return buildCommerceLinkFor(null, seoSegment, updateParameters);
      } else {
        return externalNavigationHandler.buildCaeLinkForCategory(navigation, viewName, linkParameters);
      }
    }
    // not responsible
    return null;
  }

  private boolean useCommerceCategoryLinks(Site site) {
    return externalNavigationHandler.useCommerceCategoryLinks(site);
  }

  private UriComponents buildPreviewLinkForLiveContextExternalChannel(
          @Nonnull final LiveContextExternalChannel navigation,
          final String viewName,
          final Map<String, Object> linkParameters) {
    Site site = navigation.getSite();
    String siteSegment = getSiteSegment(site);
    UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .newInstance()
            .pathSegment(SEGMENT_CATEGORY_PREVIEW)
            .pathSegment(siteSegment)
            .pathSegment(getVanityName(navigation) + "-" + navigation.getContentId());
    addViewAndParameters(uriBuilder, viewName, linkParameters);
    return uriBuilder.build();
  }

  @Required
  public void setExternalNavigationHandler(ExternalNavigationHandler externalNavigationHandler) {
    this.externalNavigationHandler = externalNavigationHandler;
  }
}
