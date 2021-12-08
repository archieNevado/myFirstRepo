import ModerationImpl from "@coremedia/studio-client.main.es-models/impl/ModerationImpl";
import TextField from "@jangaroo/ext-ts/form/field/Text";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ElasticPluginPropertyField from "./ElasticPluginPropertyField";

interface ElasticPluginPropertyFieldBaseConfig extends Config<TextField> {
}

class ElasticPluginPropertyFieldBase extends TextField {
  declare Config: ElasticPluginPropertyFieldBaseConfig;

  constructor(config: Config<ElasticPluginPropertyField> = null) {
    super(config);

    const expressionSegments = config.expression.split(".");
    if (expressionSegments && expressionSegments.length > 0) {
      const property: string = expressionSegments[expressionSegments.length - 1];
      as(ModerationImpl.getInstance(), ModerationImpl).registerModeratedProperties([property]);
    }
  }
}

export default ElasticPluginPropertyFieldBase;
