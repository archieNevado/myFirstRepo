import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import Container from "@jangaroo/ext-ts/container/Container";
import Config from "@jangaroo/runtime/Config";
import CatalogHelper from "../../helper/CatalogHelper";
import CatalogRepositoryThumbnails from "./CatalogRepositoryThumbnails";

interface CatalogRepositoryThumbnailsBaseConfig extends Config<Container>, Partial<Pick<CatalogRepositoryThumbnailsBase,
  "selectedFolderValueExpression" |
  "selectedItemsValueExpression"
>> {
}

class CatalogRepositoryThumbnailsBase extends Container {
  declare Config: CatalogRepositoryThumbnailsBaseConfig;

  /**
   * value expression for the selected folder in the library tree
   */
  selectedFolderValueExpression: ValueExpression = null;

  selectedItemsValueExpression: ValueExpression = null;

  constructor(config: Config<CatalogRepositoryThumbnails> = null) {
    super(config);
  }

  getCatalogItemsValueExpression(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): Array<any> =>
      CatalogHelper.getInstance().getChildren(this.selectedFolderValueExpression.getValue()),
    );
  }

  disableBrowserContextMenu(): void {
    /* TODO Ext6, see CMS-7893
    var thumbViewPanel:* = this.el.down('div.catalog-thumb-data-view-panel');
    thumbViewPanel.on("contextmenu", Ext.emptyFn, null, {
      preventDefault: true
    });
*/
  }

}

export default CatalogRepositoryThumbnailsBase;
