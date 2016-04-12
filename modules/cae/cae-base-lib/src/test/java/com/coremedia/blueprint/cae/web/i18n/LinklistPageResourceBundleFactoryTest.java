package com.coremedia.blueprint.cae.web.i18n;

import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.blueprint.testing.ContentTestHelper;
import com.coremedia.cap.multisite.impl.SitesServiceImpl;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.util.ResourceBundle;

import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CACHE;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CONTENT_BEAN_FACTORY;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.ID_PROVIDER;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.LINK_FORMATTER;
import static org.junit.Assert.assertEquals;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LinklistPageResourceBundleFactoryTest.LocalConfig.class)
public class LinklistPageResourceBundleFactoryTest {
  @Configuration
  @ImportResource(
          value = {
                  CONTENT_BEAN_FACTORY,
                  ID_PROVIDER,
                  LINK_FORMATTER,
                  CACHE,
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import({XmlRepoConfiguration.class, ContentTestConfiguration.class})
  public static class LocalConfig {
    public static final String PROFILE = "LinklistPageResourceBundleFactoryTest";
    private static final String CONTENT_REPOSITORY = "classpath:/com/coremedia/blueprint/cae/web/i18n/resourcebundles/content.xml";

    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_REPOSITORY);
    }
  }

  @Inject
  private ContentTestHelper contentTestHelper;

  @Test
  public void testBundleForPage() throws Exception {
    ContentBean article = contentTestHelper.getContentBean(30);
    Navigation navigation = contentTestHelper.getContentBean(20);
    Page page = new PageImpl(navigation, article, false, new SitesServiceImpl(), null);
    LinklistPageResourceBundleFactory testling = new LinklistPageResourceBundleFactory();
    ResourceBundle resourceBundle = testling.resourceBundle(page);
    assertEquals("str2fromBundle10", resourceBundle.getString("str2"));
    assertEquals("str4fromBundle20a", resourceBundle.getString("str4"));
    assertEquals("str1fromBundle20", resourceBundle.getString("str1"));
    assertEquals("str3fromBundle20", resourceBundle.getString("str3"));
    assertEquals("str5fromBundle10", resourceBundle.getString("str5"));
  }
}
