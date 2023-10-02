import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Facet from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Facet";
import SearchFacets from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/SearchFacets";
import FacetUtil from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/filters/FacetUtil";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import LivecontextStudioPlugin_properties from "../../LivecontextStudioPlugin_properties";
import CategoryFacetsPropertyField from "./CategoryFacetsPropertyField";

interface CategoryFacetsPropertyFieldBaseConfig extends Config<PropertyFieldGroup>, Partial<Pick<CategoryFacetsPropertyFieldBase,
  "externalIdPropertyName" |
  "structPropertyName"
>> {
}

class CategoryFacetsPropertyFieldBase extends PropertyFieldGroup {
  declare Config: CategoryFacetsPropertyFieldBaseConfig;

  protected static readonly NEW_EDITOR_ITEM_ID: string = "cmProductListFacetsFieldGroup";

  protected static readonly OLD_EDITOR_ITEM_ID: string = "legacyCmProductListFacetsFieldGroup";

  protected static readonly NO_CATEGORY_MSG_ITEM_ID: string = "noCategoryMessage";

  static PRODUCT_LIST_STRUCT_NAME: string = "productList";

  static SINGLE_FACET_NAME: string = "selectedFacet";

  static SINGLE_FACET_VALUE: string = "selectedFacetValue";

  static MULTI_FACETS_STRUCT_NAME: string = "filterFacets";

  static MULTI_FACETS_QUERIES_STRUCT_NAME: string = "queries";

  externalIdPropertyName: string = null;

  structPropertyName: string = null;

  #activeEditorExpression: ValueExpression = null;

  #multiFacetEnabledExpression: ValueExpression = null;

  #searchFacetsExpression: ValueExpression = null;

  #facetsExpression: ValueExpression = null;

  #legacyFacetsExpression: ValueExpression = null;

  #autofixExpression: ValueExpression = null;

  constructor(config: Config<CategoryFacetsPropertyField> = null) {
    super(config);
  }

  /**
   * Calculates the new multi facet values out of the selected category.
   */
  protected getSearchFacetsExpression(config: Config<CategoryFacetsPropertyField> = null): ValueExpression {
    if (!this.#searchFacetsExpression) {
      this.#searchFacetsExpression = ValueExpressionFactory.createFromFunction((): SearchFacets => {
        const externalId: string = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.externalIdPropertyName).getValue();
        if (!externalId) {
          return null;
        }

        const category = as(CatalogHelper.getInstance().getCatalogObject(externalId, this.bindTo), Category);
        if (!category) {
          return null;
        }

        if (!category.isLoaded()) {
          category.load();
          return undefined;
        }

        if (!category.getSearchFacets().isLoaded()) {
          category.getSearchFacets().load();
          return undefined;
        }

        return category.getSearchFacets();
      });

      this.#searchFacetsExpression.addChangeListener(bind(this, this.#searchFacetsChanged));
      this.#searchFacetsChanged(this.#searchFacetsExpression);
    }
    return this.#searchFacetsExpression;
  }

  /**
   * This is a proxy VE, used to reset the list of facet input fields.
   * When a content is reverted, the editors must be destroyed in order to be re-initialized properly.
   * If we would work directly on the FunctionVE, the list would not necessarily change, and when switching
   * to multi-facet mode again, the previously selected multi facets would be visible again, although no persisted anymore.
   * @param config
   * @return
   */
  protected getFacetsExpression(config: Config<CategoryFacetsPropertyField> = null): ValueExpression {
    if (!this.#facetsExpression) {
      this.#facetsExpression = ValueExpressionFactory.createFromValue([]);
    }
    return this.#facetsExpression;
  }

  /**
   * Calculates the legacy facet values out of the selected category.
   */
  protected getFacetsOrLegacyFacetsExpression(config: Config<CategoryFacetsPropertyField> = null): ValueExpression {
    if (!this.#legacyFacetsExpression) {
      //wildcard return value since the value can be 'Facets' or 'SearchFacets'
      this.#legacyFacetsExpression = ValueExpressionFactory.createFromFunction((): any => {
        const externalId: string = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.externalIdPropertyName).getValue();
        if (!externalId) {
          return null;
        }

        const category = as(CatalogHelper.getInstance().getCatalogObject(externalId, this.bindTo), Category);
        if (!category) {
          return null;
        }

        if (!category.isLoaded()) {
          category.load();
          return undefined;
        }

        if (category.getFacets() === undefined) {
          category.getFacets().load();
          return undefined;
        }

        if (category.getFacets().getFacets() === undefined) {
          return undefined;
        }

        return category.getFacets();
      });
    }
    return this.#legacyFacetsExpression;
  }

  /**
   * Determines if the single facet value or multi-facet editor should be active.
   * @param config
   * @return
   */
  protected getActiveEditorExpression(config: Config<CategoryFacetsPropertyField> = null): ValueExpression {
    if (!this.#activeEditorExpression) {
      this.#activeEditorExpression = ValueExpressionFactory.createFromFunction((): string => {
        const externalId: string = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.externalIdPropertyName).getValue();
        //show missing category hint
        if (!externalId) {
          return CategoryFacetsPropertyFieldBase.NO_CATEGORY_MSG_ITEM_ID;
        }

        //show new editor by default
        const multiFacetEnabled: boolean = this.#getMultiFacetEnabledExpression(config).getValue();
        if (multiFacetEnabled === undefined) {
          return CategoryFacetsPropertyFieldBase.NEW_EDITOR_ITEM_ID;
        }

        if (!multiFacetEnabled) {
          return CategoryFacetsPropertyFieldBase.OLD_EDITOR_ITEM_ID;
        }

        //show old editor if there are still legacy values persisted
        const legacyQueryValue: string = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.SINGLE_FACET_VALUE).getValue();
        const legacyQueryName: string = config.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.SINGLE_FACET_NAME).getValue();
        if (legacyQueryValue || legacyQueryName) {
          return CategoryFacetsPropertyFieldBase.OLD_EDITOR_ITEM_ID;
        }

        return CategoryFacetsPropertyFieldBase.NEW_EDITOR_ITEM_ID;
      });
    }
    return this.#activeEditorExpression;
  }

  /**
   * The switch between the two editor is calculated through the persisted struct format.
   * So we simple add or remove the legacy values to select the new editor.
   */
  protected toggleFacetMode(): void {
    const itemId: string = this.getActiveEditorExpression().getValue();
    if (itemId === CategoryFacetsPropertyFieldBase.OLD_EDITOR_ITEM_ID) {
      this.#migrateSingleToMultiValue();
    }
  }

  /**
   * Calculates if the multi-facet feature is enabled.
   */
  #getMultiFacetEnabledExpression(config: Config<CategoryFacetsPropertyField>): ValueExpression {
    if (!this.#multiFacetEnabledExpression) {
      this.#multiFacetEnabledExpression = ValueExpressionFactory.createFromFunction((): boolean => {
        const searchFacets: SearchFacets = this.getSearchFacetsExpression(config).getValue();
        if (searchFacets === undefined) {
          return undefined;
        }

        return searchFacets !== null;
      });
    }
    return this.#multiFacetEnabledExpression;
  }

  /**
   * Delegates the values of the actual facets VE to the editor facet list VE.
   */
  #searchFacetsChanged(ve: ValueExpression): void {
    const searchFacet: SearchFacets = ve.getValue();
    if (searchFacet && searchFacet.getFacets()) {
      this.getFacetsExpression().setValue(searchFacet.getFacets());
      this.#updateLabels(searchFacet.getFacets());
    } else {
      this.getFacetsExpression().setValue([]);
    }
  }

  /**
   * Writes the default label for facet fields into the matching resource bundle.
   * This is required to have a meaningful name inside the issues panel when an error occurs.
   * @param facets the list of facets to create labels for
   */
  #updateLabels(facets: Array<any>): void {
    const validationBundle = resourceManager.getResourceBundle(null, ContentTypes_properties);
    facets.forEach((f: Facet): void => {
      validationBundle.content["CMProductList_localSettings.productList.filterFacets." + f.getKey() + "_text"] = LivecontextStudioPlugin_properties["CMProductList_localSettings.productList_text"];
    });
  }

  /**
   * Decides to show the migration button or not.
   */
  protected getBtnVisibilityExpression(config: Config<CategoryFacetsPropertyField>): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean => {
      const multiFacetsEnabled: boolean = this.#getMultiFacetEnabledExpression(config).getValue();
      if (!multiFacetsEnabled) {
        return true;
      }
      return this.getActiveEditorExpression().getValue() === CategoryFacetsPropertyFieldBase.NEW_EDITOR_ITEM_ID;
    });
  }

  /**
   * VE to calculate the auto fix button visibility
   */
  protected getAutoFixExpression(config: Config<CategoryFacetsPropertyField>): ValueExpression {
    if (!this.#autofixExpression) {
      this.#autofixExpression = ValueExpressionFactory.createFromFunction((): boolean => {
        const multiFacetsExpression = this.bindTo.extendBy(ContentPropertyNames.PROPERTIES, config.structPropertyName, CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME);
        const filterFacetsStruct: Struct = multiFacetsExpression.getValue();
        if (filterFacetsStruct) {
          const searchFacets: SearchFacets = this.getSearchFacetsExpression(config).getValue();
          if (searchFacets && searchFacets.getFacets()) {
            const facets: Array<any> = searchFacets.getFacets();
            const storedNames = filterFacetsStruct.getType().getPropertyNames();

            //check if there is a stored multi facet key that does not exist anymore
            for (const facetId of storedNames as string[]) {
              if (!FacetUtil.validateFacetId4Facets(facets, facetId)) {
                return true;
              }
            }
          }
        }
        return false;
      });
    }
    return this.#autofixExpression;
  }

  /**
   * Used to fix invalid struct formats.
   * E.g. when a search facet is not available anymore, the struct still exists without the chance to overwrite it
   * with another configuration. This requires a manual fix by the user to avoid an automatic checkout.
   */
  protected autoFixFormat(): void {
    const multiFacetsExpression = this.bindTo.extendBy(ContentPropertyNames.PROPERTIES, this.structPropertyName, CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME, CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME);
    const filterFacetsStruct: Struct = multiFacetsExpression.getValue();
    if (filterFacetsStruct) {
      const searchFacets: SearchFacets = this.getSearchFacetsExpression().getValue();
      if (searchFacets && searchFacets.getFacets()) {
        const facets: Array<any> = searchFacets.getFacets();
        const storedNames = filterFacetsStruct.getType().getPropertyNames();
        for (const facetId of storedNames as string[]) {
          if (!FacetUtil.validateFacetId4Facets(facets, facetId)) {
            filterFacetsStruct.getType().removeProperty(facetId);
          }
        }
      }
    }
  }

  /**
   * For old Product Lists, the 'filterFacets' struct does not exist.
   * So for migration, we remove the old values from the 'productList' struct and add the 'filterFacets' struct.
   * Finally, we trigger the BindComponentsPlugin for the rendering of the multi facet editors.
   */
  #migrateSingleToMultiValue(): void {
    //this ensures that the editors are destroyed an re-initialized when content is reverted
    this.getFacetsExpression().setValue([]);

    const searchFacets: SearchFacets = this.getSearchFacetsExpression().getValue();
    const productListStructExpression = this.bindTo.extendBy(ContentPropertyNames.PROPERTIES, this.structPropertyName, CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME);
    const parentStruct: Struct = productListStructExpression.getValue();

    parentStruct.getType().addStructProperty(CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME);
    const facetQuery: string = parentStruct.get(CategoryFacetsPropertyFieldBase.SINGLE_FACET_VALUE);
    if (facetQuery) {
      const facetId = FacetUtil.findFacetIdForQuery(searchFacets.getFacets(), facetQuery);
      if (facetId) {
        productListStructExpression
          .extendBy(CategoryFacetsPropertyFieldBase.MULTI_FACETS_STRUCT_NAME)
          .extendBy([facetId])
          .extendBy(CategoryFacetsPropertyFieldBase.MULTI_FACETS_QUERIES_STRUCT_NAME)
          .setValue([facetQuery]);
      }
    }
    parentStruct.getType().removeProperty(CategoryFacetsPropertyFieldBase.SINGLE_FACET_VALUE);
    parentStruct.getType().removeProperty(CategoryFacetsPropertyFieldBase.SINGLE_FACET_NAME);

    //this invoke later is necessary, otherwise the BindComponentsPlugin of the group editor wouldn't notice a reset
    EventUtil.invokeLater((): void =>
      this.#searchFacetsChanged(this.getSearchFacetsExpression()),
    );
  }

  protected override onDestroy(): void {
    super.onDestroy();
    this.#searchFacetsExpression && this.#searchFacetsExpression.removeChangeListener(bind(this, this.#searchFacetsChanged));
  }
}

export default CategoryFacetsPropertyFieldBase;
