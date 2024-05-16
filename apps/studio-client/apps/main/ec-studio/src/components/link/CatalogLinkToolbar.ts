import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import HideObsoleteSeparatorsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/HideObsoleteSeparatorsPlugin";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import ILinkListWrapper from "@coremedia/studio-client.link-list-models/ILinkListWrapper";
import LinkListRemoveAction from "@coremedia/studio-client.main.editor-components/sdk/actions/LinkListRemoveAction";
import OpenEntitiesInTabsAction from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenEntitiesInTabsAction";
import PropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyField";
import ActionRef from "@jangaroo/ext-ts/ActionRef";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ECommerceStudioPlugin from "../../ECommerceStudioPlugin";

interface CatalogLinkToolbarConfig extends Config<Toolbar>, Partial<Pick<CatalogLinkToolbar,
  "linkListWrapper" |
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "readOnlyValueExpression" |
  "propertyName" |
  "hideOpenInTab" |
  "hideRemove" |
  "additionalToolbarItems"
>> {
}

class CatalogLinkToolbar extends Toolbar {
  declare Config: CatalogLinkToolbarConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogLinkToolbar";

  constructor(config: Config<CatalogLinkToolbar> = null) {
    super(ConfigUtils.apply(Config(CatalogLinkToolbar, {
      ui: ToolbarSkin.FIELD.getSkin(),

      plugins: [
        Config(AddItemsPlugin, { items: config.additionalToolbarItems }),
        Config(HideObsoleteSeparatorsPlugin),
      ],

      defaultType: PropertyField.xtype,

      defaults: Config<PropertyField>({
        bindTo: config.bindTo,
        ...{ readOnlyValueExpression: config.readOnlyValueExpression },
        forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
      }),
      items: [
        /* Button for deletion */
        Config(IconButton, {
          itemId: ECommerceStudioPlugin.REMOVE_LINK_BUTTON_ITEM_ID,
          hidden: config.hideRemove,
          baseAction: Config(ActionRef, { actionId: LinkListRemoveAction.ACTION_ID }),
        }),

        Config(IconButton, {
          itemId: ECommerceStudioPlugin.OPEN_IN_TAB_BUTTON_ITEM_ID,
          hidden: config.hideOpenInTab,
          baseAction: Config(ActionRef, { actionId: OpenEntitiesInTabsAction.ACTION_ID }),
        }),
      ],
    }), config));
  }

  linkListWrapper: ILinkListWrapper = null;

  /**
   * A property path expression leading to the Bean whose property is edited.
   * This property editor assumes that this bean has a property 'properties'.
   */
  bindTo: ValueExpression = null;

  /**
   * ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  /**
   * ValueExpression which makes the delete button read-only if it is evaluated to true.
   */
  readOnlyValueExpression: ValueExpression = null;

  /**
   * The name of the sting property of the Bean to bind in this field.
   * The string property holds the id of the catalog product
   */
  propertyName: string = null;

  /**
   * Set to true if the open in tab button should be hidden. Default is false
   */
  hideOpenInTab: boolean = false;

  /**
   * Set to true if the remove button should be hidden. Default is false
   */
  hideRemove: boolean = false;

  additionalToolbarItems: Array<any> = null;
}

export default CatalogLinkToolbar;
