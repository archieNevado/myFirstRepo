package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.cap.common.Blob;
import com.coremedia.objectserver.beans.ContentBean;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.activation.MimeType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@DefaultAnnotation(NonNull.class)
public class BlobFromContentBeanSetting implements Blob {
  private final ContentBean contentBean;
  private final String settingsName;
  private final Blob blob;

  public BlobFromContentBeanSetting(ContentBean contentBean, String settingsName, Blob blob) {
    this.contentBean = contentBean;
    this.blob = blob;
    this.settingsName = settingsName;
  }

  public ContentBean getContentBean() {
    return contentBean;
  }

  public Blob getBlob() {
    return blob;
  }

  public String getSettingsName() {
    return settingsName;
  }

  @Override
  public MimeType getContentType() {
    return blob.getContentType();
  }

  @Override
  public int getSize() {
    return blob.getSize();
  }

  @Override
  public String getETag() {
    return blob.getETag();
  }

  @Override
  public void writeOn(OutputStream out) throws IOException {
      blob.writeOn(out);
  }

  @Override
  public InputStream getInputStream() {
    return blob.getInputStream();
  }

  @Override
  public byte[] asBytes() {
    return new byte[0];
  }

  @Override
  public void dispose() {

  }
}
