package com.coremedia.ecommerce.studio.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.PATH_CATALOG_ALIAS;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.PATH_ID;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.PATH_SITE_ID;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.PATH_WORKSPACE_ID;
import static com.coremedia.ecommerce.studio.rest.AbstractCatalogResource.QUERY_ID;

/**
 * Handler used for product variant resource requests with "/" in the external id.
 * Id is passed as query parameter instead of path parameter.
 * The handler delegates to the original {@link ProductVariantResource}.
 * See also {@link com.coremedia.ecommerce.studio.rest.filter.CatalogResourceEncodingFilter}
 */
@RestController
public class ProductVariantResourceEncodedId {
  static final String URI_PATH = "livecontext/sku/{" + PATH_SITE_ID + "}/{" + PATH_CATALOG_ALIAS + "}/{" + PATH_WORKSPACE_ID + "}";

  private final ProductVariantResource productVariantResource;

  @Autowired
  public ProductVariantResourceEncodedId(ProductVariantResource productVariantResource) {
    this.productVariantResource = productVariantResource;
  }

  @GetMapping(value = ProductVariantResourceEncodedId.URI_PATH, produces = MediaType.APPLICATION_JSON_VALUE, params = QUERY_ID)
  public AbstractCatalogRepresentation get(@PathVariable Map<String, String> params, @RequestParam String id) {
    params.put(PATH_ID, id);
    return productVariantResource.getRepresentation(params);
  }
}
