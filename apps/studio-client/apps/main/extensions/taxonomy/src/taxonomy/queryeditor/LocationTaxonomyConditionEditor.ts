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

interface LocationTaxonomyConditionEditorConfig extends Config<TaxonomyConditionEditorBase>, Partial<Pick<LocationTaxonomyConditionEditor,
  "taxonomyId"
>> {
}

class LocationTaxonomyConditionEditor extends TaxonomyConditionEditorBase {
  declare Config: LocationTaxonomyConditionEditorConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.locationTaxonomyConditionEditor";

  /**
   * the id of the taxonomy whose tree is used to add items from.
   */
  taxonomyId: string = "Location";

  constructor(config: Config<LocationTaxonomyConditionEditor> = null) {
    super(ConfigUtils.apply(Config(LocationTaxonomyConditionEditor, {

      items: [
        Config(TaxonomyLinkListPropertyField, {
          bindTo: config.bindTo,
          itemId: "taxonomyLinkList",
          taxonomyIdExpression: ValueExpressionFactory.createFromValue(config.taxonomyId),
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          labelSeparator: ":",
          anchor: "100%",
          propertyName: "localSettings.fq.locationtaxonomy",
          taxonomyLinkListSideButtonVerticalAdjustment: -12,
          taxonomyLinkListSideButtonHorizontalAdjustment: -6,
          taxonomyLinkListSideButtonRenderToFunction: (host: Component): Element => {
            const parrentContainer = as(host.findParentByType(LocationTaxonomyConditionEditor), LocationTaxonomyConditionEditor);
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

export default LocationTaxonomyConditionEditor;
