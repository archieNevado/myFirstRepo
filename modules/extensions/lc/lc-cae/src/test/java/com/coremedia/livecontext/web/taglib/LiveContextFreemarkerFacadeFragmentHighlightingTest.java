package com.coremedia.livecontext.web.taglib;

import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.livecontext.fragment.FragmentContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.web.taglib.LiveContextFreemarkerFacade.HAS_ITEMS;
import static com.coremedia.livecontext.web.taglib.LiveContextFreemarkerFacade.IS_IN_LAYOUT;
import static com.coremedia.livecontext.web.taglib.LiveContextFreemarkerFacade.PLACEMENT_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LiveContextFreemarkerFacadeFragmentHighlightingTest {

  @Spy
  private LiveContextFreemarkerFacade testling;

  @Mock
  private FragmentContext fragmentContext;

  @Mock (answer = Answers.RETURNS_DEEP_STUBS)
  private PageGridPlacement pageGridPlacement;

  @Before
  public void setUp() throws Exception {
    doReturn(true).when(testling).isMetadataEnabled();
    doReturn(fragmentContext).when(testling).fragmentContext();
    when(fragmentContext.isFragmentRequest()).thenReturn(true);
    when(pageGridPlacement.getName()).thenReturn("myPlacementName");
  }

  @Test
  public void noMetadataInfoAvailable() throws Exception {
    doReturn(false).when(testling).isMetadataEnabled();
    Map fragmentHighlightingMetaData = testling.getFragmentHighlightingMetaData("anyPlacementName");
    assertEquals(Collections.emptyMap(), fragmentHighlightingMetaData);
  }

  @Test
  public void notInFragmentRequest() throws Exception {
    doReturn(null).when(testling).fragmentContext();
    Map fragmentHighlightingMetaData = testling.getFragmentHighlightingMetaData("anyPlacementName");
    assertEquals(Collections.emptyMap(), fragmentHighlightingMetaData);
    doReturn(fragmentContext).when(testling).fragmentContext();
    when(fragmentContext.isFragmentRequest()).thenReturn(false);
    fragmentHighlightingMetaData = testling.getFragmentHighlightingMetaData("anyPlacementName");
    assertEquals(Collections.emptyMap(), fragmentHighlightingMetaData);
  }

  //functional tests
  @Test
  public void placementNotInLayout() throws Exception {
    Map<String, Object> fragmentHighlightingMetaData = testling.getFragmentHighlightingMetaData("myPlacementName");
    assertFalse(getIsInLayout(fragmentHighlightingMetaData));
    assertEquals("myPlacementName", getPlacementName(fragmentHighlightingMetaData));
  }

  @Test
  public void placementHasNoItems() throws Exception {
    when(pageGridPlacement.getItems().isEmpty()).thenReturn(true);
    Map<String, Object> fragmentHighlightingMetaData = testling.getFragmentHighlightingMetaData(pageGridPlacement);
    assertTrue(getIsInLayout(fragmentHighlightingMetaData));
    assertFalse(getHasItems(fragmentHighlightingMetaData));
    assertEquals("myPlacementName", getPlacementName(fragmentHighlightingMetaData));
  }

  @Test
  public void placementHasItems() throws Exception {
    Map<String, Object> fragmentHighlightingMetaData = testling.getFragmentHighlightingMetaData(pageGridPlacement);
    assertTrue(getIsInLayout(fragmentHighlightingMetaData));
    assertTrue(getHasItems(fragmentHighlightingMetaData));
    assertEquals("myPlacementName", getPlacementName(fragmentHighlightingMetaData));
  }

  private Boolean getIsInLayout(Map<String, Object> fragmentHighlightingMetaData){
    return (Boolean) getMetaData(fragmentHighlightingMetaData).get(IS_IN_LAYOUT);
  }

  private String getPlacementName(Map<String, Object> fragmentHighlightingMetaData) {
    return (String) getMetaData(fragmentHighlightingMetaData).get(PLACEMENT_NAME);
  }

  private Boolean getHasItems(Map<String, Object> fragmentHighlightingMetaData) {
    return (Boolean) getMetaData(fragmentHighlightingMetaData).get(HAS_ITEMS);
  }

  private Map getMetaData(Map<String, Object> fragmentHighlightingMetaData) {
    return (Map) ((List<Object>) fragmentHighlightingMetaData.get("fragmentRequest")).get(0);
  }


}