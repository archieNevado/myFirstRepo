package com.coremedia.livecontext.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.ecommerce.test.TestVendors;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.ContextCollectionImpl;
import com.coremedia.personalization.context.MapPropertyMaintainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceIdOrThrow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceSegmentSourceTest {

  private static final String USER1_NAME = "testUser";
  private static final String USER1_ID = "4711";

  private CommerceSegmentSource testling;

  @Mock
  private CommerceConnection commerceConnection;

  private ContextCollection contextCollection;

  @Mock
  private SegmentService segmentService;

  @Before
  public void setup() {
    testling = new CommerceSegmentSource();
    testling.setContextName("commerce");

    when(commerceConnection.getIdProvider()).thenReturn(TestVendors.getIdProvider("vendor"));
    when(commerceConnection.getSegmentService()).thenReturn(Optional.of(segmentService));

    contextCollection = new ContextCollectionImpl();

  }

  @Test
  public void testPreHandle() {
    Segment seg1 = mockSegment("vendor:///catalog/segment/id1");
    Segment seg2 = mockSegment("vendor:///catalog/segment/id2");

    List<Segment> segmentList = List.of(seg1, seg2);
    when(segmentService.findSegmentsForCurrentUser(any(StoreContext.class), any(UserContext.class))).thenReturn(segmentList);

    var storeContext = StoreContextBuilderImpl.from(commerceConnection, "any-site-id").build();
    var userContext = UserContext.builder()
            .withUserId(USER1_ID)
            .withUserName(USER1_NAME)
            .build();
    var request = new MockHttpServletRequest();
    request.setAttribute(StoreContext.class.getName(), storeContext);
    request.setAttribute(UserContext.class.getName(), userContext);

    testling.preHandle(request, new MockHttpServletResponse(), contextCollection);

    verifyThatProfileContainsSegments();
  }

  @Test
  public void testPreHandleFromUserContext() {
    var storeContext = StoreContextBuilderImpl.from(commerceConnection, "any-site-id")
            .withUserSegments("id1,id2")
            .build();

    var dummyUserContext = UserContext.builder().build();

    var request = new MockHttpServletRequest();
    request.setAttribute(StoreContext.class.getName(), storeContext);
    request.setAttribute(UserContext.class.getName(), dummyUserContext);

    testling.preHandle(request, new MockHttpServletResponse(), contextCollection);

    verifyThatProfileContainsSegments();
    verify(segmentService, times(0)).findSegmentsForCurrentUser(storeContext, dummyUserContext);
  }

  // --------------- Helper ----------------------

  private Segment mockSegment(String id) {
    Segment seg = mock(Segment.class);
    when(seg.getId()).thenReturn(parseCommerceIdOrThrow(id));
    return seg;
  }

  private void verifyThatProfileContainsSegments() {
    MapPropertyMaintainer profile = (MapPropertyMaintainer) contextCollection.getContext("commerce");
    assertThat(profile).isNotNull();
    assertThat(profile.getProperty("usersegments"))
            .isEqualTo("vendor:///catalog/segment/id1,vendor:///catalog/segment/id2,");
  }
}
