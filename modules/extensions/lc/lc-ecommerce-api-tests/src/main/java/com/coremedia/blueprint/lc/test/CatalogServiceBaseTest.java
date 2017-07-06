package com.coremedia.blueprint.lc.test;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.catalog.AxisFilter;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public abstract class CatalogServiceBaseTest extends AbstractServiceTest {

  @Inject
  CatalogService testling;

  @Value("${PRODUCT_CODE}")
  protected String PRODUCT_CODE;

  @Value("${PRODUCT_NAME}")
  protected String PRODUCT_NAME;

  @Value("${PRODUCT_CODE_WITH_SLASH}")
  protected String PRODUCT_CODE_WITH_SLASH;

  @Value("${PRODUCT_SEO_SEGMENT}")
  protected String PRODUCT_SEO_SEGMENT;

  @Value("${CATEGORY_CODE}")
  protected String CATEGORY_CODE;

  @Value("${CATEGORY_WITH_SLASH}")
  protected String CATEGORY_WITH_SLASH;

  @Value("${CATEGORY_SEO_SEGMENT}")
  protected String CATEGORY_SEO_SEGMENT;

  @Value("${CATEGORY_SEO_SEGMENT_DE}")
  protected String CATEGORY_SEO_SEGMENT_DE;

  @Value("${CATEGORY_NAME}")
  protected String CATEGORY_NAME;

  @Value("${PRODUCT_VARIANT_CODE}")
  protected String PRODUCT_VARIANT_CODE;

  @Value("${PRODUCT_VARIANT_WITH_SLASH}")
  protected String PRODUCT_VARIANT_WITH_SLASH;

  @Value("${SEARCH_TERM_1}")
  protected String SEARCH_TERM_1;

  @Value("${SEARCH_TERM_2}")
  protected String SEARCH_TERM_2;

  @Value("${TOP_CATEGORY_NAME}")
  protected String TOP_CATEGORY_NAME;

  @Value("${LEAF_CATEGORY_CODE}")
  protected String LEAF_CATEGORY_CODE;

  @Value("${FILTER_NAME}")
  protected String FILTER_NAME;

  @Value("${FILTER_VALUE}")
  protected String FILTER_VALUE;

  protected void testFindProductById() throws Exception {
    String productId = getIdProvider().formatProductId(PRODUCT_CODE);

    Product product = testling.findProductById(productId);

    assertThat(product.getExternalId()).isEqualTo(PRODUCT_CODE);
    assertProduct(product);
    assertThat(product.getDefiningAttributes()).isNotEmpty();
  }

  protected void testFindProductByIdNotFound() throws Exception {
    String productId = getIdProvider().formatProductId("blablablub");

    Product product = testling.findProductById(productId);

    assertThat(product).isNull();
  }

  protected void testFindProductByIdWithSlash() {
    String productId = getIdProvider().formatProductId(PRODUCT_CODE_WITH_SLASH);

    Product product = testling.findProductById(productId);

    assertThat(product).isNotNull();
    assertThat(product.getExternalId()).isEqualTo(PRODUCT_CODE_WITH_SLASH);
  }

  protected void testFindProductBySeoSegment() throws Exception {
    Product product = testling.findProductBySeoSegment(PRODUCT_SEO_SEGMENT);

    assertThat(product.getSeoSegment()).isEqualTo(PRODUCT_SEO_SEGMENT);
    assertProduct(product);
  }

  protected void testFindProductBySeoSegmentIsNull() throws Exception {
    Product product = testling.findProductBySeoSegment("blablablub");

    assertThat(product).isNull();
  }

  protected void testFindProductVariantById() throws Exception {
    String productVariantId = getIdProvider().formatProductVariantId(PRODUCT_VARIANT_CODE);

    ProductVariant productVariant = testling.findProductVariantById(productVariantId);
    assertThat(productVariant).isNotNull();
    assertThat(productVariant.getExternalId()).isEqualTo(PRODUCT_VARIANT_CODE);
    assertProductVariant(productVariant);

    assertThat(productVariant.getDefiningAttributes()).isNotEmpty();
    assertThat(productVariant.getDescribingAttributes()).isNotEmpty();

    Product product = productVariant.getParent();
    assertProduct(product);
  }

  protected void testFindProductVariantByIdWithSlash() throws Exception {
    String productVariantId = getIdProvider().formatProductVariantId(PRODUCT_VARIANT_WITH_SLASH);

    ProductVariant productVariant = testling.findProductVariantById(productVariantId);

    assertThat(productVariant).isNotNull();
    assertThat(productVariant.getExternalId()).isEqualTo(PRODUCT_VARIANT_WITH_SLASH);
  }

  protected void testFindProductsByCategory() throws Exception {
    String productId = getIdProvider().formatProductId(PRODUCT_CODE);

    Product product = testling.findProductById(productId);
    assertThat(product).isNotNull();

    Category category = product.getCategory();
    List<Product> productsByCategory = testling.findProductsByCategory(category);
    assertThat(productsByCategory).isNotEmpty();
    assertProduct(productsByCategory.get(0));
    assertThat(productsByCategory.get(0).getCategory()).isEqualTo(category);
  }

  protected void testFindProductsByCategoryIsEmpty() throws Exception {
    Category rootCategory = testling.findRootCategory();//does not have any products
    List<Product> products = testling.findProductsByCategory(rootCategory);

    assertThat(products).isEmpty();
  }

  protected void testFindProductsByCategoryIsRoot() throws Exception {
    String categoryId = getIdProvider().formatCategoryId("ROOT");

    Category rootCategory = testling.findCategoryById(categoryId);
    List<Product> products = testling.findProductsByCategory(rootCategory);

    assertThat(products).isEmpty();
  }

  protected void testFindTopCategories() throws Exception {
    List<Category> topCategories = testling.findTopCategories(null);

    assertThat(topCategories).isNotEmpty();

    Category category = topCategories.get(0);
    assertCategory(category);

    Category categoryParent = category.getParent();
    assertThat(categoryParent).isNotNull();
    assertThat(categoryParent.isRoot()).isTrue();
  }

  protected void testFindRootCategory() throws Exception {
    String categoryId = getIdProvider().formatCategoryId("ROOT");

    Category rootCategory = testling.findCategoryById(categoryId);

    assertThat(rootCategory).isNotNull();
    assertThat(rootCategory.isRoot()).isTrue();
    assertThat(rootCategory.getChildren()).isNotEmpty();
    assertThat(rootCategory.getBreadcrumb()).isEmpty();
  }

  protected void testFindSubCategories() throws Exception {
    Category category = findAndAssertCategory(TOP_CATEGORY_NAME, null);

    List<Category> subCategories = testling.findSubCategories(category);
    assertThat(subCategories).isNotEmpty();

    Category firstSubCategory = subCategories.get(0);
    assertCategory(firstSubCategory);
    assertThat(firstSubCategory.getBreadcrumb()).hasSize(2);
  }

  protected void testFindSubCategoriesIsEmpty() throws Exception {
    String categoryId = getIdProvider().formatCategoryId(LEAF_CATEGORY_CODE);

    Category leafCategory = testling.findCategoryById(categoryId);
    List<Category> subCategories = testling.findSubCategories(leafCategory);

    assertThat(subCategories).isEmpty();
  }

  protected void testFindCategoryById() throws Exception {
    String categoryId = getIdProvider().formatCategoryId(CATEGORY_CODE);

    Category category = testling.findCategoryById(categoryId);

    assertCategory(category);
  }

  protected void testFindCategoryByIdWithSlash() {
    String categoryId = getIdProvider().formatCategoryId(CATEGORY_WITH_SLASH);

    Category category = testling.findCategoryById(categoryId);

    assertThat(category).isNotNull();
  }

  protected void testFindCategoryByIdIsNull() {
    String categoryId = getIdProvider().formatCategoryId("balablablub");

    Category category = testling.findCategoryById(categoryId);

    assertThat(category).isNull();
  }

  protected void testFindCategoryBySeoSegment() throws Exception {
    Category category = testling.findCategoryBySeoSegment(CATEGORY_SEO_SEGMENT);

    assertCategory(category);
  }

  protected void testFindGermanCategoryBySeoSegment() throws Exception {
    StoreContext storeContext = testConfig.getStoreContext();
    Locale germanLocale = new Locale("de", "DE");
    storeContext.put(LOCALE, germanLocale);
    setStoreContext(storeContext);

    Category category = testling.findCategoryBySeoSegment("kleider");

    assertThat(category).isNotNull();
    assertThat(category.getLocale()).isEqualTo(germanLocale);
    assertThat(category.getParent().getLocale()).isEqualTo(germanLocale);
  }

  protected void testFindCategoryBySeoSegmentIsNull() throws Exception {
    Category category = testling.findCategoryBySeoSegment("blablablub");

    assertThat(category).isNull();
  }

  protected void testSearchProducts() throws Exception {
    SearchResult<Product> searchResult = testling.searchProducts(SEARCH_TERM_1, null);
    assertThat(searchResult).isNotNull();
    assertThat(searchResult.getSearchResult()).isNotEmpty();

    // search product below category
    String categoryId = getIdProvider().formatCategoryId(LEAF_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId);
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    SearchResult<Product> searchResultByCategory = testling.searchProducts(SEARCH_TERM_1, searchParams);
    assertThat(searchResultByCategory).isNotNull();
    assertThat(searchResultByCategory.getSearchResult().size()).isGreaterThanOrEqualTo(3);
    assertThat(searchResultByCategory.getTotalCount()).isLessThan(searchResult.getTotalCount());

    // search product with paging
    Map<String, String> pagingParams = new HashMap<>();
    pagingParams.put(CatalogService.SEARCH_PARAM_PAGESIZE, "10");
    pagingParams.put(CatalogService.SEARCH_PARAM_PAGENUMBER, "1");
    SearchResult<Product> searchResultPaging = testling.searchProducts(SEARCH_TERM_1, pagingParams);
    assertThat(searchResultPaging).isNotNull();
    Product product1 = searchResultPaging.getSearchResult().get(9);

    pagingParams.put(CatalogService.SEARCH_PARAM_PAGESIZE, "9");
    pagingParams.put(CatalogService.SEARCH_PARAM_PAGENUMBER, "2");
    searchResultPaging = testling.searchProducts(SEARCH_TERM_1, pagingParams);
    Product product2 = searchResultPaging.getSearchResult().get(0);
    assertThat(searchResultPaging).isNotNull();
    assertThat(product1.getId()).isEqualTo(product2.getId());

    // search product with invalid param
    Map<String, String> ignoredParam = new HashMap<>();
    ignoredParam.put("blub", "10");
    ignoredParam.put(CatalogService.SEARCH_PARAM_PAGESIZE, "10");
    ignoredParam.put(CatalogService.SEARCH_PARAM_PAGENUMBER, "1");
    SearchResult<Product> searchResultIgnoredParam = testling.searchProducts(SEARCH_TERM_1, ignoredParam);
    assertThat(searchResultIgnoredParam).isNotNull();

    // search product no hits
    SearchResult<Product> searchResultEmpty = testling.searchProducts("schnasndasn", null);
    assertThat(searchResultEmpty).isNotNull();
    assertThat(searchResultEmpty.getSearchResult()).isEmpty();

    // search product multiple words
    SearchResult<Product> searchResultMultipleWords = testling.searchProducts(SEARCH_TERM_1 + " " + SEARCH_TERM_2, null);
    assertThat(searchResultMultipleWords).isNotNull();
    assertThat(searchResultMultipleWords.getSearchResult()).isNotEmpty();

    // search product multiple words no hit
    SearchResult<Product> searchResultMultipleWords2 = testling.searchProducts(SEARCH_TERM_2 + " schnasndasn", null);
    assertThat(searchResultMultipleWords2).isNotNull();
    assertThat(searchResultMultipleWords2.getSearchResult()).isEmpty();
  }

  @Nonnull
  protected Category findAndAssertCategory(@Nonnull String name, @Nullable Category parent) {
    List<Category> topCategories = parent == null ? testling.findTopCategories(null) : testling.findSubCategories(parent);
    assertThat(topCategories).isNotEmpty();

    Category category = null;
    for (Category c : topCategories) {
      if (name.equals(c.getName())) {
        category = c;
      }
    }
    assertThat(category).as("Category '%s' not found", name).isNotNull();

    return category;
  }

  protected void testSearchProductVariants() throws Exception {
    SearchResult<ProductVariant> searchResult1 = testling.searchProductVariants(SEARCH_TERM_1 + " " + SEARCH_TERM_2,
            null);
    assertThat(searchResult1).isNotNull();

    List<ProductVariant> searchResult1ProductVariants = searchResult1.getSearchResult();
    assertThat(searchResult1ProductVariants).isNotEmpty();
    assertThat(checkIfClassIsContained(searchResult1ProductVariants, "ProductVariantImpl")).isTrue();
    assertThat(!checkIfClassIsContained(searchResult1ProductVariants, "ProductImpl")).isTrue();

    // search product variants by parent part number

    SearchResult<ProductVariant> searchResult2 = testling.searchProductVariants(PRODUCT_CODE, null);
    assertThat(searchResult2).isNotNull();

    List<ProductVariant> searchResult2ProductVariants = searchResult2.getSearchResult();
    assertThat(searchResult2ProductVariants)
            .as("Search result must not be empty (search product variants by parent part number)")
            .isNotEmpty();
    assertThat(checkIfClassIsContained(searchResult2ProductVariants, "ProductVariantImpl")).isTrue();
    assertThat(!checkIfClassIsContained(searchResult2ProductVariants, "ProductImpl")).isTrue();
  }

  protected void testSearchFacetsProducts(String query, Map<String, String> searchParams) throws Exception {
    SearchResult<Product> searchResult = testling.searchProducts(query, searchParams);
    assertThat(searchResult).isNotNull();

    List<SearchFacet> facets = searchResult.getFacets();
    assertThat(facets).isNotNull();
    assertThat(facets).isNotEmpty();

    SearchFacet aFacet = facets.get(0);
    aFacet = aFacet.getChildFacets().isEmpty() ? aFacet : aFacet.getChildFacets().get(0);
    testSearchFacet(aFacet);
  }

  protected void testSearchFacet(SearchFacet aFacet) {
    assertThat(aFacet.getCount()).isGreaterThan(0);
    assertThat(aFacet.getLabel()).isNotNull();
    assertThat(aFacet.getQuery()).isNotNull();
    assertThat(aFacet.getExtendedData()).isNotNull();
  }

  protected void testWithStoreContext() {
    StoreContext storeContext = getCurrentStoreContext();
    assertThat(storeContext.getLocale()).isNotEqualTo(Locale.GERMAN);

    StoreContext tempStoreContext = testConfig.getStoreContext();
    tempStoreContext.put(StoreContextImpl.LOCALE, Locale.GERMAN);

    String productId = getIdProvider().formatProductId(PRODUCT_CODE);

    CatalogService catalogServiceWithTempStoreContext = testling.withStoreContext(tempStoreContext);
    Product product = catalogServiceWithTempStoreContext.findProductById(productId);

    assertThat(product.getLocale()).isEqualTo(Locale.GERMAN);
  }

  protected void testWithStoreContextRethrowException() {
    CatalogService catalogServiceWithTempStoreContext = null;

    try {
      StoreContext storeContext = StoreContextImpl.newStoreContext();
      catalogServiceWithTempStoreContext = testling.withStoreContext(storeContext);
    } catch (CommerceException e) {
      e.printStackTrace();
      fail("Exception not expected here, but later...");
    }

    assertThat(catalogServiceWithTempStoreContext).isNotNull();

    // should fail with commerce exception
    String productId = getIdProvider().formatProductId(PRODUCT_CODE);
    catalogServiceWithTempStoreContext.findProductById(productId); // NOSONAR
  }

  protected void assertCategory(Category category) {
    assertThat(category).isNotNull();
    assertThat(category.getExternalId()).isNotEmpty();
//    assertThat(category.getExternalId()).isEqualTo(CATEGORY_CODE);
//    assertThat(category.getName()).isEqualTo(CATEGORY_NAME);
    assertThat(category.getName()).isNotEmpty();
    assertThat(category.getParent()).isNotNull();
//    assertThat(category.getSeoSegment()).isEqualTo(CATEGORY_SEO_SEGMENT);
    assertThat(category.getDefaultImageUrl()).isNotNull();
    assertThat(category.getThumbnailUrl()).isNotNull();
    assertThat(category.getShortDescription()).isNotNull(); // TODO: test more properties
    assertThat(category.getChildren()).isNotNull();
    assertThat(category.getProducts()).isNotNull();

    List<Category> categoryBreadcrumb = category.getBreadcrumb();
    assertThat(categoryBreadcrumb).isNotEmpty();
    assertThat(categoryBreadcrumb.get(categoryBreadcrumb.size() - 1)).isEqualTo(category);
    assertThat(category.getLocale()).isEqualTo(getCurrentStoreContext().getLocale());
  }

  protected void assertProductVariant(ProductVariant productVariant) throws CommerceException {
    assertThat(productVariant).isNotNull();

    Product parentProduct = productVariant.getParent();
    assertThat(parentProduct).isNotNull();
    assertThat(productVariant.getName()).isNotNull();
//    assertThat(productVariant.getSeoSegment()).isEqualTo("travel-laptop-cla022-220301");
    assertThat(productVariant.getDefaultImageUrl()).endsWith(".jpg");
    assertThat(productVariant.getThumbnailUrl()).endsWith(".jpg");
    // TODO: add tests for describing and definig attributes
    assertThat(productVariant.getOfferPrice()).isNotNull();
  }

  static StoreContext getCurrentStoreContext() {
    return DefaultConnection.get().getStoreContext();
  }

  private void setStoreContext(StoreContext storeContext) {
    DefaultConnection.get().setStoreContext(storeContext);
  }

  protected static CommerceIdProvider getIdProvider() {
    return DefaultConnection.get().getIdProvider();
  }

  protected void assertProduct(Product product) throws CommerceException {
    assertThat(product).isNotNull();
    assertThat(product.getName()).isNotEmpty();
    assertThat(product.getDefaultImageUrl()).endsWith(".jpg");
    assertThat(product.getThumbnailUrl()).endsWith(".jpg");
    assertThat(product.getCategory()).isNotNull();

    // test attributes
    assertThat(product.getDefiningAttributes()).isNotEmpty();

    // TODO: add test for describing attibutes

    // test variants
    List<ProductVariant> variants = product.getVariants();
    assertThat(variants).isNotEmpty();

    // test axis filter
    List<String> variantAxisNames = product.getVariantAxisNames();
    if (!variantAxisNames.isEmpty()) {
      List<ProductVariant> filteredVariants = product.getVariants(new AxisFilter(variantAxisNames.get(0), "*"));
      assertThat(variants.size()).isGreaterThanOrEqualTo(filteredVariants.size());
    }
  }

  protected boolean checkIfClassIsContained(Collection<?> items, String containedClassType) {
    return items.stream().anyMatch(item -> item != null && item.getClass().getSimpleName().equals(containedClassType));
  }
}
