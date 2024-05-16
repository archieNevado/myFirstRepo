import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import ReadOnlyCatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/ReadOnlyCatalogLinkPropertyField";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import CollapsiblePanel from "@coremedia/studio-client.main.editor-components/sdk/premular/CollapsiblePanel";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPluginBase from "../LivecontextStudioPluginBase";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

interface CommerceCatalogHierarchyFormConfig extends Config<CollapsiblePanel>, Partial<Pick<CommerceCatalogHierarchyForm,
  "bindTo"
>> {
}

class CommerceCatalogHierarchyForm extends CollapsiblePanel {
  declare Config: CommerceCatalogHierarchyFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceCatalogHierarchyForm";

  bindTo: ValueExpression = null;

  constructor(config: Config<CommerceCatalogHierarchyForm> = null) {
    super(ConfigUtils.apply(Config(CommerceCatalogHierarchyForm, {
      title: LivecontextStudioPlugin_properties.Commerce_Category_Hierarchy_title,
      itemId: "catalogHierarchy",
      defaults: {
        bindTo: config.bindTo,
        labelSeparator: "",
        labelAlign: "top",
      },
      ...ConfigUtils.append({
        plugins: [
          Config(BindPropertyPlugin, {
            componentProperty: "collapsed",
            boundValueChanged: (component: CollapsiblePanel, valueExp: ValueExpression): void => {
              if (valueExp.getValue() === true) {
                component.collapse("top", true);
              } else if (
                valueExp.getValue() === false) {
                component.expand(false);
              }
            },
            bindTo: LivecontextStudioPluginBase.isContentLedValueExpression(config.bindTo),
          }),
        ],
      }),
      items: [
        Config(CollapsiblePanel, {
          title: LivecontextStudioPlugin_properties.Commerce_Category_PropertyGroup_parentCategory_title,
          itemId: "parentCategory",
          items: [
            Config(ReadOnlyCatalogLinkPropertyField, {
              propertyName: CatalogObjectPropertyNames.PARENT,
              emptyText: LivecontextStudioPlugin_properties.Commerce_Category_PropertyGroup_parentCategory_emptyText,
            }),
          ],
          defaults: {
            bindTo: config.bindTo,
            labelSeparator: "",
            labelAlign: "top",
          },
        }),

        Config(CollapsiblePanel, {
          title: LivecontextStudioPlugin_properties.Commerce_Category_PropertyGroup_subcategories_title,
          itemId: "subcategories",
          items: [
            Config(ReadOnlyCatalogLinkPropertyField, {
              propertyName: CatalogObjectPropertyNames.SUB_CATEGORIES,
              emptyText: LivecontextStudioPlugin_properties.Commerce_Category_subcategories_emptyText,
            }),
          ],
          defaults: {
            bindTo: config.bindTo,
            labelSeparator: "",
            labelAlign: "top",
          },
        }),

        Config(CollapsiblePanel, {
          title: LivecontextStudioPlugin_properties.Commerce_Category_PropertyGroup_products_title,
          itemId: "products",
          items: [
            Config(ReadOnlyCatalogLinkPropertyField, {
              propertyName: CatalogObjectPropertyNames.PRODUCTS,
              emptyText: LivecontextStudioPlugin_properties.Commerce_Category_products_emptyText,
            }),
          ],
          defaults: {
            bindTo: config.bindTo,
            labelSeparator: "",
            labelAlign: "top",
          },
        }),
      ],
      layout: Config(VBoxLayout, { align: "stretch" }),
    }), config));
  }
}

export default CommerceCatalogHierarchyForm;
