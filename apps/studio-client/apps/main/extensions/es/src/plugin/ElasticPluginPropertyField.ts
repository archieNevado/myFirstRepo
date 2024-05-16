import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import ModerationImpl from "@coremedia/studio-client.main.es-models/impl/ModerationImpl";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ElasticPluginPropertyFieldBase from "./ElasticPluginPropertyFieldBase";

interface ElasticPluginPropertyFieldConfig extends Config<ElasticPluginPropertyFieldBase>, Partial<Pick<ElasticPluginPropertyField,
  "expression"
>> {
}

class ElasticPluginPropertyField extends ElasticPluginPropertyFieldBase {
  declare Config: ElasticPluginPropertyFieldConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.elastic.social.studio.config.elasticPluginPropertyField";

  constructor(config: Config<ElasticPluginPropertyField> = null) {
    super(ConfigUtils.apply(Config(ElasticPluginPropertyField, {

      plugins: [
        Config(BindPropertyPlugin, {
          bidirectional: true,
          bindTo: new ConfigBasedValueExpression({
            expression: config.expression,
            context: ModerationImpl.getInstance(),
          }),
        }),
      ],

    }), config));
  }

  expression: string = null;
}

export default ElasticPluginPropertyField;
