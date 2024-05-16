package com.coremedia.blueprint.localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaxonomyLocalizationSettings {
  private Locale defaultLocale = Locale.getDefault();
  private List<Locale> supportedLocales = new ArrayList<>();

  public Locale getDefaultLocale() {
    return defaultLocale;
  }

  public void setDefaultLocale(Locale defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  public List<Locale> getSupportedLocales() {
    return supportedLocales;
  }

  public void setSupportedLocales(List<Locale> supportedLocales) {
    this.supportedLocales = supportedLocales;
  }
}
