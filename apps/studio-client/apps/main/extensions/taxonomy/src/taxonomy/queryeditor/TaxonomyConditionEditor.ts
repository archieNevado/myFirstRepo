import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import Component from "@jangaroo/ext-ts/Component";
import Element from "@jangaroo/ext-ts/dom/Element";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyLinkListPropertyField from "../selection/TaxonomyLinkListPropertyField";
import TaxonomyConditionEditorBase from "./TaxonomyConditionEditorBase";

interface TaxonomyConditionEditorConfig extends Config<TaxonomyConditionEditorBase>, Partial<Pick<TaxonomyConditionEditor,
  "taxonomyId"
>> {
}

class TaxonomyConditionEditor extends TaxonomyConditionEditorBase {
  declare Config: TaxonomyConditionEditorConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyConditionEditor";

  /**
   * the id of the taxonomy whose tree is used to add items from.
   */
  taxonomyId: string = "Subject";

  constructor(config: Config<TaxonomyConditionEditor> = null) {
    super(ConfigUtils.apply(Config(TaxonomyConditionEditor, {

      items: [
        /* Although the base class initializes the struct (and so the linkType could also be determined by the struct)
     make sure only the specified content type may be added to the link list. This also makes it not necessary
     to create the empty struct before the LinkListPropertyField's constructor is called.  */
        Config(TaxonomyLinkListPropertyField, {
          bindTo: config.bindTo,
          linkType: config.contentType,
          itemId: "taxonomyLinkList",
          taxonomyIdExpression: ValueExpressionFactory.createFromValue(config.taxonomyId),
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          labelSeparator: ":",
          anchor: "100%",
          propertyName: "localSettings.fq." + config.propertyName,
          taxonomyLinkListSideButtonVerticalAdjustment: -6,
          taxonomyLinkListSideButtonHorizontalAdjustment: -6,
          taxonomyLinkListSideButtonRenderToFunction: (host: Component): Element => {
            const parrentContainer = as(host.findParentByType(TaxonomyConditionEditor), TaxonomyConditionEditor);
            return parrentContainer.el;
          },
          ...ConfigUtils.append({
            plugins: [
              Config(BindDisablePlugin, {
                bindTo: config.bindTo,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              }),
            ],
          }),
        }),
      ],
      layout: Config(AnchorLayout),

    }), config));
  }
}

export default TaxonomyConditionEditor;
