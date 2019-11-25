package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document that describes a single locale.
 */
public class LocaleDocument extends AbstractOCDocument {

  /**
   * The country/region code for this Locale,
   * which will either be the empty string or an uppercase ISO 3166 2-letter code.
   */
  @JsonProperty("country")
  private String country;

  /**
   * Flag that is <code>true</code>, if the locale is the default one to use if an explicit locale is not specified.
   */
  @JsonProperty("default")
  private boolean isDefault;

  /**
   * The name for the Locale's country that is appropriate for display to the user,
   * or an empty string if no country has been specified for the Locale.
   * The display country is returned in the language defined for this locale,
   * and not in the language of the session locale.
   */
  @JsonProperty("display_country")
  private String displayCountry;

  /**
   * The name for the Locale's language that is appropriate for display to the user,
   * or an empty string if no language has been specified for the Locale.
   * The display language is returned in the language defined for this locale,
   * and not in the language of the session locale.
   */
  @JsonProperty("display_language")
  private String displayLanguage;

  /**
   * The name for the Locale that is appropriate for display to the user,
   * or an empty string if no display name has been specified for the Locale.
   * The display name is returned in the language defined for this locale, and not in the language of the session locale.
   */
  @JsonProperty("display_name")
  private String displayName;

  /**
   * The identifier of the Locale.
   * Contains a combination of the language and the country key, concatenated by "-", e.g. "en-US".
   * This attribute is the primary key of the class.
   */
  @JsonProperty("id")
  private String id;

  /**
   * The three-letter abbreviation for this Locale's country,
   * or an empty string if no country has been specified for the Locale.
   */
  @JsonProperty("iso3_country")
  private String iso3Country;

  /**
   * The three-letter abbreviation for this Locale's language,
   * or an empty string if no language has been specified for the Locale.
   */
  @JsonProperty("iso3_language")
  private String iso3Language;

  /**
   * The language code for this Locale,
   * which will either be the empty string or a lowercase ISO 639 code.
   */
  @JsonProperty("language")
  private String language;

  /**
   * The display name of the Locale.
   * This uses the current request locale to localize the value.
   */
  @JsonProperty("name")
  private String name;


  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean aDefault) {
    isDefault = aDefault;
  }

  public String getDisplayCountry() {
    return displayCountry;
  }

  public void setDisplayCountry(String displayCountry) {
    this.displayCountry = displayCountry;
  }

  public String getDisplayLanguage() {
    return displayLanguage;
  }

  public void setDisplayLanguage(String displayLanguage) {
    this.displayLanguage = displayLanguage;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getIso3Country() {
    return iso3Country;
  }

  public void setIso3Country(String iso3Country) {
    this.iso3Country = iso3Country;
  }

  public String getIso3Language() {
    return iso3Language;
  }

  public void setIso3Language(String iso3Language) {
    this.iso3Language = iso3Language;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
