package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.blueprint.studio.rest.UploadControlAttributes;
import com.coremedia.blueprint.themeimporter.ThemeImporter;
import com.coremedia.blueprint.themeimporter.ThemeImporterResult;
import com.coremedia.cap.common.Blob;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Required;

import javax.activation.MimeType;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * If the file to be uploaded is a theme, process it with the theme importer
 * instead of simply creating a CMDownload document.
 */
public class ThemeUploadInterceptor extends ContentWriteInterceptorBase {
  private String dataProperty;
  private ThemeImporter themeImporter;


  // --- construct and configure ------------------------------------

  @Required
  public void setDataProperty(String dataProperty) {
    this.dataProperty = dataProperty;
  }

  @Required
  public void setThemeImporter(ThemeImporter themeImporter) {
    this.themeImporter = themeImporter;
  }


  // --- ContentWriteInterceptor ------------------------------------

  /**
   * Checks if the uploaded file is a theme and if so, invokes the
   * theme importer
   */
  @Override
  public void intercept(ContentWriteRequest request) {
    Blob themeBlob = fetchThemeBlob(request);
    if (themeBlob!=null) {
      try (InputStream is = themeBlob.getInputStream()) {
        ThemeImporterResult themeImporterResult = themeImporter.importThemes(request.getParent().getPath(), is);
        request.setAttribute(UploadControlAttributes.DO_NOTHING, true);
        request.setAttribute(UploadControlAttributes.UPLOADED_DOCUMENTS, themeImporterResult.getThemeDescriptors());
      } catch (IOException e) {
        throw new RuntimeException("Error closing blob input stream", e);
      }
    }
  }


  // --- internal ---------------------------------------------------

  /**
   * Extracts the theme blob from the request
   *
   * @return the theme blob, or null if there is no theme blob.
   */
  private Blob fetchThemeBlob(ContentWriteRequest request) {
    Object value = request.getProperties().get(dataProperty);
    if (value instanceof Blob) {
      Blob blob = (Blob) value;
      return isZip(blob) && isTheme(blob) ? blob : null;
    }
    return null;
  }

  /**
   * Check whether the blob is a zip.
   */
  @VisibleForTesting
  static boolean isZip(Blob blob) {
    MimeType mimeType = blob.getContentType();
    String primaryType = mimeType.getPrimaryType();
    String subType = mimeType.getSubType();
    return "application".equals(primaryType) &&
            ("zip".equals(subType) || "x-zip".equals(subType) || "x-zip-compressed".equals(subType));
  }

  /**
   * Check whether the blob is a theme.
   * <p>
   * We assume that the blob is a theme if it is a zip and it contains a
   * THEME-METADATA directory.
   * <p>
   * This is of course not bullet proof, but in the context of a generic file
   * upload we cannot be absolutely sure.
   *
   * @param zipBlob must be a zip blob
   */
  @VisibleForTesting
  static boolean isTheme(Blob zipBlob) {
    try (InputStream is = zipBlob.getInputStream();
         ZipInputStream zis = new ZipInputStream(is)) {
      for (ZipEntry entry=zis.getNextEntry(); entry!=null; entry=zis.getNextEntry()) {
        // Directories do not necessarily appear as separate zip entries, but
        // only implicitely with a longer path.  (Observed for our corporate
        // theme, same with "unzip -l" command.)  Therefore we check with
        // startsWith.
        if (entry.getName().startsWith(ThemeImporter.THEME_METADATA_DIR+"/")) {
          return true;
        }
      }
      return false;
    } catch (IOException e) {
      throw new RuntimeException("Error closing blob input stream", e);
    }
  }
}
