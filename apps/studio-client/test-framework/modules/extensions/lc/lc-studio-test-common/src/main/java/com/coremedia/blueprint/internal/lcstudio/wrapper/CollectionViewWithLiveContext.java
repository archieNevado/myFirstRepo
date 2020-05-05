package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.cms.editor.EditorDefault;
import com.coremedia.uitesting.cms.editor.components.collectionview.CollectionView;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;

@SuppressWarnings({"ConstantConditions", "OverlyComplexClass", "OverlyCoupledClass"})
@ExtJSObject(xtype = CollectionView.XTYPE)
@EditorDefault
public class CollectionViewWithLiveContext extends Container {

  @FindByExtJS(itemId = "collections-panel")
  private CollectionsPanelWithLiveContext collectionsPanel;


  public CatalogRepositoryList getCatalogRepositoryList() {
    return collectionsPanel.getCatalogRepositoryList();
  }

  public CatalogSearchList getCatalogSearchList() {
    return collectionsPanel.getCatalogSearchList();
  }
}
