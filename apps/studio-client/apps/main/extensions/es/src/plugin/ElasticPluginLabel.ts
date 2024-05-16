import ConfigBasedValueExpression from "@coremedia/studio-client.ext.ui-components/data/ConfigBasedValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import ModerationImpl from "@coremedia/studio-client.main.es-models/impl/ModerationImpl";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface ElasticPluginLabelConfig extends Config<DisplayField>, Partial<Pick<ElasticPluginLabel,
  "expression"
>> {
}

class ElasticPluginLabel extends DisplayField {
  declare Config: ElasticPluginLabelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.elastic.social.studio.config.elasticPluginLabel";

  constructor(config: Config<ElasticPluginLabel> = null) {
    super(ConfigUtils.apply(Config(ElasticPluginLabel, {

      plugins: [
        Config(BindPropertyPlugin, {
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

export default ElasticPluginLabel;
