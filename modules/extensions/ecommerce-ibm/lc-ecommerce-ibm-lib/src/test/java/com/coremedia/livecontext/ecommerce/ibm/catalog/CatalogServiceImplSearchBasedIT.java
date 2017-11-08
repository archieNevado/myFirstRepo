package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion;
import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Search REST interface.
 */
@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
public class CatalogServiceImplSearchBasedIT extends IbmCatalogServiceBaseTest {

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
  @Override
  public void testFindProductsByCategoryIsRoot() throws Exception {
    super.testFindProductsByCategoryIsRoot();
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
  }

  @Betamax(tape = "csi_testSearchProductsWithOffset_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testSearchProductsWithOffset() throws Exception {
    super.testSearchProductsWithOffset();
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
  public void testSearchFacetsProductVariants() throws Exception {
    SearchResult<ProductVariant> searchResult = testling.searchProductVariants(SEARCH_TERM_1 + " " + SEARCH_TERM_2, emptyMap(), getStoreContext());
    assertNotNull(searchResult);

    List<SearchFacet> facets = searchResult.getFacets();
    assertNotNull(facets);
    assertTrue(!facets.isEmpty());
    testSearchFacet(facets.get(0));
  }

  @Test
  @Betamax(tape = "csi_testSearchProducts_search", match = {MatchRule.path, MatchRule.query})
  public void testSearchFacetsProducts() throws Exception {
    super.testSearchFacetsProducts(SEARCH_TERM_1, emptyMap());
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

  @Test
  @Betamax(tape = "csi_testFindSubCategoriesWithContract_search", match = {MatchRule.path, MatchRule.query})
  @Override
  public void testFindSubCategoriesWithContract() throws Exception {
    super.testFindSubCategoriesWithContract();
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
  @Override
  public void testFindRootCategory() throws Exception {
    super.testFindRootCategory();
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

  @Test(expected = CommerceException.class)
  @Override
  public void testWithStoreContextRethrowException() {
    super.testWithStoreContextRethrowException();
  }
}
