package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.push.PushService;
import com.coremedia.livecontext.ecommerce.push.PushState;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.coremedia.rest.linking.EntityResource;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.WORKSPACE_ID_NONE;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.CATALOG_ALIAS;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.ID;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.SITE_ID;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.WORKSPACE_ID;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;


@Produces(MediaType.APPLICATION_JSON)
@Path(CommerceBeanPushStateResource.URI_PATH)
public class CommerceBeanPushStateResource implements EntityResource<PushState> {
  private static final String PATH_TYPE = "type";
  static final String URI_PATH = "livecontext/pushState/{type:[^/]+}/{siteId:[^/]+}/{catalogAlias:[^/]+}/{workspaceId:[^/]+}/{id:.+}";

  final CommerceConnectionSupplier commerceConnectionSupplier;

  private String type;
  private String siteId;
  private CatalogAlias catalogAlias;
  private WorkspaceId workspaceId;
  private String id;

  public CommerceBeanPushStateResource(@NonNull CommerceConnectionSupplier commerceConnectionSupplier) {
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  @Override
  public PushState getEntity() {
    Optional<CommerceConnection> connectionOpt = commerceConnectionSupplier.findConnection(siteId);
    if (!connectionOpt.isPresent()){
      return null;
    }
    CommerceConnection connection = connectionOpt.get();
    CurrentCommerceConnection.set(connection);

    Optional<PushService> pushServiceOpt = connection.getPushService();
    if (!pushServiceOpt.isPresent()){
      return null;
    }
    PushService pushService = pushServiceOpt.get();

    StoreContext entityStoreContext = getEntityStoreContext(workspaceId, catalogAlias, connection);

    CommerceId commerceId = getCommerceId(id, type, catalogAlias,  connection.getIdProvider());
    if (commerceId == null) {
      return null;
    }

    CommerceBean bean = connection.getCommerceBeanFactory().createBeanFor(commerceId, entityStoreContext);

    return pushService.getPushState(CommerceIdFormatterHelper.format(bean.getId()), bean.getContext());
  }

  @Override
  public void setEntity(PushState entity) {
    //nothing to do
  }

  @NonNull
  private static StoreContext getEntityStoreContext(WorkspaceId workspaceId, CatalogAlias catalogAlias, CommerceConnection connection) {
    StoreContext siteStoreContext = connection.getStoreContext();
    return connection.getStoreContextProvider()
              .buildContext(siteStoreContext)
              .withCatalogAlias(catalogAlias)
              .withWorkspaceId(workspaceId)
              .build();
  }


  @Nullable
  private static CommerceId getCommerceId(String id, String type, CatalogAlias catalogAlias, CommerceIdProvider idProvider){
    CommerceId commerceId = null;
    if (type.equals(CATEGORY.type())) {
      commerceId = idProvider.formatCategoryId(catalogAlias, id);
    } else if (type.equals(PRODUCT.type())) {
      commerceId = idProvider.formatProductId(catalogAlias, id);
    } else if (type.equals(SKU.type())) {
      commerceId = idProvider.formatProductVariantId(catalogAlias, id);
    }
    return commerceId;
  }

  @GET
  public PushStateRepresentation get() {
    PushState pushState = getEntity();
    return pushState != null ? getRepresentation(pushState) : null;
  }

  static PushStateRepresentation getRepresentation(@NonNull PushState pushState){
    Date date = pushState.getModificationDate()
            .map(modificationDate -> Date.from(modificationDate.toInstant())).orElse(null);

    return new PushStateRepresentation(pushState.getState().name(), date);
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

  public String getType() {
    return type;
  }

  @PathParam(PATH_TYPE)
  public void setType(String type) {
    this.type = type;
  }

  public String getId() {
    return id;
  }

  @PathParam(ID)
  public void setId(String id) {
    this.id = id;
  }
}
