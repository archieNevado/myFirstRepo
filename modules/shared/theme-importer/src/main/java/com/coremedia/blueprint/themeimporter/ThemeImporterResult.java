package com.coremedia.blueprint.themeimporter;

import com.coremedia.cap.content.Content;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Provides some results of a theme import.
 * <p>
 * May be interesting for subsequent processing in some clients.
 */
public interface ThemeImporterResult {
  /**
   * Returns the theme descriptors of the import.
   *
   * @return a collection of content, type CMTheme
   */
  @Nonnull
  Collection<Content> getThemeDescriptors();
}
