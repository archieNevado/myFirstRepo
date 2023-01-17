package com.coremedia.blueprint.cae.services.validation;


import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.blueprint.common.services.validation.Validator;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

public class ValidationServiceImpl<T> implements ValidationService<T> {
  private List<Validator<T>> validators = new ArrayList<>();

  /**
   * Setter for {@link #validators}
   *
   * @param validators the list of validators to use
   */
  @Required
  public void setValidators(List<Validator<T>> validators) {
    this.validators = validators;
  }

  @NonNull
  @Override
  public List<? extends T> filterList(@NonNull List<? extends T> source) {
    List<? extends T> filteredList = source;
    for (Validator<T> validator : validators) {
      filteredList = validator.filterList(filteredList);
    }
    return List.copyOf(filteredList);
  }

  @Override
  public boolean validate(@Nullable T source) {
    if (source == null) {
      return false;
    }
    for (Validator<T> validator : validators) {
      if (!validator.validate(source)) {
        return false;
      }
    }
    return true;
  }

}
