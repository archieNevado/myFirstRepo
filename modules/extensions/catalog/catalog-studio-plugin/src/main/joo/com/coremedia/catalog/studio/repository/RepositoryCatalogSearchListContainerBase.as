package com.coremedia.catalog.studio.repository {

import com.coremedia.blueprint.studio.config.catalog.repositoryCatalogSearchListContainer;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.collectionview.SortableSwitchingContainer;
import com.coremedia.cms.editor.sdk.config.collectionView;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class RepositoryCatalogSearchListContainerBase extends SortableSwitchingContainer {

  public function RepositoryCatalogSearchListContainerBase(config:repositoryCatalogSearchListContainer = null) {
    super(config);
  }

  internal function getActiveViewExpression():ValueExpression {
    var collectionViewModelExpression:ValueExpression = ComponentContextManager.getInstance().getContextExpression(this, collectionView.MODEL_VARIABLE_NAME);
    return ValueExpressionFactory.createFromFunction(function():String {
      var model:CollectionViewModel = collectionViewModelExpression.getValue();
      return model ? model.getMainStateBean().get(CollectionViewModel.VIEW_PROPERTY):undefined;
    });
  }
}
}
