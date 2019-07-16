package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop;

import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.ProductSearchHitDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.ProductSearchRefinementDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents.ShopProductSearchResultDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources.ShopProductSearchResource;
import com.google.common.base.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_CATEGORYID;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_FACET;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_OFFSET;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_ORDERBY;
import static com.coremedia.livecontext.ecommerce.catalog.CatalogService.SEARCH_PARAM_TOTAL;
import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ProductSearchResourceIT extends ShopApiResourceTestBase {

  private static final String CATEGORY_ID = "womens-clothing-bottoms";

  @Autowired
  private ShopProductSearchResource resource;

  @Test
  public void testGetFacetsOfACategory() {
    if (useBetamaxTapes()) {
      return;
    }

    Map<String, String> params = newHashMap();
    params.put(SEARCH_PARAM_CATEGORYID, CATEGORY_ID);
    Optional<ShopProductSearchResultDocument> searchResultDocument = resource.searchProducts("", params, storeContext);
    assertThat(searchResultDocument).isPresent();

    List<ProductSearchRefinementDocument> refinements = searchResultDocument.get().getRefinements();
    assertThat(refinements.size()).isEqualTo(4);

    refinements.forEach(this::assertRefinement);
  }

  @Test
  public void testSearchWithFacets() {
    if (useBetamaxTapes()) {
      return;
    }

    //test first without specified category
    Map<String, String> params = newHashMap();
    params.put(SEARCH_PARAM_TOTAL, "300");
    Optional<ShopProductSearchResultDocument> searchResultDocument = resource.searchProducts("", params, storeContext);
    assertThat(searchResultDocument).isPresent();
    //The maximum number of instances per request is 200
    assertThat(searchResultDocument.get().getHits().size()).isEqualTo(200);
    assertThat(searchResultDocument.get().getCount()).isEqualTo(200);
    //The total can though exceed the the maximum of 200.
    assertThat(searchResultDocument.get().getTotal()).isEqualTo(610);

    //test the facet on a category
    params.put(SEARCH_PARAM_CATEGORYID, CATEGORY_ID);
    params.put(SEARCH_PARAM_FACET, "c_refinementColor=Blue");
    searchResultDocument = resource.searchProducts("", params, storeContext);
    assertThat(searchResultDocument).isPresent();
    assertThat(searchResultDocument.get().getHits().size()).isEqualTo(27);
    assertThat(searchResultDocument.get().getCount()).isEqualTo(27);
    assertThat(searchResultDocument.get().getTotal()).isEqualTo(27);
  }

  @Test
  public void testSearchWithSortingAndOffset() {
    if (useBetamaxTapes()) {
      return;
    }

    Map<String, String> params = newHashMap();
    params.put(SEARCH_PARAM_TOTAL, "10");
    params.put(SEARCH_PARAM_CATEGORYID, CATEGORY_ID);
    params.put(SEARCH_PARAM_FACET, "c_refinementColor=Blue");
    params.put(SEARCH_PARAM_ORDERBY, "ORDER_BY_TYPE_PRICE_ASC");

    //give me the prices
    params.put("expand", "prices");

    Optional<ShopProductSearchResultDocument> searchResultDocument = resource.searchProducts("", params, storeContext);
    assertThat(searchResultDocument).isPresent();
    assertThat(searchResultDocument.get().getHits().size()).isEqualTo(10);

    ProductSearchHitDocument cheapestProduct = searchResultDocument.get().getHits().get(0);
    assertThat(cheapestProduct.getPrice()).isEqualTo(28.0);

    ProductSearchHitDocument mostExpProduct = searchResultDocument.get().getHits().get(9);
    assertThat(mostExpProduct.getPrice()).isEqualTo(42.0);

    params.put(SEARCH_PARAM_ORDERBY, "ORDER_BY_TYPE_PRICE_DSC");

    searchResultDocument = resource.searchProducts("", params, storeContext);
    assertThat(searchResultDocument).isPresent();
    assertThat(searchResultDocument.get().getHits().size()).isEqualTo(10);

    mostExpProduct = searchResultDocument.get().getHits().get(0);
    assertThat(mostExpProduct.getPrice()).isEqualTo(71.0);

    cheapestProduct = searchResultDocument.get().getHits().get(9);
    assertThat(cheapestProduct.getPrice()).isEqualTo(53.0);

    //skip the first 10 most expensive products.
    params.put(SEARCH_PARAM_OFFSET, "10");

    searchResultDocument = resource.searchProducts("", params, storeContext);
    assertThat(searchResultDocument).isPresent();
    assertThat(searchResultDocument.get().getHits().size()).isEqualTo(10);

    mostExpProduct = searchResultDocument.get().getHits().get(0);
    assertThat(mostExpProduct.getPrice()).isEqualTo(49.68);

    cheapestProduct = searchResultDocument.get().getHits().get(9);
    assertThat(cheapestProduct.getPrice()).isEqualTo(39.59);
  }

  private void assertRefinement(ProductSearchRefinementDocument refinement) {
    assertThat(refinement).isNotNull();

    assertThat(Strings.isNullOrEmpty(refinement.getAttributeId())).as("Refinement attribute id is not set.").isFalse();

    assertThat(Strings.isNullOrEmpty(refinement.getLabel())).as("Refinement label is not set.").isFalse();

    List<SearchFacet> refinementValues = refinement.getValues();
    assertThat(refinementValues).isNotNull();
    assertThat(refinementValues.size()).isGreaterThan(0);
    refinementValues.forEach(this::assertRefinementValue);
  }

  private void assertRefinementValue(SearchFacet refinementValue) {
    assertThat(refinementValue).isNotNull();

    assertThat(Strings.isNullOrEmpty(refinementValue.getLabel())).as("RefinementValue label is not set.").isFalse();
    assertThat(Strings.isNullOrEmpty(refinementValue.getQuery())).as("RefinementValue value is not set.").isFalse();
  }
}
