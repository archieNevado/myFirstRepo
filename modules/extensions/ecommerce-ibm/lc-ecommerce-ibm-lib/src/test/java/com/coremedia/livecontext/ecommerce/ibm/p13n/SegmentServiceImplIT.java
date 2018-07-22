package com.coremedia.livecontext.ecommerce.ibm.p13n;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SEGMENT;
import static com.coremedia.livecontext.ecommerce.ibm.common.WcsVersion.WCS_VERSION_7_7;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = IbmServiceTestBase.LocalConfig.class)
@ActiveProfiles(IbmServiceTestBase.LocalConfig.PROFILE)
public class SegmentServiceImplIT extends IbmServiceTestBase {

  private static final String REGISTERED_CUSTOMERS = "Registered Customers";

  @Inject
  private SegmentServiceImpl testling;

  @Inject
  private WorkspaceService workspaceService;

  @Before
  @Override
  public void setup() {
    super.setup();

    boolean olderThan_7_7 = StoreContextHelper.getWcsVersion(getStoreContext()).lessThan(WCS_VERSION_7_7);
    Assumptions.assumeFalse(olderThan_7_7);

    UserContextHelper.setCurrentContext(UserContext.builder().build());
  }

  @Betamax(tape = "ssi_testFindAllSegments", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindAllSegments() throws Exception {
    List<Segment> segments = testling.findAllSegments(getStoreContext());
    assertThat(segments)
            .isNotEmpty()
            .last()
            .satisfies(s -> {
              assertThat(s).hasFieldOrPropertyWithValue("name", "Repeat Customers");
              assertThat(s).extracting(repeatCustomer -> repeatCustomer.getId().getCommerceBeanType())
                      .containsExactly(SEGMENT);
            });
  }

  @Betamax(tape = "ssi_testFindSegmentById", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindSegmentById() throws Exception {
    Segment registeredCustomers = testling.findAllSegments(getStoreContext())
            .stream()
            .filter(s -> REGISTERED_CUSTOMERS.equals(s.getName()))
            .findFirst()
            .orElse(null);

    assertThat(registeredCustomers).isNotNull();
    String externalTechId = registeredCustomers.getExternalTechId();
    assertThat(externalTechId).isNotBlank();

    CommerceId registeredCustomersId = CommerceIdParserHelper.parseCommerceIdOrThrow("ibm:///x/segment/" + externalTechId);
    Segment segment2 = testling.findSegmentById(registeredCustomersId, getStoreContext());
    assertThat(segment2).isNotNull();

    CommerceId commerceId = segment2.getId();
    assertThat(commerceId.getCommerceBeanType()).isEqualTo(SEGMENT);
    assertThat(segment2.getName()).isEqualTo(REGISTERED_CUSTOMERS);
    assertThat(segment2).isEqualTo(registeredCustomers);
  }

  @Betamax(tape = "ssi_testFindSegmentsByUser", match = {MatchRule.path, MatchRule.query})
  @Test
  @org.junit.Ignore("TW-356")
  public void testFindSegmentsByUser() throws Exception {
    UserContext userContext = UserContext.builder()
            .withUserId(System.getProperty("lc.test.user2.id", "4"))
            .withUserName(testConfig.getUser2Name())
            .build();
    UserContextHelper.setCurrentContext(userContext);

    List<Segment> segments = testling.findSegmentsForCurrentUser(getStoreContext());
    assertThat(segments).isNotEmpty()
            .extracting("name")
            .containsExactly(REGISTERED_CUSTOMERS, "Male Customers", "Frequent Buyer");
  }

  @Test
  public void testFindSegmentsByUserInWS() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    UserContext userContext = UserContext.builder()
            .withUserId(System.getProperty("lc.test.user2.id", "4"))
            .withUserName(testConfig.getUser2Name())
            .build();
    UserContextHelper.setCurrentContext(userContext);

    StoreContext storeContext = getStoreContext();
    Workspace workspace = findAnniversaryWorkspace(storeContext);
    storeContext.setWorkspaceId(WorkspaceId.of(workspace.getExternalTechId()));

    List<Segment> segments = testling.findSegmentsForCurrentUser(storeContext);
    assertThat(segments).isNotEmpty()
            .extracting("name")
            .containsExactly("Loyal, early Perfect Chef Customer", "Frequent Buyer", "Male Customers", REGISTERED_CUSTOMERS);
  }

  @NonNull
  private Workspace findAnniversaryWorkspace(@NonNull StoreContext storeContext) {
    return workspaceService.findAllWorkspaces(storeContext).stream()
            .filter(w -> w.getName().startsWith("Anniversary"))
            .findFirst().orElseThrow(IllegalStateException::new);
  }
}
