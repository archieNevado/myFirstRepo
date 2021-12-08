import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import FormatUtil from "@coremedia/studio-client.ext.cap-base-components/util/FormatUtil";
import LazyItemsContainerMixin from "@coremedia/studio-client.ext.ui-components/mixins/LazyItemsContainerMixin";
import FilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/FilterPanel";
import Container from "@jangaroo/ext-ts/container/Container";
import CheckboxGroup from "@jangaroo/ext-ts/form/CheckboxGroup";
import Checkbox from "@jangaroo/ext-ts/form/field/Checkbox";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetConstants from "../AssetConstants";
import StringListCheckboxFilterPanel from "./StringListCheckboxFilterPanel";

interface StringListCheckboxFilterPanelBaseConfig extends Config<FilterPanel>, Partial<Pick<StringListCheckboxFilterPanelBase,
  "availableValuesValueExpression" |
  "solrField" |
  "propertyName"
>> {
}

class StringListCheckboxFilterPanelBase extends FilterPanel {
  declare Config: StringListCheckboxFilterPanelBaseConfig;

  #checkboxContainer: Container = null;

  constructor(config: Config<StringListCheckboxFilterPanel> = null) {
    super(config);
    this.addListener(LazyItemsContainerMixin.LAZY_ITEMS_ADDED_EVENT, bind(this, this.#afterLazyItemsAdded));
  }

  #afterLazyItemsAdded(): void {
    this.#checkboxContainer = as(this.getComponent("checkboxContainer"), Container);
    this.availableValuesValueExpression.addChangeListener(bind(this, this.#createCheckboxes));
    this.#createCheckboxes();
  }

  /**
   * A value expression evaluating to a list of strings. For each string one checkbox is rendered.
   * The checkbox label is localized in the file AMStudioPlugin.properties with the following pattern:
   * 'Asset_metadata_[propertyName]_[value]_text'.
   */
  availableValuesValueExpression: ValueExpression = null;

  /**
   * The solr field to pose the query against. If not set the filterId is used.
   */
  solrField: string = null;

  /**
   * The property in the metadata struct of the Asset. If not set the filterId is used.
   */
  propertyName: string = null;

  override buildQuery(): string {
    const selectedValues: Array<any> = this.getStateBean().get(this.getFilterId());
    if (!selectedValues || selectedValues.length === 0) {
      return "";
    }

    return FormatUtil.format("{0}:({1})", this.solrField || this.getFilterId(), selectedValues.join(" AND "));
  }

  override getDefaultState(): any {
    const state: Record<string, any> = {};
    state[this.getFilterId()] = undefined;
    return state;
  }

  #createCheckboxes(): void {
    const availableValues: Array<any> = this.availableValuesValueExpression.getValue();
    if (!availableValues || availableValues.length === 0) {
      return;
    }

    const checkboxConfigs = availableValues.map((availableValue: string): Config<Checkbox> => {
      const propertyKey = "Asset_" + AssetConstants.PROPERTY_ASSET_METADATA + "_" + (this.propertyName || this.getFilterId()) + "_" + availableValue + "_text";
      const checkboxConfig = Config(Checkbox);
      checkboxConfig.boxLabel = AMStudioPlugin_properties[propertyKey] || availableValue;
      checkboxConfig.name = this.getFilterId();
      checkboxConfig.inputValue = availableValue;
      checkboxConfig.itemId = availableValue;
      checkboxConfig.hideLabel = true;
      return checkboxConfig;
    });

    const checkboxGroupConfig = Config(CheckboxGroup);
    checkboxGroupConfig.items = checkboxConfigs;

    this.#checkboxContainer.removeAll();
    this.#checkboxContainer.add(checkboxGroupConfig);

    this.updateLayout();
  }

  transformer(strings: Array<any>): any {
    const valueObject: Record<string, any> = {};

    if (strings.length == 1 && strings[0]) {
      valueObject[this.getFilterId()] = strings[0];
    } else {
      valueObject[this.getFilterId()] = strings;
    }
    return valueObject;
  }

  reverseTransformer(selectedCheckboxes: any): Array<any> {
    if (selectedCheckboxes[this.getFilterId()]) {
      return [].concat(selectedCheckboxes[this.getFilterId()]);
    }
    return undefined;
  }

  /**
   * @inheritDoc
   */
  override getActiveFilterCount(): number {
    const selectedValues: Array<any> = this.getStateBean().get(this.getFilterId());
    if (!selectedValues || selectedValues.length === 0) {
      return 0;
    }

    return selectedValues.length;
  }
}

export default StringListCheckboxFilterPanelBase;
