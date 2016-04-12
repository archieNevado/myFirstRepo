package com.coremedia.blueprint.jsonprovider.shoutem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link ShoutemController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ShoutemCaeTestConfiguration.class)
@ActiveProfiles(ShoutemCaeTestConfiguration.PROFILE)
public class ShoutemControllerTest {

  // the component to test
  @Inject
  private ShoutemController shoutemController;

  @Test
  public void testNotImplemented() throws Exception {
    // create http request
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(ShoutemApi.PARAM_METHOD, "method/not_a_method");
    request.setPathInfo("/blueprint/servlet/shoutemapi/media");
    // create http response
    MockHttpServletResponse response = new MockHttpServletResponse();

    // run the controller and verify the result
    ModelAndView modelAndView = shoutemController.handleRequest(request, response);
    assertNull(modelAndView);
    assertEquals(ShoutemController.RESPONSE_MESSAGE_NOT_IMPLEMENTED, response.getContentAsString());
  }

  @Test
  public void testSiteSegmentNotFound() throws Exception {
    // create http request
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(ShoutemApi.PARAM_METHOD, ShoutemApi.METHOD_GET_SERVICE_INFO);
    request.setPathInfo("/blueprint/servlet/");
    // create http response
    MockHttpServletResponse response = new MockHttpServletResponse();

    // run the controller and verify the result
    ModelAndView modelAndView = shoutemController.handleRequest(request, response);
    assertNull(modelAndView);
    assertTrue(response.getContentAsString().contains("SITE SEGMENT NOT FOUND IN REQUEST URI"));
  }

  @Test
  public void testGetServiceInfo() throws Exception {
    // create http request
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(ShoutemApi.PARAM_METHOD, ShoutemApi.METHOD_GET_SERVICE_INFO);
    request.setPathInfo("/blueprint/servlet/shoutemapi/media");
    // create http response
    MockHttpServletResponse response = new MockHttpServletResponse();

    // run the controller and verify the result
    ModelAndView modelAndView = shoutemController.handleRequest(request, response);
    assertNull(modelAndView);

    JSONAssert.assertEquals("{'server_type':'coremedia','api_version':1}", response.getContentAsString(), true);
  }

}
