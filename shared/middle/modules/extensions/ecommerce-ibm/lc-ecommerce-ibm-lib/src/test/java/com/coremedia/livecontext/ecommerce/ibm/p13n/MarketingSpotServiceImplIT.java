package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentUserContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.lc.test.SwitchableHoverflyExtension;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmStoreContextBuilder;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmTestConfig;
import com.coremedia.livecontext.ecommerce.p13n.MarketingImage;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingText;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.specto.hoverfly.junit5.api.HoverflyConfig;
import io.specto.hoverfly.junit5.api.HoverflySimulate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static com.coremedia.blueprint.lc.test.HoverflyTestHelper.useTapes;
import static com.coremedia.livecontext.ecommerce.ibm.catalog.IbmCatalogServiceBaseTest.IBM_TEST_URL;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_8_0;
import static com.coremedia.livecontext.ecommerce.ibm.p13n.MarketingSpotServiceImpl.toMarketingSpotId;
import static com.coremedia.livecontext.ecommerce.ibm.p13n.MarketingSpotServiceImpl.toMarketingSpotTechId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Test for {@link com.coremedia.livecontext.ecommerce.ibm.p13n.MarketingSpotServiceImpl}
 */
@ExtendWith({SwitchableHoverflyExtension.class})
@HoverflySimulate(
        source = @HoverflySimulate.Source(
                "wcs8_MarketingSpotServiceImplIT.json"
        ),
        // Re-Record as soon as source file is not available.
        enableAutoCapture = true,
        config = @HoverflyConfig(
                // map the "shop-ref.ecommerce.coremedia.com" to an existing ip of a wcs system in your /etc/hosts file
                destination = IBM_TEST_URL,
                disableTlsVerification = true
        )
)
@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
class MarketingSpotServiceImplIT extends IbmServiceTestBase {

  private static final String MARKETING_SPOT_EXTERNAL_ID1 = "ApparelRow1_Content";
  private static final String MARKETING_SPOT_EXTERNAL_ID2 = "BoysRow4_CatEntries";
  private static final String MARKETING_SPOT_EXTERNAL_ID3 = "PC_Homepage_Offer";
  private static final String MARKETING_SPOT_WS_EXTERNAL_ID = "PC_Anniversary_Offer_Products";

  private static final WorkspaceId WORKSPACE_ID = WorkspaceId.of("10001");

  @Inject
  private MarketingSpotServiceImpl testling;

  @Inject
  private IbmTestConfig testConfig;

  @BeforeEach
  @Override
  public void setup() {
    super.setup();

    UserContext userContext = UserContext.builder().build();
    CurrentUserContext.set(userContext);
  }

  @Test
  void testFindMarketingSpots() {
    List<MarketingSpot> marketingSpots = testling.findMarketingSpots(storeContext);
    assertThat(marketingSpots.size()).isGreaterThan(100);

    MarketingSpot spot = marketingSpots.get(0);
    assertThat(spot.getName()).isNotNull();
    assertThat(spot.getId()).isNotNull();
    assertThat(spot.getExternalId()).isNotNull();
  }

  @Test
  void testFindAllMarketingSpotsInWorkspace() {
    if (useTapes()) {
      return;
    }

    List<MarketingSpot> marketingSpotsWithoutWorkspace = testling.findMarketingSpots(storeContext);
    assertThat(marketingSpotsWithoutWorkspace.size()).isGreaterThan(100);

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
    assertThat(marketingSpotsWithWorkspace).hasSize(marketingSpotsWithoutWorkspace.size() + 2);
  }

  @Test
  void testFindMarketingSpotInWSByExternalId() {
    if (useTapes() || WCS_VERSION_8_0.lessThan(testConfig.getWcsVersion())) {
      return;
    }

    StoreContextImpl storeContextWithWorkspaceId = setWorkspaceId((StoreContextImpl) storeContext, WORKSPACE_ID);

    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_WS_EXTERNAL_ID, storeContextWithWorkspaceId);
    assertThat(spot).isNotNull();
    assertThat(spot.getId()).isNotNull();
    assertThat(spot.getName()).isEqualTo(MARKETING_SPOT_WS_EXTERNAL_ID);

    List<CommerceObject> entities = spot.getEntities();
    assertThat(entities).as("entities should have size of 3").hasSize(3);

    CommerceObject item = entities.get(0);
    assertThat(item).as("Product expected").isInstanceOf(Product.class);
  }

  @Test
  void testFindMarketingSpotByExternalId1() {
    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID1, storeContext);
    assertThat(spot).isNotNull();

    List<CommerceObject> entities = spot.getEntities();
    assertThat(entities).as("entities should have size of 1").hasSize(1);

    CommerceObject item = entities.get(0);
    assertThat(item instanceof MarketingImage || item instanceof MarketingText).as("MarketingContent expected").isTrue();
  }

  @Test
  void testFindMarketingSpotByExternalId2() {
    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID2, storeContext);
    assertThat(spot).isNotNull();

    List<CommerceObject> entities = spot.getEntities();
    assertThat(entities).as("entities should have size > 0").isNotEmpty();

    int count = 0;
    for (CommerceObject item : entities) {
      count++;
      assertThat(item).as("Product expected").isInstanceOf(Product.class);
      Product product = (Product) item;

      if (product.getName().equals("Gusso Green Khaki Shirt")) {
        assertThat(true).as("Found the expected product").isTrue();
        break;
      }

      if (count == entities.size()) {
        fail("Can not find the expected product");
        break;
      }
    }
  }

  @Test
  void testFindMarketingSpotByExternalId3() {
    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID3, storeContext);
    assertThat(spot).isNotNull();

    List entities = spot.getEntities();
    assertThat(entities).as("entities should have size > 0").isNotEmpty();
    Object item = entities.get(0);
    assertThat(item).as("Product expected").isInstanceOf(MarketingText.class);

    MarketingText first = (MarketingText) item;
    String firstText = first.getText();
    assertThat(firstText)
            .as("the other text should be there")
            .isNotEmpty()
            .doesNotContain("Men's");
  }

  @Test
  void testFindMarketingSpotPersonalizedOld() {
    UserContext userContext = UserContext.builder().withUserName(testConfig.getUser2Name()).build();
    CurrentUserContext.set(userContext);

    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID3, storeContext);
    assertThat(spot).isNotNull();

    List entities = spot.getEntities();
    assertThat(entities).as("entities should have size > 0").isNotEmpty();
    assertThat(entities.get(0)).as("MarketingText expected").isInstanceOf(MarketingText.class);

    MarketingText first = (MarketingText) entities.get(0);
    assertThat(first.getText()).as("the men's text should be there").contains("Men's");
  }

  @Test
  void testFindMarketingSpotPersonalizedTestContext() {
    if (useTapes()) {
      return;
    }

    String userSegments = testConfig.getUserSegment1Id().concat("," + testConfig.getUserSegment2Id());

    StoreContext storeContextWithUserSegments = IbmStoreContextBuilder
            .from((StoreContextImpl) storeContext)
            .withUserSegments(userSegments)
            .build();

    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID3, storeContextWithUserSegments);
    assertThat(spot).isNotNull();

    List entities = spot.getEntities();
    assertThat(entities).as("entities should have size > 0").isNotEmpty();
    assertThat(entities.get(0)).as("MarketingText expected").isInstanceOf(MarketingText.class);

    MarketingText first = (MarketingText) entities.get(0);
    assertThat(first.getText()).as("the men's text should be there").contains("Men's");
  }

  @Test
  void testFindMarketingSpotByExternalTechId() {
    MarketingSpot spot = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID1, storeContext);
    assertThat(spot).isNotNull();

    spot = findMarketingSpotByExternalTechId(spot.getExternalTechId(), storeContext);
    assertThat(spot).isNotNull();
    assertThat(spot.getDescription()).isNotNull();
    assertThat(spot.getExternalId()).isNotNull();
    assertThat(spot.getExternalTechId()).isNotNull();
    assertThat(spot.getName()).isNotNull();
  }

  @Test
  void testFindMarketingSpotByExternalIdForStudio() {
    testling.getMarketingSpotWrapperService().setUseServiceCallsForStudio(true);
    try {
      MarketingSpot test = findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID1, storeContext);
      assertThat(test).isNotNull();
      assertThat(test.getDescription()).isNotNull();
      assertThat(test.getExternalId()).isNotNull();
      assertThat(test.getExternalTechId()).isNotNull();
      assertThat(test.getName()).isNotNull();
    } finally {
      testling.getMarketingSpotWrapperService().setUseServiceCallsForStudio(false);
    }
  }

  @Test
  void testSearchMarketingSpots() {
    SearchResult<MarketingSpot> marketingSpots = testling.searchMarketingSpots("Shirts", null, storeContext);
    assertThat(marketingSpots.getSearchResult()).isNotEmpty();
  }

  @NonNull
  private static StoreContextImpl setWorkspaceId(@NonNull StoreContextImpl storeContext,
                                                 @Nullable WorkspaceId workspaceId) {
    return IbmStoreContextBuilder
            .from(storeContext)
            .withWorkspaceId(workspaceId)
            .build();
  }

  @Nullable
  private MarketingSpot findMarketingSpotByExternalTechId(@NonNull String externalTechId, StoreContext storeContext) {
    CommerceId marketingSpotTechId = toMarketingSpotTechId(externalTechId);
    return testling.findMarketingSpotById(marketingSpotTechId, storeContext);
  }

  @Nullable
  private MarketingSpot findMarketingSpotByExternalId(@NonNull String externalId, StoreContext storeContext) {
    CommerceId marketingSpotId = toMarketingSpotId(externalId);
    return testling.findMarketingSpotById(marketingSpotId, storeContext);
  }
}
