package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.HANDLERS;
import static com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImplSearchBasedIT.LocalConfig.PROFILE;

/**
 * Tests for BOD REST interface.
 */
@ContextConfiguration(classes = CatalogServiceImplSearchBasedIT.LocalConfig.class)
@ActiveProfiles(PROFILE)
public class CatalogServiceImplSearchBasedIT extends BaseTestsCatalogServiceImpl {
  @Configuration
  @ImportResource(
          value = {
                  HANDLERS,
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml",
                  "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-search.xml",
                  "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import(XmlRepoConfiguration.class)
  @Profile(PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "CatalogServiceImplSearchBasedTest";
  }

  @Before
  public void setup() {
    super.setup();
    testling.getCatalogWrapperService().clearLanguageMapping();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @Betamax(tape = "csi_testFindProductByPartNumber_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalId() throws Exception {
    super.testFindProductByExternalId();
  }

  @Betamax(tape = "csi_testFindProductByPartNumberWithSlash_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalIdWithSlash() throws Exception {
    super.testFindProductByExternalIdWithSlash();
  }

  @Betamax(tape = "csi_testFindProductByPartNumberIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalIdIsNull() throws Exception {
    super.testFindProductByExternalIdIsNull();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechId() throws Exception {
    super.testFindProductByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechIdIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechIdIsNull() throws Exception {
    super.testFindProductByExternalTechIdIsNull();
  }

  @Betamax(tape = "csi_testFindProductByExternalIdReturns502_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalIdReturns502() throws Exception {
    super.testFindProductByExternalIdReturns502();
  }

  @Betamax(tape = "csi_testFindProduct2ByExternalId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProduct2ByExternalId() throws Exception {
    super.testFindProduct2ByExternalId();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalId() throws Exception {
    super.testFindProductVariantByExternalId();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalIdWithSlash_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalIdWithSlash() throws Exception {
    super.testFindProductVariantByExternalIdWithSlash();
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

  @Betamax(tape = "csi_testSearchProducts_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testSearchProducts() throws Exception {
    super.testSearchProducts();
  }

  @Test
  @Betamax(tape = "csi_testSearchProductVariants_search", match = {MatchRule.path, MatchRule.query})
  @Override
  public void testSearchProductVariants() throws Exception {
    super.testSearchProductVariants();
  }

  @Betamax(tape = "csi_testFindTopCategories_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindTopCategories() throws Exception {
    super.testFindTopCategories();
  }

  @Betamax(tape = "csi_testFindSubCategories_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategories() throws Exception {
    super.testFindSubCategories();
  }

  @Betamax(tape = "csi_testFindSubCategoriesWithContract_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Ignore
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
    StoreContextHelper.setCurrentContext(testConfig.getGermanStoreContext());
    super.testFindGermanCategoryBySeoSegment();
  }

  @Betamax(tape = "csi_testFindCategoryBySeoSegmentIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegmentIsNull() throws Exception {
    super.testFindCategoryBySeoSegmentIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryByPartNumber_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalId() {
    super.testFindCategoryByExternalId();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalIdIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalIdIsNull() {
    super.testFindCategoryByExternalIdIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryByPartNumberWithSlash_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalIdWithSlash() {
    super.testFindCategoryByExternalIdWithSlash();
  }
}
