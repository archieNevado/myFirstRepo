import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import BooleanPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/BooleanPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";
import CMChannelFormBase from "../CMChannelFormBase";

interface VisibilityDocumentFormConfig extends Config<PropertyFieldGroup> {
}

class VisibilityDocumentForm extends PropertyFieldGroup {
  declare Config: VisibilityDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.visibilityDocumentForm";

  constructor(config: Config<VisibilityDocumentForm> = null) {
    super(ConfigUtils.apply(Config(VisibilityDocumentForm, {
      itemId: "visibilityDocumentForm",
      title: CustomLabels_properties.PropertyGroup_Visibility_label,

      ...ConfigUtils.append({
        plugins: [
          Config(BindPropertyPlugin, {
            bidirectional: true,
            bindTo: CMChannelFormBase.getIsRootChannelValueExpression(config.bindTo),
            componentProperty: "hidden",
          }),
        ],
      }),
      items: [
        Config(BooleanPropertyField, {
          hideLabel: true,
          propertyName: "hidden",
        }),
        Config(BooleanPropertyField, {
          hideLabel: true,
          propertyName: "hiddenInSitemap",
        }),
        Config(BooleanPropertyField, {
          hideLabel: true,
          propertyName: "notSearchable",
        }),
      ],

    }), config));
  }
}

export default VisibilityDocumentForm;
