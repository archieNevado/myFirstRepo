package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.ChildRepresentation;
import com.coremedia.ecommerce.studio.rest.model.Contracts;
import com.coremedia.ecommerce.studio.rest.model.Marketing;
import com.coremedia.ecommerce.studio.rest.model.Segments;
import com.coremedia.ecommerce.studio.rest.model.Workspaces;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.linking.RemoteBeanLink;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Store representation for JSON.
 */
public class StoreRepresentation extends AbstractCatalogRepresentation {

  private StoreContext context;

  private String vendorVersion;
  private String vendorUrl;
  private String vendorName;

  private boolean marketingEnabled = false;
  private RemoteBeanLink rootCategory;

  public void setContext(StoreContext context) {
    this.context = context;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getName() {
    return context.getStoreName();
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getStoreId() {
    return context.getStoreId();
  }

  // The entries correspond to those of #getChildrenByName()
  public List<Object> getTopLevel() {
    List<Object> topLevel = new ArrayList<>();
    if (isMarketingEnabled()) {
      topLevel.add(getMarketing());
    }
    topLevel.add(rootCategory);
    return topLevel;
  }

  // The entries correspond to those of #getTopLevel()
  public Map<String, ChildRepresentation> getChildrenByName() {
    Map<String, ChildRepresentation> result = new LinkedHashMap<>();
    if (isMarketingEnabled()) {
      result.put("store-marketing", new ChildRepresentation("store-marketing", getMarketing()));
    }
    result.put("root-category", new ChildRepresentation("root-category", rootCategory));
    return result;
  }

  public Marketing getMarketing() {
    return new Marketing(context);
  }

  public Segments getSegments() {
    return new Segments(context);
  }

  public Contracts getContracts() {
    return new Contracts(context);
  }

  public Workspaces getWorkspaces() {
    return new Workspaces(context);
  }

  public String getVendorUrl() {
    return vendorUrl;
  }

  public void setVendorUrl(String vendorUrl) {
    this.vendorUrl = vendorUrl;
  }

  public String getVendorVersion() {
    return vendorVersion;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public void setVendorVersion(String vendorVersion) {
    this.vendorVersion = vendorVersion;
  }

  public boolean isMarketingEnabled() {
    return marketingEnabled;
  }

  public void setMarketingEnabled(boolean marketingEnabled) {
    this.marketingEnabled = marketingEnabled;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Map<String, String> getWcsTimeZone() {
    return (Map<String, String>) context.get("wcsTimeZone");
  }

  public void setRootCategory(RemoteBeanLink rootCategory) {
    this.rootCategory = rootCategory;
  }

  public RemoteBeanLink getRootCategory() {
    return rootCategory;
  }
}
