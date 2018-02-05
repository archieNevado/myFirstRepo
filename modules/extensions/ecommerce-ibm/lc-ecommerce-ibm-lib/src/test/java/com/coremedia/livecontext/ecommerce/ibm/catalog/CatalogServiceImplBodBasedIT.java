package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for BOD REST interface.
 */
@ContextConfiguration(classes = {IbmServiceTestBase.LocalConfig.class, CatalogServiceImplBodBasedIT.LocalConfig.class})
public class CatalogServiceImplBodBasedIT extends IbmCatalogServiceBaseTest {
  @Configuration
  @ImportResource(
          value = {
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-bod-customizers.xml"
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import(XmlRepoConfiguration.class)
  @Profile(IbmServiceTestBase.LocalConfig.PROFILE)
  public static class LocalConfig {
  }

  @Betamax(tape = "csi_testFindCategoryById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryById() throws Exception {
    super.testFindCategoryById();
  }

  @Betamax(tape = "csi_testFindProductByIdNotFound", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByIdNotFound() throws Exception {
    super.testFindProductByIdNotFound();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechId() throws Exception {
    super.testFindProductByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechIdIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechIdIsNull() throws Exception {
    super.testFindProductByExternalTechIdIsNull();
  }

  @Betamax(tape = "csi_testFindProductVariantById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantById() throws Exception {
    super.testFindProductVariantById();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalTechId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalTechId() throws Exception {
    super.testFindProductVariantByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductBySeoSegment() throws Exception {
    super.testFindProductBySeoSegment();
  }

  @Betamax(tape = "csi_testFindProductBySeoSegmentIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductBySeoSegmentIsNull() throws Exception {
    super.testFindProductBySeoSegmentIsNull();
  }

  @Betamax(tape = "csi_testFindProductsByCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategory() throws Exception {
    super.testFindProductsByCategory();
  }

  @Betamax(tape = "csi_testFindProductsByCategoryIsEmpty", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategoryIsEmpty() throws Exception {
    super.testFindProductsByCategoryIsEmpty();
  }

  @Betamax(tape = "csi_testFindProductsByCategoryIsRoot", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategoryIsRoot() throws Exception {
    super.testFindProductsByCategoryIsRoot();
  }

  @Betamax(tape = "csi_testSearchProducts", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testSearchProducts() throws Exception {
    super.testSearchProducts();
  }

  @Test
  @Betamax(tape = "csi_testSearchProductVariants", match = {MatchRule.path, MatchRule.query})
  @Override
  public void testSearchProductVariants() throws Exception {
    super.testSearchProductVariants();
  }

  @Betamax(tape = "csi_testFindTopCategories", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindTopCategories() throws Exception {
    super.testFindTopCategories();
  }

  @Betamax(tape = "csi_testFindSubCategories", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategories() throws Exception {
    super.testFindSubCategories();
  }

  @Betamax(tape = "csi_testFindSubCategoriesIsEmpty", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategoriesIsEmpty() throws Exception {
    super.testFindSubCategoriesIsEmpty();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalTechId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalTechId() throws Exception {
    super.testFindCategoryByExternalTechId();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalTechIdIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalTechIdIsNull() throws Exception {
    super.testFindCategoryByExternalTechIdIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegment() throws Exception {
    super.testFindCategoryBySeoSegment();
  }

  @Betamax(tape = "csi_testFindCategoryBySeoSegmentIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegmentIsNull() throws Exception {
    super.testFindCategoryBySeoSegmentIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryByIdIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByIdIsNull() {
    super.testFindCategoryByIdIsNull();
  }

  @Betamax(tape = "csi_testWithStoreContext", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testWithStoreContext() {
    super.testWithStoreContext();
  }

  @Override
  @Test(expected = CommerceException.class)
  public void testWithStoreContextRethrowException() {
    super.testWithStoreContextRethrowException();
  }

  @Betamax(tape = "csi_testFindProductById", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductById() throws Exception {
    super.testFindProductById();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalIdWithContractSupport", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalIdWithContractSupport() throws Exception {
    super.testFindProductVariantByExternalIdWithContractSupport();
  }

  @Test
  @Override
  @Ignore("BOD cannot handle slash in product code")
  public void testFindProductByIdWithSlash() {
    super.testFindProductByIdWithSlash();
  }

  @Betamax(tape = "csi_testFindProductMultiSEOByExternalTechId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductMultiSEOByExternalTechId() throws Exception {
    super.testFindProductMultiSEOByExternalTechId();
  }

  @Test
  @Override
  @Ignore("BOD cannot handle slash in sku code")
  public void testFindProductVariantByIdWithSlash() throws Exception {
    super.testFindProductVariantByIdWithSlash();
  }

  @Betamax(tape = "csi_testFindRootCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindRootCategory() throws Exception {
    super.testFindRootCategory();
  }

  @Test
  @Override
  @Ignore("No Contract Support for BOD Handler")
  public void testFindTopCategoriesWithContractSupport() throws Exception {
    super.testFindTopCategoriesWithContractSupport();
  }

  @Betamax(tape = "csi_testFindCategoryMultiSEOByExternalTechId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryMultiSEOByExternalTechId() throws Exception {
    StoreContext storeContext = getStoreContext();
    CommerceId categoryId = getIdProvider().formatCategoryId(storeContext.getCatalogAlias(), CATEGORY1_WITH_MULTI_SEO);
    Category category = testling.findCategoryById(categoryId, storeContext);
    String seoSegment = category.getSeoSegment();
    assertNotNull(seoSegment);
    assertFalse(seoSegment.contains(";"));
  }

  @Betamax(tape = "csi_testFindGermanCategoryBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindGermanCategoryBySeoSegment() throws Exception {
    super.testFindGermanCategoryBySeoSegment();
  }

  @Test
  @Override
  @Ignore("BOD cannot handle slash in category code")
  public void testFindCategoryByIdWithSlash() {
    super.testFindCategoryByIdWithSlash();
  }
}
