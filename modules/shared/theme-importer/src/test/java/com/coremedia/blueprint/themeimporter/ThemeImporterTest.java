package com.coremedia.blueprint.themeimporter;

import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import com.coremedia.mimetype.TikaMimeTypeService;
import org.apache.commons.io.IOUtils;
import org.junit.After;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
    TikaMimeTypeService tikaMimeTypeService = new TikaMimeTypeService();
    tikaMimeTypeService.init();
    themeImporter = new ThemeImporter(capConnection, tikaMimeTypeService, new LocalizationService(null, null, null));
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

    // The three themes have conflicting jquery.js declarations.
    // basicTheme must be the last here, because its jquery.js declaration
    // contains the disableCompress and ieExpression values asserted below.
    themeImporter.importThemes(THEMES, corporateTheme, perfectChefTheme, basicTheme);

    Content corporateTheme = capConnection.getContentRepository().getChild(corporateThemePath);
    assertNotNull(corporateTheme);
    assertNotNull(corporateTheme.getProperties().get("css"));
    assertEquals(7, ((List) corporateTheme.getProperties().get("css")).size());
    assertNotNull(corporateTheme.getProperties().get("javaScripts"));
    assertEquals(17, ((List) corporateTheme.getProperties().get("javaScripts")).size());
    assertNotNull(corporateTheme.getProperties().get("javaScriptLibs"));
    assertEquals(0, ((List) corporateTheme.getProperties().get("javaScriptLibs")).size());
    assertNotNull(corporateTheme.getProperties().get("templateSets"));
    assertEquals(1, ((List) corporateTheme.getProperties().get("templateSets")).size());

    Content basicTheme = capConnection.getContentRepository().getChild(basicThemePath);
    assertNotNull(basicTheme);
    assertNotNull(basicTheme.getProperties().get("css"));
    assertEquals(11, ((List) basicTheme.getProperties().get("css")).size());
    assertNotNull(basicTheme.getProperties().get("javaScripts"));
    assertEquals(20, ((List) basicTheme.getProperties().get("javaScripts")).size());

    // These jquery assertions are only valid if basicTheme is the last to be imported
    Content jquery = capConnection.getContentRepository().getChild("/Themes/basic/vendor/jquery.js");
    assertEquals(1, jquery.getInt("disableCompress"));
    assertEquals("lte IE 9", jquery.getString("ieExpression"));

    Content normalize = capConnection.getContentRepository().getChild("/Themes/basic/vendor/normalize.css");
    assertEquals(1, normalize.getInt("disableCompress"));
    assertEquals("", normalize.getString("ieExpression"));
    Content externalNormalize = capConnection.getContentRepository().getChild("/Themes/basic/external/normalize.css");
    assertEquals("https://cdnjs.cloudflare.com/ajax/libs/normalize/4.1.1/normalize.css", externalNormalize.getString("dataUrl"));
    Content bootstrapMin = capConnection.getContentRepository().getChild("/Themes/corporate/js/bootstrap.min.js");
    assertEquals("Minified files should automatically have the flag disabledCompress set", 1, bootstrapMin.getInt("disableCompress"));
    Content minJs = capConnection.getContentRepository().getChild("/Themes/perfectchef/vendor/jquery.elevateZoom.min.js");
    assertEquals(1, minJs.getInt("disableCompress"));
    Content perfectChefTheme = capConnection.getContentRepository().getChild(perfectChefThemePath);
    assertEquals(2, perfectChefTheme.getLinks("resourceBundles").size());

    checkOneCheckedInVersion(capConnection.getContentRepository().getChild(THEMES));
  }

  @Test
  public void testLocalePattern() {
    String patternStr = "Corporate(_.*)?\\.properties";
    Pattern pattern = Pattern.compile(patternStr);
    Matcher matcher = pattern.matcher("Corporate_en.properties");
    assertTrue(matcher.matches());
    matcher = pattern.matcher("Corporate_de_DE.properties");
    assertTrue(matcher.matches());
    matcher = pattern.matcher("Corporate_de_DE_hamburg.properties");
    assertTrue(matcher.matches());
    matcher = pattern.matcher("Corporate.properties");
    assertTrue(matcher.matches());
  }


  // --- internal ---------------------------------------------------

  private void checkOneCheckedInVersion(Content folder) {
    for (Content child : folder.getChildren()) {
      if (child.isFolder()) {
        checkOneCheckedInVersion(child);
      } else {
        assertTrue(child.isCheckedIn());
        assertEquals(1, IdHelper.parseVersionId(child.getCheckedInVersion().getId()));
      }
    }

  }
}