import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import QuickCreateLinklistMenu from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateLinklistMenu";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";
import ValidityColumn from "../columns/ValidityColumn";

interface ItemsFormConfig extends Config<PropertyFieldGroup> {
}

class ItemsForm extends PropertyFieldGroup {
  declare Config: ItemsFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.itemsForm";

  /**
   * A constant for the linklist property name
   */
  static readonly ITEMS_PROPERTY_NAME: string = "items";

  constructor(config: Config<ItemsForm> = null) {
    super(ConfigUtils.apply(Config(ItemsForm, {
      title: BlueprintDocumentTypes_properties.CMCollection_items_text,
      itemId: "itemsForm",

      items: [
        Config(LinkListPropertyField, {
          propertyName: ItemsForm.ITEMS_PROPERTY_NAME,
          showThumbnails: true,
          hideLabel: true,
          bindTo: config.bindTo,
          additionalToolbarItems: [
            Config(Separator),
            Config(QuickCreateLinklistMenu, {
              bindTo: config.bindTo,
              propertyName: ItemsForm.ITEMS_PROPERTY_NAME,
            }),
          ],
          fields: [
            Config(DataField, {
              name: ValidityColumn.STATUS_ID,
              mapping: "",
              convert: ValidityColumn.convert,
            }),
          ],
          columns: [
            Config(LinkListThumbnailColumn),
            Config(TypeIconColumn),
            Config(NameColumn, { flex: 1 }),
            Config(ValidityColumn),
            Config(StatusColumn),
          ],
        }),
      ],

    }), config));
  }
}

export default ItemsForm;
