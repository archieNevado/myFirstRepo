import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMElement from "@coremedia/studio-client.ext.ui-components/models/bem/BEMElement";
import BEMMixin from "@coremedia/studio-client.ext.ui-components/plugins/BEMMixin";
import BEMPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BEMPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import ContainerSkin from "@coremedia/studio-client.ext.ui-components/skins/ContainerSkin";
import PanelSkin from "@coremedia/studio-client.ext.ui-components/skins/PanelSkin";
import SplitterSkin from "@coremedia/studio-client.ext.ui-components/skins/SplitterSkin";
import TabBarSkin from "@coremedia/studio-client.ext.ui-components/skins/TabBarSkin";
import TextfieldSkin from "@coremedia/studio-client.ext.ui-components/skins/TextfieldSkin";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import Container from "@jangaroo/ext-ts/container/Container";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Splitter from "@jangaroo/ext-ts/resizer/Splitter";
import TabBar from "@jangaroo/ext-ts/tab/Bar";
import TabPanel from "@jangaroo/ext-ts/tab/Panel";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Spacer from "@jangaroo/ext-ts/toolbar/Spacer";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import CutKeywordAction from "../action/CutKeywordAction";
import PasteKeywordAction from "../action/PasteKeywordAction";
import TaxonomySearchField from "../selection/TaxonomySearchField";
import TaxonomyExplorerColumn from "./TaxonomyExplorerColumn";
import TaxonomyExplorerPanelBase from "./TaxonomyExplorerPanelBase";
import TaxonomyStandAloneDocumentView from "./TaxonomyStandAloneDocumentView";

interface TaxonomyExplorerPanelConfig extends Config<TaxonomyExplorerPanelBase>, Partial<Pick<TaxonomyExplorerPanel,
  "siteSelectionExpression" |
  "searchResultExpression"
>> {
}

class TaxonomyExplorerPanel extends TaxonomyExplorerPanelBase {
  declare Config: TaxonomyExplorerPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyExplorerPanel";

  /**
   * The itemId of the first taxonomy explorer toolbar separator.
   */
  static readonly TAXONOMY_EXPLORER_SEP_FIRST_ITEM_ID: string = "taxonomyExplorerSepFirst";

  /**
   * The itemId of the second taxonomy explorer toolbar separator.
   */
  static readonly TAXONOMY_EXPLORER_SEP_SECOND_ITEM_ID: string = "taxonomyExplorerSepSecond";

  static readonly TAXONOMY_EXPLORER_PANEL_BLOCK: BEMBlock = new BEMBlock("cm-taxonomy-explorer-panel");

  static readonly TAXONOMY_EXPLORER_PANEL_ELEMENT: BEMElement = TaxonomyExplorerPanel.TAXONOMY_EXPLORER_PANEL_BLOCK.createElement("explorer-panel");

  static readonly TAXONOMY_DOCUMENT_VIEW_ITEM_ID: string = "taxonomyStandaloneDocumentView";

  constructor(config: Config<TaxonomyExplorerPanel> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomyExplorerPanel, {
      ui: PanelSkin.FRAME.getSkin(),
      bodyCls: TaxonomyExplorerPanel.TAXONOMY_EXPLORER_PANEL_ELEMENT.getCSSClass(),
      layout: Config(HBoxLayout, { align: "stretch" }),
      plugins: [
        Config(BEMPlugin, { block: TaxonomyExplorerPanel.TAXONOMY_EXPLORER_PANEL_BLOCK }),
      ],
      items: [
        /*Tree Panel Left*/
        Config(Container, {
          id: "taxSplitPanelLeft",
          flex: 1,
          items: [
            /*Fix Root items panel*/
            Config(TaxonomyExplorerColumn, {
              width: 200,
              id: "taxonomyRootsColumn",
              itemId: "taxonomyRootsColumn",
              siteSelectionExpression: config.siteSelectionExpression,
              clipboardValueExpression: this.getClipboardValueExpression(),
              selectedNodeExpression: this.getSelectedValueExpression(),
              ...Config<BEMMixin>({ bemElement: "root" }),
            }),
            /*Dynamic columns panel*/
            Config(Container, {
              itemId: "columnsContainer",
              id: "columnsContainer",
              flex: 1,
              ui: ContainerSkin.LIGHT.getSkin(),
              autoScroll: true,
              defaultType: TaxonomyExplorerColumn.xtype,
              defaults: Config<TaxonomyExplorerColumn>({
                width: 200,
                ...Config<BEMMixin>({ bemElement: "non-root" }),
              }),
              items: [],
              layout: Config(HBoxLayout, { align: "stretch" }),
            }),
          ],
          layout: Config(HBoxLayout, { align: "stretch" }),
        }),

        Config(Splitter, {
          width: "4px",
          ui: SplitterSkin.DARK.getSkin(),
        }),

        Config(Container, {
          itemId: "preview",
          layout: Config(VBoxLayout, { align: "stretch" }),
          items: [
            Config(TabPanel, {
              id: "taxonomyTabs",
              items: [
              ],
              tabBar: Config(TabBar, { ui: TabBarSkin.WORKAREA_PANEL.getSkin() }),
            }),

            /*Display Panel Right*/
            Config(TaxonomyStandAloneDocumentView, {
              id: TaxonomyExplorerPanel.TAXONOMY_DOCUMENT_VIEW_ITEM_ID,
              width: 400,
              flex: 1,
              minWidth: 200,
              bindTo: this.getDisplayedTaxonomyContentExpression(),
              forceReadOnlyValueExpression: this.getForceReadOnlyValueExpression(),
              ...Config<BEMMixin>({ bemElement: "form" }),
            }),
          ],
        }),
      ],
      tbar: Config(Toolbar, {
        ui: ToolbarSkin.WORKAREA.getSkin(),
        items: [
          Config(IconButton, {
            itemId: "delete",
            disabled: true,
            text: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_remove_button_label,
            tooltip: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_remove_button_label,
            handler: bind(this, this.deleteNodes),
            iconCls: CoreIcons_properties.trash_bin,
          }),
          Config(IconButton, {
            itemId: "add",
            disabled: true,
            text: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_add_button_label,
            tooltip: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_add_button_label,
            iconCls: CoreIcons_properties.add_tag,
            handler: bind(this, this.createChildNode),
          }),
          Config(Separator, {
            itemId: TaxonomyExplorerPanel.TAXONOMY_EXPLORER_SEP_FIRST_ITEM_ID,
            hidden: true,
          }),
          Config(IconButton, {
            itemId: "reload",
            disabled: false,
            id: "taxonomy-reload-button",
            text: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_reload_button_label,
            tooltip: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_reload_button_label,
            iconCls: CoreIcons_properties.reload,
            handler: bind(this, this.reload),
          }),
          Config(Separator, {
            itemId: TaxonomyExplorerPanel.TAXONOMY_EXPLORER_SEP_SECOND_ITEM_ID,
            hidden: true,
          }),
          Config(IconButton, {
            itemId: "cut",
            disabled: true,
            text: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_cut_button_label,
            tooltip: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_cut_button_label,
            iconCls: CoreIcons_properties.cut,
            baseAction: new CutKeywordAction({
              clipboardValueExpression: this.getClipboardValueExpression(),
              selectionExpression: this.getSelectedValueExpression(),
            }),
          }),
          Config(IconButton, {
            itemId: "paste",
            disabled: true,
            text: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_paste_button_label,
            tooltip: TaxonomyStudioPlugin_properties.TaxonomyExplorerPanel_paste_button_label,
            iconCls: CoreIcons_properties.paste,
            baseAction: new PasteKeywordAction({
              clipboardValueExpression: this.getClipboardValueExpression(),
              selectionExpression: this.getSelectedValueExpression(),
            }),
          }),
          Config(Spacer),
          Config(TaxonomySearchField, {
            searchResultExpression: config.searchResultExpression,
            siteSelectionExpression: config.siteSelectionExpression,
            resetOnBlur: true,
            width: 300,
            ui: TextfieldSkin.DEFAULT.getSkin(),
            itemId: "taxonomySearchField",
            id: "taxonomySearchField",
          }),
        ],
        defaultType: IconButton.xtype,
        defaults: Config<IconButton>({
          scale: "medium",
          ui: ButtonSkin.WORKAREA.getSkin(),
        }),
      }),

    }), config))());
  }

  /**
   * Contains the active selected site, selected by site chooser component.
   */
  siteSelectionExpression: ValueExpression = null;

  /**
   * Contains a node list with the path of the of the selected node.
   */
  searchResultExpression: ValueExpression = null;
}

export default TaxonomyExplorerPanel;
