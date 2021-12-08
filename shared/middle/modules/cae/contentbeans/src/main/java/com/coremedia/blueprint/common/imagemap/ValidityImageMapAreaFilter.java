package com.coremedia.blueprint.common.imagemap;

import com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions;
import com.coremedia.blueprint.common.contentbeans.CMImageMap;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBean;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The ValidityImageMapAreaFilter class checks, if linked contents are valid.
 */
public class ValidityImageMapAreaFilter implements ImageMapAreaFilterable {

  private ValidationService validationService;

  @Override
  public List<Map<String, Object>> filter(List<Map<String, Object>> areas, CMImageMap imageMap) {

    return areas.stream().filter(map -> {
      if (map == null) {
        return false;
      }
      Object linkedContent = map.get(ImageFunctions.LINKED_CONTENT);
      if (linkedContent == null || !validationService.validate(linkedContent)) {
        return false;
      } else if (linkedContent instanceof ContentBean) {
        ContentBean cb = (ContentBean) linkedContent;
        Content c = cb.getContent();
        return c != null && c.isInProduction();
      }
      // should not happen
      return true;
    }).collect(Collectors.toList());
  }

  @Required
  public void setValidationService(ValidationService validationService) {
    this.validationService = validationService;
  }
}
