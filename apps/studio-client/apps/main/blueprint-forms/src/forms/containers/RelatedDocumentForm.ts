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
import CustomLabels_properties from "../../CustomLabels_properties";
import ValidityColumn from "../columns/ValidityColumn";

interface RelatedDocumentFormConfig extends Config<PropertyFieldGroup> {
}

class RelatedDocumentForm extends PropertyFieldGroup {
  declare Config: RelatedDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.relatedDocumentForm";

  constructor(config: Config<RelatedDocumentForm> = null) {
    super(ConfigUtils.apply(Config(RelatedDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Related_label,
      itemId: "relatedDocumentForm",
      collapsed: false,

      items: [
        Config(LinkListPropertyField, {
          itemId: "related",
          propertyName: "related",
          hideLabel: true,
          showThumbnails: true,
          additionalToolbarItems: [
            Config(Separator),
            Config(QuickCreateLinklistMenu, {
              bindTo: config.bindTo,
              propertyName: "related",
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

export default RelatedDocumentForm;
