package com.coremedia.blueprint.cae.richtext.filter;

import com.google.common.base.MoreObjects;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Objects;

import static com.coremedia.blueprint.cae.richtext.filter.SaxAttributes.WHITESPACE_DELIM_PATTERN;
import static java.lang.String.format;

/**
 * Implementation of the configuration item for
 * {@link ReservedClassToElementFilter}.
 *
 * @since 2210.1
 */
final class ReservedClassToElementConfigImpl implements ReservedClassToElementConfig {
  /**
   * Target mapped element name in HTML.
   */
  @NonNull
  private final String elementNameHtml;
  /**
   * Original element name in CoreMedia Rich Text 1.0, the mapping applies to.
   */
  @NonNull
  private final String elementNameRichText;
  /**
   * Marker class to denote, that a given rich text element shall be mapped
   * to an alternative element in HTML.
   */
  @NonNull
  private final String markerClass;

  ReservedClassToElementConfigImpl(@NonNull String elementNameHtml, @NonNull String elementNameRichText, @NonNull String markerClass) {
    this.elementNameRichText = requireValidValue(elementNameRichText, "elementNameRichText");
    this.elementNameHtml = requireValidValue(elementNameHtml, "elementNameHtml");
    this.markerClass = requireValidValue(markerClass, "markerClass");
  }

  @NonNull
  private static String requireValidValue(String value, @NonNull String name) {
    requireNonNull(value, name);
    requireNonBlank(value, name);
    requireNoWhitespaceContained(value, name);

    return value;
  }

  private static void requireNonNull(String value, @NonNull String name) {
    Objects.requireNonNull(value, format("%s must not be null.", name));
  }

  private static void requireNonBlank(@NonNull String value, @NonNull String name) {
    if (value.isBlank()) {
      throw new IllegalArgumentException(format("%s must not be blank.", name));
    }
  }

  private static void requireNoWhitespaceContained(@NonNull String value, @NonNull String name) {
    if (WHITESPACE_DELIM_PATTERN.splitAsStream(value).count() > 1) {
      throw new IllegalArgumentException(format("%s must not contain whitespace characters.", name));
    }
  }

  @Override
  @NonNull
  public String getElementNameHtml() {
    return elementNameHtml;
  }

  @Override
  @NonNull
  public String getElementNameRichText() {
    return elementNameRichText;
  }

  @Override
  @NonNull
  public String getMarkerClass() {
    return markerClass;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservedClassToElementConfigImpl that = (ReservedClassToElementConfigImpl) o;
    return elementNameHtml.equals(that.elementNameHtml) && elementNameRichText.equals(that.elementNameRichText) && markerClass.equals(that.markerClass);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elementNameHtml, elementNameRichText, markerClass);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("elementNameHtml", elementNameHtml)
            .add("elementNameRichText", elementNameRichText)
            .add("markerClass", markerClass)
            .toString();
  }
}
