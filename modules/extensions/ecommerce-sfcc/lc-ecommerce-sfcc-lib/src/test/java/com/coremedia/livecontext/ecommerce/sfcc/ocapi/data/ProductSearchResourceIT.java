package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductSearchResultDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.ProductSearchResource;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductSearchResourceIT extends DataApiResourceTestBase {

  @Autowired
  private ProductSearchResource resource;

  @Test
  public void testSearchProducts() {
    if (useBetamaxTapes()) {
      return;
    }

    Map<String, String> params = newHashMap();
    //params.put("categoryId", "electronics-digital-cameras");

    Optional<ProductSearchResultDocument> searchResultDocument = resource
            .searchProducts("nikon", params, emptySet(), getCurrentStoreContext());
    assertThat(searchResultDocument).isPresent();

    List<ProductDocument> products = searchResultDocument.get().getHits();
    assertThat(products).isNotEmpty();
    products.forEach(this::assertProduct);
  }

  @Test
  public void testSearchProductsForMultipleCategories() {
    if (useBetamaxTapes()) {
      return;
    }

    Map<String, String> params = newHashMap();

    int countHitsCombined = getSearchProductsCount("pants", params, ImmutableSet.of("newarrivals-womens", "newarrivals-mens"));
    int countHitsWomens = getSearchProductsCount("pants", params, ImmutableSet.of("newarrivals-womens"));
    int countHitsMens = getSearchProductsCount("pants", params, ImmutableSet.of("newarrivals-mens"));

    assertThat(countHitsMens + countHitsWomens).isEqualTo(countHitsCombined);
  }

  private int getSearchProductsCount(@Nonnull String query, @Nonnull Map<String, String> params,
                                     @Nonnull Set<String> categoryIds) {
    Optional<ProductSearchResultDocument> resultDocument = resource
            .searchProducts(query, params, categoryIds, getCurrentStoreContext());
    assertThat(resultDocument).isPresent();

    return resultDocument.get().getCount();
  }

  @Test
  public void testGetProductsById() {
    if (useBetamaxTapes()) {
      return;
    }

    List<String> productIds = newArrayList("nikon-d40-wlens", "nikon-d60-wlens", "nikon-d90-wlens");

    List<ProductDocument> productDocuments = resource.getProductsById(productIds, getCurrentStoreContext());

    assertThat(productDocuments).as("No SKUs found.").isNotEmpty();
  }
}
