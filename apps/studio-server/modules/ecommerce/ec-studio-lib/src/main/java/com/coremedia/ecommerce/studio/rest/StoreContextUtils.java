package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
class StoreContextUtils {

  private StoreContextUtils() {
  }

  static StoreContext cloneWithCatalog(StoreContext originalContext, CatalogAlias catalogAlias,
                                    CatalogAliasTranslationService catalogAliasTranslationService) {
    if (catalogAlias.equals(originalContext.getCatalogAlias())) {
      return originalContext;
    }
    return catalogAliasTranslationService.getCatalogIdForAlias(catalogAlias, originalContext)
            .map(catalogId -> cloneWithCatalog(originalContext, catalogId, catalogAlias))
            .orElse(originalContext);
  }

  static StoreContext cloneWithCatalog(StoreContext originalContext, CatalogId catalogId,
                                    CatalogAliasTranslationService catalogAliasTranslationService) {
    if (catalogId.equals(originalContext.getCatalogId().orElse(null))) {
      return originalContext;
    }
    return catalogAliasTranslationService.getCatalogAliasForId(catalogId, originalContext)
            .map(catalogAlias -> cloneWithCatalog(originalContext, catalogId, catalogAlias))
            .orElse(originalContext);
  }

  private static StoreContext cloneWithCatalog(StoreContext storeContext, CatalogId catalogId, CatalogAlias catalogAlias) {
    return storeContext.getConnection().getStoreContextProvider().buildContext(storeContext)
            .withCatalogId(catalogId)
            .withCatalogAlias(catalogAlias)
            .build();
  }
}
