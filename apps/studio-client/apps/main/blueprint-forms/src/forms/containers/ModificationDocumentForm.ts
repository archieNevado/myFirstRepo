import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import PropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyField";
import DatePropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/DatePropertyField";
import SetPropertyLabelPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyLabelPlugin";
import Component from "@jangaroo/ext-ts/Component";
import Container from "@jangaroo/ext-ts/container/Container";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface ModificationDocumentFormConfig extends Config<Container>, Partial<Pick<ModificationDocumentForm,
  "bindTo" |
  "forceReadOnlyValueExpression"
>> {
}

class ModificationDocumentForm extends Container {
  declare Config: ModificationDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.modificationDocumentForm";

  constructor(config: Config<ModificationDocumentForm> = null) {
    super(ConfigUtils.apply(Config(ModificationDocumentForm, {

      plugins: [
        Config(SetPropertyLabelPlugin, {
          bindTo: config.bindTo,
          propertyName: "queryPubFrom",
        }),
      ],
      items: [
        Config(Container, {
          layout: "hbox",
          defaultType: PropertyField.xtype,
          defaults: Config<PropertyField>({
            bindTo: config.bindTo,
            forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          }),
          items: [
            Config(DatePropertyField, {
              flex: 1,
              propertyName: "queryPubFrom",
            }),
            Config(Component, { width: 10 }),
            Config(DatePropertyField, {
              flex: 1,
              propertyName: "queryPubTo",
            }),
          ],
        }),
      ],

    }), config));
  }

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;
}

export default ModificationDocumentForm;
