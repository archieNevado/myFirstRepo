import Facet from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Facet";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import Container from "@jangaroo/ext-ts/container/Container";
import Config from "@jangaroo/runtime/Config";
import CategoryFacetComboField from "./CategoryFacetComboField";
import CategoryFacetField from "./CategoryFacetField";
import CategoryFacetTagField from "./CategoryFacetTagField";

interface CategoryFacetFieldBaseConfig extends Config<Container>, Partial<Pick<CategoryFacetFieldBase,
  "facet" |
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "structPropertyName"
>> {
}

class CategoryFacetFieldBase extends Container {
  declare Config: CategoryFacetFieldBaseConfig;

  facet: Facet = null;

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  structPropertyName: string = null;

  constructor(config: Config<CategoryFacetField> = null) {
    super(config);

    let xType = CategoryFacetComboField.xtype;
    if (config.facet.isMultiSelect()) {
      xType = CategoryFacetTagField.xtype;
    }

    const editorCfg: Record<string, any> = {
      bindTo: config.bindTo,
      facet: config.facet,
      forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
      structPropertyName: config.structPropertyName,
      xtype: xType,
    };

    const editor: any = ComponentManager.create(editorCfg);
    this.add(editor);
  }
}

export default CategoryFacetFieldBase;
