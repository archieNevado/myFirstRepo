import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import StringUtil from "@coremedia/studio-client.client-core/util/StringUtil";
import MessageBoxUtilInternal from "@coremedia/studio-client.ext.ui-components/messagebox/MessageBoxUtilInternal";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import DeleteSavedSearchActionBase from "@coremedia/studio-client.main.editor-components/sdk/actions/DeleteSavedSearchActionBase";
import CollectionViewStateInterceptor from "@coremedia/studio-client.main.editor-components/sdk/desktop/CollectionViewStateInterceptor";
import SavedSearchModel from "@coremedia/studio-client.main.editor-components/sdk/desktop/SavedSearchModel";
import { is, mixin } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import ECommerceStudioPlugin_properties from "../ECommerceStudioPlugin_properties";

/**
 * Checks if the content folder of the saved search is still valid.
 */
class CommerceCategoryCollectionViewStateInterceptor implements CollectionViewStateInterceptor {
  constructor() {
  }

  intercept(state: SavedSearchModel, callback: AnyFunction): void {
    if (!this.#isApplicable(state)) {
      callback(state);
      return;
    }

    const name = state.getName();
    const delTitle = ECommerceStudioPlugin_properties.saveSearch_invalidCategory_title;
    let delMsg = ECommerceStudioPlugin_properties.saveSearch_invalidCategory_text;
    delMsg = StringUtil.format(delMsg, name);

    const delButtons: Record<string, any> = {
      yes: ECommerceStudioPlugin_properties.saveSearch_invalidCategory_delete_btn_text,
      cancel: Editor_properties.dialog_defaultCancelButton_text,
    };
    MessageBoxUtilInternal.show(delTitle, delMsg, null, delButtons, this.#getDeleteSavedSearchCallback(state, callback));
  }

  #isApplicable(state: SavedSearchModel): boolean {
    const folder: RemoteBean = state.getFolder();
    const name = state.getName();
    return name && folder && is(folder, Category) && !folder.getState().exists;
  }

  #getDeleteSavedSearchCallback(state: SavedSearchModel, callback: AnyFunction): AnyFunction {
    return (btn: string): void => {
      if (btn === "yes") {
        const name = state.getName();
        DeleteSavedSearchActionBase.deleteSearch(name);
      }
    };
  }
}
mixin(CommerceCategoryCollectionViewStateInterceptor, CollectionViewStateInterceptor);

export default CommerceCategoryCollectionViewStateInterceptor;
