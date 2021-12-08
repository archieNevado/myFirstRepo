import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import LinkListViewDragZone from "@coremedia/studio-client.ext.link-list-components/LinkListViewDragZone";
import LinkListBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/LinkListBEMEntities";
import StatefulTableView from "@coremedia/studio-client.ext.ui-components/components/StatefulTableView";
import BEMPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BEMPlugin";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import BindSelectionPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindSelectionPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import DisplayFieldSkin from "@coremedia/studio-client.ext.ui-components/skins/DisplayFieldSkin";
import TableViewSkin from "@coremedia/studio-client.ext.ui-components/skins/TableViewSkin";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import BindReadOnlyPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindReadOnlyPlugin";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import Column from "@jangaroo/ext-ts/grid/column/Column";
import GridViewDragDropPlugin from "@jangaroo/ext-ts/grid/plugin/DragDrop";
import RowSelectionModel from "@jangaroo/ext-ts/selection/RowModel";
import Fill from "@jangaroo/ext-ts/toolbar/Fill";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomySuggestionsLinkListPanelBase from "./TaxonomySuggestionsLinkListPanelBase";

interface TaxonomySuggestionsLinkListPanelConfig extends Config<TaxonomySuggestionsLinkListPanelBase>, Partial<Pick<TaxonomySuggestionsLinkListPanel,
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "propertyName"
>> {
}

class TaxonomySuggestionsLinkListPanel extends TaxonomySuggestionsLinkListPanelBase {
  declare Config: TaxonomySuggestionsLinkListPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomySuggestionsLinkListPanel";

  constructor(config: Config<TaxonomySuggestionsLinkListPanel> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomySuggestionsLinkListPanel, {
      hideHeaders: true,

      plugins: [
        Config(BindDisablePlugin, {
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          bindTo: config.bindTo,
        }),
        Config(BindListPlugin, {
          bindTo: this.getSuggestionsExpression(),
          fields: [
            Config(DataField, {
              name: "name",
              ifUnreadable: bind(this, this.formatUnreadableName),
              mapping: (config["contentPropertyPath"] ? config["contentPropertyPath"] + "." : "") + "name",
            }),
            Config(DataField, { name: "id" }),
            Config(DataField, { name: "weight" }),
          ],
        }),
        Config(BindSelectionPlugin, {
          selectedPositions: this.getSelectedPositionsExpression(),
          selectedValues: this.getSelectedValuesExpression(),
        }),
        Config(BEMPlugin, {
          block: LinkListBEMEntities.BLOCK,
          bodyElement: LinkListBEMEntities.ELEMENT_LIST,
          modifier: LinkListBEMEntities.MODIFIER_NO_TAIL,
        }),
      ],
      dockedItems: [
        Config(Toolbar, {
          dock: "top",
          maskOnDisable: false,
          ui: ToolbarSkin.HEADER_GRID_100.getSkin(),
          items: [
            Config(DisplayField, {
              ui: ConfigUtils.asString(DisplayFieldSkin.BOLD),
              value: TaxonomyStudioPlugin_properties.TaxonomyLinkList_suggestions_title,
            }),
            Config(Fill),
            Config(Button, {
              ui: ButtonSkin.SIMPLE.getSkin(),
              text: TaxonomyStudioPlugin_properties.TaxonomyLinkList_suggestions_add_all,
              handler: bind(this, this.addAllKeywordsHandler),
              plugins: [
                Config(BindDisablePlugin, {
                  forceReadOnlyValueExpression: this.getAddAllDisabledVE(config.forceReadOnlyValueExpression),
                  bindTo: config.bindTo,
                }),
              ],
            }),
          ],
        }),
        Config(Toolbar, {
          dock: "bottom",
          focusableContainer: false,
          maskOnDisable: false,
          ui: ToolbarSkin.EMBEDDED_FOOTER.getSkin(),
          items: [
            Config(Container, { flex: 1 }),
            Config(Button, {
              ui: ButtonSkin.SIMPLE.getSkin(),
              text: TaxonomyStudioPlugin_properties.TaxonomyLinkList_suggestions_reload,
              handler: bind(this, this.reloadKeywordsHandler),
            }),
          ],
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
      selModel: new RowSelectionModel({ mode: "MULTI" }),
      viewConfig: Config(StatefulTableView, {
        ui: TableViewSkin.DEFAULT.getSkin(),
        deferEmptyText: false,
        stripeRows: false,
        emptyText: TaxonomyStudioPlugin_properties.TaxonomySuggestions_empty_text,
        plugins: [
          Config(GridViewDragDropPlugin, {
            ddGroup: "ContentLinkDD",
            enableDrop: false,
            dragZone: Config(LinkListViewDragZone),
          }),
          Config(BindReadOnlyPlugin, {
            bindTo: config.bindTo,
            forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          }),
        ],
      }),

    }), config))());
  }

  /**
   * The content bean value expression.
   */
  bindTo: ValueExpression = null;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  /**
   * The property name of the content that should contains the taxonomy to display.
   */
  propertyName: string = null;
}

export default TaxonomySuggestionsLinkListPanel;
