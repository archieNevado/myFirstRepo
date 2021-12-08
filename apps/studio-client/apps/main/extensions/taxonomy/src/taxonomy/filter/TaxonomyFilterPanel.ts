import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BEMPlugin";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import HorizontalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/HorizontalSpacingPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Container from "@jangaroo/ext-ts/container/Container";
import ComboBox from "@jangaroo/ext-ts/form/field/ComboBox";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import ToolTip from "@jangaroo/ext-ts/tip/ToolTip";
import TableView from "@jangaroo/ext-ts/view/Table";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import OpenTaxonomyChooserAction from "../action/OpenTaxonomyChooserAction";
import TaxonomySearchField from "../selection/TaxonomySearchField";
import TaxonomyFilterPanelBase from "./TaxonomyFilterPanelBase";

interface TaxonomyFilterPanelConfig extends Config<TaxonomyFilterPanelBase> {
}

/**
 * A filter for the collection view that allows to select the taxonomy keywords of documents
 * to be included in the search result.
 */
class TaxonomyFilterPanel extends TaxonomyFilterPanelBase {
  declare Config: TaxonomyFilterPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyFilterPanel";

  static override readonly BLOCK: BEMBlock = new BEMBlock("cm-taxonomy-filter-panel");

  constructor(config: Config<TaxonomyFilterPanel> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomyFilterPanel, {
      itemId: ConfigUtils.asString(config.itemId || config.taxonomyId),
      title: TaxonomyStudioPlugin_properties[config.taxonomyId],
      items: [
        Config(Container, {
          plugins: [
            Config(HorizontalSpacingPlugin),
          ],
          items: [
            Config(TaxonomySearchField, {
              searchResultExpression: this.getSearchResultExpression(),
              resetOnBlur: true,
              flex: 1,
              itemId: "taxonomyFilterSearchField",
              taxonomyIdExpression: ValueExpressionFactory.createFromValue(config.taxonomyId),
              siteSelectionExpression: config.siteSelectionExpression,
              listeners: {
                afterrender: (combo: ComboBox): void => {
                  const toolTip = new ToolTip(Config(ToolTip));
                  const toolTipText = TaxonomyStudioPlugin_properties.TaxonomySearch_empty_text;
                  toolTip.setTarget(combo.getEl());
                  toolTip.mon(toolTip, "afterrender", (): void =>
                    toolTip.update(toolTipText));
                },
              },
            }),
            Config(IconButton, {
              iconCls: TaxonomyStudioPlugin_properties.Taxonomy_action_icon,
              text: TaxonomyStudioPlugin_properties.Taxonomy_action_tooltip,
              tooltip: TaxonomyStudioPlugin_properties.Taxonomy_action_tooltip,
              baseAction: new OpenTaxonomyChooserAction({
                propertyValueExpression: this.getSelectionExpression(),
                taxonomyIdExpression: ValueExpressionFactory.createFromValue(config.taxonomyId),
              }),
            }),
          ],
          layout: Config(HBoxLayout),
        }),
        Config(GridPanel, {
          hideHeaders: true,
          itemId: TaxonomyFilterPanelBase.TAXONOMY_NODE_GRID_ITEM_ID,
          plugins: [
            Config(BindListPlugin, {
              bindTo: this.getSelectionExpression(),
              fields: [
                Config(DataField, { name: "name" }),
                Config(DataField, { name: "html" }),
              ],
            }),
            Config(BindVisibilityPlugin, {
              bindTo: this.getSelectionExpression(),
              transformer: (selection: Array<any>): boolean => selection.length > 0,
            }),
          ],
          columns: [
            Config(Column, {
              stateId: "name",
              sortable: false,
              dataIndex: "name",
              renderer: bind(this, this.taxonomyRenderer),
              flex: 1,
            }),
          ],
          viewConfig: Config(TableView, { stripeRows: false }),
        }),
      ],
      ...ConfigUtils.append({
        plugins: [
          Config(BEMPlugin, { block: TaxonomyFilterPanel.BLOCK }),
          Config(VerticalSpacingPlugin),
        ],
      }),

    }), config))());
  }
}

export default TaxonomyFilterPanel;
