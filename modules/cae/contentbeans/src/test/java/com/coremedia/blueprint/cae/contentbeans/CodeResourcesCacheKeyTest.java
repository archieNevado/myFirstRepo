package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(value = MockitoJUnitRunner.class)
public class CodeResourcesCacheKeyTest {
  @Mock
  private CMContext navigationBean;
  @Mock
  private Content navigationContent;
  @Mock
  private CMContext parentBean;
  @Mock
  private Content parentContent;
  @Mock
  private CMContext rootBean;
  @Mock
  private Content rootContent;
  @Mock
  private Content theme;

  @Mock
  private Navigation nonCMNavigation;

  @Before
  public void setup() {
    // Mock the original behaviour of CMContextImpl#getContext()
    when(navigationBean.getContext()).thenReturn(navigationBean);
    when(navigationBean.getContent()).thenReturn(navigationContent);
    // Just to prevent NPEs, we use theme to control the actual tests.
    when(navigationContent.getLinks("css")).thenReturn(Collections.emptyList());

    // same for parent and root
    when(parentBean.getContext()).thenReturn(parentBean);
    when(parentBean.getContent()).thenReturn(parentContent);
    when(parentContent.getLinks("css")).thenReturn(Collections.emptyList());
    when(rootBean.getContext()).thenReturn(rootBean);
    when(rootBean.getContent()).thenReturn(rootContent);
    when(rootContent.getLinks("css")).thenReturn(Collections.emptyList());

    // wire the hierarchy
    when(navigationBean.getParentNavigation()).thenReturn(parentBean);
    when(parentBean.getParentNavigation()).thenReturn(rootBean);
  }

  // If no code is found, the topmost CMNavigation must be returned.
  // This is important for cache performance.
  @Test
  public void testNoCodeAtAll() {
    CMNavigation actual = CodeResourcesCacheKey.findRelevantNavigation(navigationBean, "css");
    assertEquals(rootBean, actual);
  }

  @Test
  public void testDirectCode() {
    when(navigationContent.getLink("theme")).thenReturn(theme);
    when(parentContent.getLink("theme")).thenReturn(theme);
    when(rootContent.getLink("theme")).thenReturn(theme);
    CMNavigation actual = CodeResourcesCacheKey.findRelevantNavigation(navigationBean, "css");
    assertEquals(navigationBean, actual);
  }

  // Code is inherited from the nearest ancestor that has code.
  @Test
  public void testInheritedCode() {
    when(parentContent.getLink("theme")).thenReturn(theme);
    when(rootContent.getLink("theme")).thenReturn(theme);
    CMNavigation actual = CodeResourcesCacheKey.findRelevantNavigation(navigationBean, "css");
    assertEquals(parentBean, actual);
  }

  @Test
  public void testNonCMNavigation() {
    when(nonCMNavigation.getContext()).thenReturn(navigationBean);
    when(parentContent.getLink("theme")).thenReturn(theme);
    CMNavigation actual = CodeResourcesCacheKey.findRelevantNavigation(nonCMNavigation, "css");
    assertEquals(parentBean, actual);
  }

  // This is the only case in which findRelevantNavigation returns null.
  // Cannot happen in production, as long as we invoke findRelevantNavigation
  // only with CMNavigation objects.
  // This test only ensures recursion robustness.
  @Test
  public void testNoCMAncestorAtAll() {
    CMNavigation actual = CodeResourcesCacheKey.findRelevantNavigation(nonCMNavigation, "css");
    assertNull(actual);
  }
}
