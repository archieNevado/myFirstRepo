import BlueprintDocumentTypes_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintDocumentTypes_properties";
import VisibilityDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/VisibilityDocumentForm";
import AugmentationUtil from "@coremedia-blueprint/studio-client.main.ec-studio/helper/AugmentationUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPluginBase from "../LivecontextStudioPluginBase";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import CommerceCatalogHierarchyForm from "../desktop/CommerceCatalogHierarchyForm";
import CommerceChildCategoriesForm from "../desktop/CommerceChildCategoriesForm";

interface CommerceAugmentedCategoryStructureFormConfig extends Config<DocumentForm> {
}

/**
 * The structure form of an augmented category. Content based.
 */
class CommerceAugmentedCategoryStructureForm extends DocumentForm {
  declare Config: CommerceAugmentedCategoryStructureFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceAugmentedCategoryStructureForm";

  static readonly VISIBILITY_ITEM_ID: string = "visibility";

  #catalogObjectExpression: ValueExpression = null;

  constructor(config: Config<CommerceAugmentedCategoryStructureForm> = null) {
    super((()=>{
      this.#catalogObjectExpression = AugmentationUtil.getCatalogObjectExpression(config.bindTo);
      return ConfigUtils.apply(Config(CommerceAugmentedCategoryStructureForm, {
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
              Config(CommerceChildCategoriesForm, { itemId: "commerceChildCategories" }),
              Config(Component, { height: 6 }),
              Config(VisibilityDocumentForm, { itemId: CommerceAugmentedCategoryStructureForm.VISIBILITY_ITEM_ID }),
            ],
          }),
          /* let's have a property editor to fix legacy content (new children are stored in struct) */
          Config(PropertyFieldGroup, {
            title: BlueprintDocumentTypes_properties.CMExternalChannel_legacy_children_text,
            itemId: "navigationChildren",
            ...ConfigUtils.append({
              plugins: [
                Config(BindVisibilityPlugin, { bindTo: AugmentationUtil.hasChildCategoriesExpression(config.bindTo) }),
              ],
            }),
            items: [
              Config(LinkListPropertyField, {
                bindTo: config.bindTo,
                propertyName: "children",
                itemId: "navigationChildrenLinkList",
              }),
            ],
          }),
          Config(CommerceCatalogHierarchyForm, { bindTo: this.#catalogObjectExpression }),
        ],

      }), config);
    })());
  }
}

export default CommerceAugmentedCategoryStructureForm;
