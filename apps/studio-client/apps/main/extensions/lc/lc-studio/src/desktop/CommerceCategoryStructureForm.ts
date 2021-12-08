import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import ReadOnlyCatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/ReadOnlyCatalogLinkPropertyField";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPluginBase from "../LivecontextStudioPluginBase";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import SelectSubCategoriesRadioGroup from "../components/SelectSubCategoriesRadioGroup";
import CommerceCatalogHierarchyForm from "./CommerceCatalogHierarchyForm";

interface CommerceCategoryStructureFormConfig extends Config<DocumentForm> {
}

/**
 * The structure form of a catalog category. Commerce object based.
 */
class CommerceCategoryStructureForm extends DocumentForm {
  declare Config: CommerceCategoryStructureFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceCategoryStructureForm";

  constructor(config: Config<CommerceCategoryStructureForm> = null) {
    super(ConfigUtils.apply(Config(CommerceCategoryStructureForm, {
      title: LivecontextStudioPlugin_properties.Commerce_Tab_structure_title,

      items: [
        Config(PropertyFieldGroup, {
          title: LivecontextStudioPlugin_properties.Commerce_Category_Website_Navigation,
          itemId: "websiteNavigation",
          ...ConfigUtils.append({
            plugins: [
              Config(BindVisibilityPlugin, { bindTo: LivecontextStudioPluginBase.isContentLedValueExpression(config.bindTo) }),
            ],
          }),
          items: [
            Config(SelectSubCategoriesRadioGroup, {
              itemId: "inheritOrSelectRadioGroup",
              bindTo: ValueExpressionFactory.createFromValue(false),
              disabled: true,
            }),
            Config(Component, { height: 6 }),
            Config(ReadOnlyCatalogLinkPropertyField, {
              propertyName: CatalogObjectPropertyNames.SUB_CATEGORIES,
              emptyText: LivecontextStudioPlugin_properties.Commerce_Category_subcategories_emptyText,
            }),
          ],
        }),
        Config(CommerceCatalogHierarchyForm),
      ],

    }), config));
  }
}

export default CommerceCategoryStructureForm;
