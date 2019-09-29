package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class CustomerDocument extends AbstractOCDocument {

  @JsonProperty("addresses")
  private List<AddressDocument> addresses;

  @JsonProperty("auth_type")
  private String authType;

  @JsonProperty("creation_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SHOP_API_DATE_PATTERN)
  private Date creationDate;

  @JsonProperty("customer_id")
  private String customerId;

  @JsonProperty("customer_no")
  private String customerNo;

  @JsonProperty("email")
  private String email;

  @JsonProperty("gender")
  private int gender;

  @JsonProperty("state_code")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  public String getAuthType() {
    return authType;
  }

  public void setAuthType(String authType) {
    this.authType = authType;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getCustomerNo() {
    return customerNo;
  }

  public void setCustomerNo(String customerNo) {
    this.customerNo = customerNo;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
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

  public List<AddressDocument> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<AddressDocument> addresses) {
    this.addresses = addresses;
  }
}
