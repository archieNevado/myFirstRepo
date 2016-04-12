package com.coremedia.livecontext.ecommerce.ibm.inventory;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link com.coremedia.livecontext.ecommerce.ibm.inventory.AvailabilityServiceImpl}
 */
@ContextConfiguration(classes = AbstractServiceTest.LocalConfig.class)
@ActiveProfiles(AbstractServiceTest.LocalConfig.PROFILE)
public class AvailabilityServiceImplIT extends AbstractServiceTest {

  private static final String PRODUCT2_CODE = "AuroraWMDRS-1";

  @Inject
  AvailabilityServiceImpl testling;
  @Inject
  CatalogService catalogService;

  @Before
  public void setup() {
    super.setup();
    testling.getAvailabilityWrapperService().clearLanguageMapping();
  }

  @Betamax(tape = "csi_testGetAvailabilityInfo", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testGetAvailabilityInfo() {
    List<ProductVariant> variants = catalogService.findProductById(
            Commerce.getCurrentConnection().getIdProvider().formatProductId(PRODUCT2_CODE)).getVariants();
    ProductVariant productVariant = variants.get(0);
    ProductVariant productVariant2 = (ProductVariant) testling.getCommerceBeanFactory().createBeanFor(CommerceIdHelper.formatProductVariantTechId(productVariant.getExternalTechId()), testConfig.getStoreContext());
    assertEquals(productVariant, productVariant2);

    //test multiple variants
    Map<ProductVariant, AvailabilityInfo> inventoryAvailabilityMap = testling.getAvailabilityInfo(variants);
    assertTrue(inventoryAvailabilityMap.size() > 1);
    AvailabilityInfo availabilityInfo = inventoryAvailabilityMap.get(productVariant2);
    assertNotNull(availabilityInfo);

    //test single AvailabilityInfo
    assertEquals(AvailabilityInfo.STATUS_AVAILABLE, availabilityInfo.getInventoryStatus());
    assertTrue(availabilityInfo.getQuantity() >= 1.0f);

    //test identity of Availability
    assertEquals(availabilityInfo, testling.getAvailabilityInfo(productVariant2));
    assertEquals(availabilityInfo, productVariant2.getAvailabilityInfo());
  }

}
