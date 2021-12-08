import MultiItemSplitter from "@coremedia/studio-client.ext.ui-components/layouts/MultiItemSplitter";
import SplitterSkin from "@coremedia/studio-client.ext.ui-components/skins/SplitterSkin";
import TabBarSkin from "@coremedia/studio-client.ext.ui-components/skins/TabBarSkin";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import ActionsToolbarContainer from "@coremedia/studio-client.main.editor-components/sdk/desktop/ActionsToolbarContainer";
import TabExpandPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/TabExpandPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PreviewPanel from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanel";
import Container from "@jangaroo/ext-ts/container/Container";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import TabBar from "@jangaroo/ext-ts/tab/Bar";
import TabPanel from "@jangaroo/ext-ts/tab/Panel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CommerceFormToolbar from "./CommerceFormToolbar";
import CommerceWorkAreaTabBase from "./CommerceWorkAreaTabBase";

interface CommerceWorkAreaTabConfig extends Config<CommerceWorkAreaTabBase>, Partial<Pick<CommerceWorkAreaTab,
  "subTabs" |
  "object"
>> {
}

class CommerceWorkAreaTab extends CommerceWorkAreaTabBase {
  declare Config: CommerceWorkAreaTabConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceWorkAreaTab";

  constructor(config: Config<CommerceWorkAreaTab> = null) {
    super((()=> ConfigUtils.apply(Config(CommerceWorkAreaTab, {

      items: [
        Config(Container, {
          itemId: CommerceWorkAreaTabBase.DOCUMENT_CONTAINER_ITEM_ID,
          layout: "vbox",
          flex: 1,
          height: "100%",
          minWidth: 470,
          items: [
            Config(CommerceFormToolbar, {
              bindTo: this.getEntityExpression(),
              width: "100%",
              ui: ToolbarSkin.WORKAREA.getSkin(),
              collapseHandler: (): void => this.collapsePanel(CommerceWorkAreaTabBase.DOCUMENT_CONTAINER_ITEM_ID),
            }),
            Config(TabPanel, {
              collapsible: false,
              scrollable: true,
              activeItem: 0,
              flex: 1,
              itemId: "tabs",
              height: "100%",
              width: "100%",
              items: config.subTabs,
              plugins: [
                Config(TabExpandPlugin),
              ],
              defaultType: DocumentForm.xtype,
              defaults: Config<DocumentForm>({ bindTo: this.getEntityExpression() }),
              tabBar: Config(TabBar, { ui: TabBarSkin.WORKAREA_PANEL.getSkin() }),
            }),
          ],
        }),
        /* SEPARATOR*/
        Config(MultiItemSplitter, {
          itemId: CommerceWorkAreaTabBase.PREVIEW_SPLIT_BAR_ITEM_ID,
          stateId: "premular-splitter",
          ui: SplitterSkin.PREMULAR.getSkin(),
          width: "4px",
          collapseOnDblClick: false,
        }),

        /* PREVIEW */
        Config(PreviewPanel, {
          itemId: CommerceWorkAreaTabBase.PREVIEW_PANEL_ITEM_ID,
          flex: 1,
          height: "100%",
          minWidth: 300,
          collapsible: true,
          collapseHandler: (): void => this.collapsePanel(CommerceWorkAreaTabBase.PREVIEW_PANEL_ITEM_ID),
          bindTo: this.getEntityExpression(),
          reloadAfterChangesDelay: 1000,
        }),

        /* ACTIONS TOOLBAR */
        Config(ActionsToolbarContainer),
      ],
      layout: Config(HBoxLayout),
    }), config))());
  }

  subTabs: Array<any> = null;

  object: any = null;
}

export default CommerceWorkAreaTab;
