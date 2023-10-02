import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import UndocContentUtil from "@coremedia/studio-client.cap-rest-client/content/UndocContentUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Model from "@jangaroo/ext-ts/data/Model";
import Store from "@jangaroo/ext-ts/data/Store";
import ComboBox from "@jangaroo/ext-ts/form/field/ComboBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";

interface TaxonomyComboBaseConfig extends Config<LocalComboBox>, Partial<Pick<TaxonomyComboBase,
  "selectionExpression" |
  "filterExpression"
>> {
}

/**
 * The base class of the taxonomy combo.
 * The taxonomy combo displays all available taxonomies, global and site depending ones.
 */
class TaxonomyComboBase extends LocalComboBox {
  declare Config: TaxonomyComboBaseConfig;

  /**
   * Contains the selected taxonomy
   */
  selectionExpression: ValueExpression = null;

  filterExpression: ValueExpression = null;

  #taxonomiesExpression: ValueExpression = null;

  constructor(config: Config<TaxonomyComboBase> = null) {
    super(config);
  }

  protected override afterRender(): void {
    super.afterRender();
    this.on("select", bind(this, this.#valueSelected));
    this.getStore().on("load", bind(this, this.#storeLoaded));
  }

  /**
   * The selection listener method for the combo box.
   * @param combo the combo box
   * @param record the selected record
   */
  #valueSelected(combo: ComboBox, record: Model): void {
    const id: string = record.data.id;
    const content = UndocContentUtil.getContent(id);
    this.filterExpression.setValue("");
    this.selectionExpression.setValue(content);
  }

  /**
   * Selects the first record in the combo box and propagates the selection.
   *
   * @param store the store of the combo box
   * @param records the store records
   * @param successful if loading was successful
   */
  #storeLoaded(store: Store, records: Array<any>, successful: boolean): void {
    if (successful && records.length > 0) {
      const r1: Model = records[0];
      // set the value in the combo box
      this.setValue(r1.data.id);
      // propagate selection
      this.#valueSelected(this, r1);
    }
  }

  /**
   * Returns the value expression that contains the all taxonomies.
   * @return
   */
  protected getTaxonomiesExpression(): ValueExpression {
    if (!this.#taxonomiesExpression) {
      this.#taxonomiesExpression = ValueExpressionFactory.createFromFunction((): Array<any> => {
        const records = [];
        const remoteBean = beanFactory._.getRemoteBean("topicpages/taxonomies");
        if (!remoteBean.isLoaded()) {
          remoteBean.load();
          return undefined;
        }

        const values: Array<any> = remoteBean.get("items");
        for (const value of values as Content[]) {
          if (!value.isLoaded()) {
            value.load();
            return undefined;
          }

          if (value.getPath() == undefined) {
            return undefined;
          }

          records.push({
            id: value.getId(),
            path: TaxonomyComboBase.formatDisplayName(value),
          });
        }

        return records;
      });
    }
    return this.#taxonomiesExpression;
  }

  protected static formatDisplayName(content: Content): string {
    const site = editorContext._.getSitesService().getSiteNameFor(content);
    if (site) {
      return content.getName() + " (" + site + ")";
    }
    return content.getName();
  }
}

export default TaxonomyComboBase;
