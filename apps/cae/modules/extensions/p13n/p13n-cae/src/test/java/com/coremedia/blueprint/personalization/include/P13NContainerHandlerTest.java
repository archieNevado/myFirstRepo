package com.coremedia.blueprint.personalization.include;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.layout.DynamizableCMTeasableContainer;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.HandlerHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_CONTAINER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class P13NContainerHandlerTest {

  private P13NContainerHandler testling;

  @Mock
  private BeanFactory beanFactory;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private ContextHelper contextHelper;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private SitesService sitesService;

  @Mock
  private ContentLinkBuilder contentLinkBuilder;

  @Mock
  private DynamizableCMTeasableContainer dynamizableContainer;

  @Mock
  private CMNavigation navigation;

  @Before
  public void setUp() throws Exception {
    testling = new P13NContainerHandler();
    testling.setBeanFactory(beanFactory);
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setSitesService(sitesService);
    testling.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    testling.setContextHelper(contextHelper);
    testling.setContentLinkBuilder(contentLinkBuilder);

    when(beanFactory.getBean("cmPage", PageImpl.class)).thenReturn(new PageImpl(false, sitesService, Cache.currentCache(), null, null, null));

    Content navigationContent = mock(Content.class);
    ContentType navigationContentType = mock(ContentType.class);

    when(navigation.getContent()).thenReturn(navigationContent);
    when(navigationContent.getType()).thenReturn(navigationContentType);
    when(navigationContentType.getName()).thenReturn(CMChannel.NAME);
  }

  @Test
  public void testHandleChannelRequest() {
    CMChannel cmContentBean = mock(CMChannel.class);

    configureContext("helios", navigation);

    MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    ModelAndView modelAndView = testling.handleRequest("helios", cmContentBean, "related", "myView", mockRequest);

    assertEquals("myView", modelAndView.getViewName());
    assertTrue(modelAndView.getModel().get("self") instanceof DynamizableCMTeasableContainer);
    assertTrue(modelAndView.getModel().get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION) instanceof CMNavigation);
    assertTrue(modelAndView.getModel().get(ContextHelper.ATTR_NAME_PAGE) instanceof Page);
  }

  @Test
  public void testHandleFragmentRequestWrongContent() {
    CMArticle cmArticle = mock(CMArticle.class);

    ModelAndView modelAndView = testling.handleRequest("helios", cmArticle, "related", "myView", new MockHttpServletRequest());
    assertEquals(HandlerHelper.notFound().getModel(), modelAndView.getModel());
  }

  @Test
  public void testBuildLinkForRelatedLinks() {
    UriTemplate uriTemplate = mock(UriTemplate.class);
    when(uriTemplate.toString()).thenReturn(P13NContainerHandler.DYNAMIC_CONTAINER_URI_PATTERN);

    Content teasableContent = mock(Content.class);
    CMTeasable teasable = mock(CMTeasable.class);

    when(dynamizableContainer.getTeasable()).thenReturn(teasable);
    when(dynamizableContainer.getTeasable().getContentId()).thenReturn(4711);
    when(dynamizableContainer.getTeasable().getContent()).thenReturn(teasableContent);
    when(dynamizableContainer.getPropertyPath()).thenReturn("related");

    configureSegmentPath("helios");

    Map<String, Object> linkParameters = new HashMap<>();
    linkParameters.put("targetView", "myTargetView");

    UriComponents uriComponents = testling.buildLink(dynamizableContainer, uriTemplate, linkParameters);

    assertEquals("Expected link does not match built link.",
            '/' + PREFIX_DYNAMIC + '/' + SEGMENTS_CONTAINER + "/p13n/helios/4711/related", uriComponents.getPath());
  }

  @Test
  public void testBuildLinkForItems() {
    UriTemplate uriTemplate = mock(UriTemplate.class);
    when(uriTemplate.toString()).thenReturn(P13NContainerHandler.DYNAMIC_CONTAINER_URI_PATTERN);

    Content teasableContent = mock(Content.class);
    CMTeasable teasable = mock(CMTeasable.class);

    when(dynamizableContainer.getTeasable()).thenReturn(teasable);
    when(dynamizableContainer.getTeasable().getContentId()).thenReturn(4711);
    when(dynamizableContainer.getTeasable().getContent()).thenReturn(teasableContent);
    when(dynamizableContainer.getPropertyPath()).thenReturn(null);

    configureSegmentPath("helios");

    Map<String, Object> linkParameters = new HashMap<>();
    linkParameters.put("targetView", "myTargetView");

    UriComponents uriComponents = testling.buildLink(dynamizableContainer, uriTemplate, linkParameters);

    assertEquals("Expected link does not match built link.",
            '/' + PREFIX_DYNAMIC + '/' + SEGMENTS_CONTAINER + "/p13n/helios/4711/items", uriComponents.getPath());
  }

  private void configureContext(String context, Navigation navigation) {
    when(navigationSegmentsUriHelper.parsePath(eq(Collections.singletonList(context)))).thenReturn(navigation);
  }

  private void configureSegmentPath(String context) {
    CMNavigation cmNavigation = mock(CMNavigation.class);
    when(contextHelper.currentSiteContext()).thenReturn(cmNavigation);
    List<String> pathList = new ArrayList<>();
    pathList.add(context);
    when(navigationSegmentsUriHelper.getPathList(cmNavigation)).thenReturn(pathList);
  }
}
