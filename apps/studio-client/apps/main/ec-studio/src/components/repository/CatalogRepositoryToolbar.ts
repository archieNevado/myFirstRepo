import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ApproveAction from "@coremedia/studio-client.ext.cap-base-components/actions/ApproveAction";
import ApprovePublishAction from "@coremedia/studio-client.ext.cap-base-components/actions/ApprovePublishAction";
import PublishAction from "@coremedia/studio-client.ext.cap-base-components/actions/PublishAction";
import WithdrawAction from "@coremedia/studio-client.ext.cap-base-components/actions/WithdrawAction";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import HideObsoleteSeparatorsPlugin
  from "@coremedia/studio-client.ext.ui-components/plugins/HideObsoleteSeparatorsPlugin";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import DeleteAction from "@coremedia/studio-client.main.editor-components/sdk/actions/DeleteAction";
import OpenEntitiesInTabsAction
  from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenEntitiesInTabsAction";
import BookmarkAction from "@coremedia/studio-client.main.editor-components/sdk/bookmarks/BookmarkAction";
import CopyToClipboardAction from "@coremedia/studio-client.main.editor-components/sdk/clipboard/CopyToClipboardAction";
import ActionRef from "@jangaroo/ext-ts/ActionRef";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";

interface CatalogRepositoryToolbarConfig extends Config<Toolbar>, Partial<Pick<CatalogRepositoryToolbar,
  "selectedItemsValueExpression"
  >> {
}

class CatalogRepositoryToolbar extends Toolbar {
  declare Config: CatalogRepositoryToolbarConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogRepositoryToolbar";

  static readonly COPY_BUTTON_ITEM_ID: string = "copyToClipboard";

  static readonly DELETE_BUTTON_ITEM_ID: string = "delete";

  static readonly OPEN_IN_TAB_BUTTON_ITEM_ID: string = "openInTab";

  static readonly BOOKMARK_BUTTON_ITEM_ID: string = "bookmarkButton";

  static readonly APPROVE_BUTTON_ITEM_ID: string = "approve";

  static readonly PUBLISH_BUTTON_ITEM_ID: string = "publish";

  static readonly WITHDRAW_BUTTON_ITEM_ID: string = "withdraw";

  static readonly APPROVE_PUBLISH_BUTTON_ITEM_ID: string = "finish";

  static readonly REPOSITORY_TOOLBAR_SPACER_FIRST_ITEM_ID: string = "catalogRepositoryToolbarSpacerFirst";

  static readonly REPOSITORY_TOOLBAR_SPACER_SECOND_ITEM_ID: string = "catalogRepositoryToolbarSpacerSecond";

  static readonly REPOSITORY_TOOLBAR_SPACER_THIRD_ITEM_ID: string = "catalogRepositoryToolbarSpacerThird";

  static readonly REPOSITORY_TOOLBAR_SPACER_FOURTH_ITEM_ID: string = "catalogRepositoryToolbarSpacerFourth";

  constructor(config: Config<CatalogRepositoryToolbar> = null) {
    super(ConfigUtils.apply(Config(CatalogRepositoryToolbar, {
      ariaLabel: ECommerceStudioPlugin_properties.CollectionView_catalogRepositoryToolbar_label,
      itemId: "commerceToolbar",
      enableOverflow: true,
      ui: ToolbarSkin.LIGHT.getSkin(),
      flex: 1,
      items: [
        Config(IconButton, {
          itemId: CatalogRepositoryToolbar.COPY_BUTTON_ITEM_ID,
          baseAction: Config(ActionRef, { actionId: CopyToClipboardAction.ACTION_ID }),
        }),
        Config(Separator, { itemId: CatalogRepositoryToolbar.REPOSITORY_TOOLBAR_SPACER_FIRST_ITEM_ID }),
        Config(IconButton, {
          itemId: CatalogRepositoryToolbar.OPEN_IN_TAB_BUTTON_ITEM_ID,
          baseAction: new OpenEntitiesInTabsAction({ entitiesValueExpression: config.selectedItemsValueExpression }),
        }),
        Config(Separator, { itemId: CatalogRepositoryToolbar.REPOSITORY_TOOLBAR_SPACER_SECOND_ITEM_ID }),
        Config(IconButton, {
          itemId: CatalogRepositoryToolbar.BOOKMARK_BUTTON_ITEM_ID,
          baseAction: Config(ActionRef, { actionId: BookmarkAction.ACTION_ID }),
        }),
        Config(Separator),
        Config(IconButton, {
          itemId: CatalogRepositoryToolbar.APPROVE_BUTTON_ITEM_ID,
          baseAction: Config(ActionRef, { actionId: ApproveAction.ACTION_ID }),
        }),
        Config(IconButton, {
          itemId: CatalogRepositoryToolbar.PUBLISH_BUTTON_ITEM_ID,
          baseAction: Config(ActionRef, { actionId: PublishAction.ACTION_ID }),
        }),
        Config(IconButton, {
          itemId: CatalogRepositoryToolbar.APPROVE_PUBLISH_BUTTON_ITEM_ID,
          baseAction: Config(ActionRef, { actionId: ApprovePublishAction.ACTION_ID }),
        }),
        Config(Separator, { itemId: CatalogRepositoryToolbar.REPOSITORY_TOOLBAR_SPACER_THIRD_ITEM_ID }),
        Config(IconButton, {
          itemId: CatalogRepositoryToolbar.WITHDRAW_BUTTON_ITEM_ID,
          baseAction: Config(ActionRef, { actionId: WithdrawAction.ACTION_ID }),
        }),
        Config(Separator, { itemId: CatalogRepositoryToolbar.REPOSITORY_TOOLBAR_SPACER_FOURTH_ITEM_ID }),
        Config(IconButton, {
          itemId: CatalogRepositoryToolbar.DELETE_BUTTON_ITEM_ID,
          baseAction: Config(ActionRef, { actionId: DeleteAction.ACTION_ID }),
        }),
      ],
      layout: Config(HBoxLayout, <Config<HBoxLayout>>{ triggerWidth: 26 }),
      plugins: [
        Config(HideObsoleteSeparatorsPlugin),
      ],

    }), config));
  }

  /**
   * value expression for the selected items, either in the list view, or - if the selection there is empty - the
   * selected folder in the tree view.
   */
  selectedItemsValueExpression: ValueExpression = null;
}

export default CatalogRepositoryToolbar;
