import BeanState from "@coremedia/studio-client.client-core/data/BeanState";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import BindSelectionPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindSelectionPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import { as, bind, cast } from "@jangaroo/runtime";
import { serviceAgent } from "@coremedia/service-agent";
import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import QuickSearch from "@coremedia/studio-client.quicksearch-models/QuickSearch";
import DragZone from "@jangaroo/ext-ts/dd/DragZone";
import Event from "@jangaroo/ext-ts/event/Event";
import DataView from "@jangaroo/ext-ts/view/View";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import QuickSearchType from "@coremedia/studio-client.quicksearch-models/QuickSearchType";
import BEMElement from "@coremedia/studio-client.ext.ui-components/models/bem/BEMElement";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Template from "@jangaroo/ext-ts/Template";
import XTemplate from "@jangaroo/ext-ts/XTemplate";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import QuickSearchResultList from "@coremedia/studio-client.quicksearch-models/QuickSearchResultList";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import { createWorkAreaServiceDescriptor } from "@coremedia/studio-client.form-services-api/WorkAreaServiceDescriptor";
import Logger from "@coremedia/studio-client.client-core-impl/logging/Logger";
import QuickSearchDialog from "@coremedia/studio-client.main.quicksearch-components/components/QuickSearchDialog";
import QuickSearchResult from "@coremedia/studio-client.quicksearch-models/QuickSearchResult";
import Ext from "@jangaroo/ext-ts";
import DragDropVisualFeedback from "@coremedia/studio-client.ext.interaction-components/DragDropVisualFeedback";
import AugmentationUtil from "../helper/AugmentationUtil";

interface QuickSearchCommerceDataViewConfig extends Config<DataView>, Partial<Pick<QuickSearchCommerceDataView,
  "searchType" |
  "quickSearch">> {
}

class QuickSearchCommerceDataView extends DataView implements QuickSearchResultList {
  declare Config: QuickSearchCommerceDataViewConfig;

  quickSearch: QuickSearch;

  searchType: QuickSearchType<CatalogObject>;

  dragZone: DragZone = null;

  static readonly #LIST_BLOCK: BEMBlock = new BEMBlock("search-result-section");

  static readonly #LIST_ELEMENT_ENTRY: BEMElement = QuickSearchCommerceDataView.#LIST_BLOCK.createElement("entry");

  static readonly #LIST_ELEMENT_ICON: BEMElement = QuickSearchCommerceDataView.#LIST_BLOCK.createElement("icon");

  static readonly #LIST_ELEMENT_TEXT: BEMElement = QuickSearchCommerceDataView.#LIST_BLOCK.createElement("text");

  static readonly #ITEM_FOCUSED: BEMBlock = new BEMBlock("x-view-item-focused");

  static override readonly xtype: string = "com.coremedia.cms.editor.config.quickSearchCommerceDataView";

  /**
   * A value expression evaluating to an Array of Content that is to be shown.
   */
  #resultListExpression: ValueExpression = null;

  #selectionExpression: ValueExpression<any>;

  #selectionExpressionInternal: ValueExpression<Object[]>;

  static readonly #TEMPLATE: Template = new XTemplate(
    "<table class=\"" + QuickSearchCommerceDataView.#LIST_BLOCK + "\">",
    "  <tpl for=\".\">",
    "    <tr class=\"" + QuickSearchCommerceDataView.#LIST_ELEMENT_ENTRY + "\">",
    "      <td class=\"" + QuickSearchCommerceDataView.#LIST_ELEMENT_ICON + "\">",
    "        <span width=\"16\" height=\"16\" class=\"",
    "<tpl if=\"readable\">{typeClass}</tpl>",
    "\"></span>",
    "      </td>",
    "      <td class=\"" + QuickSearchCommerceDataView.#LIST_ELEMENT_TEXT + "\">",
    "        <tpl if=\"readable\">{name}</tpl>",
    "      </td>",
    "    </tr>",
    "  </tpl>",
    "</table>",
    {},
  ).compile();

  protected override afterRender(): void {
    super.afterRender();

    // listen to click events
    this.on("itemclick", bind(this, this.#listEntryClicked));
    this.on("itemdblclick", QuickSearchCommerceDataView.#listEntryDoubleClicked);
    this.on("focusenter", bind(this, this.#containerFocussed));

    const dragZoneCfg = Config(DragZone);
    dragZoneCfg.ddGroup = "ContentLinkDD";
    dragZoneCfg.scroll = false;
    (dragZoneCfg as unknown)["getDragData"] = ((e: Event): any => {
      const sourceEl = e.getTarget(this.itemSelector, 10);
      if (sourceEl) {
        const record = cast(BeanRecord, this.getRecord(sourceEl));
        const contents = [record.getBean()];

        const d: any = window.document.createElement("DIV");
        d["id"] = Ext.id();
        d.innerHTML = DragDropVisualFeedback.getHtmlFeedback(contents);
        return {
          sourceEl: sourceEl,
          repairXY: Ext.fly(sourceEl).getXY(),
          ddel: d,
          contents: contents,
          copy: true,
        };
      }
      return undefined;
    });
    dragZoneCfg["getRepairXY"] = function(this: DragZone): any {
      return this.dragData.repairXY;
    };

    //noinspection PointlessBooleanExpressionJS
    dragZoneCfg.enableHtml5DD = dragZoneCfg.enableHtml5DD !== false;
    (dragZoneCfg as unknown)["onDrag"] = (event: any): boolean => {
      return true;
    };

    this.dragZone = new DragZone(this.getEl(), dragZoneCfg);
    this.dragZone.invalidHandleTypes = {};
    this.loadMask && this.loadMask.destroy();
  }

  #listEntryClicked(dataView: DataView, record: BeanRecord, node: HTMLElement, index: number, e: Event): void {
    if (Event.ENTER === e.getKey() && e.getTarget(QuickSearchCommerceDataView.#ITEM_FOCUSED.getCSSSelector(), null, true)) {
      QuickSearchCommerceDataView.#listEntryDoubleClicked(dataView, record, node, index, e);
    } else if (e.getTarget(QuickSearchCommerceDataView.#LIST_ELEMENT_TEXT.getCSSSelector(), null, true)) {
      const item = as(record.getBean(), RemoteBean);
      this.quickSearch.setSelection(item);
    }
  }

  static #listEntryDoubleClicked(dataView: DataView, record: BeanRecord, node: HTMLElement, index: number, e: Event): void {
    // make sure only text element can be clicked
    if (e.getTarget(QuickSearchCommerceDataView.#LIST_ELEMENT_TEXT.getCSSSelector(), null, true) ||
      (Event.ENTER === e.getKey() && e.getTarget(QuickSearchCommerceDataView.#ITEM_FOCUSED.getCSSSelector(), null, true))) {
      const item = as(record.getBean(), RemoteBean);

      serviceAgent.fetchService(createWorkAreaServiceDescriptor()).then(workAreaService => {
        workAreaService.openEntitiesInTabs([item.getUriPath()]);
      }).catch((): void =>
        Logger.warn("WorkArea Service Not Available"),
      );
      QuickSearchDialog.exit();
    }
  }

  #containerFocussed(): void {
    const selection: Array<BeanRecord> = this["getSelection"]();
    if (selection && selection.length > 0) {
      const bean = selection[0].getBean();
      this.quickSearch.setSelection(bean);
      return;
    }

    const items = this.#getResultListExpression().getValue();
    if (items && items.length > 0) {
      this.#selectionExpression.setValue(items[0]);
    }
  }

  #getResultListExpression(config?: QuickSearchCommerceDataViewConfig): ValueExpression {
    if (!this.#resultListExpression) {
      this.#resultListExpression = ValueExpressionFactory.createFromFunction(() => {
        const quickSearchResult: QuickSearchResult = config.quickSearch.getSearchResults().get(config.searchType.getId());
        return quickSearchResult.getResults();
      });
    }
    return this.#resultListExpression;
  }

  #getSelectionExpressionInternal(): ValueExpression {
    if (!this.#selectionExpressionInternal) {
      this.#selectionExpressionInternal = ValueExpressionFactory.createFromValue([]);
      this.#selectionExpressionInternal.addChangeListener(bind(this, this.#selectionChanged));
    }
    return this.#selectionExpressionInternal;
  }

  #selectionChanged(ve: ValueExpression): void {
    const selection = ve.getValue();
    this.#selectionExpression.setValue(selection[0]);
  }

  getSearchModelTypeName(): string {
    return CatalogObject.name;
  }

  protected override beforeDestroy(): void {
    this.#selectionExpressionInternal && this.#selectionExpressionInternal.removeChangeListener(bind(this, this.#selectionChanged));
    this.dragZone && this.dragZone.unreg();
    super.beforeDestroy();
  }

  static getIconStyleClassForType(type: any): any {
    return AugmentationUtil.getTypeCls(type);
  }

  constructor(config: Config<QuickSearchCommerceDataView> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#selectionExpression = ValueExpressionFactory.create(QuickSearch.ACTIVE_SELECTION, config.quickSearch);

    super(ConfigUtils.apply(Config(QuickSearchCommerceDataView, {
      itemSelector: QuickSearchCommerceDataView.#LIST_ELEMENT_ENTRY.getCSSSelector(),
      singleSelect: true,
      multiSelect: false,
      deferEmptyText: true,
      tpl: QuickSearchCommerceDataView.#TEMPLATE,
      plugins: [
        Config(BindListPlugin, {
          bindTo: this$.#getResultListExpression(config),
          fields: [
            Config(DataField, {
              name: "typeClass",
              convert: QuickSearchCommerceDataView.getIconStyleClassForType,
              mapping: "",
            }),
            Config(DataField, {
              name: "name",
              ifUnreadable: "ignored",
              ifError: "error accessing data",
              mapping: "name",
            }),
            Config(DataField, {
              name: "id",
              mapping: "uriPath",
            }),
            Config(DataField, {
              name: "readable",
              mapping: BeanState.PROPERTY_NAME + ".readable",
            }),
          ],
        }),
        Config(BindSelectionPlugin, { selectedValues: this$.#getSelectionExpressionInternal() }),
      ],
    }), config));
  }
}

export default QuickSearchCommerceDataView;
