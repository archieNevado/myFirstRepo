package com.coremedia.blueprint.cae.view;

import com.coremedia.blueprint.cae.contentbeans.CodeResourcesCacheKey;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CodeResources;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.blueprint.testing.ContentTestHelper;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;

import static com.coremedia.blueprint.cae.view.MergedCssResourcesViewTest.LocalConfig.PROFILE;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CONTENT_BEAN_FACTORY;
import static org.junit.Assert.assertEquals;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

/**
 * Tests {@link com.coremedia.blueprint.cae.view.CodeResourcesView}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = MergedCssResourcesViewTest.LocalConfig.class)
@ActiveProfiles(PROFILE)
public class MergedCssResourcesViewTest {
  @Configuration
  @ImportResource(
          value = {
                  CONTENT_BEAN_FACTORY,
                  "classpath:/framework/spring/blueprint-contentbeans.xml",
                  "classpath:/com/coremedia/cache/cache-services.xml",
                  "classpath:spring/test/dummy-views.xml",
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import({XmlRepoConfiguration.class, ContentTestConfiguration.class})
  @Profile(PROFILE)
  public static class LocalConfig {
    public static final String PROFILE = "MergedCssResourcesViewTest";
    private static final String CONTENT_REPOSITORY = "classpath:/com/coremedia/blueprint/cae/view/mergedcodeview/content.xml";

    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_REPOSITORY);
    }
  }

  private static final String NAVIGATION_ID = "4";
  private CodeResources codeResources;
  @Inject
  private MockHttpServletRequest request;
  @Inject
  private MockHttpServletResponse response;
  @Inject
  private ContentTestHelper contentTestHelper;
  @Inject
  private CodeResourcesView testling;

  @Before
  public void setup() {
    CMContext navigation = contentTestHelper.getContentBean(NAVIGATION_ID);
    codeResources = new CodeResourcesCacheKey(navigation, "css", false).evaluate(null);
  }

  @Test
  public void testManagedResources() throws UnsupportedEncodingException {
    testling.render(codeResources, "css", request, response);

    String expected = ".my-custom-class-34{content:css code id 34}\n" +
            ".my-custom-class-32{content:css code id 32}\n" +
            ".my-custom-class-30{content:css code id 30}\n";

    assertEquals("Output does not match", expected, response.getContentAsString());

  }
}
