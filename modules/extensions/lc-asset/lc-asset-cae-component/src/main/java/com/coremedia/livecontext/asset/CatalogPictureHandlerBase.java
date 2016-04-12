package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.cae.handlers.HandlerBase;
import com.coremedia.blueprint.cae.handlers.TransformedBlobHandler;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.objectserver.web.HandlerHelper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CatalogPictureHandlerBase extends HandlerBase {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogPictureHandlerBase.class);

  public static final String FORMAT_KEY_THUMBNAIL = "thumbnail";
  public static final String FORMAT_KEY_FULL = "full";

  private AssetService assetService;
  private LiveContextSiteResolver siteResolver;
  private ContentBeanFactory contentBeanFactory;
  private Map<String, String> pictureFormats;

  protected static final String STORE_ID = "storeId";
  protected static final String LOCALE = "locale";
  protected static final String PART_NUMBER = "partNumber";
  protected static final String FORMAT_NAME = "formatName";

  /**
   * handle picture request with the given store id, locale, picture format name, reference id and request
   * @param storeId the store id
   * @param locale the locale
   * @param formatName the picture format name
   * @param id the reference id
   * @param request the request
   */
  protected ModelAndView handleRequestWidthHeight(String storeId,
                                                String locale,
                                                String formatName,
                                                String id,
                                                HttpServletRequest request) throws IOException {
    Site site = siteResolver.findSiteFor(storeId, LocaleUtils.toLocale(locale));
    if (site == null) {
      //Site not found
      return HandlerHelper.notFound();
    }

    Content catalogPictureObject  = findCatalogPictureFor(id, site);

    if (catalogPictureObject == null) {
      //Picture not found
      return HandlerHelper.notFound();
    }

    String pictureFormat = pictureFormats.get(formatName);
    if (pictureFormat == null) {
      //format not found
      return HandlerHelper.notFound();
    }

    //picture format value consists of <transformation segment>/<width>/<height>
    String[] split = pictureFormat.split("/");
    String transformationName = split[0];
    String width = split[1];
    String height = split[2];

    //redirect
    request.setAttribute(ViewUtils.PARAMETERS, ImmutableMap.<String, Object>of(
            TransformedBlobHandler.WIDTH_SEGMENT, width,
            TransformedBlobHandler.HEIGHT_SEGMENT, height,
            TransformedBlobHandler.TRANSFORMATION_SEGMENT, transformationName
    ));

    CMPicture catalogPicture = contentBeanFactory.createBeanFor(catalogPictureObject, CMPicture.class);
    return HandlerHelper.redirectTo(catalogPicture.getTransformedData(transformationName));
  }

  /**
   * find the catalog picture of the given reference id and site
   * @param id the reference id
   * @param site the given site
   * @return the found catalog picture document
   */
  private Content findCatalogPictureFor(String id, Site site) {
    if(null != assetService) {
      List<Content> pictureList = assetService.findPictures(id);
      if (pictureList.size() > 1) {
        LOG.debug("More than one CMPicture found for the catalog object with the id " + id + " in the site " + site.getName());
      }
      if(!pictureList.isEmpty()) {
        return pictureList.get(0);
      }
    }
    return null;
  }

  @Required
  public void setSiteResolver(LiveContextSiteResolver siteResolver) {
    this.siteResolver = siteResolver;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setPictureFormats(Map<String, String> pictureFormats) {
    this.pictureFormats = pictureFormats;
  }

  @Autowired(required = false)
  public void setAssetService(AssetService assetService) {
    this.assetService = assetService;
  }

}
