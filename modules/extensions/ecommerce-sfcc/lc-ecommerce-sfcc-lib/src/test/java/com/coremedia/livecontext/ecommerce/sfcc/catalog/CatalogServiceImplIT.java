package com.coremedia.livecontext.ecommerce.sfcc.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.lc.test.CatalogServiceBaseTest;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.ecommerce.sfcc.SfccTestInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServiceTestBaseConfiguration.class, initializers = SfccTestInitializer.class)
public class CatalogServiceImplIT extends CatalogServiceBaseTest {

  private static final String ROOT_CATEGORY_ID = "root";

  @MockBean
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Inject
  CatalogServiceImpl testling;

  @Value("${PRODUCT_CODE}")
  private String PRODUCT_CODE;

  @Value("${PRODUCT_CODE_B2B}")
  private String PRODUCT_CODE_B2B;

  @Value("${PRODUCT_NAME}")
  private String PRODUCT_NAME;

  @Value("${PRODUCT_CODE_WITH_SLASH}")
  private String PRODUCT_CODE_WITH_SLASH;

  @Value("${PRODUCT_SEO_SEGMENT}")
  private String PRODUCT_SEO_SEGMENT;

  @Value("${CATEGORY_CODE}")
  private String CATEGORY_CODE;

  @Value("${CATEGORY_CODE_B2B}")
  private String CATEGORY_CODE_B2B;

  @Value("${CATEGORY_WITH_SLASH}")
  private String CATEGORY_WITH_SLASH;

  @Value("${CATEGORY_SEO_SEGMENT}")
  private String CATEGORY_SEO_SEGMENT;

  @Value("${CATEGORY_SEO_SEGMENT_DE}")
  private String CATEGORY_SEO_SEGMENT_DE;

  @Value("${CATEGORY_NAME}")
  private String CATEGORY_NAME;

  @Value("${PRODUCT_VARIANT_CODE}")
  private String PRODUCT_VARIANT_CODE;

  @Value("${PRODUCT_VARIANT_WITH_SLASH}")
  private String PRODUCT_VARIANT_WITH_SLASH;

  @Value("${SEARCH_TERM_1}")
  private String SEARCH_TERM_1;

  @Value("${SEARCH_TERM_2}")
  private String SEARCH_TERM_2;

  @Value("${SEARCH_TERM_3}")
  private String SEARCH_TERM_3;

  @Value("${TOP_CATEGORY_NAME}")
  private String TOP_CATEGORY_NAME;

  @Value("${LEAF_CATEGORY_CODE}")
  private String LEAF_CATEGORY_CODE;

  @Value("${FILTER_NAME}")
  private String FILTER_NAME;

  @Value("${FILTER_VALUE}")
  private String FILTER_VALUE;

  @Before
  public void setup() {
    super.setup();

    when(catalogAliasTranslationService.getCatalogIdForAlias(any(), any(), any(StoreContext.class)))
            .thenReturn(Optional.of(CatalogId.of("storefront-catalog-non-en")));
  }

  @Test
  public void testProductVariantAttributes() {
    if (useBetamaxTapes()) {
      return;
    }

    Product tg250 = testling.findProductById(getIdProvider().formatProductId(null, "TG250"), storeContext);
    ProductVariant productVariantA = tg250.getVariants().get(0);
    ProductVariant productVariantB = tg250.getVariants().get(1);

    List<ProductAttribute> definingAttributesA = productVariantA.getDefiningAttributes();
    List<ProductAttribute> definingAttributesB = productVariantB.getDefiningAttributes();

    assertThat(definingAttributesA.size()).isEqualTo(definingAttributesB.size());

    String colorA = (String) productVariantA.getAttributeValue(definingAttributesA.get(0).getId());
    String colorB = (String) productVariantB.getAttributeValue(definingAttributesB.get(0).getId());
    assertThat(colorA).isEqualTo(colorB);
    assertThat(colorA).isEqualTo("Black");

    String sizeA = (String) productVariantA.getAttributeValue(definingAttributesA.get(1).getId());
    String sizeB = (String) productVariantB.getAttributeValue(definingAttributesB.get(1).getId());
    assertThat(sizeA).isNotEqualTo(sizeB);
  }

  @Test
  public void testFindProductByIdNotFound() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindProductByIdNotFound();
  }

  @Test
  public void testFindProductById() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindProductById();
  }

  @Test
  public void testFindProductVariantById() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindProductVariantById();
  }

  @Test
  public void testFindProductsByCategory() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindProductsByCategory();
  }

  @Test
  public void testFindProductsByCategoryIsEmpty() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindProductsByCategoryIsEmpty();
  }

  @Test
  public void testFindProductsByCategoryIsRoot() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    testFindProductsByCategoryIsRoot(ROOT_CATEGORY_ID);
  }

  @Test
  public void testFindTopCategories() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindTopCategories();
  }

  @Test
  public void testFindRootCategory() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    testFindRootCategory(ROOT_CATEGORY_ID);
  }

  @Test
  public void testFindSubCategories() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindSubCategories();
  }

  @Test
  public void testFindSubCategoriesIsEmpty() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindSubCategoriesIsEmpty();
  }

  @Test
  public void testFindCategoryById() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindCategoryById();
  }

  @Test
  public void testFindCategoryByIdIsNull() {
    if (useBetamaxTapes()) {
      return;
    }

    super.testFindCategoryByIdIsNull();
  }

  @Test
  public void testSearchProducts() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testSearchProducts();
  }

  @Test
  public void testGetFacetSearchProducts() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }
    super.testGetFacetSearchProducts();
  }

  @Test
  public void testSearchProductsWithFacet() {
    if (useBetamaxTapes()) {
      return;
    }

    CommerceId categoryId = getIdProvider().formatCategoryId(null, LEAF_CATEGORY_CODE);
    Category category = testling.findCategoryById(categoryId, storeContext);
    Map<String, String> searchParams = new HashMap<>();

    searchParams.put(CatalogService.SEARCH_PARAM_FACET_SUPPORT, "true");
    //test the price facet
    searchParams.put(CatalogService.SEARCH_PARAM_TOTAL, "200");
    searchParams.put(CatalogService.SEARCH_PARAM_FACET, "price=(20..50)");
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    SearchResult<Product> searchProducts = testling.searchProducts("", searchParams, storeContext);
    assertThat(searchProducts).isNotNull();
    assertThat(searchProducts.getTotalCount()).isEqualTo(70);

    //test the color facet
    searchParams.put(CatalogService.SEARCH_PARAM_FACET, "c_refinementColor=Blue");
    searchProducts = testling.searchProducts("", searchParams, storeContext);
    assertThat(searchProducts).isNotNull();
    assertThat(searchProducts.getTotalCount()).isEqualTo(27);
  }

  @Test
  public void testSearchProductsWithOffset() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testSearchProductsWithOffset();
  }

  @Test
  public void testSearchProductVariants() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    super.testSearchProductVariants();
  }

  @Test
  public void testWithStoreContext() {
    if (useBetamaxTapes()) {
      return;
    }

    super.testWithStoreContext();
  }

  protected void assertProduct(Product product) {
    assertThat(product).isNotNull();
    assertThat(product.getName()).isNotEmpty();
    assertThat(product.getDefaultImageUrl()).endsWith(".jpg");
    assertThat(product.getThumbnailUrl()).endsWith(".jpg");
    assertThat(product.getCategory()).isNotNull();

    // test attributes
    /*assertThat(product.getDefiningAttributes()).isNotEmpty();*/

    // test variants
    List<ProductVariant> variants = product.getVariants();
    assertThat(variants).isNotEmpty();

    // test axis filter
    /*List<String> variantAxisNames = product.getVariantAxisNames();
    if (!variantAxisNames.isEmpty()) {
      List<ProductVariant> filteredVariants = product.getVariants(new AxisFilter(variantAxisNames.get(0), "*"));
      assertThat(variants.size()).isGreaterThanOrEqualTo(filteredVariants.size());
    }*/
  }

  @Override
  protected void assertProductVariant(ProductVariant productVariant) {
    assertThat(productVariant).isNotNull();

    Product parentProduct = productVariant.getParent();
    assertThat(parentProduct).isNotNull();
    assertThat(productVariant.getName()).isNotNull();
    assertThat(productVariant.getDefaultImageUrl()).endsWith(".jpg");
    assertThat(productVariant.getThumbnailUrl()).endsWith(".jpg");
    // OfferPrice not yet available
    //assertThat(productVariant.getOfferPrice()).isNotNull();
  }

  @Override
  protected void assertCategory(Category category) {
    assertThat(category).isNotNull();
    assertThat(category.getExternalId()).isNotEmpty();
    assertThat(category.getName()).isNotEmpty();
    assertThat(category.getParent()).isNotNull();
    // do not chech image data, since images are not always given
    //assertThat(category.getDefaultImageUrl()).isNotNull();
    // we do not have test data with thumbnail
    //assertThat(category.getThumbnailUrl()).isNotNull();
    assertThat(category.getShortDescription()).isNotNull();
    assertThat(category.getChildren()).isNotNull();
    assertThat(category.getProducts()).isNotNull();

    List<Category> categoryBreadcrumb = category.getBreadcrumb();
    assertThat(categoryBreadcrumb).isNotEmpty();
    assertThat(categoryBreadcrumb.get(categoryBreadcrumb.size() - 1)).isEqualTo(category);
    assertThat(category.getLocale()).isNotNull();
  }
}
