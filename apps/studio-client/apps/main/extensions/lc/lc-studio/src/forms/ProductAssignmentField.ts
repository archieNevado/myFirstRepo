import TimelineForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/TimelineForm";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import SearchContentLinkSuggester from "@coremedia/studio-client.content-link-list-models/SearchContentLinkSuggester";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

interface ProductAssignmentFieldConfig extends Config<PropertyFieldGroup> {
}

class ProductAssignmentField extends PropertyFieldGroup {
  declare Config: ProductAssignmentFieldConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.productAssignmentField";

  constructor(config: Config<ProductAssignmentField> = null) {
    super(ConfigUtils.apply(Config(ProductAssignmentField, {
      title: LivecontextStudioPlugin_properties.CMVideo_productAssignments_title,
      collapsed: false,
      padding: "6 6 6 0",
      plugins: [
        Config(BindVisibilityPlugin, { bindTo: ProductAssignmentField.#getVisibleForViewTypeValueExpression(config.bindTo, "shoppable") }),
      ],
      items: [
        Config(LinkListPropertyField, {
          linkType: "CMTeasable",
          itemId: "default",
          showThumbnails: true,
          propertyName: "timeLine.defaultTarget",
          bindTo: config.bindTo,
          maxCardinality: 1,
          linkSuggester: new SearchContentLinkSuggester({
            contentValueExpression: config.bindTo,
            linkTypeName: "CMProductTeaser",
          }),
        }),
        Config(TimelineForm),
      ],
    }), config));
  }

  /**
   * Checks if the view type value matches the given string value.
   * The string value may be in in CSV format.
   * @param bindTo the ValueExpression that contains the content to check the view type value for
   * @param viewTypes the view type names to compare in CSV format
   */
  static #getVisibleForViewTypeValueExpression(bindTo: ValueExpression, viewTypes: string): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): boolean => {
      const values = viewTypes.split(",");
      const viewTypeArray: Array<any> = bindTo.extendBy(ContentPropertyNames.PROPERTIES + ".viewtype").getValue();
      if (viewTypeArray === undefined) {
        return undefined;
      }

      if (!viewTypeArray || viewTypeArray.length === 0) {
        return false;
      }

      const viewType: Content = viewTypeArray[0];
      for (const vt of values as string[]) {
        if (vt === viewType.getName()) {
          return true;
        }
      }

      return false;
    });
  }
}

export default ProductAssignmentField;
