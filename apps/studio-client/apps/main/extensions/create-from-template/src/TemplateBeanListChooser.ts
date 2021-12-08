import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CreateFromTemplateStudioPluginSettings_properties from "./CreateFromTemplateStudioPluginSettings_properties";
import TemplateBeanListChooserBase from "./TemplateBeanListChooserBase";

interface TemplateBeanListChooserConfig extends Config<TemplateBeanListChooserBase> {
}

class TemplateBeanListChooser extends TemplateBeanListChooserBase {
  declare Config: TemplateBeanListChooserConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.template.TemplateBeanListChooser";

  constructor(config: Config<TemplateBeanListChooser> = null) {
    super((()=> ConfigUtils.apply(Config(TemplateBeanListChooser, {
      propertyName: CreateFromTemplateStudioPluginSettings_properties.template_property,

      dataFields: [
        Config(DataField, {
          name: "iconUri",
          mapping: "name",
          convert: bind(this, this.computeIconURL),
        }),
        Config(DataField, {
          name: "description",
          mapping: "name",
          convert: bind(this, this.getDescription),
        }),
      ],
    }), config))());
  }
}

export default TemplateBeanListChooser;
