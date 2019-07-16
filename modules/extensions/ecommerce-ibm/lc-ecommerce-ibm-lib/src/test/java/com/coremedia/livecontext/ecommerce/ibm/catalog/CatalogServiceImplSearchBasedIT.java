package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion;
import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Search REST interface.
 */
public class CatalogServiceImplSearchBasedIT extends IbmCatalogServiceBaseTest {

  private static final String ROOT_CATEGORY_ID = "ROOT";

  @Betamax(tape = "csi_testFindProductById_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductById() throws Exception {
    super.testFindProductById();
  }

  @Betamax(tape = "csi_testFindB2BProductFromMultiCatalogs_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindB2BProductFromMultiCatalogs() throws Exception {
    super.testFindB2BProductFromMultiCatalogs();
  }

  @Betamax(tape = "csi_testFindCategoryById_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryById() throws Exception {
    super.testFindCategoryById();
  }

  @Betamax(tape = "csi_testFindB2BCategoryFromMultiCatalogs_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindB2BCategoryFromMultiCatalogs() throws Exception {
    super.testFindB2BCategoryFromMultiCatalogs();
  }

  @Betamax(tape = "csi_testFindProductByPartNumberWithSlash_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByIdWithSlash() {
    if (testConfig.getWcsVersion().lessThan(WcsVersion.WCS_VERSION_8_0)) {
      return;
    }
    super.testFindProductByIdWithSlash();
  }

  @Betamax(tape = "csi_testFindProductByIdNotFound_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByIdNotFound() throws Exception {
    super.testFindProductByIdNotFound();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechId() throws Exception {
    super.testFindProductByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductMultiSEOSegmentsByExternalTechId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductMultiSEOByExternalTechId() throws Exception {
    super.testFindProductMultiSEOByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechIdIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechIdIsNull() throws Exception {
    super.testFindProductByExternalTechIdIsNull();
  }

  @Betamax(tape = "csi_testFindProductVariantById_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantById() throws Exception {
    super.testFindProductVariantById();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalIdWithContractSupport_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalIdWithContractSupport() throws Exception {
    super.testFindProductVariantByExternalIdWithContractSupport();
  }

  @Betamax(tape = "csi_testFindProductVariantByIdWithSlash_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByIdWithSlash() throws Exception {
    if (testConfig.getWcsVersion().lessThan(WcsVersion.WCS_VERSION_8_0)) {
      return;
    }
    super.testFindProductVariantByIdWithSlash();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalTechId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalTechId() throws Exception {
    super.testFindProductVariantByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductBySeoSegment_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductBySeoSegment() throws Exception {
    super.testFindProductBySeoSegment();
  }

  @Betamax(tape = "csi_testFindProductBySeoSegmentIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductBySeoSegmentIsNull() throws Exception {
    super.testFindProductBySeoSegmentIsNull();
  }

  @Betamax(tape = "csi_testFindProductsByCategory_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategory() throws Exception {
    super.testFindProductsByCategory();
  }

  @Betamax(tape = "csi_testFindProductsByCategoryIsEmpty_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategoryIsEmpty() throws Exception {
    super.testFindProductsByCategoryIsEmpty();
  }

  @Betamax(tape = "csi_testFindProductsByCategoryIsRoot_search", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindProductsByCategoryIsRoot() throws Exception {
    testFindProductsByCategoryIsRoot(ROOT_CATEGORY_ID);
  }

  @Betamax(tape = "csi_testSearchProducts_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testSearchProducts() throws Exception {
    super.testSearchProducts();
  }

  @Betamax(tape = "csi_testSortedSearchProducts_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testSortedSearchProducts() throws Exception {
    super.testSortedSearchProducts();

    //additional tests
    CommerceId categoryId = getIdProvider().formatCategoryId(null, LEAF_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, storeContext);
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    searchParams.put(CatalogService.SEARCH_PARAM_ORDERBY, "ORDER_BY_TYPE_CATEGORY_ASC");
    SearchResult<Product> searchProducts = testling.searchProducts(SEARCH_TERM_1, searchParams, storeContext);
    assertThat(searchProducts).isNotNull();
    int total = searchProducts.getTotalCount();
    List<Product> products = searchProducts.getSearchResult();
    int counter = 1;
    while (counter < total) {
      Product previousProduct = products.get(counter - 1);
      Product currentProduct = products.get(counter);
      String previousProductName = previousProduct.getName();
      String currentProductName = currentProduct.getName();
      assertThat(previousProductName.compareTo(currentProductName) < 0).isTrue();
      counter++;
    }
  }

  @Betamax(tape = "csi_testSearchProductsWithOffset_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testSearchProductsWithOffset() throws Exception {
    super.testSearchProductsWithOffset();
  }

  @Betamax(tape = "csi_testGetFacetSearchProducts_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testGetFacetSearchProducts() throws Exception {
    super.testGetFacetSearchProducts();
  }

  @Betamax(tape = "csi_testSearchProductsWithFacet_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testSearchProductsWithFacet() throws Exception {
    super.testSearchProductsWithFacet();
  }

  @Test
  @Betamax(tape = "csi_testSearchProductVariants_search", match = {MatchRule.path, MatchRule.query})
  @Override
  public void testSearchProductVariants() throws Exception {
    super.testSearchProductVariants();
  }

  @Test
  @Betamax(tape = "csi_testGetCatalogs", match = {MatchRule.path, MatchRule.query})
  @Override
  public void testGetCatalogs() {
    super.testGetCatalogs();
  }

  @Test
  @Betamax(tape = "csi_testGetDefaultCatalog", match = {MatchRule.path, MatchRule.query})
  @Override
  public void testGetDefaultCatalog() {
    super.testGetDefaultCatalog();
  }

  @Test
  @Betamax(tape = "csi_testSearchProductVariants_search", match = {MatchRule.path, MatchRule.query})
  public void testSearchFacetsProductVariants() {
    SearchResult<ProductVariant> searchResult = testling.searchProductVariants(SEARCH_TERM_1 + " " + SEARCH_TERM_2, emptyMap(), storeContext);
    assertNotNull(searchResult);

    List<SearchFacet> facets = searchResult.getFacets();
    assertNotNull(facets);
    assertTrue(!facets.isEmpty());
    assertSearchFacet(facets.get(0));
  }

  @Test
  @Betamax(tape = "csi_testSearchProducts_search", match = {MatchRule.path, MatchRule.query})
  public void testSearchFacetsProducts() throws Exception {
    super.testSearchFacetsProducts(SEARCH_TERM_1, new HashMap<>());
  }

  @Betamax(tape = "csi_testFindTopCategories_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindTopCategories() throws Exception {
    super.testFindTopCategories();
  }

  @Test
  @Betamax(tape = "csi_testFindTopCategoriesWithContractSupport_search", match = {MatchRule.path, MatchRule.query})
  @Override
  public void testFindTopCategoriesWithContractSupport() throws Exception {
    super.testFindTopCategoriesWithContractSupport();
  }

  @Betamax(tape = "csi_testFindSubCategories_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategories() throws Exception {
    super.testFindSubCategories();
  }

  @Betamax(tape = "csi_testFindSubCategoriesIsEmpty_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategoriesIsEmpty() throws Exception {
    super.testFindSubCategoriesIsEmpty();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalTechId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalTechId() throws Exception {
    super.testFindCategoryByExternalTechId();
  }

  @Betamax(tape = "csi_testFindCategoryMultiSEOSegmentsByExternalTechId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryMultiSEOByExternalTechId() throws Exception {
    super.testFindCategoryMultiSEOByExternalTechId();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalTechIdIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalTechIdIsNull() throws Exception {
    super.testFindCategoryByExternalTechIdIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryBySeoSegment_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegment() throws Exception {
    super.testFindCategoryBySeoSegment();
  }

  @Betamax(tape = "csi_testFindGermanCategoryBySeoSegment_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindGermanCategoryBySeoSegment() throws Exception {
    super.testFindGermanCategoryBySeoSegment();
  }

  @Betamax(tape = "csi_testFindCategoryBySeoSegmentIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegmentIsNull() throws Exception {
    super.testFindCategoryBySeoSegmentIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryByIdIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByIdIsNull() {
    super.testFindCategoryByIdIsNull();
  }

  @Test
  @Betamax(tape = "csi_testFindRootCategory_search", match = {MatchRule.path, MatchRule.query})
  public void testFindRootCategory() throws Exception {
    testFindRootCategory(ROOT_CATEGORY_ID);
  }

  @Betamax(tape = "csi_testFindCategoryByPartNumberWithSlash_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByIdWithSlash() {
    if (testConfig.getWcsVersion().lessThan(WcsVersion.WCS_VERSION_8_0)) {
      return;
    }
    super.testFindCategoryByIdWithSlash();
  }

  @Betamax(tape = "csi_testWithStoreContext_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testWithStoreContext() {
    super.testWithStoreContext();
  }
}
