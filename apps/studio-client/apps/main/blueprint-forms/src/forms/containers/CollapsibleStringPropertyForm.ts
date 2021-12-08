import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import PropertyFieldGroupBase from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroupBase";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import PanelTitle from "@jangaroo/ext-ts/panel/Title";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface CollapsibleStringPropertyFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<CollapsibleStringPropertyForm,
  "propertyName"
>> {
}

class CollapsibleStringPropertyForm extends PropertyFieldGroup {
  declare Config: CollapsibleStringPropertyFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.collapsibleStringPropertyForm";

  constructor(config: Config<CollapsibleStringPropertyForm> = null) {
    super(ConfigUtils.apply(Config(CollapsibleStringPropertyForm, {
      title: PropertyFieldGroupBase.formatTitle(config.bindTo, config.propertyName,
        PanelTitle.getText(config.title)),
      itemId: PropertyFieldGroupBase.formatItemId(config.propertyName, "Form"),

      items: [
        Config(StringPropertyField, {
          bindTo: config.bindTo,
          hideLabel: true,
          propertyName: config.propertyName,
        }),
      ],

    }), config));
  }

  /** The name to bind the string property editor to. */
  propertyName: string = null;
}

export default CollapsibleStringPropertyForm;
