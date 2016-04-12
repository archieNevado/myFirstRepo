package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 * Validates if the uploaded file is an archive.
 */
public class ArchiveValidator extends ContentTypeValidatorBase {
  private final static Logger LOG = LoggerFactory.getLogger(ArchiveValidator.class);
  private String propertyName;

  @Override
  public void validate(Content content, Issues issues) {
    Blob blob = content.getBlob(propertyName);
    if (blob != null) {
      String subType = blob.getContentType().getSubType();
      try {
        String zip = new MimeType("application", "zip").getSubType();
        String jar = new MimeType("application", "java-archive").getSubType();

        if (!subType.contentEquals(zip) && !subType.contentEquals(jar)) {
          issues.addIssue(Severity.ERROR, propertyName, propertyName + "_not_archive_file");
        }
      } catch (MimeTypeParseException e) {
        LOG.error("Failed to create MimeType for ArchiveValidator " + e.getMessage());
      }
    }
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }
}