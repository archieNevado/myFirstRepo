package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.blueprint.lc.test.CatalogServiceBaseTest;
import com.coremedia.livecontext.ecommerce.catalog.AxisFilter;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.SystemProperties;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmTestConfig;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_8;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public abstract class IbmCatalogServiceBaseTest extends CatalogServiceBaseTest {

  protected static final String BEAN_NAME_CATALOG_SERVICE = "catalogService";

  private static final String PRODUCT_VARIANT_CODE_B2B = "FB041_410101";

  private static final String PRODUCT1_WITH_MULTI_SEO = "PC_WALL_CLOCK";
  private static final String PRODUCT2_WITH_MULTI_SEO = "PC_CANDELABRA";
  private static final String PRODUCT3_WITH_MULTI_SEO = "PC_FRUITBOWL";

  protected static final String CATEGORY1_WITH_MULTI_SEO = "PC_Magazines";
  private static final String CATEGORY2_WITH_MULTI_SEO = "PC_ToEat";
  private static final String CATEGORY3_WITH_MULTI_SEO = "PC_Glasses";

  @Inject
  @Named(BEAN_NAME_CATALOG_SERVICE)
  CatalogServiceImpl testling;

  @Inject
  ContractService contractService;

  @Inject
  UserSessionService userSessionService;

  @Inject
  IbmTestConfig testConfig;

  @Inject
  StoreInfoService storeInfoService;

  @Before
  @Override
  public void setup() {
    super.setup();
    testConfig.setWcsVersion(storeInfoService.getWcsVersion());
    testling.getCatalogWrapperService().clearLanguageMapping();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  public void testFindProductVariantByExternalIdWithContractSupport() throws Exception {
    if ((!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) || StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_8)) {
      return;
    }

    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    ProductVariant productVariant = testling.findProductVariantById(CommerceIdHelper.formatProductVariantId(PRODUCT_VARIANT_CODE_B2B));
    assertNotNull(productVariant);
    BigDecimal offerPrice = productVariant.getOfferPrice();

    prepareContextsForContractBasedPreview();
    ProductVariant productVariantContract = testling.findProductVariantById(CommerceIdHelper.formatProductVariantId(PRODUCT_VARIANT_CODE_B2B));
    BigDecimal offerPriceContract = productVariantContract.getOfferPrice();

    assertTrue("Contract price for product should be lower", offerPrice.floatValue() > offerPriceContract.floatValue());
  }

  public void testFindProductByExternalTechId() throws Exception {
    Product product1 = testling.findProductByExternalId(PRODUCT_CODE);
    Product product2 = testling.findProductByExternalTechId(product1.getExternalTechId());
    assertProduct(product2);
  }

  public void testFindProductByExternalTechIdIsNull() throws Exception {
    Product product = testling.findProductByExternalTechId("blablablub");
    assertNull(product);
  }

  public void testFindProductMultiSEOByExternalTechId() throws Exception {
    Product product = testling.findProductByExternalId(PRODUCT1_WITH_MULTI_SEO);
    assertNotNull(product.getSeoSegment());
    assertTrue(product.getSeoSegment().contains(testConfig.getStoreName().toLowerCase()));
    product = testling.findProductByExternalId(PRODUCT2_WITH_MULTI_SEO);
    assertNotNull(product.getSeoSegment());
    assertTrue(product.getSeoSegment().contains(testConfig.getStoreName().toLowerCase()));
    product = testling.findProductByExternalId(PRODUCT3_WITH_MULTI_SEO);
    assertNotNull(product.getSeoSegment());
    assertFalse(product.getSeoSegment().contains(testConfig.getStoreName().toLowerCase()));;
  }

  public void testFindProductVariantByExternalTechId() throws Exception {
    ProductVariant productVariant = testling.findProductVariantById(CommerceIdHelper.formatProductVariantId(PRODUCT_VARIANT_CODE));
    assertEquals(PRODUCT_VARIANT_CODE, productVariant.getExternalId());
    String techId = productVariant.getExternalTechId();
    ProductVariant productVariant2 = testling.findProductVariantById(CommerceIdHelper.formatProductVariantTechId(techId));
    assertEquals(productVariant, productVariant2);
    assertProductVariant(productVariant2);
  }

  public void testFindTopCategoriesWithContractSupport() throws Exception {
    if ((!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) || StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_8)) {
      return;
    }

    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    List<Category> topCategories = testling.findTopCategories(null);
    int topCategoriesCount = topCategories.size();

    prepareContextsForContractBasedPreview();
    List<Category> topCategoriesContract = testling.findTopCategories(null);
    int topCategoriesContractCount = topCategoriesContract.size();

    assertTrue("Contract filter for b2b topcategories not working", topCategoriesCount > topCategoriesContractCount);
  }

  public void testFindSubCategoriesWithContract() throws Exception {
    if ((!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) || StoreContextHelper.getWcsVersion(testConfig.getStoreContext()).lessThan(WCS_VERSION_7_8)) {
      return;
    }

    DefaultConnection.get().setStoreContext(testConfig.getB2BStoreContext());
    Category category = findAndAssertCategory("Lighting", null);
    assertNotNull(category);

    category = findAndAssertCategory("Fasteners", null);
    assertNotNull(category);
    List<Category> subCategoriesNoContract = testling.findSubCategories(category);
    assertTrue(subCategoriesNoContract.size() >= 3);

    //test b2b categories with contract
    String[] contractIds = getContractIdsForUser( "bmiller", "passw0rd");

    DefaultConnection.get().getStoreContext().setContractIds(contractIds);
    List<Category> subCategoriesWithContract = testling.findSubCategories(category);
    assertEquals(2, subCategoriesWithContract.size());
    assertTrue(CommerceIdHelper.isCategoryId(subCategoriesWithContract.get(0).getId()));
    assertEquals("Bolts", subCategoriesWithContract.get(0).getName());
    assertEquals("Screws", subCategoriesWithContract.get(1).getName());
  }

  public void testFindCategoryByExternalTechId() throws Exception {
    Category category1 = testling.findCategoryById(CommerceIdHelper.formatCategoryId(CATEGORY_CODE));
    Category category2 = testling.findCategoryById(CommerceIdHelper.formatCategoryTechId(category1.getExternalTechId()));
    assertCategory(category2);
  }

  public void testFindCategoryMultiSEOByExternalTechId() throws Exception {
    Category category = testling.findCategoryById(CommerceIdHelper.formatCategoryId(CATEGORY1_WITH_MULTI_SEO));
    assertNotNull(category.getSeoSegment());
    assertTrue(category.getSeoSegment().contains(testConfig.getStoreName().toLowerCase()));
    category = testling.findCategoryById(CommerceIdHelper.formatCategoryId(CATEGORY2_WITH_MULTI_SEO));
    assertNotNull(category.getSeoSegment());
    assertTrue(category.getSeoSegment().contains(testConfig.getStoreName().toLowerCase()));
    category = testling.findCategoryById(CommerceIdHelper.formatCategoryId(CATEGORY3_WITH_MULTI_SEO));
    assertNotNull(category.getSeoSegment());
    assertFalse(category.getSeoSegment().contains(testConfig.getStoreName().toLowerCase()));
  }

  public void testFindCategoryByExternalTechIdIsNull() throws Exception {
    Category category = testling.findCategoryById(CommerceIdHelper.formatCategoryTechId("blablablub"));
    assertNull(category);
  }

  public void testFindProductByExternalIdReturns502() throws Exception {
    String endpoint = testling.getCatalogWrapperService().getCatalogConnector().getServiceEndpoint(StoreContextHelper.getCurrentContext());
    testling.getCatalogWrapperService().getCatalogConnector().setServiceEndpoint("http://unknownhost.unknowndomain/wcs/resources");
    Throwable exception = null;
    Product product = null;
    try {
      product = testling.findProductByExternalId("UNCACHED_PRODUCT");
    } catch (Throwable e) {
      exception = e;
    } finally {
      testling.getCatalogWrapperService().getCatalogConnector().setServiceEndpoint(endpoint);
    }
    assertNull(product);
    assertTrue("CommerceException expected", exception instanceof CommerceException);
  }

  private void prepareContextsForContractBasedPreview() {
    StoreContext b2BStoreContext = testConfig.getB2BStoreContext();
    StoreContextHelper.setCurrentContext(b2BStoreContext);
    UserContext userContext = userContextProvider.createContext(testConfig.getPreviewUserName());
    UserContextHelper.setCurrentContext(userContext);
    Collection<Contract> contracts = contractService.findContractIdsForUser(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
    assertNotNull(contracts);
    Iterator<Contract> iterator  = contracts.iterator();
    Contract contract = null;
    while (iterator.hasNext()) {
      contract = iterator.next();
      String contractName = contract.getName();
      if (contractName != null && contractName.contains("Applicances Expert")) {
        break;
      }
    }
    assertNotNull(contract);
    b2BStoreContext.setContractIdsForPreview(new String[]{contract.getExternalTechId()});
  }

  private String[] getContractIdsForUser(String user, String password){
    HttpServletRequest request = new MockHttpServletRequest();
    HttpServletResponse response = new MockHttpServletResponse();

    UserContext userContext = UserContextHelper.createContext(user, user);
    UserContextHelper.setCurrentContext(userContext);
    boolean loginSuccess = userSessionService.loginUser(request, response, user, password);
    assertTrue(loginSuccess);
    assertNotNull(userContext.getCookieHeader());

    Collection<Contract> contractIdsForUser = contractService.findContractIdsForUser(userContext,
            DefaultConnection.get().getStoreContext());
    String[] contractIdsArr = Iterables.toArray(Iterables.transform(contractIdsForUser, new Function<Contract, String>() {
      @Nullable
      @Override
      public String apply(@Nullable Contract contract) {
        assert contract != null;
        return contract.getExternalTechId();
      }
    }), String.class);
    return contractIdsArr;
  }

  @Override
  protected void assertProduct(Product product) throws CommerceException {
    super.assertProduct(product);
    //test attributes
    List<ProductAttribute> definingAttributes = product.getDefiningAttributes();
    assertNotNull(definingAttributes);
    assertThat(definingAttributes.isEmpty(), is(false));

    List<ProductAttribute> describingAttributes = product.getDescribingAttributes();
    assertNotNull(describingAttributes);
    assertThat(describingAttributes.isEmpty(), is(false));

    //test variants
    List<ProductVariant> variants = product.getVariants();
    assertNotNull(variants);
    assertThat(variants.isEmpty(), is(false));

    //test axis filter
    List<String> variantAxisNames = product.getVariantAxisNames();
    if (!variantAxisNames.isEmpty()){
      List<ProductVariant> filteredVariants = product.getVariants(new AxisFilter(variantAxisNames.get(0), "*"));
      assertTrue(variants.size() >= filteredVariants.size());
    }
  }

  @Override
  protected void assertProductVariant(ProductVariant productVariant) throws CommerceException {
    super.assertProductVariant(productVariant);
    List<ProductAttribute> describingAttributes = productVariant.getDescribingAttributes();
    assertThat(describingAttributes.isEmpty(), is(false));
    List<ProductAttribute> definingAttributes = productVariant.getDefiningAttributes();
    assertThat(definingAttributes.isEmpty(), is(false));
  }
}
