package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CatalogResultDocument;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

/**
 * Catalog resource.
 */
@DefaultAnnotation(NonNull.class)
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
  public List<CatalogDocument> getCatalogs(StoreContext storeContext) {
    ListMultimap<String, String> queryParams = ImmutableListMultimap
            .of("select", "(**)");

    return getConnector()
            .getResource(CATALOGS_PATH, emptyMap(), queryParams, CatalogResultDocument.class, storeContext)
            .map(CatalogResultDocument::getData)
            .orElseGet(Collections::emptyList);
  }

  /**
   * Returns a catalog document for the given catalog id.
   *
   * @param catalogId the id of the catalog
   * @return the catalog document, or nothing if it does not exist
   */
  public Optional<CatalogDocument> getCatalogById(String catalogId, StoreContext storeContext) {
    Map<String, String> pathParameters = singletonMap(CATALOG_ID_PARAM, catalogId);

    return getConnector().getResource(CATALOG_PATH, pathParameters, CatalogDocument.class, storeContext);
  }
}
