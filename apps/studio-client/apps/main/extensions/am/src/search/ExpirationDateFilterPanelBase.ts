import FormatUtil from "@coremedia/studio-client.ext.cap-base-components/util/FormatUtil";
import FilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/FilterPanel";
import Config from "@jangaroo/runtime/Config";
import ExpirationDateFilterPanel from "./ExpirationDateFilterPanel";

interface ExpirationDateFilterPanelBaseConfig extends Config<FilterPanel> {
}

class ExpirationDateFilterPanelBase extends FilterPanel {
  declare Config: ExpirationDateFilterPanelBaseConfig;

  static readonly KEY: string = "key";

  static readonly DATE: string = "date";

  static readonly #SOLR_FIELD: string = "expirationDate";

  constructor(config: Config<ExpirationDateFilterPanel> = null) {
    super(config);
  }

  override buildQuery(): string {
    const key: string = this.getStateBean().get(ExpirationDateFilterPanelBase.KEY);
    if (!key || key === "any") {
      return "";
    }

    if (key === "byDate") {
      const date: Date = this.getStateBean().get(ExpirationDateFilterPanelBase.DATE);
      if (!date) {
        return "";
      }

      const dateAsISO8601: string = date.toISOString(); // TODO: add to jangaroo as API - supported since IE9
      return FormatUtil.format("{0}:[* TO {1}]", ExpirationDateFilterPanelBase.#SOLR_FIELD, dateAsISO8601);
    }

    let queryFormat = "";
    switch (key) {
    case "inOneDay":
      queryFormat = "{0}:[* TO NOW/DAY+1DAY]";
      break;
    case "inOneWeek":
      queryFormat = "{0}:[* TO NOW/DAY+7DAYS]";
      break;
    case "inTwoWeeks":
      queryFormat = "{0}:[* TO NOW/DAY+14DAYS]";
      break;
    case "inOneMonth":
      queryFormat = "{0}:[* TO NOW/DAY+1MONTH]";
      break;
    }

    return FormatUtil.format(queryFormat, ExpirationDateFilterPanelBase.#SOLR_FIELD);
  }

  /**
   * @inheritDoc
   */
  override getActiveFilterCount(): number {
    const key: string = this.getStateBean().get(ExpirationDateFilterPanelBase.KEY);
    if (!key || key === "any") {
      return 0;
    }

    return 1;
  }

  override getDefaultState(): any {
    const state: Record<string, any> = {}; // NOSONAR
    state[ExpirationDateFilterPanelBase.KEY] = "any";
    state[ExpirationDateFilterPanelBase.DATE] = null;
    return state;
  }

}

export default ExpirationDateFilterPanelBase;
