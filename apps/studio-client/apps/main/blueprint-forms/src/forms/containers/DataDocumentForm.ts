import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BlobPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BlobPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface DataDocumentFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<DataDocumentForm,
  "dataUrlLabel" |
  "helpText"
>> {
}

class DataDocumentForm extends PropertyFieldGroup {
  declare Config: DataDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.dataDocumentForm";

  constructor(config: Config<DataDocumentForm> = null) {
    super(ConfigUtils.apply(Config(DataDocumentForm, {
      title: config.title,
      itemId: "dataDocumentForm",

      items: [
        Config(BlobPropertyField, {
          propertyName: "data",
          hideLabel: true,
          helpText: config.helpText,
        }),
        Config(Component, { height: 6 }),
        Config(StringPropertyField, {
          propertyName: "dataUrl",
          itemId: "dataUrl",
          fieldLabel: config.dataUrlLabel,
        }),
      ],

    }), config));
  }

  /** The title to apply to the URL string property */
  dataUrlLabel: string = null;

  /** The helptext that is shown next to the upload button  */
  helpText: string = null;
}

export default DataDocumentForm;
