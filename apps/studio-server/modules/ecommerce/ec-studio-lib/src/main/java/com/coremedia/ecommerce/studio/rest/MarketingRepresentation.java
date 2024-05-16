package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.ChildRepresentation;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Marketing representation for JSON.
 */
public class MarketingRepresentation extends AbstractCatalogRepresentation {

  private List<MarketingSpot> marketingSpots = Collections.emptyList();

  public List<MarketingSpot> getMarketingSpots() {
    return marketingSpots;
  }

  public void setMarketingSpots(List<MarketingSpot> marketingSpots) {
    this.marketingSpots = RepresentationHelper.sort(marketingSpots);
  }

  public List<ChildRepresentation> getChildrenData() {
    List<ChildRepresentation> result = new ArrayList<>();
    for (MarketingSpot child : marketingSpots) {
      ChildRepresentation childRepresentation = new ChildRepresentation();
      childRepresentation.setChild(child);
      childRepresentation.setDisplayName(child.getExternalId());
      result.add(childRepresentation);
    }
    return RepresentationHelper.sortChildren(result);
  }

}
