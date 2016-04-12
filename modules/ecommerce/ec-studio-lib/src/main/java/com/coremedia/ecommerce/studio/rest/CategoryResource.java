package com.coremedia.ecommerce.studio.rest;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.studio.rest.model.ChildRepresentation;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A catalog {@link Category} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/category/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:.+}")
public class CategoryResource extends CommerceBeanResource<Category> {

  private AugmentationService augmentationService;
  private SitesService sitesService;

  @Override
  public CategoryRepresentation getRepresentation() {
    CategoryRepresentation categoryRepresentation = new CategoryRepresentation();
    fillRepresentation(categoryRepresentation);
    return categoryRepresentation;
  }

  protected void fillRepresentation(CategoryRepresentation representation) {
    try {
      super.fillRepresentation(representation);
      Category entity = getEntity();
      representation.setName(entity.getName());
      String shortDescription = entity.getShortDescription().asXml();
      representation.setShortDescription(shortDescription);
      String longDescription = entity.getLongDescription().asXml();
      representation.setLongDescription(longDescription);
      representation.setThumbnailUrl(RepresentationHelper.modifyAssetImageUrl(entity.getThumbnailUrl(), getContentRepositoryResource().getEntity()));
      representation.setParent(entity.getParent());
      representation.setSubCategories(entity.getChildren());
      representation.setProducts(entity.getProducts());
      representation.setStore((new Store(entity.getContext())));
      representation.setDisplayName(entity.getDisplayName());

      List<CommerceBean> children = new ArrayList<>();
      children.addAll(representation.getSubCategories());
      children.addAll(representation.getProducts());
      representation.setChildren(children);
      // get visuals directly via AssetService to avoid fallback to default picture
      AssetService assetService = getConnection().getAssetService();
      if(null != assetService) {
        representation.setVisuals(assetService.findVisuals(entity.getReference(), false));
      }
      representation.setPictures(entity.getPictures());
      representation.setDownloads(entity.getDownloads());

      Map<String, ChildRepresentation> result = new LinkedHashMap<>();
      for (CommerceBean child : children) {
        ChildRepresentation childRepresentation = new ChildRepresentation();
        childRepresentation.setChild(child);
        if(child instanceof Category) {
          childRepresentation.setDisplayName(((Category)child).getDisplayName());
        }
        else {
          childRepresentation.setDisplayName(child.getExternalId());
        }

        result.put(child.getId(), childRepresentation);
      }
      representation.setChildrenByName(result);

      representation.setContent(getContent());

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Category doGetEntity() {
    CommerceConnection commerceConnection = getConnection();
    String categoryId = commerceConnection.getIdProvider().formatCategoryId(getId());
    return commerceConnection.getCatalogService().findCategoryById(categoryId);
  }

  @Autowired(required = false)
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  /**
   * @return the augmented category document which links to this category
   */
  @Nullable
  private Content getContent() {
    if(augmentationService == null) {
      return null;
    }

    Site site = sitesService.getSite(getEntity().getContext().getSiteId());
    if (site == null) {
      return null;
    }

    return augmentationService.getContent(getEntity());
  }

}
