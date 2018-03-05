package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CatalogsResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

public class CatalogsResourceIT extends DataApiResourceTestBase {

  @Autowired
  private CatalogsResource resource;

  @Test
  public void testGetCatalogs() {
    if (useBetamaxTapes()) {
      return;
    }

    List<CatalogDocument> catalogs = resource.getCatalogs();

    assertThat(catalogs).isNotNull();
  }

  @Test
  public void testGetCatalogById() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<CatalogDocument> catalog = resource.getCatalogById("storefront-catalog-en");

    assertThat(catalog).isPresent();
    assertThat(catalog).hasValueSatisfying(c -> assertThat(c.getId()).isEqualTo("storefront-catalog-en"));
  }
}
