package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.rest.linking.EntityResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

  public String getId() {
    return id;
  }

  @GET
  public AbstractCatalogRepresentation get() {
    return getRepresentation();
  }

  protected abstract AbstractCatalogRepresentation getRepresentation();

  @PathParam(ID)
  public void setId(@Nullable String id) {
    if (id != null) {
      try {
        this.id = URLDecoder.decode(id, "UTF-8");
        return;
      } catch (UnsupportedEncodingException e) { //NOSONAR - exception ignored on purpose
        //ignore
      }
    }
    this.id = id;
  }

  @PathParam(SITE_ID)
  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }

  @PathParam(WORKSPACE_ID)
  public void setWorkspaceId(@Nullable String workspaceId) {
    this.workspaceId = workspaceId == null ? StoreContextImpl.NO_WS_MARKER : workspaceId;
  }

  public String getSiteId() {
    return siteId;
  }

  public String getWorkspaceId() {
    return workspaceId;
  }

  @Nullable
  protected StoreContext getStoreContext() {
    CommerceConnection connection = getConnection();
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

  @Nullable
  protected CommerceConnection getConnection() {
    return Commerce.getCurrentConnection();
  }

  protected UserContext getUserContext() {
    return getConnection().getUserContext();
  }

  @Nonnull
  protected String getExternalIdFromId(@Nonnull String remoteBeanId) {
    // we assume that the substring after the last '/' is the external id
    return remoteBeanId.substring(remoteBeanId.lastIndexOf('/') + 1);
  }

  @Nullable
  public MarketingSpotService getMarketingSpotService() {
    return getConnection().getMarketingSpotService();
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
