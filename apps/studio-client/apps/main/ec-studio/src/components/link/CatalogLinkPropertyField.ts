import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import ContextMenuPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ContextMenuPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Actions_properties from "@coremedia/studio-client.main.editor-components/sdk/actions/Actions_properties";
import LinkListRemoveAction from "@coremedia/studio-client.main.editor-components/sdk/actions/LinkListRemoveAction";
import OpenEntitiesInTabsAction from "@coremedia/studio-client.main.editor-components/sdk/actions/OpenEntitiesInTabsAction";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogDragDropVisualFeedback from "../../dragdrop/CatalogDragDropVisualFeedback";
import CatalogHelper from "../../helper/CatalogHelper";
import CatalogLinkContextMenu from "./CatalogLinkContextMenu";
import CatalogLinkListColumn from "./CatalogLinkListColumn";
import CatalogLinkPropertyFieldBase from "./CatalogLinkPropertyFieldBase";
import CatalogLinkToolbar from "./CatalogLinkToolbar";

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
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(CatalogLinkPropertyField, {
      readOnlyValueExpression: this$.getReadOnlyVE(config),
      dropAreaHandler: bind(CatalogHelper.getInstance(), CatalogHelper.getInstance().openCatalog),
      htmlFeedback: CatalogDragDropVisualFeedback.getHtmlFeedback,
      selectedPositionsExpression: this$.getSelectedPositionsVE(),
      selectedValuesExpression: this$.getSelectedVE(),
      linkListWrapper: this$.getLinkListWrapper(config),

      ...ConfigUtils.append({
        actionList: [
          new LinkListRemoveAction({
            text: Actions_properties.Action_deleteSelectedLinks_text,
            tooltip: Actions_properties.Action_deleteSelectedLinks_tooltip,
            linkListWrapper: this$.getLinkListWrapper(config),
            selectedValuesExpression: this$.getSelectedVE(),
            selectedPositionsExpression: this$.getSelectedPositionsVE(),
          }),
          new OpenEntitiesInTabsAction({ entitiesValueExpression: this$.getSelectedVE() }),
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
        linkListWrapper: this$.getLinkListWrapper(config),
        additionalToolbarItems: config.additionalToolbarItems,
        bindTo: config.bindTo,
        hideOpenInTab: config.hideOpenInTab,
        hideRemove: config.hideRemove,
      }),

      ...ConfigUtils.append({
        plugins: [
          Config(ContextMenuPlugin, {
            contextMenu: Config(CatalogLinkContextMenu, {
              linkListWrapper: this$.getLinkListWrapper(config),
              hideOpenInTab: config.hideOpenInTab,
              hideRemove: config.hideRemove,
            }),
          }),
        ],
      }),
    }), config));
  }

  additionalToolbarItems: Array<any> = null;

  hideOpenInTab: boolean = false;

  hideRemove: boolean = false;

  hideCatalog: boolean = false;
}

export default CatalogLinkPropertyField;
