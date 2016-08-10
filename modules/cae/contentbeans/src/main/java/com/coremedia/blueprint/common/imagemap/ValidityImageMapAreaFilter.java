package com.coremedia.blueprint.common.imagemap;

import com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions;
import com.coremedia.blueprint.common.contentbeans.CMImageMap;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * The ValidityImageMapAreaFilter class checks, if linked contents are valid.
 */
public class ValidityImageMapAreaFilter implements ImageMapAreaFilterable {

  private ValidationService validationService;

  @Override
  public List<Map<String, Object>> filter(List<Map<String, Object>> areas, CMImageMap imageMap) {

    Iterable<Map<String, Object>> filteredAreas = Iterables.filter(areas, new Predicate<Map<String, Object>>() {
      @Override
      public boolean apply(@Nullable Map<String, Object> map) {
        if (map == null) {
          return false;
        }
        Object linkedContent = map.get(ImageFunctions.LINKED_CONTENT);
        return validationService.validate(linkedContent);
      }
    });
    return Lists.newArrayList(filteredAreas);
  }

  @Required
  public void setValidationService(ValidationService validationService) {
    this.validationService = validationService;
  }
}
