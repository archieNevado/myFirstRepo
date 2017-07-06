package com.coremedia.ecommerce.studio.rest;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.cap.content.ContentRepositoryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.net.URLEncoder;

import static java.text.MessageFormat.format;

/**
 * An abstract catalog object as a RESTful resource.
 */
public abstract class CommerceBeanResource<Entity extends CommerceBean> extends AbstractCatalogResource<Entity> {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceBeanResource.class);
  private static final String ID_AND_SITE_PARAM = "{0}&site={1}";

  private AugmentationService augmentationService;
  private SitesService sitesService;

  @Inject
  private ContentRepositoryResource contentRepositoryResource;

  protected void fillRepresentation(CommerceBeanRepresentation representation) {
    CommerceBean entity = getEntity();

    if (entity == null) {
      CatalogRestException catalogRestException = new CatalogRestException(Response.Status.NOT_FOUND,
              CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN,
              "Could not load commerce bean with id " + getId());
      LOG.error("Error loading commerce bean", catalogRestException);
      throw catalogRestException;
    }

    representation.setId(entity.getId());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
    representation.setCustomAttributes(entity.getCustomAttributes());

    //set preview url
    representation.setPreviewUrl(computePreviewUrl());

  }

  String computePreviewUrl() {
    String previewControllerUriPattern = getContentRepositoryResource().getPreviewControllerUrlPattern();
    return formatPreviewUrl(previewControllerUriPattern, URLEncoder.encode(getEntity().getId()), getSiteId());
  }

  public static String formatPreviewUrl(String previewControllerUriPattern, String id, String siteId) {
    // position 0 is reserved for formatted IDs, position 1 is reserved for numeric content IDs
    // the site param is appended to the formatted ID
    String idAndSiteParam = format(ID_AND_SITE_PARAM, id, siteId);
    return format(previewControllerUriPattern, idAndSiteParam);
  }

  public ContentRepositoryResource getContentRepositoryResource() {
    return contentRepositoryResource;
  }

  @Override
  public void setEntity(Entity entity) {
    setId(entity.getExternalId());
    StoreContext context = entity.getContext();
    setSiteId(context.getSiteId());
    setWorkspaceId(context.getWorkspaceId());
  }

  /**
   * @return the augmenting content which links to this commerce resource
   */
  @Nullable
  protected Content getContent() {
    if (augmentationService == null) {
      return null;
    }

    Site site = sitesService.getSite(getEntity().getContext().getSiteId());
    if (site == null) {
      return null;
    }

    return augmentationService.getContent(getEntity());
  }

  /**
   * Set augmentation service in case the commerce bean can be augmented.
   * @param augmentationService the augmentation service matching the concrete resource type
   */
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Autowired
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

}
