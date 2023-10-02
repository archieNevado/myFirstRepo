import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import StatefulTextArea from "@coremedia/studio-client.ext.ui-components/components/StatefulTextArea";
import BindBlobPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindBlobPropertyPlugin";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BindReadOnlyPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindReadOnlyPlugin";
import SetPropertyEmptyTextPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyEmptyTextPlugin";
import SetPropertyLabelPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyLabelPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import SearchValidator from "./SearchValidator";

interface SearchQueryPropertyFieldConfig extends Config<StatefulTextArea>, Partial<Pick<SearchQueryPropertyField,
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "propertyName"
>> {
}

class SearchQueryPropertyField extends StatefulTextArea {
  declare Config: SearchQueryPropertyFieldConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.personalization.editorplugin.config.searchQueryPropertyField";

  constructor(config: Config<SearchQueryPropertyField> = null) {
    super(ConfigUtils.apply(Config(SearchQueryPropertyField, {
      name: "properties." + config.propertyName,
      anchor: "100%",
      checkChangeBuffer: 500,
      labelSeparator: "",

      plugins: [
        Config(SetPropertyLabelPlugin, {
          bindTo: config.bindTo,
          propertyName: config.propertyName,
        }),
        Config(SetPropertyEmptyTextPlugin, {
          bindTo: config.bindTo,
          propertyName: config.propertyName,
        }),
        Config(BindReadOnlyPlugin, {
          bindTo: config.bindTo,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
        }),
        Config(PropertyFieldPlugin, { propertyName: config.propertyName }),
        Config(BindBlobPropertyPlugin, {
          bindTo: config.bindTo.extendBy("properties", config.propertyName),
          mimeType: "text/plain",
        }),
        Config(SearchValidator),
      ],

    }), config));
  }

  /**
   * a property path expression leading to the Bean whose property is edited.
   * This property editor assumes that this bean has a property 'properties'.
   */
  bindTo: ValueExpression = null;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  /** the property of the Bean to bind in this field */
  propertyName: string = null;
}

export default SearchQueryPropertyField;
