package com.coremedia.cms.content_feeder.blueprint.validators;

import com.coremedia.cms.middle.blueprint.validators.ValidatorsAutoConfiguration;
import com.coremedia.springframework.customizer.Customize;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Customizations of Validators for the Content Feeder.
 */
@AutoConfiguration(after = ValidatorsAutoConfiguration.class)
public class ContentFeederValidatorsAutoConfiguration {

  /**
   * Do not compute image dimensions from blob data in the ImageCropSizeValidator,
   * which is a potentially expensive operation.
   * <p>
   * This means that if the crops are to be computed and
   * the image dimensions are not available in the width / height properties of the CMPicture content,
   * no issues for the crop sizes will be computed.
   */
  @Customize(mode = Customize.Mode.REPLACE, value = "imageCropSizeValidator.computeImageDimensions")
  @Bean(autowireCandidate = false)
  boolean disableImageCropSizeValidation() {
    return false;
  }
}
