package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.blueprint.themeimporter.ThemeImporter;
import com.coremedia.cap.common.Blob;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import org.springframework.beans.factory.annotation.Required;

import javax.activation.MimeType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ZipUploadInterceptor extends ContentWriteInterceptorBase {
  private String dataProperty;
  private ThemeImporter themeImporter;


  @Override
  public void intercept(ContentWriteRequest request) {
    Map<String, Object> properties = request.getProperties();
    if (properties.containsKey(dataProperty)) {
      Object value = properties.get(dataProperty);
      if (value instanceof Blob && isZip(((Blob) value).getContentType())) {
        try (InputStream is = ((Blob) value).getInputStream()) {
          themeImporter.importThemes(request.getParent().getPath(), is);
        } catch (IOException e) {
          throw new RuntimeException("Error closing blob input stream", e);
        }
      }
    }
  }

  private static boolean isZip(MimeType mimeType) {
    return mimeType.toString().equals("application/zip") || mimeType.toString().equals("application/x-zip-compressed") || mimeType.toString().equals("application/x-zip");
  }


  @Required
  public void setDataProperty(String dataProperty) {
    this.dataProperty = dataProperty;
  }

  @Required
  public void setThemeImporter(ThemeImporter themeImporter) {
    this.themeImporter = themeImporter;
  }
}
