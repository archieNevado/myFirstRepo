package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupUtil;
import com.google.common.annotations.VisibleForTesting;

import java.util.Collection;
import java.util.List;

import static com.coremedia.util.StringUtil.isEmpty;

/**
 *Validate that at least one property from a list of a documents properties is not empty.
 *
 */
public class AtLeastOneNotEmptyValidator  extends ContentTypeValidatorBase{
  static final String AT_LEAST_ONE_NOT_EMPTY_CODE = "atLeastOneNotEmpty";
  static final String EXACTLY_ONE_SET_CODE = "exactlyOneMustBeSet";
  
  private List<String> properties;
  private String showIssueForProperty;
  private boolean exactlyOneMustBeSet;

  @Override
  public void validate(Content content, Issues issues) {
    if (isEmpty(showIssueForProperty)) {
      showIssueForProperty = properties.get(0);
    }

    boolean oneValueSet = false;
    for (String property : properties) {
      Object propertyValue = content.getProperties().get(property);
      if (!isEmptyProperty(propertyValue)) {
        if (exactlyOneMustBeSet) {
          if (oneValueSet) {
            warn(issues);
            return;
          }
          oneValueSet = true;
        } else {
          return;
        }
      }
    }
    if (!oneValueSet) {
      error(issues);
    }
  }

  private void error(Issues issues){
    issues.addIssue(Severity.ERROR, showIssueForProperty, AT_LEAST_ONE_NOT_EMPTY_CODE);
  }

  private void warn(Issues issues){
    issues.addIssue(Severity.WARN, showIssueForProperty, EXACTLY_ONE_SET_CODE);
  }

  private boolean isEmptyProperty(Object propertyValue){
    if(propertyValue == null) {
      return true;
    }
    if (propertyValue instanceof Collection && !((Collection) propertyValue).isEmpty()) {
      return false;
    }

    if (propertyValue instanceof String && !((String) propertyValue).isEmpty()) {
      return false;
    }

    if (propertyValue instanceof Markup && !isEmptyRichtext((Markup) propertyValue)) {
      return false;
    }

    if (propertyValue instanceof Blob && ((Blob) propertyValue).getSize() != 0) {
      return false;
    }
    return true;
  }

  @VisibleForTesting
  boolean isEmptyRichtext(Markup propertyValue) {
    return MarkupUtil.isEmptyRichtext(propertyValue, true);
  }

  /**
   * @param properties the list of properties to validate that at least one property is not empty
   */
  public void setProperties(List<String> properties) {
    this.properties = properties;
  }

  /**
   * @param showIssueForProperty the property the issue should be associated to, if not set the default is the first property of the properties list
   */
  public void setShowIssueForProperty(String showIssueForProperty) {
    this.showIssueForProperty = showIssueForProperty;
  }

  public void setExactlyOneMustBeSet(boolean exactlyOneMustBeSet) {
    this.exactlyOneMustBeSet = exactlyOneMustBeSet;
  }
}
