package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.linking.EntityResource;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;

import static java.util.Objects.requireNonNull;

/**
 * An abstract catalog object as a RESTful resource.
 */
public abstract class AbstractCatalogResource<Entity extends CommerceObject> implements EntityResource<Entity> {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractCatalogResource.class);

  private static final String ID = "id";
  private static final String SITE_ID = "siteId";
  private static final String WORKSPACE_ID = "workspaceId";

  private String id;
  private String siteId;
  private String workspaceId = StoreContextImpl.NO_WS_MARKER;

  @GET
  public AbstractCatalogRepresentation get() {
    return getRepresentation();
  }

  protected abstract AbstractCatalogRepresentation getRepresentation();

  public String getId() {
    return id;
  }

  @PathParam(ID)
  public void setId(@Nullable String id) {
    this.id = id != null ? decodeId(id) : null;
  }

  @Nonnull
  @VisibleForTesting
  static String decodeId(@Nonnull String id) {
    // At least, encoded `+` chars (`%2B`) must be decoded because
    // some program logic double escapes it in order to avoid `+`
    // characters being unescaped to SPACE characters.
    return id.replace("%2B", "+");
  }

  public String getSiteId() {
    return siteId;
  }

  @PathParam(SITE_ID)
  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }

  public String getWorkspaceId() {
    return workspaceId;
  }

  @PathParam(WORKSPACE_ID)
  public void setWorkspaceId(@Nullable String workspaceId) {
    this.workspaceId = workspaceId == null ? StoreContextImpl.NO_WS_MARKER : workspaceId;
  }

  @Nullable
  protected StoreContext getStoreContext() {
    CommerceConnection connection = DefaultConnection.get();
    if (connection == null) {
      return null;
    }

    StoreContext storeContext = connection.getStoreContext();
    if (storeContext == null) {
      return null;
    }

    storeContext.setWorkspaceId(workspaceId);
    return storeContext;
  }

  @Nonnull
  protected CommerceConnection getConnection() {
    return requireNonNull(DefaultConnection.get(), "no commerce connection available");
  }

  @Nonnull
  protected String getExternalIdFromId(@Nonnull String remoteBeanId) {
    // we assume that the substring after the last '/' is the external id
    return remoteBeanId.substring(remoteBeanId.lastIndexOf('/') + 1);
  }

  @Nullable
  @Override
  public Entity getEntity() {
    if (getStoreContext() == null) {
      return null;
    }

    return doGetEntity();
  }

  protected abstract Entity doGetEntity();
}
