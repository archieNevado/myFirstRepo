import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ShowMetadataIconDisplayFieldAction from "@coremedia/studio-client.main.editor-components/sdk/actions/metadata/ShowMetadataIconDisplayFieldAction";
import Breadcrumb from "@coremedia/studio-client.main.editor-components/sdk/components/breadcrumb/Breadcrumb";
import PreviewContextMenu from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewContextMenu";
import MetadataTreeModel from "@coremedia/studio-client.main.editor-components/sdk/preview/metadata/MetadataTreeModel";
import Item from "@jangaroo/ext-ts/menu/Item";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OpenCreateExternalPageMenuItem from "../../src/pbe/OpenCreateExternalPageMenuItem";
import ShopPageShowInLibraryMenuItem from "../../src/pbe/ShopPageShowInLibraryMenuItem";

interface TestPreviewContextMenuConfig extends Config<PreviewContextMenu>, Partial<Pick<TestPreviewContextMenu,
  "bindTo"
>> {
}

class TestPreviewContextMenu extends PreviewContextMenu {
  declare Config: TestPreviewContextMenuConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.testPreviewContextMenu";

  constructor(config: Config<TestPreviewContextMenu> = null) {
    super((()=> ConfigUtils.apply(Config(TestPreviewContextMenu, {

      items: [
        Config(Breadcrumb, {
          id: "breadcrumb",
          itemId: PreviewContextMenu.BREADCRUMB_ITEM_ID,
          treeModel: new MetadataTreeModel(config.metadataTreeValueExpression),
          enableOverflow: false,
          disableNavigation: true,
          hideElementTexts: true,
          hideOnEmpty: false,
          disableElementStrategy: this["disableBreadcrumbElementStrategy"],
          selectedNodeValueExpression: config.selectedNodeValueExpression,
        }),

        Config(Item, {
          itemId: PreviewContextMenu.TITLE_MENU_ITEM_ID,
          canActivate: false,
          baseAction: new ShowMetadataIconDisplayFieldAction({
            metadataValueExpression: config.selectedNodeValueExpression,
            hideOnDisable: true,
          }),
        }),
        Config(OpenCreateExternalPageMenuItem),
        Config(ShopPageShowInLibraryMenuItem),
      ],

    }), config))());
  }

  bindTo: ValueExpression = null;
}

export default TestPreviewContextMenu;
