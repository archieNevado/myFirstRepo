package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.contentbeans.BlobFromContentBeanSetting;
import com.coremedia.blueprint.testing.ContentTestHelper;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.UrlBlob;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.HttpError;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;

import static com.coremedia.blueprint.cae.handlers.BlobHandlerTest.LocalConfig.PROFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = BlobHandlerTest.LocalConfig.class)
@ActiveProfiles(PROFILE)
public class BlobHandlerTest {

  @Configuration(proxyBeanMethods = false)
  @Profile(PROFILE)
  @Import({
          HandlerTestConfiguration.class,
  })
  static class LocalConfig {
    public static final String PROFILE = "BlobHandlerTest";
    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(
              "classpath:/com/coremedia/blueprint/cae/handlers/blob-test-content.xml",
              "classpath:/com/coremedia/blueprint/cae/handlers/blob-test-users.xml"
      );
    }
  }

  private static final String CONTENT_ID = "16";
  private static final String NAME = "propertyName";
  private static final String ETAG = "3E725F3FDEEFBD62BCDF68EA08FC4803";
  private static final String CUSTOM_FILENAME = "myfile.abc";
  // /resource/data/{contentId}/{propertyName}/{etag}/{filename}
  private static final String URI_PREFIX = "/resource/data/" + CONTENT_ID + "/" + NAME + "/" + ETAG + "/";
  private static final String URI_JPG = URI_PREFIX + "16data.jpg";
  private static final String URI_CUSTOM = URI_PREFIX + CUSTOM_FILENAME;

  @Inject
  private MockMvc mockMvc;
  @Inject
  private LinkFormatter linkFormatter;
  @Inject
  private ContentTestHelper contentTestHelper;

  @Before
  public void setup() {
  }

  @After
  public void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  /**
   * Tests link generation
   */
  @Test
  public void testLink() {
    String link = formatLink(toBlobFromContentBeanSetting(), null);
    assertEquals(URI_JPG.replace("16data.jpg", "file.png"), link);
  }

  /**
   * Tests link generation with filename customization
   */
  @Test
  public void testLinkWithFilename() {
    String link = formatLink(toBlobFromContentBeanSetting(), CUSTOM_FILENAME);
    assertEquals(URI_CUSTOM, link);
  }

  @Test
  public void testRequest() throws Exception {
    ModelAndView modelAndView = handleRequest(URI_JPG);
    Object rootModel = HandlerHelper.getRootModel(modelAndView);
    assertTrue(rootModel instanceof UrlBlob);
    assertEquals("file:/16data.jpg", ((UrlBlob) rootModel).getUrl().toString());
    assertEquals(ETAG, ((UrlBlob) rootModel).getETag());
  }

  @Test
  public void testRequestNotFound() throws Exception {
    ModelAndView modelAndView = handleRequest("/resource/data/" + CONTENT_ID + "/" + NAME + "/-/file.raw");
    assertEquals(new HttpError(404), HandlerHelper.getRootModel(modelAndView));
  }

  // --- internal ---------------------------------------------------

  private BlobFromContentBeanSetting toBlobFromContentBeanSetting() {
    return new BlobFromContentBeanSetting(contentTestHelper.getContentBean(CONTENT_ID), NAME, (Blob) contentTestHelper.getContent(CONTENT_ID).getStruct("localSettings").get(NAME));
  }

  private String formatLink(Object bean, String filename) {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
    request.setAttribute(BlobHandler.ATTRIBUTE_FILENAME, filename);
    request.setCharacterEncoding("UTF-8");
    return linkFormatter.formatLink(bean, null, request, new MockHttpServletResponse(), false);
  }

  private ModelAndView handleRequest(String path) throws Exception {
    MockHttpServletRequestBuilder req = MockMvcRequestBuilders
            .get(path)
            .characterEncoding("UTF-8");
    return mockMvc.perform(req).andReturn().getModelAndView();
  }
}
