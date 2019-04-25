package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.push.PushService;
import com.coremedia.livecontext.ecommerce.push.PushState;
import com.coremedia.rest.linking.EntityResource;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.Optional;

import static com.coremedia.ecommerce.studio.rest.ContentPushStateResource.URI_PATH;

@Produces(MediaType.APPLICATION_JSON)
@Path(URI_PATH)
public class ContentPushStateResource implements EntityResource<PushState> {

  private static final String PATH_PARAM_CAP_ID = "id";
  static final String URI_PATH = "livecontext/pushState/content/{" + PATH_PARAM_CAP_ID + ":[0-9]+}";

  private final ContentRepository contentRepository;
  private final CommerceConnectionSupplier commerceConnectionSupplier;

  private String id;

  public ContentPushStateResource(@NonNull ContentRepository contentRepository, @NonNull CommerceConnectionSupplier commerceConnectionSupplier) {
    this.contentRepository = contentRepository;
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  public String getId() {
    return id;
  }

  @PathParam(PATH_PARAM_CAP_ID)
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public PushState getEntity() {
    Content content = contentRepository.getContent(getId());

    Optional<CommerceConnection> connection = commerceConnectionSupplier.findConnection(content);
    if (!connection.isPresent()){
      return null;
    }

    Optional<PushService> pushServiceOptional = connection.flatMap(CommerceConnection::getPushService);
    if (!pushServiceOptional.isPresent()){
      return null;
    }

    return pushServiceOptional
            .map(pushService -> pushService.getPushState(content.getId(), connection.get().getStoreContext()))
            .orElse(null);
  }

  @Override
  public void setEntity(PushState entity) {
    //nothing to do
  }

  @GET
  public PushStateRepresentation get(@Context UriInfo uriInfo) {
    PushState pushState = getEntity();
    return pushState != null ? getRepresentation(pushState) : null;
  }


  static PushStateRepresentation getRepresentation(@NonNull PushState pushState){
    Date date = pushState.getModificationDate()
            .map(modificationDate -> Date.from(modificationDate.toInstant())).orElse(null);

    return new PushStateRepresentation(pushState.getState().name(), date);
  }
}
