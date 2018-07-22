package com.coremedia.livecontext.handler.util;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentParameters;

import edu.umd.cs.findbugs.annotations.NonNull;

public class CaeStoreContextUtil {

  private CaeStoreContextUtil() {
  }

  /**
   * Update the StoreContext with fragment parameter information
   *
   * @param catalogAliasTranslationService
   * @param storeContext                   current store context
   * @param fragmentParameters             parameters coming from fragment urls
   * @param site                           the current site
   */
  public static void updateStoreContextWithFragmentParameters(@NonNull CatalogAliasTranslationService catalogAliasTranslationService,
                                                              @NonNull StoreContext storeContext,
                                                              @NonNull FragmentParameters fragmentParameters,
                                                              @NonNull Site site) {
    fragmentParameters.getCatalogId().ifPresent(catalogId -> {
      CatalogAlias catalogAlias = catalogAliasTranslationService.getCatalogAliasForId(catalogId, site.getId())
              .orElse(null);
      storeContext.setCatalog(catalogAlias, catalogId);
    });
  }
}
