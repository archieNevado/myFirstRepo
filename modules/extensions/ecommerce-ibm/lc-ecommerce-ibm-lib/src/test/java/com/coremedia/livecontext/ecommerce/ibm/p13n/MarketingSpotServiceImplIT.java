package com.coremedia.livecontext.ecommerce.ibm.p13n;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmStoreContextBuilder;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmTestConfig;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.MarketingImage;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingText;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.coremedia.livecontext.ecommerce.ibm.p13n.MarketingSpotServiceImpl.toMarketingSpotId;
import static com.coremedia.livecontext.ecommerce.ibm.p13n.MarketingSpotServiceImpl.toMarketingSpotTechId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

/**
 * Test for {@link com.coremedia.livecontext.ecommerce.ibm.p13n.MarketingSpotServiceImpl}
 */
@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class MarketingSpotServiceImplIT extends IbmServiceTestBase {

  private static final String MARKETING_SPOT_EXTERNAL_ID1 = "ApparelRow1_Content";
  private static final String MARKETING_SPOT_EXTERNAL_ID2 = "BoysRow4_CatEntries";
  private static final String MARKETING_SPOT_EXTERNAL_ID3 = "PC_Homepage_Offer";
  private static final String MARKETING_SPOT_WS_EXTERNAL_ID = "PC_Anniversary_Offer_Products";

  private static final WorkspaceId WORKSPACE_ID = WorkspaceId.of("10001");

  @Inject
  MarketingSpotServiceImpl testling;

  @Inject
  IbmTestConfig testConfig;

  @Betamax(tape = "csi_testFindMarketingSpots", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindMarketingSpots() {
    UserContext userContext = UserContext.builder().build();
    UserContextHelper.setCurrentContext(userContext);

    List<MarketingSpot> marketingSpots = testling.findMarketingSpots(storeContext);
    assertTrue(marketingSpots.size() > 100);

    MarketingSpot spot = marketingSpots.get(0);
    assertNotNull(spot.getName());
    assertNotNull(spot.getId());
    assertNotNull(spot.getExternalId());
  }

  @Test
  public void testFindAllMarketingSpotsInWorkspace() {
    if (useBetamaxTapes()) {
      return;
    }

    List<MarketingSpot> marketingSpotsWithoutWorkspace = testling.findMarketingSpots(storeContext);
    assertTrue(marketingSpotsWithoutWorkspace.size() > 100);

    StoreContext storeContextWithWorkspaceId = setWorkspaceId((StoreContextImpl) storeContext, WORKSPACE_ID);

    List<MarketingSpot> marketingSpotsWithWorkspace = testling.findMarketingSpots(storeContextWithWorkspaceId);

    // Flaky test:
    // Sometimes the amount of marketing spots is the same for requests with and without a workspace id.
    // It looks like the issue is on the wcs side. The amount of returned marketing spots seems to be the same,
    // if there is only short time between the marketing spot requests with and without a workspace id.
    //
    // We only compare the results exactly when different amounts of marketing spots are returned.
    // If the amount is the same, the test is skipped.
    assumeTrue(marketingSpotsWithWorkspace.size() != marketingSpotsWithoutWorkspace.size());
    assertEquals(marketingSpotsWithWorkspace.size(), marketingSpotsWithoutWorkspace.size() + 2);
  }

  @Test
  public void testFindMarketingSpotInWSByExternalId() {
    if (useBetamaxTapes()) {
      return;
    }

    StoreContextImpl storeContextWithWorkspaceId = setWorkspaceId((StoreContextImpl) storeContext, WORKSPACE_ID);

    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_WS_EXTERNAL_ID, storeContextWithWorkspaceId);
    assertNotNull(spot);
    assertNotNull(spot.getId());
    assertEquals(MARKETING_SPOT_WS_EXTERNAL_ID, spot.getName());

    List<CommerceObject> entities = spot.getEntities();
    assertNotNull(entities);
    assertEquals("entities should have size of 3", entities.size(), 3);

    CommerceObject item = entities.get(0);
    assertTrue("Product expected", item instanceof Product);
  }

  @Betamax(tape = "csi_testFindMarketingSpotByExternalId1", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindMarketingSpotByExternalId1() {
    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID1, storeContext);
    assertNotNull(spot);

    List<CommerceObject> entities = spot.getEntities();
    assertNotNull(entities);
    assertEquals("entities should have size of 1", entities.size(), 1);

    CommerceObject item = entities.get(0);
    assertTrue("MarketingContent expected", item instanceof MarketingImage || item instanceof MarketingText);
  }

  @Betamax(tape = "csi_testFindMarketingSpotByExternalId2", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindMarketingSpotByExternalId2() {
    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID2, storeContext);
    assertNotNull(spot);

    List<CommerceObject> entities = spot.getEntities();
    assertNotNull(entities);
    assertFalse("entities should have size > 0", entities.isEmpty());

    int count = 0;
    for (CommerceObject item : entities) {
      count++;
      assertTrue("Product expected", item instanceof Product);
      Product product = (Product) item;

      if (product.getName().equals("Gusso Green Khaki Shirt")) {
        assertTrue("Found the expected product", true);
        break;
      }

      if (count == entities.size()) {
        fail("Can not find the expected product");
        break;
      }
    }
  }

  @Betamax(tape = "csi_testFindMarketingSpotByExternalId3", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindMarketingSpotByExternalId3() {
    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID3, storeContext);
    assertNotNull(spot);

    List entities = spot.getEntities();
    assertNotNull(entities);
    assertFalse("entities should have size > 0", entities.isEmpty());
    assertTrue("Product expected", entities.get(0) instanceof MarketingText);

    MarketingText first = (MarketingText) entities.get(0);
    assertTrue("the other text should be there", !first.getText().isEmpty() && !first.getText().contains("Men's"));
  }

  @Betamax(tape = "csi_testFindMarketingSpotPersonalizedOld", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindMarketingSpotPersonalizedOld() {
    UserContext userContext = UserContext.builder().withUserName(testConfig.getUser2Name()).build();
    UserContextHelper.setCurrentContext(userContext);

    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID3, storeContext);
    assertNotNull(spot);

    List entities = spot.getEntities();
    assertNotNull(entities);
    assertFalse("entities should have size > 0", entities.isEmpty());
    assertTrue("MarketingText expected", entities.get(0) instanceof MarketingText);

    MarketingText first = (MarketingText) entities.get(0);
    assertTrue("the men's text should be there", first.getText().contains("Men's"));
  }

  @Test
  public void testFindMarketingSpotPersonalizedTestContext() {
    if (useBetamaxTapes()) {
      return;
    }

    String userSegments = testConfig.getUserSegment1Id().concat("," + testConfig.getUserSegment2Id());

    StoreContext storeContextWithUserSegments = IbmStoreContextBuilder
            .from((StoreContextImpl) storeContext)
            .withUserSegments(userSegments)
            .build();

    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID3, storeContextWithUserSegments);
    assertNotNull(spot);

    List entities = spot.getEntities();
    assertNotNull(entities);
    assertFalse("entities should have size > 0", entities.isEmpty());
    assertTrue("MarketingText expected", entities.get(0) instanceof MarketingText);

    MarketingText first = (MarketingText) entities.get(0);
    assertTrue("the men's text should be there", first.getText().contains("Men's"));
  }

  @Betamax(tape = "csi_testFindMarketingSpotByExternalTechId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindMarketingSpotByExternalTechId() {
    UserContext userContext = UserContext.builder().build();
    UserContextHelper.setCurrentContext(userContext);

    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID1, storeContext);
    assertNotNull(spot);

    spot = findMarketingSpotByExternalTechId(spot.getExternalTechId(), storeContext);
    assertNotNull(spot);
    assertNotNull(spot.getDescription());
    assertNotNull(spot.getExternalId());
    assertNotNull(spot.getExternalTechId());
    assertNotNull(spot.getName());
  }

  @Nullable
  @VisibleForTesting
  MarketingSpot findMarketingSpotByExternalTechId(@NonNull String externalTechId, StoreContext storeContext) {
    CommerceId marketingSpotTechId = toMarketingSpotTechId(externalTechId);
    return testling.findMarketingSpotById(marketingSpotTechId, storeContext);
  }

  @Nullable
  @VisibleForTesting
  MarketingSpot findMarketingSpotByExternalId(@NonNull String externalId, StoreContext storeContext) {
    CommerceId marketingSpotId = toMarketingSpotId(externalId);
    return testling.findMarketingSpotById(marketingSpotId, storeContext);
  }

  @Betamax(tape = "csi_testFindMarketingSpotByExternalIdForStudio", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindMarketingSpotByExternalIdForStudio() {
    UserContext userContext = UserContext.builder().build();
    UserContextHelper.setCurrentContext(userContext);

    testling.getMarketingSpotWrapperService().setUseServiceCallsForStudio(true);
    try {
      MarketingSpot test = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID1, storeContext);
      assertNotNull(test);
      assertNotNull(test.getDescription());
      assertNotNull(test.getExternalId());
      assertNotNull(test.getExternalTechId());
      assertNotNull(test.getName());
    } finally {
      testling.getMarketingSpotWrapperService().setUseServiceCallsForStudio(false);
    }
  }

  @Betamax(tape = "csi_testSearchMarketingSpots", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testSearchMarketingSpots() {
    UserContext userContext = UserContext.builder().build();
    UserContextHelper.setCurrentContext(userContext);

    SearchResult<MarketingSpot> marketingSpots = testling.searchMarketingSpots("Shirts", null, storeContext);
    assertFalse(marketingSpots.getSearchResult().isEmpty());
  }

  @NonNull
  private static StoreContextImpl setWorkspaceId(@NonNull StoreContextImpl storeContext,
                                                 @Nullable WorkspaceId workspaceId) {
    return IbmStoreContextBuilder
            .from(storeContext)
            .withWorkspaceId(workspaceId)
            .build();
  }
}
