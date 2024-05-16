package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class Store implements CommerceObject {

  private StoreContext context;

  public Store(StoreContext context) {
    this.context = context;
  }

  public String getId() {
    return "store-" + context.getStoreName();
  }

  public StoreContext getContext() {
    return context;
  }

  @NonNull
  public Optional<Catalog> getDefaultCatalog(@NonNull CommerceConnection commerceConnection) {
    var catalogService = commerceConnection.getCatalogService();

    if (!hasStoreName()) {
      return Optional.empty();
    }

    return catalogService.getDefaultCatalog(context);
  }

  @NonNull
  public List<Catalog> getCatalogs(@NonNull CommerceConnection commerceConnection) {
    CatalogService catalogService = commerceConnection.getCatalogService();

    if (!hasStoreName()) {
      return emptyList();
    }

    return catalogService.getCatalogs(context);
  }

  @NonNull
  public List<Category> getRootCategories(@NonNull CommerceConnection commerceConnection) {
    CatalogService catalogService = commerceConnection.getCatalogService();

    if (!hasStoreName()) {
      return emptyList();
    }

    return catalogService.getCatalogs(context).stream()
            .map(Catalog::getRootCategory)
            .filter(Objects::nonNull)
            .collect(toList());
  }

  private boolean hasStoreName() {
    return context.getStoreName() != null;
  }
}
