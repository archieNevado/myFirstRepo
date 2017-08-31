package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A catalog {@link com.coremedia.livecontext.ecommerce.p13n.Segment} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/segment/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class SegmentResource extends AbstractCatalogResource<Segment> {

  @Override
  public SegmentRepresentation getRepresentation() {
    SegmentRepresentation representation = new SegmentRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(SegmentRepresentation representation) {
    Segment entity = getEntity();

    if (entity == null) {
      throw new CatalogBeanNotFoundRestException("Could not load segment bean.");
    }

    representation.setId(entity.getId());
    representation.setName(entity.getName());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());
  }

  @Override
  protected Segment doGetEntity() {
    SegmentService segmentService = getSegmentService();
    return segmentService != null ? segmentService.findSegmentById(getId()): null;
  }

  @Override
  public void setEntity(Segment segment) {
    if (segment.getId() != null) {
      String extId = getExternalIdFromId(segment.getId());
      setId(extId);
    } else {
      setId(segment.getExternalId());
    }
    setSiteId(segment.getContext().getSiteId());
    setWorkspaceId(segment.getContext().getWorkspaceId());
  }

  public SegmentService getSegmentService() {
    return DefaultConnection.get().getSegmentService();
  }


}
