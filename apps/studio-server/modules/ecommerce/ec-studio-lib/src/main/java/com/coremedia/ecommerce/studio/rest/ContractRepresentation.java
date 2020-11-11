package com.coremedia.ecommerce.studio.rest;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Segment representation for JSON.
 *
 * @deprecated This class is part of the commerce integration "b2b support" that is not
 * supported by the Commerce Hub architecture. It will be removed or changed in the future.
 */
@Deprecated
public class ContractRepresentation extends AbstractCatalogRepresentation {

  private String name;
  private String externalId;
  private String externalTechId;

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getName() {
    return name;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getExternalId() {
    return externalId;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getExternalTechId(){
    return externalTechId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public void setExternalTechId(String externalTechId) {
    this.externalTechId = externalTechId;
  }
}
