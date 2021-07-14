package com.coremedia.ecommerce.studio.library {
import com.coremedia.cms.editor.sdk.actions.DeleteSavedSearchActionBase;
import com.coremedia.cms.editor.sdk.desktop.*;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.messagebox.MessageBoxUtilInternal;
import com.coremedia.ui.util.StringUtil;

import mx.resources.ResourceManager;

/**
 * Checks if the content folder of the saved search is still valid.
 */
public class CommerceCategoryCollectionViewStateInterceptor implements CollectionViewStateInterceptor {
  public function CommerceCategoryCollectionViewStateInterceptor() {
  }

  public function intercept(state:SavedSearchModel, callback:Function):void {
    if (!isApplicable(state)) {
      callback(state);
      return;
    }

    var name:String = state.getName();
    var delTitle:String = ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'saveSearch_invalidCategory_title');
    var delMsg:String = ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'saveSearch_invalidCategory_text');
    delMsg = StringUtil.format(delMsg, name);

    var delButtons:Object = {
      yes: ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'saveSearch_invalidCategory_delete_btn_text'),
      cancel: ResourceManager.getInstance().getString('com.coremedia.cms.editor.Editor', 'dialog_defaultCancelButton_text')
    };
    MessageBoxUtilInternal.show(delTitle, delMsg, null, delButtons, getDeleteSavedSearchCallback(state, callback));
  }

  private function isApplicable(state:SavedSearchModel):Boolean {
    var folder:RemoteBean = state.getFolder();
    var name:String = state.getName();
    return name && folder && folder is Category && !folder.getState().exists;
  }

  private function getDeleteSavedSearchCallback(state:SavedSearchModel, callback:Function):Function {
    return function (btn:String):void {
      if (btn === 'yes') {
        var name:String = state.getName();
        DeleteSavedSearchActionBase.deleteSearch(name);
      }
    }
  }
}
}
