import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListUtil from "@coremedia/studio-client.main.editor-components/sdk/util/LinkListUtil";
import Config from "@jangaroo/runtime/Config";
import PictureDocumentForm from "./PictureDocumentForm";

interface PictureDocumentFormBaseConfig extends Config<PropertyFieldGroup> {
}

class PictureDocumentFormBase extends PropertyFieldGroup {
  declare Config: PictureDocumentFormBaseConfig;

  #gridEmptyValueExpression: ValueExpression = null;

  constructor(config: Config<PictureDocumentForm> = null) {
    super(config);
  }

  getGridEmptyValueExpression(config: Config<PictureDocumentForm>): ValueExpression {
    if (!this.#gridEmptyValueExpression) {
      this.#gridEmptyValueExpression = ValueExpressionFactory.createFromFunction(
        (): boolean =>
          LinkListUtil.getFreeCapacity(
            config.bindTo,
            config.picturePropertyName,
            config.maxCardinality) < 1,

      );
    }
    return this.#gridEmptyValueExpression;
  }
}

export default PictureDocumentFormBase;
