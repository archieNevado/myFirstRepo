import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import DateTimePropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/DateTimePropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface VisibilityConfigurationFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<VisibilityConfigurationForm,
  "propertyName"
>> {
}

/**
 * This is a form panel which combines several form elements to an editor for local settings to configure
 * the visibility behaviour. A combination of two date time field specifing visibleFrom and visibleTo.
 */
class VisibilityConfigurationForm extends PropertyFieldGroup {
  declare Config: VisibilityConfigurationFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.visibilityConfigurationForm";

  static readonly VISIBLE_FROM_ITEM_ID: string = "visibleFrom";

  static readonly VISIBLE_TO_ITEM_ID: string = "visibleTo";

  propertyName: string = null;

  constructor(config: Config<VisibilityConfigurationForm> = null) {
    super(ConfigUtils.apply(Config(VisibilityConfigurationForm, {
      itemId: "visibilityConfigurationForm",
      header: false,
      propertyNames: [],

      items: [
        Config(DateTimePropertyField, {
          itemId: VisibilityConfigurationForm.VISIBLE_FROM_ITEM_ID,
          propertyName: config.propertyName + ".visibleFrom",
          isLoadedPropertyName: config.propertyName,
        }),
        Config(DateTimePropertyField, {
          itemId: VisibilityConfigurationForm.VISIBLE_TO_ITEM_ID,
          propertyName: config.propertyName + ".visibleTo",
          isLoadedPropertyName: config.propertyName,
        }),
      ],
    }), config));
  }
}

export default VisibilityConfigurationForm;
