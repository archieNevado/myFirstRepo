package com.coremedia.livecontext.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.ContextCollectionImpl;
import com.coremedia.personalization.context.MapPropertyMaintainer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommerceSegmentSourceTest {

  public static final String USER1_NAME = "testUser";
  public static final String USER1_ID = "4711";

  CommerceSegmentSource testling;

  CommerceConnection commerceConnection;

  @Before
  public void setup() {
    initMocks(this);
    testling = new CommerceSegmentSource();
    testling.setContextName("commerce");

    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
    commerceConnection.getUserContext().setUserId(USER1_ID);
    commerceConnection.getUserContext().setUserName(USER1_NAME);
    Commerce.setCurrentConnection(commerceConnection);
  }

  @Test
  public void testPreHandle() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    ContextCollection contextCollection = new ContextCollectionImpl();

    Segment seg1 = mock(Segment.class);
    when(seg1.getName()).thenReturn("name1");
    when(seg1.getExternalId()).thenReturn("extId1");
    when(seg1.getExternalTechId()).thenReturn("techId1");
    when(seg1.getId()).thenReturn("id1");

    Segment seg2 = mock(Segment.class);
    when(seg2.getName()).thenReturn("name2");
    when(seg2.getExternalId()).thenReturn("extId2");
    when(seg2.getExternalTechId()).thenReturn("techId2");
    when(seg2.getId()).thenReturn("id2");

    List<Segment> segmentList = new ArrayList<>();
    segmentList.add(seg1);
    segmentList.add(seg2);
    when(commerceConnection.getSegmentService().findSegmentsForCurrentUser()).thenReturn(segmentList);

    testling.preHandle(request, response, contextCollection);
    MapPropertyMaintainer profile = (MapPropertyMaintainer) contextCollection.getContext("commerce");
    assertNotNull(profile);
    assertEquals("vendor:///catalog/segment/id1,vendor:///catalog/segment/id2,", profile.getProperty("usersegments"));
  }

  @Test
  public void testPreHandleFromUserContext() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    ContextCollection contextCollection = new ContextCollectionImpl();

    commerceConnection.getStoreContext().setUserSegments("id1,id2");
    testling.preHandle(request, response, contextCollection);
    MapPropertyMaintainer profile = (MapPropertyMaintainer) contextCollection.getContext("commerce");
    assertNotNull(profile);
    assertEquals("vendor:///catalog/segment/id1,vendor:///catalog/segment/id2,", profile.getProperty("usersegments"));
    verify(commerceConnection.getSegmentService(), times(0)).findSegmentsForCurrentUser();
  }

}
