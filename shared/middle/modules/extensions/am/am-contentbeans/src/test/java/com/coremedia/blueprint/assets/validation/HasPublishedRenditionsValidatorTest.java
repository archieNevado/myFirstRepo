package com.coremedia.blueprint.assets.validation;

import com.coremedia.blueprint.assets.contentbeans.AMAsset;
import com.coremedia.blueprint.assets.contentbeans.AMAssetRendition;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@ActiveProfiles("HasPublishedRenditionsValidatorTest")
@RunWith(MockitoJUnitRunner.class)
public class HasPublishedRenditionsValidatorTest {

  private DeliveryConfigurationProperties deliveryConfigurationProperties;

  private HasPublishedRenditionsValidator validator;

  @Mock
  private AMAsset asset;

  @Mock
  private AMAssetRendition assetRendition;

  @Before
  public void setup() {
    deliveryConfigurationProperties = new DeliveryConfigurationProperties();
    deliveryConfigurationProperties.setPreviewMode(false);

    validator = new HasPublishedRenditionsValidator();
    validator.setDeliveryConfigurationProperties(deliveryConfigurationProperties);
  }

  @Test
  public void testValidate() {
    when(asset.getPublishedRenditions()).thenReturn(Collections.<AMAssetRendition>emptyList());
    boolean result = validator.validate(asset);
    assertFalse("Asset without published renditions should be invalid.", result);

    when(asset.getPublishedRenditions()).thenReturn(Collections.singletonList(assetRendition));
    result = validator.validate(asset);
    assertTrue("Asset with published renditions should be valid.", result);
  }

  @Test
  public void testValidateWithPreview() {
    deliveryConfigurationProperties.setPreviewMode(true);

    when(asset.getRenditions()).thenReturn(Collections.<AMAssetRendition>emptyList());
    boolean result = validator.validate(asset);
    assertFalse("Asset without published renditions should be invalid.", result);

    when(asset.getRenditions()).thenReturn(Collections.singletonList(assetRendition));
    result = validator.validate(asset);
    assertTrue("Asset with published renditions should be valid.", result);
  }
}
