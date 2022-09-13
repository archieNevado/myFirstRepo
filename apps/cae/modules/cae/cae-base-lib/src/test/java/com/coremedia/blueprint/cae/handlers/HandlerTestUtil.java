package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.web.servlet.ModelAndView;

import static com.coremedia.objectserver.web.HandlerHelper.VIEWNAME_DEFAULT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utilities for Handler and Linkscheme tests
 */
public final class HandlerTestUtil {

  // static utility class
  private HandlerTestUtil() {
  }

  // --- check utilities --------------------------------------------

  /**
   * Check that the mav holds a Page, return the Page.
   */
  public static Page extractPage(ModelAndView mav) {
    assertThat(mav).isNotNull();
    Object self = HandlerHelper.getRootModel(mav);
    assertThat(self).isInstanceOf(Page.class);
    return (Page) self;
  }

  /**
   * Check if a Page model consists of the expected content and channel.
   * <p>
   * Only applicable for content backed Pages.
   */
  public static void checkPage(ModelAndView mav, int contentId, int channelId) {
    Page page = extractPage(mav);
    Object content = page.getContent();
    assertThat(content).isInstanceOf(CMLinkable.class)
                    .returns(contentId, o -> ((CMLinkable)o).getContentId());
    Navigation navigation = page.getNavigation();
    assertThat(navigation).isInstanceOf(CMNavigation.class)
                    .returns(channelId, o -> ((CMNavigation)o).getContentId());
  }

  /**
   * Check if the model represents the expected Navigation.
   */
  public static void checkNavigation(ModelAndView mav, int channelId) {
    Object self = HandlerHelper.getRootModel(mav);
    assertThat(self).isInstanceOf(CMNavigation.class)
                    .returns(channelId, o -> ((CMNavigation)o).getContentId());
  }

  /**
   * Check if the model represents the expected class.
   */
  public static void checkModelAndView(ModelAndView mav, String expectedView, Class<?> clazz) {
    Object self = HandlerHelper.getRootModel(mav);
    assertThat(self).isInstanceOf(clazz);
    checkView(mav, expectedView);
  }

  /**
   * Check for the expected view.
   */
  public static void checkView(ModelAndView mav, String expectedView) {
    String view = mav.getViewName();
    if (expectedView==null) {
      // null and DEFAULT are equivalent and normalized to null during view dispatching anyway.
      assertThat(view).satisfiesAnyOf(s -> assertThat(s).isNull(), s -> assertThat(s).isEqualTo(VIEWNAME_DEFAULT));
    } else {
      assertThat(view).isEqualTo(expectedView);
    }
  }

  /**
   * Check if the model represents an HttpError.
   */
  public static void checkError(ModelAndView mav, int errorCode) {
    assertThat(HandlerHelper.isError(mav, errorCode)).isTrue();
  }

}
