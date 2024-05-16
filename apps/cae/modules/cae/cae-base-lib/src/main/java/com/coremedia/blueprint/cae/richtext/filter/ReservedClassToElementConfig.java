package com.coremedia.blueprint.cae.richtext.filter;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Configuration for a mapping from CoreMedia RichText 1.0 element to
 * representation in HTML as denoted by a given marker class attribute.
 *
 * @since 2210.1
 */
public interface ReservedClassToElementConfig {
  /**
   * The element name to represent the given element in HTML.
   *
   * @return name of the element in HTML
   */
  @NonNull
  String getElementNameHtml();

  /**
   * The name of the element in CoreMedia RichText 1.0.
   *
   * @return name of the element in CoreMedia RichText 1.0
   */
  @NonNull
  String getElementNameRichText();

  /**
   * <p>
   * The class name stored in class attribute (one of them), which denotes
   * that the given Rich Text Element shall be mapped to the corresponding
   * HTML element. This class is considered a <em>reserved class</em>, which
   * is, that it must not be used for different purpose. Violation may
   * produce ambiguous mapping results.
   * </p>
   *
   * @return marker class value contained in class attribute value
   */
  @NonNull
  String getMarkerClass();

  /**
   * <p>
   * Factory method for providing mapping configuration for a simplified
   * use-case. The simplification assumes, that the marker class is the
   * same as the element name in HTML.
   * </p>
   * <p>
   * For example, the simplification assumes, that a mapping for
   * {@code <mark>} is represented in CoreMedia Rich Text 1.0 as
   * {@code <span class="mark">}.
   * </p>
   *
   * @param elementNameRichText element name in CoreMedia Rich Text 1.0
   * @param elementNameHtml     element name in HTML, which is also taken as
   *                            marker class
   * @return configuration
   */
  @NonNull
  static ReservedClassToElementConfig of(@NonNull String elementNameRichText, @NonNull String elementNameHtml) {
    return of(elementNameRichText, elementNameHtml, elementNameHtml);
  }

  /**
   * <p>
   * Factory method for providing mapping configuration.
   * </p>
   * <p>
   * <strong>Corner case:</strong>
   * You may also use this mapping to remove a given class from given element.
   * To do so, element names in rich text and in HTML must be identical.
   * </p>
   * <p>
   * <strong>Ambiguous mappings:</strong>
   * If for a given rich text element multiple marker classes are set, one of
   * the mappings is applied by random and all other marker classes get stripped
   * from the given element.
   * </p>
   *
   * @param elementNameRichText element name in CoreMedia Rich Text 1.0
   * @param markerClass         the class attribute value that signals mapping from
   *                            {@code elementNameRichText} to
   *                            {@code elementNameHtml}
   * @param elementNameHtml     element name in HTML
   * @return configuration
   */
  @NonNull
  static ReservedClassToElementConfig of(@NonNull String elementNameRichText, @NonNull String markerClass, @NonNull String elementNameHtml) {
    return new ReservedClassToElementConfigImpl(elementNameHtml, elementNameRichText, markerClass);
  }
}
