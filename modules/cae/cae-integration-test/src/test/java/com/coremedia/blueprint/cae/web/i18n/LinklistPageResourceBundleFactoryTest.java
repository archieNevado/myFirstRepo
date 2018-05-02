package com.coremedia.blueprint.cae.web.i18n;

import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.testing.ContentTestConfiguration;
import com.coremedia.blueprint.testing.ContentTestHelper;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.ResourceBundle;

import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CACHE;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.CONTENT_BEAN_FACTORY;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.ID_PROVIDER;
import static com.coremedia.cap.test.xmlrepo.XmlRepoResources.LINK_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LinklistPageResourceBundleFactoryTest.LocalConfig.class)
@TestPropertySource(properties = {
        "repository.params.contentxml=classpath:com/coremedia/blueprint/cae/web/i18n/resourcebundles/content.xml",
        "repository.params.contentschemaxml=classpath:framework/doctypes/blueprint/blueprint-doctypes.xml"
})
@ActiveProfiles(LinklistPageResourceBundleFactoryTest.LocalConfig.PROFILE)
public class LinklistPageResourceBundleFactoryTest {

  private static final String TESTLING_RESOURCE = "classpath:/framework/spring/blueprint-i18n.xml";

  @Configuration
  @ComponentScan("com.coremedia.cap.common.xml")
  @ImportResource(
          value = {
                  CONTENT_BEAN_FACTORY,
                  ID_PROVIDER,
                  LINK_FORMATTER,
                  CACHE,
                  TESTLING_RESOURCE
          },
          reader = ResourceAwareXmlBeanDefinitionReader.class
  )
  @Import({XmlRepoConfiguration.class, ContentTestConfiguration.class})
  @Profile(LocalConfig.PROFILE)
  public static class LocalConfig {

    static final String PROFILE = "LinklistPageResourceBundleFactoryTest";
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private ContentTestHelper contentTestHelper;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Resource(name = "linklistPageResourceBundleFactory")
  private LinklistPageResourceBundleFactory testling;

  @Test
  public void testBundleForPage() {
    ContentBean article = contentTestHelper.getContentBean(30);
    Navigation navigation = contentTestHelper.getContentBean(20);
    Page page = new PageImpl(navigation, article, false, mock(SitesService.class), null, null, null, null);
    ResourceBundle resourceBundle = testling.resourceBundle(page, null);
    assertThat(resourceBundle.getString("str2")).isEqualTo("str2fromBundle10");
    assertThat(resourceBundle.getString("str4")).isEqualTo("str4fromBundle20a");
    assertThat(resourceBundle.getString("str1")).isEqualTo("str1fromBundle20");
    assertThat(resourceBundle.getString("str3")).isEqualTo("str3fromBundle20");
    assertThat(resourceBundle.getString("str5")).isEqualTo("str5fromBundle10");
  }

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void testBundleForPageByTheme() {
    Navigation navigation = contentTestHelper.getContentBean(220);
    Page page = new PageImpl(navigation, navigation, false, mock(SitesService.class), null, null, null, null);
    ResourceBundle resourceBundle = testling.resourceBundle(page, null);
    assertThat(resourceBundle.getString("locale")).isEqualTo("localeFromBundle_en_gb_europe");
    assertThat(resourceBundle.getString("str1")).isEqualTo("str1");
    assertThat(resourceBundle.getString("str2")).isEqualTo("str2");
    assertThat(resourceBundle.getString("str3")).isEqualTo("str3");

    navigation = contentTestHelper.getContentBean(216);
    page = new PageImpl(navigation, navigation, false, mock(SitesService.class), null, null, null, null);

    resourceBundle = testling.resourceBundle(page, null);

    assertThat(resourceBundle.getString("locale")).isEqualTo("localeFromBundle_en_gb");
    assertThat(resourceBundle.getString("str1")).isEqualTo("str1");
    assertThat(resourceBundle.getString("str2")).isEqualTo("str2");

    navigation = contentTestHelper.getContentBean(210);
    page = new PageImpl(navigation, navigation, false, mock(SitesService.class), null, null, null, null);

    resourceBundle = testling.resourceBundle(page, null);

    assertThat(resourceBundle.getString("locale")).isEqualTo("localeFromBundle_en");
    assertThat(resourceBundle.getString("str1")).isEqualTo("str1");
  }

  @Test
  public void testOverrideInheritedTheme() {
    Navigation navigation = contentTestHelper.getContentBean(226);
    Page page = new PageImpl(navigation, navigation, false, mock(SitesService.class), null, null, null, null);
    ResourceBundle resourceBundle = testling.resourceBundle(page, null);
    
    assertThat(resourceBundle.getString("locale")).isEqualTo("locale_overridden");
  }
}
