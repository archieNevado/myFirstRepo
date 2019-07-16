package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.google.common.base.Joiner;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A localized property holds locale-specific values.
 * In addition, a default value is store as a fallback if a requested locale-specific value does not exist.
 *
 * @param <T>
 */
public class LocalizedProperty<T> {

  private static final Joiner.MapJoiner JOINER = Joiner.on(", ").withKeyValueSeparator("=");
  private static final String DEFAULT = "default";

  private Map<String, T> values;

  /**
   * Creates a new localized property with an empty map.
   */
  public LocalizedProperty() {
    this(new HashMap<String, T>());
  }

  /**
   * Creates a new localized property with the provided map of locales and values.
   * @param values map of locale strings and values
   */
  public LocalizedProperty(Map<String, T> values) {
    this.values = values;
  }

  /**
   * Returns the default value.
   * @return default value or <code>null</code> if no default was specified
   */
  public T getDefaultValue() {
    return values.get(DEFAULT);
  }

  /**
   * Returns the value for the given locale string.
   *
   * @param localeString
   * @return
   */
  public T getValue(String localeString) {
    T value = values.get(localeString);
    if (value == null) {
      value = getDefaultValue();  // Fallback to default value
    }
    return value;
  }

  /**
   * Returns the value for the given locale.
   *
   * @param locale locale
   * @return
   */
  public T getValue(Locale locale) {
    if (locale == null) {
      return getDefaultValue();
    }
    return getValue(locale.toLanguageTag());
  }

  /**
   * Returns a map containing all locale specific values.
   * @return
   */
  public Map<String, T> getValues() {
    return values;
  }

  public void setValues(Map<String, T> values) {
    this.values = values;
  }

  public void addValue(String key, T value) {
    values.put(key, value);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + JOINER.join(getValues()) + "}";
  }
}
