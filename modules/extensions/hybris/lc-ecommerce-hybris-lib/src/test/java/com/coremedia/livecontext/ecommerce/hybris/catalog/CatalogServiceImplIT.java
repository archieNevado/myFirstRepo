package com.coremedia.livecontext.ecommerce.hybris.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.lc.test.CatalogServiceBaseTest;
import com.coremedia.blueprint.lc.test.TestConfig;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.hybris.HybrisTestConfig;
import com.coremedia.livecontext.ecommerce.hybris.SystemProperties;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, HybrisTestConfig.class})
public class CatalogServiceImplIT extends CatalogServiceBaseTest {

  @Inject
  CatalogServiceImpl testling;

  @Inject
  TestConfig testConfig;

  @Betamax(tape = "hy_testFindProductById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductById() throws Exception {
    super.testFindProductById();
  }

  @Betamax(tape = "hy_testFindProductById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryById() throws Exception {
    super.testFindCategoryById();
  }

  @Betamax(tape = "hy_testFindProductById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByIdNotFound() throws Exception {
    super.testFindProductByIdNotFound();
  }

  @Betamax(tape = "hy_testFindProductByIdWithSlash", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore
  public void testFindProductByIdWithSlash() {
    super.testFindProductByIdWithSlash();
  }

  @Betamax(tape = "hy_testFindProductBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore("seo segments not supported")
  public void testFindProductBySeoSegment() throws Exception {
    super.testFindProductBySeoSegment();
  }

  @Betamax(tape = "hy_testFindProductBySeoSegmentIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore("seo segments not supported")
  public void testFindProductBySeoSegmentIsNull() throws Exception {
    super.testFindProductBySeoSegmentIsNull();
  }

  @Betamax(tape = "hy_testFindProductVariantById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantById() throws Exception {
    super.testFindProductVariantById();
  }

  @Betamax(tape = "hy_testFindProductVariantsByProductId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindProductVariantsByProductId() {
    Product product = testling.findProductById(PRODUCT_CODE);
    assertThat(product).isNotNull();

    List<ProductVariant> variants = product.getVariants();
    assertThat(variants).isNotEmpty();
  }

  @Betamax(tape = "hy_testProductAxisFilters", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductAxisFilters() {
    Product product = testling.findProductById(PRODUCT_CODE);
    assertThat(product).isNotNull();

    List<String> variantAxisNames = product.getVariantAxisNames();
    assertThat(variantAxisNames).isNotEmpty();
  }

  @Betamax(tape = "hy_testFindProductVariantByIdWithSlash", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore
  public void testFindProductVariantByIdWithSlash() throws Exception {
    super.testFindProductVariantByIdWithSlash();
  }

  @Betamax(tape = "hy_testFindProductsByCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategory() throws Exception {
    super.testFindProductsByCategory();
  }

  @Betamax(tape = "hy_testFindProductsByCategoryCheckProductsOnly", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindProductsByCategoryCheckProductsOnly() throws Exception {
    String productId = getIdProvider().formatProductId(PRODUCT_CODE);
    Product product = testling.findProductById(productId);
    assertThat(product).isNotNull();

    Category category = product.getCategory();
    List<Product> productsByCategory = testling.findProductsByCategory(category);
    for (Product productInList : productsByCategory) {
      assertThat(productInList.isVariant()).isFalse();
    }
  }

  @Betamax(tape = "hy_testFindProductsByCategoryIsEmpty", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategoryIsEmpty() throws Exception {
    super.testFindProductsByCategoryIsEmpty();
  }

  @Betamax(tape = "hy_testFindProductsByCategoryIsRoot", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategoryIsRoot() throws Exception {
    super.testFindProductsByCategoryIsRoot();
  }

  @Betamax(tape = "hy_testFindTopCategories", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindTopCategories() throws Exception {
    super.testFindTopCategories();
  }

  @Betamax(tape = "hy_testFindRootCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindRootCategory() throws Exception {
    super.testFindRootCategory();
  }

  @Betamax(tape = "hy_testFindSubCategories", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategories() throws Exception {
    super.testFindSubCategories();
  }

  @Betamax(tape = "hy_testFindSubCategoriesIsEmpty", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategoriesIsEmpty() throws Exception {
    super.testFindSubCategoriesIsEmpty();
  }

  @Betamax(tape = "hy_testFindCategoryByIdWithSlash", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore
  public void testFindCategoryByIdWithSlash() {
    super.testFindCategoryByIdWithSlash();
  }

  @Betamax(tape = "hy_testFindCategoryByIdIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByIdIsNull() {
    super.testFindCategoryByIdIsNull();
  }

  @Betamax(tape = "hy_testFindCategoryBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore("seo not supported")
  public void testFindCategoryBySeoSegment() throws Exception {
    super.testFindCategoryBySeoSegment();
  }

  @Betamax(tape = "hy_testFindGermanCategoryBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  @Ignore("no seo support")
  public void testFindGermanCategoryBySeoSegment() throws Exception {
    super.testFindGermanCategoryBySeoSegment();
  }

  @Betamax(tape = "hy_testFindCategoryBySeoSegmentIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegmentIsNull() throws Exception {
    super.testFindCategoryBySeoSegmentIsNull();
  }

  @Test
  @Override
  public void testSearchProducts() throws Exception {
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) {
      return;
    }

    //TODO: call super.testSearchProducts(), when hybris rest services are customized
    SearchResult<Product> searchResult = testling.searchProducts(SEARCH_TERM_1, null);
    assertThat(searchResult).isNotNull();
    assertThat(searchResult.getSearchResult()).isNotEmpty();

    //search result paging
    //since hybris searches always for Products and ProductVariants
    //search result paging
    //TODO: fix in hybris rest implementation

    //search product below category
    Category category = testling.findCategoryById(getIdProvider().formatCategoryId(LEAF_CATEGORY_CODE));
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    SearchResult<Product> searchResultByCategory = testling.searchProducts(SEARCH_TERM_1, searchParams);
    assertThat(searchResultByCategory).isNotNull();
    assertThat(searchResultByCategory.getSearchResult().size()).isGreaterThanOrEqualTo(3);
    assertThat(searchResultByCategory.getTotalCount()).isLessThan(searchResult.getTotalCount());

    //search product with invalid param
    Map<String, String> ignoredParam = new HashMap<>();
    ignoredParam.put("blub", "10");
    ignoredParam.put(CatalogService.SEARCH_PARAM_PAGESIZE, "10");
    ignoredParam.put(CatalogService.SEARCH_PARAM_PAGENUMBER, "1");
    SearchResult<Product> searchResultIgnoredParam = testling.searchProducts(SEARCH_TERM_1, ignoredParam);
    assertThat(searchResultIgnoredParam).isNotNull();
    assertEquals("given page size has changed", searchResultIgnoredParam.getPageSize(), 10);

    //search product no hits
    SearchResult<Product> searchResultEmpty = testling.searchProducts("schnasndasn", null);
    assertThat(searchResultEmpty).isNotNull();
    assertThat(searchResultEmpty.getSearchResult()).isEmpty();

    //search product multiple words no hit
    //since hybris uses "OR" as default search operation for multiple terms
    //search result reduction is not working.
    //TODO: fix in hybris rest implementation
  }

  @Test
  public void testSearchFacetsProducts() throws Exception {
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) {
      return;
    }
    HashMap<String,String> searchParams = new HashMap<>();
    searchParams.put("fields", "DEFAULT,facets");

    super.testSearchFacetsProducts(SEARCH_TERM_1, searchParams);
  }

  @Test
  @Override
  public void testSearchProductVariants() throws Exception {
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) {
      return;
    }

    super.testSearchProductVariants();
  }

  @Betamax(tape = "hy_testWithStoreContext", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testWithStoreContext() {
    super.testWithStoreContext();
  }

  @Betamax(tape = "hy_testWithStoreContextRethrowException", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testWithStoreContextRethrowException() {
    super.testWithStoreContextRethrowException();
  }
}
