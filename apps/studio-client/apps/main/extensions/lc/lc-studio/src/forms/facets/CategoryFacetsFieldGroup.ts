import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import BindComponentsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindComponentsPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import DisplayFieldSkin from "@coremedia/studio-client.ext.ui-components/skins/DisplayFieldSkin";
import BindReadOnlyPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindReadOnlyPlugin";
import SetPropertyEmptyTextPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyEmptyTextPlugin";
import Container from "@jangaroo/ext-ts/container/Container";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../../LivecontextStudioPlugin_properties";
import CategoryFacetField from "./CategoryFacetField";
import CategoryFacetsFieldGroupBase from "./CategoryFacetsFieldGroupBase";

interface CategoryFacetsFieldGroupConfig extends Config<CategoryFacetsFieldGroupBase> {
}

class CategoryFacetsFieldGroup extends CategoryFacetsFieldGroupBase {
  declare Config: CategoryFacetsFieldGroupConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.categoryFacetsFieldGroup";

  static readonly FACET_FIELDS_ITEM_ID: string = "categoryFacetTags";

  constructor(config: Config<CategoryFacetsFieldGroup> = null) {
    super((()=> ConfigUtils.apply(Config(CategoryFacetsFieldGroup, {

      items: [
        Config(DisplayField, {
          ui: DisplayFieldSkin.SUB_LABEL_READONLY.getSkin(),
          itemId: "emptyFacetsMsgField",
          value: LivecontextStudioPlugin_properties.CategoryFacetFiltersGroup_emptyText,
          plugins: [
            Config(BindPropertyPlugin, {
              componentProperty: "hidden",
              bindTo: this.getHideNoFacetsMsgExpression(config),
            }),
          ],
        }),
        Config(Container, {
          itemId: CategoryFacetsFieldGroup.FACET_FIELDS_ITEM_ID,
          plugins: [
            Config(BindComponentsPlugin, {
              valueExpression: config.facetsExpression,
              configBeanParameterName: "facet",
              reuseComponents: false,
              clearBeforeUpdate: true,
              template: Config(CategoryFacetField, {
                bindTo: config.bindTo,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                structPropertyName: config.structPropertyName,
              }),
            }),
            Config(VerticalSpacingPlugin, { modifier: SpacingBEMEntities.VERTICAL_SPACING_MODIFIER_200 }),
          ],
          layout: Config(AnchorLayout, { manageOverflow: false }),
        }),
      ],
      ...ConfigUtils.append({
        plugins: [
          Config(SetPropertyEmptyTextPlugin, {
            bindTo: config.bindTo,
            propertyName: config.facetValuePropertyName,
          }),
          Config(BindReadOnlyPlugin, {
            forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            bindTo: config.bindTo,
          }),
          Config(VerticalSpacingPlugin, { modifier: SpacingBEMEntities.VERTICAL_SPACING_MODIFIER_200 }),
        ],
      }),
      layout: Config(AnchorLayout, { manageOverflow: false }),
    }), config))());
  }
}

export default CategoryFacetsFieldGroup;
