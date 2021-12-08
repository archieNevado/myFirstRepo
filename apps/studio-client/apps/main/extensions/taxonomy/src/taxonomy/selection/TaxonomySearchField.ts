import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import StopEventPropagationPlugin from "@coremedia/studio-client.ext.ui-components/plugins/StopEventPropagationPlugin";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import JsonStore from "@jangaroo/ext-ts/data/JsonStore";
import DataField from "@jangaroo/ext-ts/data/field/Field";
import NumberDataField from "@jangaroo/ext-ts/data/field/Number";
import BoundListView from "@jangaroo/ext-ts/view/BoundList";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomySearchFieldBase from "./TaxonomySearchFieldBase";

interface TaxonomySearchFieldConfig extends Config<TaxonomySearchFieldBase>, Partial<Pick<TaxonomySearchField,
  "searchResultExpression" |
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "propertyName" |
  "showSelectionPath" |
  "resetOnBlur"
>> {
}

class TaxonomySearchField extends TaxonomySearchFieldBase {
  declare Config: TaxonomySearchFieldConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomySearchField";

  constructor(config: Config<TaxonomySearchField> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomySearchField, {
      forceSelection: false,
      autoSelect: false,
      enableKeyEvents: true,
      queryMode: "remote",
      typeAhead: false,
      hideTrigger: true,
      triggerAction: "all",
      minChars: 2,
      queryDelay: 200,
      queryParam: "text",
      matchFieldWidth: false,
      pageSize: 0,
      selectOnFocus: true,
      emptyText: this.getEmptyTextText(config),
      ariaLabel: this.getEmptyTextText(config),
      displayField: "name",
      validator: bind(this, this.tagPrefixValidValidator),
      plugins: [
        Config(BindDisablePlugin, {
          bindTo: config.bindTo,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
        }),
        Config(StopEventPropagationPlugin),
      ],
      store: new JsonStore({
        autoDestroy: true,
        proxy: this.getSearchSuggestionsDataProxy(config),
        autoLoad: true,
        fields: [
          Config(DataField, { name: TaxonomyNode.PROPERTY_NAME }),
          Config(DataField, { name: TaxonomyNode.PROPERTY_REF }),
          Config(DataField, { name: TaxonomyNode.PROPERTY_PATH }),
          Config(DataField, { name: TaxonomyNode.PROPERTY_TAXONOMY_ID }),
          Config(DataField, {
            name: TaxonomyNode.PROPERTY_HTML,
            convert: TaxonomySearchFieldBase.renderHTML,
          }),
          Config(NumberDataField, { name: "size" }),
          Config(NumberDataField, { name: TaxonomySearchFieldBase.SUGGESTION_COUNT }),
        ],
      }),
      listConfig: Config(BoundListView, {
        loadingText: TaxonomyStudioPlugin_properties.TaxonomySearch_loading_text,
        itemTpl: TaxonomySearchFieldBase.autoSuggestResultTpl,
        scrollable: "vertical",
        emptyText: TaxonomyStudioPlugin_properties.TaxonomySearch_no_hit,
        itemSelector: "div",
      }),
    }), config))());
  }

  /**
   * Contains a list of hits that match with the current search value.
   */
  searchResultExpression: ValueExpression = null;

  /**
   * The value expression that contains the editors content.
   */
  bindTo: ValueExpression = null;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  /**
   * The property name that is edited
   */
  propertyName: string = null;

  /**
   * If true, the path of the selected node is shown as plain string in the textfield, empty string otherwise.
   * Default is true.
   */
  showSelectionPath: boolean = false;

  /** True, if the search field is used for the taxonomy administration, defaults to false. */
  resetOnBlur: boolean = false;
}

export default TaxonomySearchField;
