import UndocContent from "@coremedia/studio-client.cap-rest-client/content/UndocContent";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import PreferenceWindow from "@coremedia/studio-client.ext.frame-components/preferences/PreferenceWindow";
import MessageBoxUtilInternal from "@coremedia/studio-client.ext.ui-components/messagebox/MessageBoxUtilInternal";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import CollectionViewConstants from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewConstants";
import CompoundChildTreeModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/tree/CompoundChildTreeModel";
import StudioPreferenceWindow from "@coremedia/studio-client.main.editor-components/sdk/desktop/StudioPreferenceWindow";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import { bind, is } from "@jangaroo/runtime";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import { AnyFunction } from "@jangaroo/runtime/types";
import ECommerceStudioPlugin_properties from "../ECommerceStudioPlugin_properties";
import catalogHelper from "../catalogHelper";
import CatalogPreferencesBase from "../components/preferences/CatalogPreferencesBase";

class ShowInLibraryHelper {

  protected static readonly RESOURCE_BUNDLE: any = resourceManager.getResourceBundle(null, ECommerceStudioPlugin_properties).content;

  static readonly #HIDE_EVENT_NAME: string = "close";

  #entities: Array<any> = null;

  #catalogTreeModel: CompoundChildTreeModel = null;

  constructor(entities: Array<any>, catalogTreeModel: CompoundChildTreeModel) {
    this.#entities = entities;
    this.#catalogTreeModel = catalogTreeModel;
  }

  showItems(treeModelId: string): void {
    if (CollectionViewConstants.TREE_MODEL_ID === treeModelId) {
      // clicked on path (see DocumentPath.exml)
      this.showInContentRepositoryTree();
    } else {
      // opened via tab menu item
      this.showInCatalogTree();
    }
  }

  showInContentRepositoryTree(): void {
    // bind 'this' so that function value expression is happy
    const self = this;
    // try to open in content repository tree first
    this.#entities.forEach((entity: any): void => {
      if (!this.tryShowInContentRepositoryTree(entity)) {
        const ve = ValueExpressionFactory.createFromFunction((entity: any): boolean =>
          self.tryShowInCatalogTree(entity)
        , entity);
        ve.loadValue((): void => {
          const canShowInCatalogTree: boolean = ve.getValue();
          if (!canShowInCatalogTree) {
            this.adjustSettings(entity, bind(this, this.showInContentRepositoryTree), ShowInLibraryHelper.RESOURCE_BUNDLE.Catalog_show_in_content_tree_fails_for_Content);
          }
        });
      }
    });
  }

  showInCatalogTree(): void {
    // bind 'this' so that function value expression is happy
    const self = this;
    this.#entities.forEach((entity: any): void => {
      // try to open in catalog tree first
      const ve = ValueExpressionFactory.createFromFunction((entity: any): boolean =>
        self.tryShowInCatalogTree(entity)
      , entity);
      ve.loadValue((): void => {
        const canShowInCatalogTree: boolean = ve.getValue();
        if (!canShowInCatalogTree && !this.tryShowInContentRepositoryTree(entity)) {
          this.adjustSettings(entity, bind(this, this.showInCatalogTree), ShowInLibraryHelper.RESOURCE_BUNDLE.Catalog_show_in_catalog_tree_fails_for_Content);
        }
      });
    });
  }

  protected tryShowInContentRepositoryTree(entity: any): boolean {
    if (is(entity, UndocContent) && ShowInLibraryHelper.getShowAsContentVE().getValue()) {
      ShowInLibraryHelper.#showInRepositoryMode(entity, CollectionViewConstants.TREE_MODEL_ID);
      return true;
    }
    return false;
  }

  static getShowAsContentVE(): ValueExpression {
    return ValueExpressionFactory.create(CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY, editorContext._.getPreferences());
  }

  protected tryShowInCatalogTree(entity: any): boolean {
    const idPathFromModel = this.#catalogTreeModel.getIdPathFromModel(entity);
    if (idPathFromModel === undefined) {
      return undefined;
    }
    if (null !== idPathFromModel) {
      ShowInLibraryHelper.#showInRepositoryMode(entity, this.#catalogTreeModel.getTreeId());
      return true;
    }
    return false;
  }

  static #showInRepositoryMode(entity: any, treeModelId: string): void {
    // ignoring type of entity (show in repository doesn't really care if it's of type Content)
    editorContext._.getCollectionViewManager().showInRepository(entity, null, treeModelId);
  }

  protected adjustSettings(entity: any, callback: AnyFunction, msg: string): void {
    const buttons: Record<string, any> = {
      no: ShowInLibraryHelper.RESOURCE_BUNDLE.Catalog_show_preferences_button_text,
      cancel: Editor_properties.dialog_defaultCancelButton_text,
      yes: ShowInLibraryHelper.RESOURCE_BUNDLE.Catalog_show_switch_site_button_text,
    };
    this.openDialog(msg, buttons, entity, callback);
  }

  protected openDialog(msg: string, buttons: any, entity: any, callback: AnyFunction): void {
    MessageBoxUtilInternal.show(ShowInLibraryHelper.RESOURCE_BUNDLE.Catalog_show_in_tree_fails_title, msg, null, buttons, this.#getButtonCallback(entity.siteId, callback));
  }

  protected switchSite(siteId: string, callback: AnyFunction): void {
    //switch site
    editorContext._.getSitesService().getPreferredSiteIdExpression().setValue(siteId);
    // make sure that the new catalog is available
    catalogHelper.openCatalog();

    EventUtil.invokeLater(callback);
  }

  #getButtonCallback(siteId: string, callback: AnyFunction): AnyFunction {
    return (btn: string): void => {
      if (btn === "cancel") {
        //just cancel
      } else if (btn === "yes") {
        this.switchSite(siteId, callback);
      } else {
        //show preferences
        const prefWindow: PreferenceWindow = Ext.create(StudioPreferenceWindow, { selectedTabItemId: "contentCatalogPreferences" });
        prefWindow.show();
        //open the content in library if the user enable the show as content contentCatalogPreferences
        prefWindow.on(ShowInLibraryHelper.#HIDE_EVENT_NAME, callback);
      }
    };
  }

}

export default ShowInLibraryHelper;
