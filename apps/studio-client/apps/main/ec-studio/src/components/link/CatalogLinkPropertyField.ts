import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import LinkListThumbnailColumn
  from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import BEMPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BEMPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import ContextMenuPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ContextMenuPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Actions_properties from "@coremedia/studio-client.main.editor-components/sdk/actions/Actions_properties";
import LinkListRemoveAction from "@coremedia/studio-client.main.editor-components/sdk/actions/LinkListRemoveAction";
import OpenEntitiesInTabsAction
  from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenEntitiesInTabsAction";
import OnlyIf from "@coremedia/studio-client.main.editor-components/sdk/plugins/OnlyIf";
import Button from "@jangaroo/ext-ts/button/Button";
import Fill from "@jangaroo/ext-ts/toolbar/Fill";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogDragDropVisualFeedback from "../../dragdrop/CatalogDragDropVisualFeedback";
import CatalogHelper from "../../helper/CatalogHelper";
import CatalogLinkContextMenu from "./CatalogLinkContextMenu";
import CatalogLinkListColumn from "./CatalogLinkListColumn";
import CatalogLinkPropertyFieldBase from "./CatalogLinkPropertyFieldBase";
import CatalogLinkToolbar from "./CatalogLinkToolbar";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";

interface CatalogLinkPropertyFieldConfig extends Config<CatalogLinkPropertyFieldBase>, Partial<Pick<CatalogLinkPropertyField,
  "additionalToolbarItems" |
  "hideOpenInTab" |
  "hideRemove" |
  "hideCatalog"
  >> {
}

class CatalogLinkPropertyField extends CatalogLinkPropertyFieldBase {
  declare Config: CatalogLinkPropertyFieldConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogLinkPropertyField";

  constructor(config: Config<CatalogLinkPropertyField> = null) {
    config.showChangeReferenceButton = config.showChangeReferenceButton === true ? true : false;
    if (config.showChangeReferenceButton) {
      config.replaceOnDrop = false;
    }
    super((() => ConfigUtils.apply(Config(CatalogLinkPropertyField, {
      readOnlyValueExpression: this.getReadOnlyVE(config),
      dropAreaHandler: CatalogHelper.getInstance().openCatalog,
      htmlFeedback: CatalogDragDropVisualFeedback.getHtmlFeedback,
      selectedPositionsExpression: this.getSelectedPositionsVE(),
      selectedValuesExpression: this.getSelectedVE(),
      linkListWrapper: this.getLinkListWrapper(config),

      ...ConfigUtils.append({
        actionList: [
          new LinkListRemoveAction({
            text: Actions_properties.Action_deleteSelectedLinks_text,
            tooltip: Actions_properties.Action_deleteSelectedLinks_tooltip,
            linkListWrapper: this.getLinkListWrapper(config),
            selectedValuesExpression: this.getSelectedVE(),
            selectedPositionsExpression: this.getSelectedPositionsVE(),
          }),
          new OpenEntitiesInTabsAction({ entitiesValueExpression: this.getSelectedVE() }),
        ],
      }),

      fields: [
        Config(DataField, {
          name: "type",
          mapping: "",
          convert: CatalogLinkPropertyFieldBase.convertTypeLabel,
        }),
        Config(DataField, {
          name: "typeCls",
          mapping: "",
          convert: CatalogLinkPropertyFieldBase.convertTypeCls,
        }),
        Config(DataField, {
          name: "id",
          mapping: "",
          convert: CatalogLinkPropertyFieldBase.convertIdLabel,
        }),
        Config(DataField, {
          name: "catalog",
          mapping: "catalog.name",
        }),
        Config(DataField, {
          name: "multiCatalog",
          mapping: "store.multiCatalog",
        }),
        Config(DataField, {
          name: "name",
          mapping: "",
          convert: CatalogLinkPropertyFieldBase.convertNameLabel,
        }),
        Config(DataField, {
          name: "status",
          mapping: "",
          convert: CatalogLinkPropertyFieldBase.convertLifecycleStatus,
        }),
        Config(DataField, {
          name: "thumbnailImage",
          mapping: "",
          convert: CatalogLinkPropertyFieldBase.convertThumbnail,
        }),
      ],

      columns: [
        Config(LinkListThumbnailColumn, { hidden: config.showThumbnails === false }),
        Config(CatalogLinkListColumn, {
          catalogObjectIdDataIndex: "id",
          catalogObjectNameDataIndex: "name",
          catalogNameDataIndex: "catalog",
          multiCatalogDataIndex: "multiCatalog",
          hideCatalog: config.hideCatalog,
          flex: 1,
        }),
        Config(StatusColumn),
      ],
      tbar: Config(CatalogLinkToolbar, {
        linkListWrapper: this.getLinkListWrapper(config),
        additionalToolbarItems: config.additionalToolbarItems,
        bindTo: config.bindTo,
        hideOpenInTab: config.hideOpenInTab,
        hideRemove: config.hideRemove,
        ...ConfigUtils.append({
          plugins: [
            Config(BindPropertyPlugin, {
              bindTo: ValueExpressionFactory.createFromValue(config.showChangeReferenceButton),
              ifUndefined: false,
              componentProperty: "hidden",
            }),
          ],
        }),
      }),
      dockedItems: [
        Config(Toolbar, {
          ui: ToolbarSkin.EMBEDDED_FOOTER.getSkin(),
          dock: "bottom",
          items: [
            Config(Fill),
            Config(Button, {
              itemId: "changeCategoryReferenceButton",
              ui: ButtonSkin.SIMPLE.getSkin(),
              text: ECommerceStudioPlugin_properties.Catalog_replace_reference_button,
              handler: bind(this, this.removeCategoryReference),
              plugins: [
                Config(BindPropertyPlugin, {
                  bindTo: ValueExpressionFactory.createFromValue(!config.showChangeReferenceButton),
                  ifUndefined: true,
                  componentProperty: "hidden",
                }),
                Config(BindPropertyPlugin, {
                  bindTo: this.isChangeReferenceDisabledVE(),
                  componentProperty: "disabled",
                }),
              ],
            }),
          ],
        }),
      ],
      ...ConfigUtils.append({
        plugins: [
          // Workaround because dockedItems breaks bottom line boarding when IssuePlugin is used
          Config(OnlyIf, {
            condition: () => config.showChangeReferenceButton,
            then: [
              // Overwrite the order-bottom-width extjs class
              Config(BEMPlugin, { block: "cm-catalog-link-property-field" }),
            ],
          }),
          Config(OnlyIf, {
            condition: () => !config.showChangeReferenceButton,
            then: [
              Config(ContextMenuPlugin, {
                contextMenu: Config(CatalogLinkContextMenu, {
                  linkListWrapper: this.getLinkListWrapper(config),
                  hideOpenInTab: config.hideOpenInTab,
                  hideRemove: config.hideRemove,
                }),
              }),
            ],
          }),
        ],
      }),
    }), config))());
  }

  additionalToolbarItems: Array<any> = null;

  hideOpenInTab: boolean = false;

  hideRemove: boolean = false;

  hideCatalog: boolean = false;
}

export default CatalogLinkPropertyField;
