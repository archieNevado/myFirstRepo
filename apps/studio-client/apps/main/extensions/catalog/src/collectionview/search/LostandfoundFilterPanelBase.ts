import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import CatalogPreferencesBase from "@coremedia-blueprint/studio-client.main.ec-studio/components/preferences/CatalogPreferencesBase";
import StoreUtil from "@coremedia-blueprint/studio-client.main.ec-studio/helper/StoreUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ConditionalFilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/ConditionalFilterPanel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Config from "@jangaroo/runtime/Config";
import CatalogStudioPluginBase from "../../CatalogStudioPluginBase";

class LostandfoundFilterPanelBase extends ConditionalFilterPanel {
  static readonly LOSTANDFOUND_CHECKBOX_SELECTED: string = "lostAndFoundCheckboxSelected";

  static readonly DEFAULT_STATE: Record<string, any> = {};

  static #static = (() => {
    LostandfoundFilterPanelBase.DEFAULT_STATE[LostandfoundFilterPanelBase.LOSTANDFOUND_CHECKBOX_SELECTED] = false;
  })();

  /**
   * The query fragment to be passed to Solr.
   */
  static readonly #FILTER_QUERY_LOSTANDFOUND: string = "(type:CMProduct OR type:CMCategory) AND NOT directProductCategories:[* TO *]";

  #catalogRootExclusionsExpression: ValueExpression = null;

  #catalogRootExclusions: string = "";

  constructor(config: Config<ConditionalFilterPanel> = null) {
    super(config);
  }

  /**
   * @inheritDoc
   */
  override isApplicable(): boolean {
    return editorContext._.getPreferences().get(CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY);
  }

  /**
   * @inheritDoc
   */
  override doBuildQuery(): string {
    const stateBean = this.getStateBean();
    const lostandfoundActive: boolean = stateBean.get(LostandfoundFilterPanelBase.LOSTANDFOUND_CHECKBOX_SELECTED);
    if (lostandfoundActive) {
      return LostandfoundFilterPanelBase.#FILTER_QUERY_LOSTANDFOUND + this.#getCatalogRootExclusions();
    }
    return null;
  }

  /**
   * @inheritDoc
   */
  override getActiveFilterCount(): number {
    const stateBean = this.getStateBean();
    const lostandfoundActive: boolean = stateBean.get(LostandfoundFilterPanelBase.LOSTANDFOUND_CHECKBOX_SELECTED);
    if (lostandfoundActive) {
      return 1;
    }
    return 0;
  }

  /**
   * @inheritDoc
   */
  override getDefaultState(): any {
    return LostandfoundFilterPanelBase.DEFAULT_STATE;
  }

  /**
   * The catalog root categories have no parents, but are not to be considered
   * as orphaned.  Exclude them in the query.
   */
  #getCatalogRootExclusions(): string {
    if (!this.#catalogRootExclusionsExpression) {
      this.#catalogRootExclusionsExpression = ValueExpressionFactory.createFromFunction((): string => {
        let result = "";
        const storesExpression = ValueExpressionFactory.createFromFunction(CatalogStudioPluginBase.findCoreMediaStores);
        const stores: Array<any> = storesExpression.getValue();
        if (undefined === stores) {
          return undefined;
        }
        for (const store of stores as Store[]) {
          const category: Category = StoreUtil.getRootCategoryForStoreExpression(store).getValue();
          if (undefined === category) {
            return undefined;
          }
          if (category) {
            const externalTechId = category.getExternalTechId();
            if (undefined === externalTechId) {
              return undefined;
            }
            result += " AND NOT numericid:" + externalTechId;
          }
        }
        return result;
      });
    }

    this.#catalogRootExclusionsExpression.loadValue((exclusions: string): void => {
      this.#catalogRootExclusions = exclusions;
    });
    return this.#catalogRootExclusions;
  }
}

export default LostandfoundFilterPanelBase;
