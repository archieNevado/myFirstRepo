import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import ExtendedDisplayField from "@coremedia/studio-client.ext.ui-components/components/ExtendedDisplayField";
import SwitchingContainer from "@coremedia/studio-client.ext.ui-components/components/SwitchingContainer";
import OverflowBehaviour from "@coremedia/studio-client.ext.ui-components/mixins/OverflowBehaviour";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import DisplayFieldSkin from "@coremedia/studio-client.ext.ui-components/skins/DisplayFieldSkin";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../../LivecontextStudioPlugin_properties";
import CategoryFacetsFieldGroup from "./CategoryFacetsFieldGroup";
import CategoryFacetsPropertyFieldBase from "./CategoryFacetsPropertyFieldBase";
import SingleCategoryFacetsFieldGroup from "./SingleCategoryFacetsFieldGroup";

interface CategoryFacetsPropertyFieldConfig extends Config<CategoryFacetsPropertyFieldBase> {
}

class CategoryFacetsPropertyField extends CategoryFacetsPropertyFieldBase {
  declare Config: CategoryFacetsPropertyFieldConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.categoryFacetsPropertyField";

  static readonly SWITCH_BUTTON_ITEM_ID: string = "migrationButton";

  static readonly FIX_BUTTON_ITEM_ID: string = "autoFixButton";

  constructor(config: Config<CategoryFacetsPropertyField> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(CategoryFacetsPropertyField, {
      title: LivecontextStudioPlugin_properties.CMProductList_facets_text,
      itemId: "categoryFacetsPropertyField",

      items: [
        Config(Container, {
          items: [
            Config(DisplayField, {
              hidden: true,
              value: LivecontextStudioPlugin_properties.CMProductList_facets_remove_invalid_values_description,
              plugins: [
                Config(BindPropertyPlugin, {
                  componentProperty: "visible",
                  bindTo: this$.getAutoFixExpression(config),
                }),
              ],
            }),
            Config(Button, {
              hidden: true,
              ui: ButtonSkin.LINK.getSkin(),
              margin: "0 0 0 -2",
              handler: bind(this$, this$.autoFixFormat),
              itemId: CategoryFacetsPropertyField.FIX_BUTTON_ITEM_ID,
              text: LivecontextStudioPlugin_properties.CMProductList_facets_remove_invalid_values,
              plugins: [
                Config(BindPropertyPlugin, {
                  componentProperty: "visible",
                  bindTo: this$.getAutoFixExpression(config),
                }),
                Config(BindDisablePlugin, {
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  bindTo: config.bindTo,
                }),
              ],
            }),
            Config(DisplayField, {
              hidden: true,
              value: LivecontextStudioPlugin_properties.CMProductList_facets_mode_switch_description,
              plugins: [
                Config(BindPropertyPlugin, {
                  componentProperty: "hidden",
                  bindTo: this$.getBtnVisibilityExpression(config),
                }),
              ],
            }),
            Config(Button, {
              text: LivecontextStudioPlugin_properties.CMProductList_facets_mode_switch,
              handler: bind(this$, this$.toggleFacetMode),
              margin: "0 0 0 -2",
              itemId: CategoryFacetsPropertyField.SWITCH_BUTTON_ITEM_ID,
              ui: ButtonSkin.LINK.getSkin(),
              plugins: [
                Config(BindDisablePlugin, {
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  bindTo: config.bindTo,
                }),
                Config(BindPropertyPlugin, {
                  componentProperty: "hidden",
                  bindTo: this$.getBtnVisibilityExpression(config),
                }),
              ],
            }),
          ],
          layout: Config(VBoxLayout),
        }),
        Config(SwitchingContainer, {
          activeItemValueExpression: this$.getActiveEditorExpression(config),
          items: [
            Config(ExtendedDisplayField, {
              itemId: CategoryFacetsPropertyFieldBase.NO_CATEGORY_MSG_ITEM_ID,
              ui: DisplayFieldSkin.SUB_LABEL_READONLY.getSkin(),
              overflowBehaviour: OverflowBehaviour.BREAK_WORD,
              value: LivecontextStudioPlugin_properties.CategoryFacetFiltersGroup_noCategory,
            }),
            Config(CategoryFacetsFieldGroup, {
              itemId: CategoryFacetsPropertyFieldBase.NEW_EDITOR_ITEM_ID,
              bindTo: config.bindTo,
              facetsExpression: this$.getFacetsExpression(config),
              structPropertyName: config.structPropertyName,
              externalIdPropertyName: config.externalIdPropertyName,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              facetValuePropertyName: config.structPropertyName + "." + CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME,
            }),
            Config(SingleCategoryFacetsFieldGroup, {
              itemId: CategoryFacetsPropertyFieldBase.OLD_EDITOR_ITEM_ID,
              bindTo: config.bindTo,
              facetsExpression: this$.getFacetsOrLegacyFacetsExpression(config),
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              facetNamePropertyName: config.structPropertyName + "." + CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME + "." + CategoryFacetsPropertyFieldBase.SINGLE_FACET_NAME,
              facetValuePropertyName: config.structPropertyName + "." + CategoryFacetsPropertyFieldBase.PRODUCT_LIST_STRUCT_NAME + "." + CategoryFacetsPropertyFieldBase.SINGLE_FACET_VALUE,
            }),
          ],
        }),
      ],
      plugins: [
        Config(VerticalSpacingPlugin, { modifier: SpacingBEMEntities.VERTICAL_SPACING_MODIFIER_200 }),
      ],
      layout: Config(VBoxLayout, { align: "stretch" }),
    }), config));
  }
}

export default CategoryFacetsPropertyField;
