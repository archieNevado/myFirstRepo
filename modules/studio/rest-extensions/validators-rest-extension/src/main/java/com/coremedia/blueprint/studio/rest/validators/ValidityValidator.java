package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.GregorianCalendar;

/**
 * Validates if the ValidTo date is after the ValidFrom date.
 */

public class ValidityValidator extends ContentTypeValidatorBase {
  private String propertyValidFrom;
  private String propertyValidTo;

  @Override
  public void validate(Content content, Issues issues) {
    Object dateFrom = content.getProperties().get(propertyValidFrom);
    Object dateTo = content.getProperties().get(propertyValidTo);

    if (dateFrom != null && dateTo != null) {
      if (((GregorianCalendar) dateFrom).getTimeInMillis() > ((GregorianCalendar) dateTo).getTimeInMillis()) {
        issues.addIssue(Severity.ERROR, propertyValidFrom, propertyValidFrom + "_is_after_validTo");
      }
    }
  }

  public void setPropertyValidFrom(String propertyValidFrom) {
    this.propertyValidFrom = propertyValidFrom;
  }

  public void setPropertyValidTo(String propertyValidTo) {
    this.propertyValidTo = propertyValidTo;
  }
}