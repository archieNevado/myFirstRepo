package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.controller.EntityController;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;

/**
 * An abstract catalog object as a RESTful resource.
 */
public abstract class AbstractCatalogResource<Entity extends CommerceObject> implements EntityController<Entity> {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractCatalogResource.class);

  private final CatalogAliasTranslationService catalogAliasTranslationService;

  static final String PATH_ID = "id";
  static final String PATH_SITE_ID = "siteId";
  static final String PATH_CATALOG_ALIAS = "catalogAlias";
  public static final String QUERY_ID = "id";

  protected AbstractCatalogResource(CatalogAliasTranslationService catalogAliasTranslationService) {
    this.catalogAliasTranslationService = catalogAliasTranslationService;
  }

  @Override
  public Entity getEntity(@NonNull Map<String, String> pathVariables) {
    if (!getStoreContext(pathVariables).isPresent()) {
      return null;
    }

    return doGetEntity(pathVariables);
  }

  protected abstract Entity doGetEntity(@NonNull Map<String, String> params);

  protected abstract AbstractCatalogRepresentation getRepresentation(@NonNull Map<String, String> params);

  @NonNull
  @VisibleForTesting
  static String decodeId(@NonNull String id) {
    // At least, encoded `+` chars (`%2B`) must be decoded because
    // some program logic double escapes it in order to avoid `+`
    // characters being unescaped to SPACE characters.
    return id.replace("%2B", "+");
  }

  @NonNull
  protected Optional<StoreContext> getStoreContext(@NonNull Map<String, String> params) {
    String catalogAlias = params.get(PATH_CATALOG_ALIAS);
    var requestAttributes = RequestContextHolder.getRequestAttributes();
    if (!(requestAttributes instanceof ServletRequestAttributes)) {
      throw new IllegalStateException("Request not available.");
    }
    var request = ((ServletRequestAttributes) requestAttributes).getRequest();
    // the CommerceConnectionFilter creates the connection (with initial store context)
    // without considering the Studio catalog alias
    return CurrentStoreContext.find(request).
            map(storeContext -> cloneWithCatalog(storeContext,catalogAlias));
  }

  private StoreContext cloneWithCatalog(StoreContext storeContext, @Nullable String catalogAlias) {
    return CatalogAlias.ofNullable(catalogAlias)
            .map(alias -> StoreContextUtils.cloneWithCatalog(storeContext, alias, catalogAliasTranslationService))
            .orElse(storeContext);
  }

  CatalogAliasTranslationService getCatalogAliasTranslationService() {
    return catalogAliasTranslationService;
  }
}
