package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A catalog {@link com.coremedia.livecontext.ecommerce.p13n.MarketingSpot} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/marketingspot/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class MarketingSpotResource extends AbstractCatalogResource<MarketingSpot> {

  @Override
  public MarketingSpotRepresentation getRepresentation() {
    MarketingSpotRepresentation representation = new MarketingSpotRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(MarketingSpotRepresentation representation) {
    MarketingSpot entity = getEntity();

    if (entity == null) {
      throw new CatalogBeanNotFoundRestException("Could not load spot bean");
    }

    representation.setId(entity.getId());
    representation.setName(entity.getName());
    representation.setShortDescription(entity.getDescription());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
    representation.setStore(new Store(entity.getContext()));
  }

  @Override
  protected MarketingSpot doGetEntity() {
    return getConnection().getMarketingSpotService().findMarketingSpotByExternalId(getId());
  }

  @Override
  public void setEntity(MarketingSpot spot) {
    if (spot.getId() != null){
      String extId = getExternalIdFromId(spot.getId());
      setId(extId);
    } else {
      setId(spot.getExternalId());
    }
    setSiteId(spot.getContext().getSiteId());
    setWorkspaceId(spot.getContext().getWorkspaceId());
  }
}
