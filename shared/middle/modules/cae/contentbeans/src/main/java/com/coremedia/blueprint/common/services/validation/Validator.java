package com.coremedia.blueprint.common.services.validation;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;

/**
 * The interface for all content validators. A validator is used to check if a content item is valid.
 */
public interface Validator<S> {

  /**
   * Can this {@link Validator} validate instances of the supplied <code>clazz</code>?
   * <p>This method is <i>typically</i> implemented like so:
   * <pre class="code">return Foo.class.isAssignableFrom(clazz);</pre>
   * (Where <code>Foo</code> is the class (or superclass) of the actual
   * object instance that is to be validated.)
   *
   * @param clazz the {@link Class} that this {@link Validator} is
   *              being asked if it can validate
   * @return <code>true</code> if this {@link Validator} can indeed validate instances of the
   *         supplied <code>clazz</code>
   */
  boolean supports(@NonNull Class<?> clazz);

  /**
   * @param source The objects to be filtered
   * @return the filtered objects
   */
  @NonNull
  <R extends S> List<R> filterList(@NonNull List<R> source);

  /**
   * @param source A single object to be tested
   * @return true if valid, false otherwise. <code>null</code> is considered to be invalid.
   */
  boolean validate(@Nullable S source);
}
