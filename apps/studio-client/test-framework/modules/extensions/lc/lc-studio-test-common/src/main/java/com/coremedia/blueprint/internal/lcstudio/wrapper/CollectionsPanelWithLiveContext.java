package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.ui.components.SwitchingContainer;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@Named
@Scope("prototype")
public class CollectionsPanelWithLiveContext extends SwitchingContainer {
  @FindByExtJS(xtype = CatalogRepositoryList.XTYPE, global = false)
  private CatalogRepositoryList catalogRepositoryList;

  @FindByExtJS(xtype = CatalogSearchList.XTYPE, global = false)
  private CatalogSearchList catalogSearchList;

  public CatalogRepositoryList getCatalogRepositoryList() {
    return catalogRepositoryList;
  }

  public CatalogSearchList getCatalogSearchList() {
    return catalogSearchList;
  }
}
