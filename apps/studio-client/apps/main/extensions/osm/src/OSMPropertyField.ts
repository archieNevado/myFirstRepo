import BlueprintDocumentTypes_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintDocumentTypes_properties";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import OSMPanel from "./OSMPanel";

interface OSMPropertyFieldConfig extends Config<PropertyFieldGroup>, Partial<Pick<OSMPropertyField,
  "propertyName"
>> {
}

class OSMPropertyField extends PropertyFieldGroup {
  declare Config: OSMPropertyFieldConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.osm.config.osmPropertyField";

  constructor(config: Config<OSMPropertyField> = null) {
    super(ConfigUtils.apply(Config(OSMPropertyField, {
      title: BlueprintDocumentTypes_properties.CMLocTaxonomy_latitudeLongitude_text,
      itemId: "osmPanelForm",

      items: [
        Config(OSMPanel, {
          latLngExpression: config.bindTo.extendBy("properties", config.propertyName),
          ...ConfigUtils.append({
            plugins: [
              Config(BindDisablePlugin, {
                bindTo: config.bindTo,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              }),
            ],
          }),
        }),
      ],

    }), config));
  }

  /** the property of the Bean to bind in this field */
  propertyName: string = null;
}

export default OSMPropertyField;
