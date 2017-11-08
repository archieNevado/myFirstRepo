package com.coremedia.blueprint.elastic.social.cae.action;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.handlers.HandlerBaseTest;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.beans.ContentBeanIdConverter;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationHandlerTest extends HandlerBaseTest {

  private static final String ID = "4711";
  private static final String SOME_ACTION = "someAction";
  private static final String CONTEXT_NAME = "root";

  @Mock
  private BeanFactory beanFactory;

  @Mock
  private SitesService sitesService;

  @Mock
  private Content actionContent;

  @Mock
  private ContentType actionContentType;

  @Mock
  private CMAction action;

  @Mock
  private AuthenticationState authenticationState;

  @Mock
  private CMNavigation rootNavigation;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private ContentBeanIdConverter converter;

  @Mock
  private ContentLinkBuilder contentLinkBuilder;

  @Test
  public void testShowPageForUnknownActionWhereTheActionNameEqualsTheVanityName() throws Exception {
    when(contentLinkBuilder.getVanityName(actionContent)).thenReturn("unknownAction");

    assertModelWithPageBean(handleRequest('/'+ PREFIX_DYNAMIC+"/auth/--/root/4711/unknownAction"), rootNavigation, action);
  }

  @Test
  public void testNotFoundForUnknownActionWithSegmentMismatch() throws Exception {
    assertNotFound("segment mismatch for unknown action", handleRequest('/'+ PREFIX_DYNAMIC+"/auth/--/root/4711/unknownAction"));
  }

  @Test
  public void testGenerateActionLink() {
    when(contentLinkBuilder.getVanityName(actionContent)).thenReturn(SOME_ACTION);
    assertEquals(
            '/'+ PREFIX_DYNAMIC+"/auth/--/root/4711/" + SOME_ACTION,
            formatLink(authenticationState, null, false, ImmutableMap.<String, Object>of("action", SOME_ACTION)));
  }

  @Test
  public void testGenerateGenericActionLink() {
    when(contentLinkBuilder.getVanityName(actionContent)).thenReturn(SOME_ACTION);
    assertEquals('/'+ PREFIX_DYNAMIC+"/auth/--/root/4711/" + SOME_ACTION, formatLink(authenticationState, null, false));
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();

    AuthenticationHandler testling = new AuthenticationHandler();
    testling.setBeanFactory(beanFactory);
    testling.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    testling.setContentBeanIdConverter(converter);
    testling.setContextHelper(getContextHelper());
    testling.setUrlPathFormattingHelper(getUrlPathFormattingHelper());
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setSitesService(getSitesService());
    testling.setContentLinkBuilder(contentLinkBuilder);

    registerHandler(testling);

    when(beanFactory.getBean("cmPage", PageImpl.class)).thenReturn(new PageImpl(false, sitesService, Cache.currentCache(), null, null, null));
    when(authenticationState.getAction()).thenReturn(action);
    when(navigationSegmentsUriHelper.parsePath(eq(asList(CONTEXT_NAME)))).thenReturn(rootNavigation);
    when(converter.convert(action)).thenReturn(ID);
    when(navigationSegmentsUriHelper.getPathList(rootNavigation)).thenReturn(asList(CONTEXT_NAME));
    when(getIdActionDocConverter().convert("4711")).thenReturn(action);
    when(action.getContent()).thenReturn(actionContent);
    when(actionContent.getType()).thenReturn(actionContentType);

    setContextFor(action, rootNavigation);
  }

}
