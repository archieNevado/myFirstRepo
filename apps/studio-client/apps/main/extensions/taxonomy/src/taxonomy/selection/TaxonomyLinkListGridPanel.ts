import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import LinkListViewDragDropPlugin from "@coremedia/studio-client.ext.link-list-components/plugins/LinkListViewDragDropPlugin";
import StatefulTableView from "@coremedia/studio-client.ext.ui-components/components/StatefulTableView";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import BindSelectionPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindSelectionPlugin";
import PanelSkin from "@coremedia/studio-client.ext.ui-components/skins/PanelSkin";
import TableViewSkin from "@coremedia/studio-client.ext.ui-components/skins/TableViewSkin";
import AriaVisibilityPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/AriaVisibilityPlugin";
import LinkListBindListPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListBindListPlugin";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import RowSelectionModel from "@jangaroo/ext-ts/selection/RowModel";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyLinkListGridPanelBase from "./TaxonomyLinkListGridPanelBase";

interface TaxonomyLinkListGridPanelConfig extends Config<TaxonomyLinkListGridPanelBase> {
}

class TaxonomyLinkListGridPanel extends TaxonomyLinkListGridPanelBase {
  declare Config: TaxonomyLinkListGridPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyLinkListGridPanel";

  protected static readonly DD_GROUPS: Array<any> = ["ContentDD", "ContentLinkDD"];

  protected static readonly DRAG_GROUPS: Array<any> = ["ContentLinkDD"];

  constructor(config: Config<TaxonomyLinkListGridPanel> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomyLinkListGridPanel, {
      hideHeaders: true,
      ui: PanelSkin.NO_VALIDATION.getSkin(),
      forceFit: true,

      plugins: [
        Config(LinkListBindListPlugin, { bindTo: config.linkListWrapper.getVE() }),
        Config(AriaVisibilityPlugin, { bindTo: config.linkListWrapper.getVE() }),
        Config(BindSelectionPlugin, {
          selectedPositions: config.selectedPositionsExpression,
          selectedValues: config.selectedValuesExpression,
        }),
      ],
      columns: [
        Config(Column, {
          stateId: "name",
          sortable: false,
          dataIndex: "name",
          renderer: bind(this, this.taxonomyRenderer),
        }),
      ],
      selModel: new RowSelectionModel({ mode: config.selectionMode || "MULTI" }),
      viewConfig: Config(StatefulTableView, {
        ui: TableViewSkin.LARGE_CELLS.getSkin(),
        deferEmptyText: false,
        stripeRows: false,
        plugins: [
          Config(LinkListViewDragDropPlugin, {
            linkListWrapper: config.linkListWrapper,
            dragGroups: TaxonomyLinkListGridPanel.DRAG_GROUPS,
            dropGroups: TaxonomyLinkListGridPanel.DD_GROUPS,
          }),
          Config(BindPropertyPlugin, {
            componentProperty: "readOnly",
            bindTo: config.readOnlyValueExpression || ValueExpressionFactory.createFromValue(false),
          }),
        ],
      }),

    }), config))());
  }
}

export default TaxonomyLinkListGridPanel;
