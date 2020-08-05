package com.coremedia.blueprint.personalization.include;

import com.coremedia.blueprint.common.layout.DynamizableCMTeasableContainer;
import com.coremedia.objectserver.view.RenderNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class P13NContainerPredicateTest {

  private P13NContainerPredicate testling;

  @Mock
  private DynamizableCMTeasableContainer dynamizableContainer;

  @Before
  public void setUp() {
    testling = new P13NContainerPredicate();
    when(dynamizableContainer.isDynamic()).thenReturn(true);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
  }

  @Test
  public void testInputNotMatching() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(new Object());
    assertFalse(testling.test(input));
  }

  @Test
  public void testContainerIsStatic() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(dynamizableContainer);
    when(dynamizableContainer.isDynamic()).thenReturn(false);
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputMatchingNoView() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(dynamizableContainer);
    when(input.getView()).thenReturn(null);
    assertTrue(testling.test(input));
  }

  @Test
  public void testInputMatchingAndFragmentPreviewSet() {
    RenderNode input = mock(RenderNode.class);
    when(input.getView()).thenReturn("fragmentPreview");
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputMatchingAndMultiViewPreviewSet() {
    RenderNode input = mock(RenderNode.class);
    when(input.getView()).thenReturn("multiViewPreview");
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputMatchingAndAsPreviewSet() {
    RenderNode input = mock(RenderNode.class);
    when(input.getView()).thenReturn("asPreview");
    assertFalse(testling.test(input));
  }

  @Test
  public void testInputMatchingOtherViewSet() {
    RenderNode input = mock(RenderNode.class);
    when(input.getBean()).thenReturn(dynamizableContainer);
    when(input.getView()).thenReturn("any_view_except_fragmentPreview");
    assertTrue(testling.test(input));
  }
}
