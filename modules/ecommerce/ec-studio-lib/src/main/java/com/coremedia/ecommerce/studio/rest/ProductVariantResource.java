package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.rest.cap.content.ContentRepositoryResource;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.Objects.requireNonNull;

/**
 * A catalog {@link ProductVariant} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/sku/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:.+}")
public class ProductVariantResource extends CommerceBeanResource<ProductVariant> {

  @Inject
  private ContentRepositoryResource contentRepositoryResource;

  @Override
  public ProductVariantRepresentation getRepresentation() {
    ProductVariantRepresentation productVariantRepresentation = new ProductVariantRepresentation();
    fillRepresentation(productVariantRepresentation);
    return productVariantRepresentation;
  }

  private void fillRepresentation(ProductVariantRepresentation representation) {
    super.fillRepresentation(representation);
    ProductVariant entity = getEntity();
    if (entity == null) {
      throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load sku bean");
    }
    representation.setId(entity.getId());
    representation.setName(entity.getName());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
    String shortDescription = entity.getShortDescription().asXml();
    representation.setShortDescription(shortDescription);
    String longDescription = entity.getLongDescription().asXml();
    representation.setLongDescription(longDescription);
    String thumbnailUrl = entity.getThumbnailUrl();
    representation.setThumbnailUrl(RepresentationHelper.modifyAssetImageUrl(thumbnailUrl, contentRepositoryResource.getEntity()));
    representation.setParent(entity.getParent());
    representation.setCategory(entity.getCategory());
    representation.setStore((new Store(entity.getContext())));
    representation.setOfferPrice(entity.getOfferPrice());
    representation.setListPrice(entity.getListPrice());
    representation.setCurrency(entity.getCurrency().getSymbol(entity.getLocale()));
    // get visuals directly via AssetService to avoid fallback to default picture
    AssetService assetService = getConnection().getAssetService();
    if(null != assetService) {
      representation.setVisuals(assetService.findVisuals(entity.getReference(), false));
    }
    representation.setPictures(entity.getPictures());
    representation.setDownloads(entity.getDownloads());
    representation.setDefiningAttributes(entity.getDefiningAttributes());
    representation.setDescribingAttributes(entity.getDescribingAttributes());
  }

  @Override
  protected ProductVariant doGetEntity() {
    CommerceConnection connection = getConnection();
    CommerceIdProvider idProvider = requireNonNull(connection.getIdProvider(), "id provider not available");
    CatalogService catalogService = requireNonNull(connection.getCatalogService(), "catalog service not available");
    String productVariantId = idProvider.formatProductVariantId(getId());
    return catalogService.findProductVariantById(productVariantId);
  }

  @Override
  public void setEntity(ProductVariant productVariant) {
    setId(productVariant.getExternalId());
    setSiteId(productVariant.getContext().getSiteId());
    setWorkspaceId(productVariant.getContext().getWorkspaceId());
  }
}
