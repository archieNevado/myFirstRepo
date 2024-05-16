package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdFormatterHelper;
import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A catalog {@link Catalog} object as a RESTful resource.
 */
@RestController
@RequestMapping(value = "livecontext/catalog/{" + AbstractCatalogResource.PATH_SITE_ID + "}/{" + AbstractCatalogResource.PATH_ID + "}", produces = MediaType.APPLICATION_JSON_VALUE)
public class CatalogResource extends AbstractCatalogResource<Catalog> {

  public CatalogResource(CatalogAliasTranslationService catalogAliasTranslationService) {
    super(catalogAliasTranslationService);
  }

  @GetMapping
  public AbstractCatalogRepresentation get(@PathVariable(PATH_SITE_ID) final String siteId,
                                           @PathVariable(PATH_ID) final String id) {
    return getRepresentation(Map.of(
            PATH_SITE_ID, siteId,
            PATH_ID, id));
  }

  @Override
  protected CatalogRepresentation getRepresentation(@NonNull Map<String, String> params) {
    CatalogRepresentation representation = new CatalogRepresentation();
    fillRepresentation(params, representation);
    return representation;
  }

  private void fillRepresentation(@NonNull Map<String, String> params, CatalogRepresentation representation) {
    Catalog entity = getEntity(params);

    if (entity == null) {
      LOG.warn("Error loading catalog bean");
      throw new CatalogRestException(HttpStatus.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load workspace bean");
    }

    representation.setDefault(entity.isDefaultCatalog());
    representation.setId(CommerceIdFormatterHelper.format(entity.getId()));
    representation.setName(entity.getName().value());

    Category rootCategory = entity.getRootCategory();
    representation.setTopCategories(rootCategory.getChildren());
    representation.setRootCategory(rootCategory);
  }

  @Override
  protected Catalog doGetEntity(@NonNull Map<String, String> params) {
    CatalogId catalogId = getCatalogId(params);

    StoreContext storeContext = getStoreContext(params).orElse(null);
    if (storeContext == null) {
      return null;
    }

    return storeContext.getConnection()
            .getCatalogService()
            .getCatalog(catalogId, storeContext)
            .orElse(null);
  }

  @NonNull
  @Override
  protected Optional<StoreContext> getStoreContext(@NonNull Map<String, String> params) {
    return super.getStoreContext(params).map(storeContext -> enhanceWithCatalogAlias(storeContext, params));
  }

  @NonNull
  private StoreContext enhanceWithCatalogAlias(@NonNull StoreContext storeContext, @NonNull Map<String, String> params) {
    CatalogId catalogId = getCatalogId(params);
    CatalogAliasTranslationService catalogAliasTranslationService = getCatalogAliasTranslationService();
    return CatalogAlias.ofNullable(params.get(PATH_CATALOG_ALIAS))
            .map(catalogAlias -> StoreContextUtils.cloneWithCatalog(storeContext, catalogAlias, catalogAliasTranslationService))
            .orElseGet(() -> StoreContextUtils.cloneWithCatalog(storeContext, catalogId, catalogAliasTranslationService));
  }

  @NonNull
  @Override
  public Map<String, String> getPathVariables(@NonNull Catalog catalog) {
    CommerceId commerceId = catalog.getId();
    String extId = commerceId.getExternalId().orElseGet(catalog::getExternalId);
    Map<String, String> params = new HashMap<>();
    params.put(PATH_ID, extId);
    params.put(PATH_SITE_ID, catalog.getContext().getSiteId());
    return params;
  }

  private CatalogId getCatalogId(@NonNull Map<String, String> params) {
    return CatalogId.of(params.get(PATH_ID));
  }
}
