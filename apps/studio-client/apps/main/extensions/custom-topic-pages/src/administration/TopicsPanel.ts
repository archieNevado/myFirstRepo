import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BEMPlugin";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import ContainerSkin from "@coremedia/studio-client.ext.ui-components/skins/ContainerSkin";
import DisplayFieldSkin from "@coremedia/studio-client.ext.ui-components/skins/DisplayFieldSkin";
import TableViewSkin from "@coremedia/studio-client.ext.ui-components/skins/TableViewSkin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import Container from "@jangaroo/ext-ts/container/Container";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import Labelable from "@jangaroo/ext-ts/form/Labelable";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import RowSelectionModel from "@jangaroo/ext-ts/selection/RowModel";
import TableView from "@jangaroo/ext-ts/view/Table";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TopicPages_properties from "../TopicPages_properties";
import FilterPanel from "./FilterPanel";
import TaxonomyCombo from "./TaxonomyCombo";
import TopicsPanelBase from "./TopicsPanelBase";

interface TopicsPanelConfig extends Config<TopicsPanelBase> {
}

class TopicsPanel extends TopicsPanelBase {
  declare Config: TopicsPanelConfig;

  static readonly TOPICS_PANEL_BLOCK: BEMBlock = new BEMBlock("cm-topics-panel");

  static override readonly xtype: string = "com.coremedia.blueprint.studio.topicpages.config.topicsPanel";

  constructor(config: Config<TopicsPanel> = null) {
    super((()=> ConfigUtils.apply(Config(TopicsPanel, {
      layout: "border",
      items: [
        Config(Container, {
          region: "north",
          id: "topicPagesFormNorth",
          ui: ContainerSkin.LIGHT.getSkin(),
          items: [
            Config(Container, {
              ui: ContainerSkin.GRID_200.getSkin(),
              items: [
                Config(FieldContainer, {
                  flex: 2,
                  fieldLabel: TopicPages_properties.TopicPages_search_title,
                  items: [
                    Config(FilterPanel, {
                      filterExpression: this.getFilterValueExpression(),
                      id: "topicPagesFilterPanel",
                      applyFilterFunction: bind(this, this.reload),
                      emptyText: TopicPages_properties.TopicPages_search_emptyText,
                    }),

                  ],
                }),
                Config(TaxonomyCombo, {
                  id: "topicPagesTaxonomyCombo",
                  flex: 1,
                  filterExpression: this.getFilterValueExpression(),
                  selectionExpression: this.getTaxonomySelectionExpression(),
                }),
              ],
              layout: Config(HBoxLayout, { align: "stretch" }),
              defaultType: Labelable["xtype"],
              defaults: Config<Labelable>({
                labelSeparator: "",
                labelAlign: "top",
              }),
            }),
            Config(DisplayField, {
              padding: "0 10px 10px",
              id: "topicPagesFilteredLabel",
              ui: DisplayFieldSkin.SUB_LABEL_READONLY.getSkin(),
              value: TopicPages_properties.TopicPages_filtered,
            }),

          ],
          layout: Config(AnchorLayout),
        }),
        Config(Container, {
          region: "center",
          layout: "border",
          items: [
            Config(GridPanel, {
              region: "center",
              id: "topicPagesGrid",
              itemId: "topicsGrid",
              hideHeaders: false,
              scrollable: true,
              forceFit: true,
              plugins: [
                Config(BindDisablePlugin, {
                  bindTo: this.getTopicsExpression(),
                  componentProperty: "disableSelection",
                }),
                Config(BindListPlugin, {
                  bindTo: this.getTopicsExpression(),
                  fields: [
                    Config(DataField, { name: "name" }),
                    Config(DataField, { name: "topic" }),
                    Config(DataField, { name: "enabled" }),
                    Config(DataField, { name: "page" }),
                  ],
                }),
                Config(BEMPlugin, { block: TopicsPanel.TOPICS_PANEL_BLOCK }),
              ],
              columns: [
                Config(Column, {
                  stateId: "name",
                  header: TopicPages_properties.TopicPages_grid_header_name,
                  width: 140,
                  sortable: false,
                  dataIndex: "name",
                  renderer: TopicsPanelBase.nameRenderer,
                  flex: 1,
                }),
                Config(Column, {
                  stateId: "page",
                  header: TopicPages_properties.TopicPages_grid_header_page,
                  sortable: false,
                  width: 200,
                  dataIndex: "name",
                  listeners: { "click": bind(this, this.onPageColumnClick) },
                  renderer: bind(this, this.pageRenderer),
                }),
              ],
              selModel: new RowSelectionModel({ mode: "SINGLE" }),
              viewConfig: Config(TableView, {
                ui: TableViewSkin.LIGHT.getSkin(),
                deferEmptyText: false,
                emptyText: "",
                stripeRows: true,
              }),
            }),
          ],
        }),
      ],

    }), config))());
  }
}

export default TopicsPanel;
