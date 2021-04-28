package com.coremedia.blueprint.cae.view.resolver;

import com.coremedia.blueprint.cae.config.BlueprintViewsCaeBaseLibConfiguration;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ThemeTemplateViewRepositoryProviderTest.LocalConfig.class)
@TestPropertySource(properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:com/coremedia/blueprint/cae/view/resolver/content.xml",
        "repository.params.contentschemaxml=classpath:com/coremedia/blueprint/cae/view/resolver/test-doctypes.xml"
})
@WebAppConfiguration
@ActiveProfiles(ThemeTemplateViewRepositoryProviderTest.LocalConfig.PROFILE)
public class ThemeTemplateViewRepositoryProviderTest {

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties({
          DeliveryConfigurationProperties.class
  })
  @Import({
          BlueprintViewsCaeBaseLibConfiguration.class,
          XmlRepoConfiguration.class,
  })
  @Profile(LocalConfig.PROFILE)
  public static class LocalConfig {

    static final String PROFILE = "ThemeTemplateViewRepositoryProviderTest";
  }

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private ContentRepository contentRepository;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Resource(name = "themeTemplateViewRepositoryProvider")
  private ThemeTemplateViewRepositoryProvider testling;

  @Test
  public void testTemplatesLocations() {
    List<String> locations = testling.templateLocations("theme::2/corporate");
    assertThat(locations).hasSize(2);
    // The order matters.
    assertThat("jar:id:contentproperty:/Themes/corporate/corporate-templates/archive!/META-INF/resources/WEB-INF/templates/corporate").isEqualTo(locations.get(0));
    assertThat("jar:id:contentproperty:/Themes/corporate/bricks-templates/archive!/META-INF/resources/WEB-INF/templates/bricks").isEqualTo(locations.get(1));
  }

  @Test
  public void testViewRepositoryNames() {
    Content theme = content(2);
    List<String> locations = testling.viewRepositoryNames(theme, null);
    assertThat(locations).hasSize(2);
    // The order matters.
    assertThat("theme::2/corporate").isEqualTo(locations.get(0));
    assertThat("theme::2/bricks").isEqualTo(locations.get(1));
  }

  // --- internal ---------------------------------------------------

  private Content content(int id) {
    return contentRepository.getContent(IdHelper.formatContentId(id));
  }
}
