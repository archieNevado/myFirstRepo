import CatalogModel from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogModel";
import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import ReadOnlyCatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/ReadOnlyCatalogLinkPropertyField";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import SelectSubCategoriesRadioGroup from "../components/SelectSubCategoriesRadioGroup";
import CommerceChildCategoriesFormBase from "./CommerceChildCategoriesFormBase";

interface CommerceChildCategoriesFormConfig extends Config<CommerceChildCategoriesFormBase>, Partial<Pick<CommerceChildCategoriesForm,
  "forceReadOnlyValueExpression"
>> {
}

class CommerceChildCategoriesForm extends CommerceChildCategoriesFormBase {
  declare Config: CommerceChildCategoriesFormConfig;

  /**
   * A ValueExpression that indicates whether the property fields should be read only
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceChildCategoriesForm";

  static readonly INHERITED_CATEGORIES_ITEM_ID: string = "inheritedCategories";

  static readonly SELECTED_CATEGORIES_ITEM_ID: string = "selectedCategories";

  static #stringToBoolean(value: string): boolean {
    return value === "selectChildrenRadioButton";
  }

  constructor(config: Config<CommerceChildCategoriesForm> = null) {
    super((()=> ConfigUtils.apply(Config(CommerceChildCategoriesForm, {

      items: [
        Config(SelectSubCategoriesRadioGroup, {
          itemId: "inheritOrSelectRadioGroup",
          toValue: CommerceChildCategoriesForm.#stringToBoolean,
          bindTo: this.isSelectChildrenExpression(config.bindTo),
          ...ConfigUtils.append({
            plugins: [
              Config(BindDisablePlugin, {
                bindTo: config.bindTo,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              }),
            ],
          }),
        }),
        Config(Component, { height: 6 }),
        Config(ReadOnlyCatalogLinkPropertyField, {
          itemId: CommerceChildCategoriesForm.INHERITED_CATEGORIES_ITEM_ID,
          bindTo: this.getCategoryExpression(config.bindTo),
          propertyName: CatalogObjectPropertyNames.SUB_CATEGORIES,
          emptyText: LivecontextStudioPlugin_properties.Commerce_Category_subcategories_emptyText,
          ...ConfigUtils.append({
            plugins: [
              Config(BindVisibilityPlugin, { bindTo: this.isInheritExpression(config.bindTo) }),
            ],
          }),
        }),
        Config(CatalogLinkPropertyField, {
          itemId: CommerceChildCategoriesForm.SELECTED_CATEGORIES_ITEM_ID,
          bindTo: config.bindTo,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          propertyName: CommerceChildCategoriesFormBase.CHILDREN_PROPERTY_NAME,
          linkTypeNames: [CatalogModel.TYPE_CATEGORY],
          dropAreaText: LivecontextStudioPlugin_properties.Category_Link_empty_text,
          ...ConfigUtils.append({
            plugins: [
              Config(BindVisibilityPlugin, { bindTo: this.isSelectChildrenExpression(config.bindTo) }),
            ],
          }),
        }),
      ],
    }), config))());
  }
}

export default CommerceChildCategoriesForm;
