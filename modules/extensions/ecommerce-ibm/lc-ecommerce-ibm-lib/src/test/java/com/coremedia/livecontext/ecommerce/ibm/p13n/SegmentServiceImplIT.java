package com.coremedia.livecontext.ecommerce.ibm.p13n;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.IbmServiceTestBase;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmStoreContextBuilder;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.user.UserContext;
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

/**
 * Integration test(s) for the {@link com.coremedia.livecontext.ecommerce.p13n.SegmentService}.
 * <p>
 * Unfortunately the WCS uses technical IDs to create segments which differ between WCS7 and WCS8. That why You should
 * avoid using IDs if possible. Currently we're using following IDs for WCS versions 7 and 8:
 * <table>
 * <tr>
 * <th>Segment Name</th>
 * <th>ID WCS7</th>
 * <th>ID WCS8</th>
 * </tr>
 * <tr>
 * <td>Customers who are 40 years of age or older</td>
 * <td>8000000000000000557</td>
 * <td>8407790678950000508</td>
 * </tr>
 * <tr>
 * <td>Customers who are under 40 years of age</td>
 * <td>8000000000000000556</td>
 * <td>8407790678950000507</td>
 * </tr>
 * <tr>
 * <td>Female Customers</td>
 * <td>8000000000000000555</td>
 * <td>8407790678950000506</td>
 * </tr>
 * <tr>
 * <td>Frequent Buyer</td>
 * <td>8000000000000000751</td>
 * <td>8407790678950000652</td>
 * </tr>
 * <tr>
 * <td>Guest Shoppers</td>
 * <td>8000000000000000553</td>
 * <td>8407790678950000504</td>
 * </tr>
 * <tr>
 * <td>Male Customers</td>
 * <td>8000000000000000554</td>
 * <td>8407790678950000505</td>
 * </tr>
 * <tr>
 * <td>Registered Customers</td>
 * <td>8000000000000000551</td>
 * <td>8407790678950000502</td>
 * </tr>
 * <tr>
 * <td>Repeat Customers</td>
 * <td>8000000000000000552</td>
 * <td>8407790678950000503</td>
 * </tr>
 * </table>
 */
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

    boolean olderThan_7_7 = StoreContextHelper.getWcsVersion(storeContext).lessThan(WCS_VERSION_7_7);
    Assumptions.assumeFalse(olderThan_7_7);

    UserContextHelper.setCurrentContext(UserContext.builder().build());
  }

  @Betamax(tape = "ssi_testFindAllSegments", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindAllSegments() {
    List<Segment> segments = testling.findAllSegments(storeContext);
    assertThat(segments)
            .isNotEmpty()
            .last()
            .satisfies(s -> {
              assertThat(s).hasFieldOrPropertyWithValue("name", "Repeat Customers");
              assertThat(s).extracting(repeatCustomer -> repeatCustomer.getId().getCommerceBeanType())
                      .isEqualTo(SEGMENT);
            });
  }

  @Betamax(tape = "ssi_testFindSegmentById", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindSegmentById() {
    Segment registeredCustomers = testling.findAllSegments(storeContext)
            .stream()
            .filter(s -> REGISTERED_CUSTOMERS.equals(s.getName()))
            .findFirst()
            .orElse(null);

    assertThat(registeredCustomers).isNotNull();
    String externalTechId = registeredCustomers.getExternalTechId();
    assertThat(externalTechId).isNotBlank();

    CommerceId registeredCustomersId = CommerceIdParserHelper.parseCommerceIdOrThrow("ibm:///x/segment/" + externalTechId);
    Segment segment2 = testling.findSegmentById(registeredCustomersId, storeContext);
    assertThat(segment2).isNotNull();

    CommerceId commerceId = segment2.getId();
    assertThat(commerceId.getCommerceBeanType()).isEqualTo(SEGMENT);
    assertThat(segment2.getName()).isEqualTo(REGISTERED_CUSTOMERS);
    assertThat(segment2).isEqualTo(registeredCustomers);
  }

  @Betamax(tape = "ssi_testFindSegmentsByUser", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindSegmentsByUser() {
    UserContext userContext = UserContext.builder()
            .withUserId(System.getProperty("lc.test.user2.id", "4"))
            .withUserName(testConfig.getUser2Name())
            .build();
    UserContextHelper.setCurrentContext(userContext);

    List<Segment> segments = testling.findSegmentsForCurrentUser(storeContext);
    assertThat(segments).isNotEmpty();
    assertThat(segments.size()).isGreaterThanOrEqualTo(3);
  }

  @Test
  public void testFindSegmentsByUserInWS() {
    if (useBetamaxTapes()) {
      return;
    }

    UserContext userContext = UserContext.builder()
            .withUserId(System.getProperty("lc.test.user2.id", "4"))
            .withUserName(testConfig.getUser2Name())
            .build();
    UserContextHelper.setCurrentContext(userContext);

    WorkspaceId workspaceId = findAnniversaryWorkspaceId(storeContext);
    StoreContext storeContextWithWorkspaceId = IbmStoreContextBuilder
            .from((StoreContextImpl) storeContext)
            .withWorkspaceId(workspaceId)
            .build();

    List<Segment> segments = testling.findSegmentsForCurrentUser(storeContextWithWorkspaceId);
    assertThat(segments).isNotEmpty()
            .extracting("name")
            .contains("Loyal, early Perfect Chef Customer", "Frequent Buyer", "Male Customers", REGISTERED_CUSTOMERS);
  }

  @NonNull
  private WorkspaceId findAnniversaryWorkspaceId(@NonNull StoreContext storeContext) {
    return workspaceService.findAllWorkspaces(storeContext).stream()
            .filter(workspace -> workspace.getName().startsWith("Anniversary"))
            .map(CommerceBean::getExternalTechId)
            .map(WorkspaceId::of)
            .findFirst()
            .orElseThrow(IllegalStateException::new);
  }
}
