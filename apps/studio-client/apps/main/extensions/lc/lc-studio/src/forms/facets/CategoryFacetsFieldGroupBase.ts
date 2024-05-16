import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import Config from "@jangaroo/runtime/Config";
import CategoryFacetsFieldGroup from "./CategoryFacetsFieldGroup";

interface CategoryFacetsFieldGroupBaseConfig extends Config<Panel>, Partial<Pick<CategoryFacetsFieldGroupBase,
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "hideIssues" |
  "externalIdPropertyName" |
  "structPropertyName" |
  "facetsExpression" |
  "facetValuePropertyName"
>> {
}

class CategoryFacetsFieldGroupBase extends Panel {
  declare Config: CategoryFacetsFieldGroupBaseConfig;

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  hideIssues: boolean = false;

  externalIdPropertyName: string = null;

  structPropertyName: string = null;

  facetsExpression: ValueExpression = null;

  facetValuePropertyName: string = null;

  constructor(config: Config<CategoryFacetsFieldGroup> = null) {
    super(config);
  }

  /**
   * A message shown in case we have selected a category that has no facets.
   * @param config
   * @return
   */
  protected getHideNoFacetsMsgExpression(config: Config<CategoryFacetsFieldGroup>): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean => {
      const searchFacets: Array<any> = this.facetsExpression.getValue();
      return searchFacets && searchFacets.length > 0;
    });
  }
}

export default CategoryFacetsFieldGroupBase;
