package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to provide maps that can be used as delegates for commerce beans satisfying the needs of
 * Studio REST resource linker.
 */
class CommerceBeanDelegateProvider {

  private static final String SITE_ID = "{siteId:.*}";
  private static final String EXTERNAL_ID = "{id:.*}";
  private static final CatalogAlias CATALOG_ALIAS_TEMPLATE_VAR = CatalogAlias.of("CATALOG_ALIAS_TEMPLATE_VAR");
  private static final String CATALOG_ALIAS = "{catalogAlias:.*}";

  private static final CommerceId TEMPLATE_COMMERCE_ID = CommerceIdBuilder
          .builder(Vendor.of("none"), "none", BaseCommerceBeanType.CATALOG)
          .withExternalId(EXTERNAL_ID)
          .withCatalogAlias(CATALOG_ALIAS_TEMPLATE_VAR)
          .build();

  private static final Map<String, Object> COMMERCE_BEAN_DELEGATE = Map.of(
          "id", TEMPLATE_COMMERCE_ID,
          "externalId", EXTERNAL_ID
  );

  private CommerceBeanDelegateProvider() {
  }

  @NonNull
  static Map<String, Object> get() {
    StoreContext storeContext = createStoreContext();
    HashMap<String, Object> map = new HashMap<>(COMMERCE_BEAN_DELEGATE);
    map.put("context", storeContext);
    return Collections.unmodifiableMap(map);
  }

  @NonNull
  static StoreContext createStoreContext() {
    CommerceConnection connection = new BaseCommerceConnection();
    return StoreContextBuilderImpl.from(connection, SITE_ID)
            .build();
  }

  @NonNull
  static String postProcess(@NonNull String commerceBeanUri, @Nullable StoreContext storeContext) {
    StoreContext context = storeContext != null ? storeContext : createStoreContext();
    return commerceBeanUri
            .replace(CATALOG_ALIAS_TEMPLATE_VAR.value(), CATALOG_ALIAS)
            .replace(SITE_ID, context.getSiteId());
  }

  @NonNull
  static String forEncodedExternalId(@NonNull String commerceBeanUri, @NonNull String externalId) {
    return commerceBeanUri.replace(EXTERNAL_ID, encodePartNumber(externalId));
  }

  @NonNull
  @VisibleForTesting
  static String encodePartNumber(@NonNull String partNumber) {
    return UriUtils.encodePath(partNumber, StandardCharsets.UTF_8);
  }
}
