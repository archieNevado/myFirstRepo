package com.coremedia.blueprint.common.services.validation;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.invoke.MethodHandles.lookup;

public abstract class AbstractValidator<T> implements Validator<T> {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  /**
   * @param source The objects to be filtered
   * @return the filtered objects
   */
  @NonNull
  @Override
  public <R extends T> List<R> filterList(@NonNull List<R> source) {
    return internallyFilterList(source);
  }

  /**
   * @param source A single object to be tested
   * @return true if valid, false otherwise. <code>null</code> is considered to be invalid.
   */
  @Override
  public boolean validate(@Nullable T source) {
    if (source == null) {
      return false;
    }
    return !filterList(List.of(source)).isEmpty();
  }

  protected abstract Predicate<T> createPredicate();

  protected void addCustomDependencies(List<? extends T> result) {
    LOG.debug("The default implementation is not adding any dependencies");
  }

  /**
   * internal method which will be called by any implementing validation service
   *
   * @param allItems the items to be filtered
   * @return the filtered objects
   */
  private <R extends T> List<R> internallyFilterList(@NonNull List<R> allItems) {
    LOG.debug("Before filtering the list contained {} items ({}).", allItems.size(), allItems);

    Predicate<T> predicate = createPredicate();
    List<R> validItems = allItems.stream()
            .filter(Objects::nonNull)
            .filter(item -> filterIfSupported(item, predicate))
            .collect(Collectors.toList());

    LOG.debug("Afterwards {} items ({}).", validItems.size(), validItems);
    addCustomDependencies(allItems);
    return validItems;
  }

  private boolean filterIfSupported(T item, Predicate<T> predicate) {
    try {
      return !supports(item.getClass()) || predicate.test(item);
    } catch (Exception e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Caught exception while validating item '{}'.", item, e);
      } else {
        LOG.info("Caught exception while validating item '{}': {} (stacktrace on DEBUG level). ", item, e.getMessage());
      }
      return false;
    }
  }
}
