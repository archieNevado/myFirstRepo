package com.coremedia.blueprint.cae.services.validation;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.services.validation.Validator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ValidationServiceImplTest {
  private ValidationServiceImpl<CMLinkable> validationService;

  private final List<CMLinkable> sourceList = Collections.emptyList();

  @Before
  public void setUp() throws Exception {
    validationService = new ValidationServiceImpl<>();
  }

  @Test
  public void validateNoValidators() {
    validationService.setValidators(Collections.<Validator<CMLinkable>>emptyList());
    assertFalse(validationService.validate(null));
  }

  @Test
  public void validateMultipleValidatorsAllValid() {
    List<Validator<CMLinkable>> validators = new ArrayList<>();
    validators.add(mockValidator(true));
    validators.add(mockValidator(true));
    validators.add(mockValidator(true));
    validators.add(mockValidator(true));

    validationService.setValidators(validators);
    assertFalse(validationService.validate(null));
  }

  @Test
  public void validateMultipleValidatorsFailEarly() {
    List<Validator<CMLinkable>> validators = new ArrayList<>();
    validators.add(mockValidator(true));
    validators.add(mockValidator(false));
    validators.add(mockValidator(true));
    validators.add(mockValidator(false));

    validationService.setValidators(validators);
    assertFalse(validationService.validate(null));
    for (int i=2; i<validators.size(); i++) {
      verify(validators.get(i), never()).validate(any(CMLinkable.class));
    }
  }

  @Test
  public void filterListNoValidators() {
    validationService.setValidators(Collections.<Validator<CMLinkable>>emptyList());
    assertEquals(sourceList, validationService.filterList(sourceList));
  }

  @Test
  public void filterListMultipleValidators() {
    List<Validator<CMLinkable>> validators = new ArrayList<>();
    validators.add(mockValidator(true));
    validators.add(mockValidator(false));
    validators.add(mockValidator(true));
    validators.add(mockValidator(false));

    validationService.setValidators(validators);
    assertEquals(sourceList, validationService.filterList(sourceList));
    for (Validator validator : validators) {
      verify(validator, times(1)).filterList(sourceList);
    }
  }

  private Validator mockValidator(boolean expectedValidateResult) {
    Validator validator = mock(Validator.class);
    when(validator.validate(null)).thenReturn(expectedValidateResult);
    when(validator.filterList(sourceList)).thenReturn(sourceList);

    return validator;
  }
}
