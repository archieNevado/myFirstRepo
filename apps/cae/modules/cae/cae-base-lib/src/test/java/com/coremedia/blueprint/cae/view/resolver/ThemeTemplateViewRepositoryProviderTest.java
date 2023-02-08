package com.coremedia.blueprint.cae.view.resolver;

import com.coremedia.blueprint.cae.config.BlueprintViewsCaeBaseLibConfiguration;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        BlueprintViewsCaeBaseLibConfiguration.class,
        XmlRepoConfiguration.class,
})
@TestPropertySource(properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:com/coremedia/blueprint/cae/view/resolver/content.xml",
        "repository.params.contentschemaxml=classpath:com/coremedia/blueprint/cae/view/resolver/test-doctypes.xml",
        "cae.hashing.backward-compatibility=true",
})
@WebAppConfiguration
public class ThemeTemplateViewRepositoryProviderTest {

  @Autowired
  private ContentRepository contentRepository;

  @Autowired
  @Qualifier("themeTemplateViewRepositoryProvider")
  private ThemeTemplateViewRepositoryProvider testling;

  @Test
  public void testTemplatesLocations() {
    List<String> locations = testling.templateLocations("theme::2/corporate");
    assertThat(locations).
            containsExactly(
                    "jar:id:contentproperty:/Themes/corporate/corporate-templates/archive!/META-INF/resources/WEB-INF/templates/corporate",
                    "jar:id:contentproperty:/Themes/corporate/bricks-templates/archive!/META-INF/resources/WEB-INF/templates/bricks"
            );
  }

  @Test
  public void testViewRepositoryNames() {
    Content theme = content(2);
    List<String> locations = testling.viewRepositoryNames(theme, null);
    assertThat(locations).
            containsExactly(
                    "theme::2/corporate",
                    "theme::2/bricks"
            );
  }

  // --- internal ---------------------------------------------------

  private Content content(int id) {
    return contentRepository.getContent(IdHelper.formatContentId(id));
  }
}
