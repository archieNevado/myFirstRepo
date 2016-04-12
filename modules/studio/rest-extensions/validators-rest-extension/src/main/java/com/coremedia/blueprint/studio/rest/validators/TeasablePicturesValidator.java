package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.PredicateAwareValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.List;

/**
 * We have weakened CMTeasable#pictures from CMPicture to CMMedia, but
 * currently not all Blueprint templates support videos or spinners.
 * Therefore this validator warns about Non-Picture media items in documents
 * of some subtypes of CMTeasable.
 */
public class TeasablePicturesValidator extends PredicateAwareValidatorBase {
  private static final String PROPERTY_PICTURES = "pictures";
  private static final String TYPE_CMPICTURE = "CMPicture";
  private static final String CODE_NO_CMPICTURE = "no_cmpicture";

  @Override
  protected void doValidate(Content content, Issues issues) {
    List<Content> pictures = content.getLinks(PROPERTY_PICTURES);
    for (Content picture : pictures) {
      if (!picture.getType().isSubtypeOf(TYPE_CMPICTURE)) {
        issues.addIssue(Severity.WARN, PROPERTY_PICTURES, CODE_NO_CMPICTURE, picture);
        return;
      }
    }
  }
}
