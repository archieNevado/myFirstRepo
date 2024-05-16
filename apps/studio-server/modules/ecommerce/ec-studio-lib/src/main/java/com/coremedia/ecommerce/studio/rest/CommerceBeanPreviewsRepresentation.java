package com.coremedia.ecommerce.studio.rest;

import com.coremedia.service.previewurl.Preview;

import java.util.Map;

public class CommerceBeanPreviewsRepresentation {
  Map<String, Preview> previewMap;

  public Map<String, Preview> getPreviewMap() {
    return previewMap;
  }

  public void setPreviewMap(Map<String, Preview> previewMap) {
    this.previewMap = previewMap;
  }
}
