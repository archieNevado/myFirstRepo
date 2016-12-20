package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.cap.common.Blob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ThemeUploadInterceptorTest {
  @Mock
  private Blob blob;


  // --- Tests ------------------------------------------------------

  @Test
  public void testIsZip() {
    initBlob("application/zip", null);
    assertTrue(ThemeUploadInterceptor.isZip(blob));
  }

  @Test
  public void testIsNoZip() {
    initBlob("image/png", null);
    assertFalse(ThemeUploadInterceptor.isZip(blob));
  }

  @Test
  public void testIsTheme() {
    URL theme = ThemeUploadInterceptorTest.class.getClassLoader().getResource("com/coremedia/blueprint/studio/rest/intercept/theme.zip");
    try (InputStream is = theme.openStream()) {
      initBlob("application/zip", is);
      assertTrue(ThemeUploadInterceptor.isTheme(blob));
    } catch (IOException e) {
      throw new RuntimeException("Test resource hassle, not a product problem.", e);
    }
  }

  @Test
  public void testIsNoTheme() {
    URL noTheme = ThemeUploadInterceptorTest.class.getClassLoader().getResource("com/coremedia/blueprint/studio/rest/intercept/notheme.zip");
    try (InputStream is = noTheme.openStream()) {
      initBlob("application/zip", is);
      assertFalse(ThemeUploadInterceptor.isTheme(blob));
    } catch (IOException e) {
      throw new RuntimeException("Test resource hassle, not a product problem.", e);
    }
  }


  // --- internal ---------------------------------------------------

  private void initBlob(String mimetype, InputStream is) {
    try {
      when(blob.getContentType()).thenReturn(new MimeType(mimetype));
      when(blob.getInputStream()).thenReturn(is);
    } catch (MimeTypeParseException e) {
      throw new RuntimeException("Test setup error, not a product problem.");
    }
  }
}
