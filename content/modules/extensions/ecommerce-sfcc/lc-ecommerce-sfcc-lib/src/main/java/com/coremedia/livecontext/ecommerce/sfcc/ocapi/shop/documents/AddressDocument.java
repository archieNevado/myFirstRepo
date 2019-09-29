package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class AddressDocument extends AbstractOCDocument {
  @JsonProperty("alt")
  private String address1;

  @JsonProperty("address1")
  private String addressId;

  @JsonProperty("city")
  private String city;

  @JsonProperty("country_code")
  private String countryCode;

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("postal_code")
  private String postalCode;

  @JsonProperty("salutation")
  private String salutation;

  @JsonProperty("state_code")
  private String stateCode;

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public String getAddressId() {
    return addressId;
  }

  public void setAddressId(String addressId) {
    this.addressId = addressId;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getSalutation() {
    return salutation;
  }

  public void setSalutation(String salutation) {
    this.salutation = salutation;
  }

  public String getStateCode() {
    return stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }
}
