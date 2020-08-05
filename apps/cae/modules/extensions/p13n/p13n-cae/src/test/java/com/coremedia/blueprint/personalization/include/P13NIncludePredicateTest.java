package com.coremedia.blueprint.personalization.include;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.personalization.contentbeans.CMP13NSearch;
import com.coremedia.blueprint.personalization.contentbeans.CMSelectionRules;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.view.RenderNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Silent.class)
public class P13NIncludePredicateTest {

  P13NIncludePredicate testling;

  @Mock
  SitesService sitesService;

  @Mock
  SettingsService settingsService;

  @Mock
  CMSelectionRules selectionRules;

  @Before
  public void setUp() {
    testling = new P13NIncludePredicate(sitesService, settingsService);
    ContentSiteAspect contentSiteAspect = mock(ContentSiteAspect.class);
    when(contentSiteAspect.getSite()).thenReturn(mock(Site.class));
    when(sitesService.getContentSiteAspect(any(Content.class))).thenReturn(contentSiteAspect);
    when(selectionRules.getContent()).thenReturn(mock(Content.class));
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
  }

  @Test
  public void testInputNotMatching() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(new Object());
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputMatchingNoView() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(selectionRules);
    when(input.getView()).thenReturn(null);
    p13nSettings(true, true);
    assertTrue(testling.test(input));
  }

  @Test
  public void testInputMatchingAndFragmentPreviewSet() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(mock(CMP13NSearch.class));
    when(input.getView()).thenReturn("fragmentPreview");
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputMatchingOtherViewSet() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(selectionRules);
    when(input.getView()).thenReturn("any_view_except_fragmentPreview");
    p13nSettings(true, true);
    assertTrue(testling.test(input));
  }

  @Test
  public void testInputMatchingIncludesNotSet() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(selectionRules);
    when(input.getView()).thenReturn(null);
    p13nSettings(null, true);
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputMatchingPerItemNotSet() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(selectionRules);
    when(input.getView()).thenReturn(null);
    p13nSettings(true, null);
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputMatchingIncludesDisabled() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(selectionRules);
    when(input.getView()).thenReturn(null);
    p13nSettings(false, true);
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputMatchingPerItemDisabled() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(selectionRules);
    when(input.getView()).thenReturn(null);
    p13nSettings(false, false);
    assertFalse(testling.test(input));
  }

  private void p13nSettings(Boolean enabled, Boolean perItem) {
    when(settingsService.getSetting(eq(P13NDynamicIncludeSettings.P13N_DYNAMIC_INCLUDES_ENABLED_SETTING), eq(Boolean.class), any(Site.class)))
            .thenReturn(Optional.ofNullable(enabled));
    when(settingsService.getSetting(eq(P13NDynamicIncludeSettings.P13N_DYNAMIC_INCLUDES_PER_ITEMS_SETTING), eq(Boolean.class), any(Site.class)))
            .thenReturn(Optional.ofNullable(perItem));
  }
}
