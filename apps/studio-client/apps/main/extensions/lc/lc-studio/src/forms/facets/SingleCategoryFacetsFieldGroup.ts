import FacetUtil from "@coremedia-blueprint/studio-client.main.ec-studio/components/search/filters/FacetUtil";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BindReadOnlyPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindReadOnlyPlugin";
import ShowIssuesPlugin from "@coremedia/studio-client.main.editor-components/sdk/validation/ShowIssuesPlugin";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../../LivecontextStudioPlugin_properties";
import SingleCategoryFacetsFieldGroupBase from "./SingleCategoryFacetsFieldGroupBase";

interface SingleCategoryFacetsFieldGroupConfig extends Config<SingleCategoryFacetsFieldGroupBase> {
}

class SingleCategoryFacetsFieldGroup extends SingleCategoryFacetsFieldGroupBase {
  declare Config: SingleCategoryFacetsFieldGroupConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.singleCategoryFacetsFieldGroup";

  constructor(config: Config<SingleCategoryFacetsFieldGroup> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(SingleCategoryFacetsFieldGroup, {

      items: [
        Config(LocalComboBox, {
          itemId: "facetCombo",
          fieldLabel: LivecontextStudioPlugin_properties["CMProductList_localSettings.productList.facet.text"],
          emptyText: LivecontextStudioPlugin_properties["CMProductList_localSettings.productList.facet.emptyText"],
          displayField: "value",
          valueField: "id",
          labelSeparator: "",
          labelAlign: "top",
          anchor: "100%",
          ...ConfigUtils.append({
            plugins: [
              Config(BindListPlugin, {
                bindTo: this$.getFacetNamesExpression(config),
                fields: [
                  Config(DataField, {
                    name: "id",
                    encode: false,
                  }),
                  Config(DataField, {
                    name: "value",
                    convert: FacetUtil.localizeFacetLabel,
                  }),
                ],
              }),
              Config(BindReadOnlyPlugin, {
                bindTo: config.bindTo,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              }),
              Config(ShowIssuesPlugin, {
                bindTo: config.bindTo,
                ifUndefined: "",
                propertyName: config.facetNamePropertyName,
              }),
              Config(PropertyFieldPlugin, { propertyName: config.facetNamePropertyName }),
            ],
          }),
        }),
        Config(LocalComboBox, {
          itemId: "facetValueCombo",
          fieldLabel: LivecontextStudioPlugin_properties["CMProductList_localSettings.productList.facetValue.text"],
          emptyText: LivecontextStudioPlugin_properties["CMProductList_localSettings.productList.facetValue.emptyText"],
          displayField: "value",
          valueField: "id",
          labelSeparator: "",
          labelAlign: "top",
          anchor: "100%",
          ...ConfigUtils.append({
            plugins: [
              Config(BindListPlugin, {
                bindTo: this$.getFacetValuesExpression(config),
                fields: [
                  Config(DataField, {
                    name: "id",
                    encode: false,
                  }),
                  Config(DataField, { name: "value" }),
                ],
              }),
              Config(BindReadOnlyPlugin, {
                bindTo: config.bindTo,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              }),
              Config(ShowIssuesPlugin, {
                bindTo: config.bindTo,
                ifUndefined: "",
                propertyName: config.facetValuePropertyName,
              }),
              Config(PropertyFieldPlugin, { propertyName: config.facetValuePropertyName }),
            ],
          }),
        }),
      ],
      ...ConfigUtils.append({
        plugins: [
          Config(VerticalSpacingPlugin),
        ],
      }),
      layout: Config(AnchorLayout, { manageOverflow: false }),
    }), config));
  }
}

export default SingleCategoryFacetsFieldGroup;
