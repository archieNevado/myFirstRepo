package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.coremedia.rest.linking.EntityResource;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.WORKSPACE_ID_NONE;

/**
 * An abstract catalog object as a RESTful resource.
 */
public abstract class AbstractCatalogResource<Entity extends CommerceObject> implements EntityResource<Entity> {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractCatalogResource.class);

  private CatalogAliasTranslationService catalogAliasTranslationService;

  private static final String ID = "id";
  private static final String SITE_ID = "siteId";
  private static final String CATALOG_ALIAS = "catalogAlias";
  private static final String WORKSPACE_ID = "workspaceId";

  private String id;
  private String siteId;
  protected CatalogAlias catalogAlias;
  private WorkspaceId workspaceId = WORKSPACE_ID_NONE;

  @Nullable
  @Override
  public Entity getEntity() {
    if (getStoreContext() == null) {
      return null;
    }

    return doGetEntity();
  }

  protected abstract Entity doGetEntity();

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
    this.id = id;
  }

  @NonNull
  @VisibleForTesting
  static String decodeId(@NonNull String id) {
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

  public String getCatalogAlias() {
    return catalogAlias != null ? catalogAlias.value() : null;
  }

  @PathParam(CATALOG_ALIAS)
  public void setCatalogAlias(String catalogAliasValue) {
    catalogAlias = CatalogAlias.ofNullable(catalogAliasValue).orElse(null);
  }

  public String getWorkspaceId() {
    return workspaceId.value();
  }

  @PathParam(WORKSPACE_ID)
  public void setWorkspaceId(@Nullable String workspaceId) {
    setWorkspaceId(workspaceId != null ? WorkspaceId.of(workspaceId) : null);
  }

  public void setWorkspaceId(@Nullable WorkspaceId workspaceId) {
    this.workspaceId = workspaceId != null ? workspaceId : WORKSPACE_ID_NONE;
  }

  @Nullable
  protected StoreContext getStoreContext() {
    CommerceConnection commerceConnection = CurrentCommerceConnection.find().orElse(null);
    if (commerceConnection == null) {
      return null;
    }

    StoreContextProvider storeContextProvider = commerceConnection.getStoreContextProvider();
    StoreContext originalContext = commerceConnection.getStoreContext();

    StoreContext clonedContext = storeContextProvider.buildContext(originalContext).build();

    clonedContext.setWorkspaceId(workspaceId);
    if (catalogAlias != null && catalogAliasTranslationService != null) {
      Optional<CatalogId> catalogId = catalogAliasTranslationService.getCatalogIdForAlias(catalogAlias, siteId);
      clonedContext.setCatalog(catalogAlias, catalogId.orElse(null));
    }
    clonedContext.setSiteId(siteId);

    return clonedContext;
  }

  @NonNull
  protected CommerceConnection getConnection() {
    return CurrentCommerceConnection.get();
  }

  @Autowired
  public void setCatalogAliasTranslationService(CatalogAliasTranslationService catalogAliasTranslationService) {
    this.catalogAliasTranslationService = catalogAliasTranslationService;
  }

  protected CatalogAliasTranslationService getCatalogAliasTranslationService() {
    return catalogAliasTranslationService;
  }
}
