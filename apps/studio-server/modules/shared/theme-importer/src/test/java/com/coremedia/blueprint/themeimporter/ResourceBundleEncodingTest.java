package com.coremedia.blueprint.themeimporter;

import com.coremedia.mimetype.MimeTypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import javax.activation.MimeTypeParseException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceBundleEncodingTest {
  @Mock
  MimeTypeService mimeTypeService;

  @Test
  public void testUTF8Encoded() throws IOException, MimeTypeParseException, SAXException, ParserConfigurationException {
    when(mimeTypeService.getMimeTypeForResourceName("encoding/l10n/Bundle_de.properties")).thenReturn("text/x-java-properties");
    when(mimeTypeService.getMimeTypeForResourceName("THEME-METADATA/encoding-theme.xml")).thenReturn("text/xml");

    ImportData testling = new ImportData(mimeTypeService, null);
    try (InputStream is = getClass().getResource("./encoding-theme.zip").openStream()) {
      testling.collectFilesToImport(is);
      assertEquals("utf8_test=äöüÄÖÜß€\n", testling.resourceBundles.get("encoding/l10n/Bundle_de.properties"));
    }
  }

  @Test
  public void testDefaultEncoded() throws IOException, MimeTypeParseException, SAXException, ParserConfigurationException {
    when(mimeTypeService.getMimeTypeForResourceName("noencoding/l10n/Bundle_de.properties")).thenReturn("text/x-java-properties");
    when(mimeTypeService.getMimeTypeForResourceName("THEME-METADATA/noencoding-theme.xml")).thenReturn("text/xml");

    ImportData testling = new ImportData(mimeTypeService, null);
    try (InputStream is = getClass().getResource("./noencoding-theme.zip").openStream()) {
      testling.collectFilesToImport(is);
      assertEquals("latin1_test=äöüÄÖÜß\n", testling.resourceBundles.get("noencoding/l10n/Bundle_de.properties"));
    }
  }
}
