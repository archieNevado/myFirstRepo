package com.coremedia.blueprint.cae.web.taglib;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.navigation.Linkable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade.HAS_ITEMS;
import static com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade.IS_IN_LAYOUT;
import static com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade.PLACEMENT_NAME;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BlueprintFreemarkerFacadeTest {

  @Spy
  private TestBlueprintFreemarkerFacade testling;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private PageGridPlacement pageGridPlacement;

  @Before
  public void setUp() throws Exception {
    doReturn(true).when(testling).isMetadataEnabled();
    when(pageGridPlacement.getName()).thenReturn("myPlacementName");
  }

  @Test
  public void noMetadataInfoAvailable() throws Exception {
    doReturn(false).when(testling).isMetadataEnabled();
    Map fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData("anyPlacementName");
    assertThat(fragmentHighlightingMetaData).isEmpty();
  }

  //functional tests
  @Test
  public void placementNotInLayout() throws Exception {
    Map<String, Object> fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData("myPlacementName");
    assertThat(getIsInLayout(fragmentHighlightingMetaData)).isFalse();
    assertThat(getPlacementName(fragmentHighlightingMetaData)).isEqualTo("myPlacementName");
  }

  @Test
  public void placementHasNoItems() throws Exception {
    when(pageGridPlacement.getItems().isEmpty()).thenReturn(true);
    Map<String, Object> fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData(pageGridPlacement);
    assertThat(getIsInLayout(fragmentHighlightingMetaData)).isTrue();
    assertThat(getHasItems(fragmentHighlightingMetaData)).isFalse();
    assertThat(getPlacementName(fragmentHighlightingMetaData)).isEqualTo("myPlacementName");
  }

  @Test
  public void placementHasItems() throws Exception {
    List linkabless = asList(mock(Linkable.class), mock(CMChannel.class));
    when(pageGridPlacement.getItems()).thenReturn(linkabless);

    Map<String, Object> fragmentHighlightingMetaData = testling.getPlacementHighlightingMetaData(pageGridPlacement);
    assertThat(getIsInLayout(fragmentHighlightingMetaData)).isTrue();
    assertThat(getHasItems(fragmentHighlightingMetaData)).isTrue();
    assertThat(getPlacementName(fragmentHighlightingMetaData)).isEqualTo("myPlacementName");
  }

  private Boolean getIsInLayout(Map<String, Object> fragmentHighlightingMetaData) {
    return (Boolean) getMetaData(fragmentHighlightingMetaData).get(IS_IN_LAYOUT);
  }

  private String getPlacementName(Map<String, Object> fragmentHighlightingMetaData) {
    return (String) getMetaData(fragmentHighlightingMetaData).get(PLACEMENT_NAME);
  }

  private Boolean getHasItems(Map<String, Object> fragmentHighlightingMetaData) {
    return (Boolean) getMetaData(fragmentHighlightingMetaData).get(HAS_ITEMS);
  }

  private Map getMetaData(Map<String, Object> fragmentHighlightingMetaData) {
    return (Map) ((List<Object>) fragmentHighlightingMetaData.get("placementRequest")).get(0);
  }
}