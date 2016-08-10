package com.coremedia.blueprint.themeimporter;

import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.mimetype.DefaultMimeTypeService;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ThemeImporterTest.LocalConfig.class)
public class ThemeImporterTest {

  private static final String THEMES = "/Themes";
  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private CapConnection capConnection;

  @Configuration
  @Import(XmlRepoConfiguration.class)
  public static class LocalConfig {

    @Bean
    @Scope(SCOPE_SINGLETON)
    public XmlUapiConfig xmlUapiConfig() {
      return XmlUapiConfig.builder().withContentTypes("classpath:framework/doctypes/blueprint/blueprint-doctypes.xml").build();
    }
  }

  private InputStream corporateTheme;
  private InputStream basicTheme;
  private InputStream perfectChefTheme;
  private ThemeImporter themeImporter;

  @Before
  public void setUp() throws Exception {
    corporateTheme = getClass().getResource("./corporate-theme.zip").openStream();
    basicTheme = getClass().getResource("./basic-theme.zip").openStream();
    perfectChefTheme = getClass().getResource("./perfectchef-theme.zip").openStream();
    themeImporter = new ThemeImporter(capConnection, new DefaultMimeTypeService(true));
  }

  @After
  public void tearDown() {
    IOUtils.closeQuietly(perfectChefTheme);
    IOUtils.closeQuietly(basicTheme);
    IOUtils.closeQuietly(corporateTheme);
  }

  @Test
  public void extractFromZip() throws Exception {
    String corporateThemePath = THEMES + "/corporate/Corporate Theme";
    String basicThemePath = THEMES + "/basic/Basic Theme";
    String perfectChefThemePath = THEMES + "/perfectchef/Perfectchef Theme";
    ContentType type = capConnection.getContentRepository().getContentType("CMTheme");
    if (type != null) {
      type.create(capConnection.getContentRepository().getRoot(), corporateThemePath).checkIn();
    }

    themeImporter.importThemes(THEMES, corporateTheme, basicTheme, perfectChefTheme);

    Content corporateTheme = capConnection.getContentRepository().getChild(corporateThemePath);
    Assert.assertNotNull(corporateTheme);
    Assert.assertNotNull(corporateTheme.getProperties().get("css"));
    Assert.assertEquals(7, ((List) corporateTheme.getProperties().get("css")).size());
    Assert.assertNotNull(corporateTheme.getProperties().get("javaScripts"));
    Assert.assertEquals(17, ((List) corporateTheme.getProperties().get("javaScripts")).size());
    Assert.assertNotNull(corporateTheme.getProperties().get("javaScriptLibs"));
    Assert.assertEquals(0, ((List) corporateTheme.getProperties().get("javaScriptLibs")).size());
    Assert.assertNotNull(corporateTheme.getProperties().get("templateSets"));
    Assert.assertEquals(1, ((List) corporateTheme.getProperties().get("templateSets")).size());

    Content basicTheme = capConnection.getContentRepository().getChild(basicThemePath);
    Assert.assertNotNull(basicTheme);
    Assert.assertNotNull(basicTheme.getProperties().get("css"));
    Assert.assertEquals(11, ((List) basicTheme.getProperties().get("css")).size());
    Assert.assertNotNull(basicTheme.getProperties().get("javaScripts"));
    Assert.assertEquals(20, ((List) basicTheme.getProperties().get("javaScripts")).size());

    Content jquery = capConnection.getContentRepository().getChild("/Themes/basic/vendor/jquery.js");
    Assert.assertEquals(1, jquery.getInt("disableCompress"));
    Assert.assertEquals("lte IE 9", jquery.getString("ieExpression"));
    Content normalize = capConnection.getContentRepository().getChild("/Themes/basic/vendor/normalize.css");
    Assert.assertEquals(1, normalize.getInt("disableCompress"));
    Assert.assertEquals("", normalize.getString("ieExpression"));
    Content externalNormalize = capConnection.getContentRepository().getChild("/Themes/basic/external/normalize.css");
    Assert.assertEquals("https://cdnjs.cloudflare.com/ajax/libs/normalize/4.1.1/normalize.css", externalNormalize.getString("dataUrl"));
    Content bootstrapMin = capConnection.getContentRepository().getChild("/Themes/corporate/js/bootstrap.min.js");
    Assert.assertEquals("Minified files should automatically have the flag disabledCompress set", 1, bootstrapMin.getInt("disableCompress"));
    Content minJs = capConnection.getContentRepository().getChild("/Themes/perfectchef/vendor/jquery.elevateZoom.min.js");
    Assert.assertEquals(1, minJs.getInt("disableCompress"));
    Content perfectChefTheme = capConnection.getContentRepository().getChild(perfectChefThemePath);
    Assert.assertEquals(1, perfectChefTheme.getLinks("resourceBundles").size());
  }

}