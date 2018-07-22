package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CatalogResultDocument;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

/**
 * Catalog resource.
 */
@Service("ocapiCatalogResource")
public class CatalogsResource extends AbstractDataResource {

  private static final String CATALOGS_PATH = "/catalogs";
  private static final String CATALOG_ID_PARAM = "catalogId";
  private static final String CATALOG_PATH = CATALOGS_PATH + "/{" + CATALOG_ID_PARAM + "}";

  /**
   * Returns a list of available catalogs.
   *
   * @return the list of catalogs or en empty list of no catalogs exist
   */
  @NonNull
  public List<CatalogDocument> getCatalogs() {
    ListMultimap<String, String> queryParams = ImmutableListMultimap
            .of("select", "(**)");

    return getConnector()
            .getResource(CATALOGS_PATH, emptyMap(), queryParams, CatalogResultDocument.class)
            .map(CatalogResultDocument::getData)
            .orElseGet(Collections::<CatalogDocument>emptyList);
  }

  /**
   * Returns a catalog document for the given catalog id.
   *
   * @param catalogId the id of the catalog
   * @return the catalog document, or nothing if it does not exist
   */
  @NonNull
  public Optional<CatalogDocument> getCatalogById(@NonNull String catalogId) {
    Map<String, String> pathParameters = singletonMap(CATALOG_ID_PARAM, catalogId);

    return getConnector().getResource(CATALOG_PATH, pathParameters, CatalogDocument.class);
  }
}
