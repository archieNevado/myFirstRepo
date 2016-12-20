package com.coremedia.blueprint.themeimporter;

import com.coremedia.cap.content.Content;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

class ThemeImporterResultImpl implements ThemeImporterResult {
  private Collection<Content> themeDescriptors;


  // --- ThemeImporterResult ----------------------------------------

  @Nonnull
  @Override
  public Collection<Content> getThemeDescriptors() {
    return themeDescriptors!=null ? themeDescriptors : Collections.emptySet();
  }


  // --- create and initialize --------------------------------------

  void setThemeDescriptors(Collection<Content> themeDescriptors) {
    this.themeDescriptors = themeDescriptors;
  }
}
