import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ActionConfigUtil from "@coremedia/studio-client.ext.cap-base-components/actions/ActionConfigUtil";
import ContentAction from "@coremedia/studio-client.ext.cap-base-components/actions/ContentAction";
import ContentTreeRelationProvider from "@coremedia/studio-client.main.editor-components/sdk/ContentTreeRelationProvider";
import MessageBoxUtil from "@coremedia/studio-client.main.editor-components/sdk/util/MessageBoxUtil";
import StringUtil from "@jangaroo/ext-ts/String";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import CatalogTreeRelation from "../library/CatalogTreeRelation";
import CatalogTreeRelationHelper from "../library/CatalogTreeRelationHelper";
import CatalogActions_properties from "./CatalogActions_properties";
import UnlinkAction from "./UnlinkAction";

interface UnlinkActionBaseConfig extends Config<ContentAction> {
}

class UnlinkActionBase extends ContentAction {
  declare Config: UnlinkActionBaseConfig;

  #folderValueExpression: ValueExpression = null;

  constructor(config: Config<UnlinkAction> = null) {
    super((()=>ActionConfigUtil.extendConfiguration(resourceManager.getResourceBundle(null, CatalogActions_properties).content, config, "unlink", { handler: bind(this, this.#startUnlink) }))());
    this.#folderValueExpression = config.folderValueExpression;
  }

  protected override isDisabledFor(contents: Array<any>): boolean {
    for (const content of contents as Content[]) {
      const type: string = content.getType() && content.getType().getName();
      if (type !== CatalogTreeRelation.CONTENT_TYPE_CATEGORY && type !== CatalogTreeRelation.CONTENT_TYPE_PRODUCT) {
        return true;
      }

      const treeRelation = ContentTreeRelationProvider.getContentTreeRelation();
      if (treeRelation === undefined) {
        return undefined;
      }
      if (treeRelation === null) {
        return true;
      }

      //we must use a CatalogTreeRelation here, otherwise disable the action
      const catalogTreeRelation = as(treeRelation, CatalogTreeRelation);
      if (catalogTreeRelation == null) {
        return true;
      }

      const parents = catalogTreeRelation.getParents(content);
      if (!parents || parents.length <= 1) {
        return true;
      }
    }
    return false;
  }

  protected override isHiddenFor(contents: Array<any>): boolean {
    // only the delete button should be shown otherwise
    return this.isDisabledFor(contents);
  }

  #startUnlink(): void {
    const contents: Array<any> = this.getContents();
    if (!contents || !contents.length) {
      return;
    }

    const category: Content = this.#folderValueExpression.getValue();
    const title = CatalogActions_properties.Action_unlink_title;
    const message = StringUtil.format(CatalogActions_properties.Action_unlink_message, category.getName());
    MessageBoxUtil.showConfirmation(title, message, CatalogActions_properties.Action_unlink_text,
      (btn: any): void => {
        if (btn === "ok") {
          this.#doUnlink(category, contents);
        }
      });
  }

  /**
   * Removes the selection from the linked category.
   * The parent category is determined using the current tree selection.
   * @param contents the contents to unlink
   */
  #doUnlink(category: Content, contents: Array<any>): void {
    //validate category checkout
    if (!CatalogTreeRelationHelper.validateCheckoutState([category])) {
      return;
    }

    //validate product checkout
    const products = CatalogTreeRelationHelper.filterForType(contents, CatalogTreeRelationHelper.CONTENT_TYPE_PRODUCT);
    if (!CatalogTreeRelationHelper.validateCheckoutState(products)) {
      return;
    }

    const checkedInContents = CatalogTreeRelationHelper.storeCheckInOutState(contents.concat([category]));

    for (const content of contents as Content[]) {
      CatalogTreeRelationHelper.removeCategoryChild(category, content);
    }

    CatalogTreeRelationHelper.restoreCheckInOutState(checkedInContents);
  }
}

export default UnlinkActionBase;
