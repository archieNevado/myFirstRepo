package com.coremedia.livecontext.fragment;

import com.coremedia.objectserver.web.HandlerHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryFragmentHandlerTest extends FragmentHandlerTestBase<CategoryFragmentHandler> {

  @Test
  public void handleCategoryViewFragmentNoLiveContextNavigationFound() {
    when(getResolveContextStrategy().resolveContext(getSite(), CATEGORY_ID)).thenReturn(null);
    ModelAndView result = getTestling().createModelAndView(getFragmentParameters4Product(), request);
    assertNotNull(result);
    assertTrue(HandlerHelper.isNotFound(result));
  }

  @Test
  public void handleCategoryViewFragment() {
    ModelAndView result = getTestling().createModelAndView(getFragmentParameters4Product(), request);
    assertDefaultPage(result);
    verifyDefault();
  }

  @Override
  protected CategoryFragmentHandler createTestling() {
    return new CategoryFragmentHandler();
  }

  @Before
  public void defaultSetup() {
    super.defaultSetup();
    getTestling().setContextStrategy(resolveContextStrategy);
  }
}
