import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import RemoteService from "@coremedia/studio-client.client-core-impl/data/impl/RemoteService";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import StatefulComboBox from "@coremedia/studio-client.ext.ui-components/components/StatefulComboBox";
import ILinkListWrapper from "@coremedia/studio-client.link-list-models/ILinkListWrapper";
import Ext from "@jangaroo/ext-ts";
import XTemplate from "@jangaroo/ext-ts/XTemplate";
import JsonStore from "@jangaroo/ext-ts/data/JsonStore";
import Model from "@jangaroo/ext-ts/data/Model";
import AjaxProxy from "@jangaroo/ext-ts/data/proxy/Ajax";
import JsonReader from "@jangaroo/ext-ts/data/reader/Json";
import QuickTipManager from "@jangaroo/ext-ts/tip/QuickTipManager";
import { as, asConfig, bind, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyRenderFactory from "../rendering/TaxonomyRenderFactory";
import TaxonomySearchComboRenderer from "../rendering/TaxonomySearchComboRenderer";
import AbortingAjaxProxy from "./AbortingAjaxProxy";
import TaxonomyLinkListGridPanel from "./TaxonomyLinkListGridPanel";
import TaxonomySearchField from "./TaxonomySearchField";

interface TaxonomySearchFieldBaseConfig extends Config<StatefulComboBox>, Partial<Pick<TaxonomySearchFieldBase,
  "taxonomyIdExpression" |
  "siteSelectionExpression" |
  "linkListWrapper"
>> {
}

class TaxonomySearchFieldBase extends StatefulComboBox {
  declare Config: TaxonomySearchFieldBaseConfig;

  /**
   * Name of the property of search suggestions result containing the search hits.
   * @eventType hits
   */
  static readonly #NODES: string = "nodes";

  /**
   * Name of the property of search suggestions result item containing the number of appearances of the suggested value.
   */
  protected static readonly SUGGESTION_COUNT: string = "size";

  static autoSuggestResultTpl: XTemplate = new XTemplate(
    "<tpl for=\".\"><div style=\"width:" + TaxonomySearchComboRenderer.LIST_WIDTH + "px;padding: 2px 0px; \">{" + "html" + "}</div></tpl>",
  );

  taxonomyIdExpression: ValueExpression = null;

  /**
   * Optional list of contents to be excluded from the search suggestion result.
   */
  linkListWrapper: ILinkListWrapper = null;

  #searchResultExpression: ValueExpression = null;

  #showSelectionPath: boolean = false;

  // Consequently its always originates from onNodeSelection().
  #cachedValue: any;

  siteSelectionExpression: ValueExpression<string> = null;

  #resetOnBlur: boolean = false;

  #valueManuallyChanged: boolean = false;

  #httpProxy: AjaxProxy = null;

  constructor(config: Config<TaxonomySearchField> = null) {

    let superConfig: Config<TaxonomySearchField>;
    super((()=>{
      if (config.siteSelectionExpression) {
        this.siteSelectionExpression = config.siteSelectionExpression;
        this.siteSelectionExpression.addChangeListener(bind(this, this.#siteSelectionChanged));
      }
      this.#searchResultExpression = config.searchResultExpression;
      this.#showSelectionPath = config.showSelectionPath;

      if (this.#showSelectionPath === undefined) {
        this.#showSelectionPath = true;
      }

      this.#resetOnBlur = config.resetOnBlur;

      superConfig = Config(TaxonomySearchField);
      return Config(TaxonomySearchField, Ext.apply(superConfig, config));
    })());

    this.getStore().addListener("datachanged", bind(this, this.validate));
    config.linkListWrapper && this.getStore().getFilters().add(this.getFilterFn(config.linkListWrapper));

    this.addListener("afterrender", bind(this, this.validate));
    this.addListener("focus", bind(this, this.doFocus));
    this.addListener("select", bind(this, this.#onNodeSelection));
    this.addListener("keydown", (): void => {
      this.#valueManuallyChanged = true;
      QuickTipManager.getQuickTip().hide();
    });
  }

  protected override afterRender(): any {
    super.afterRender();
    if (this.multiSelect) {
      const $this = this;
      const parent = this.findParentByType(TaxonomyLinkListGridPanel.xtype);
      if (parent) {
        parent.addListener("afterlayout", function(): void {
          const p = $this.getPicker();
          if (p && p.rendered) {
            p.setY(parent.getHeight() + parent.getY());
          }
        });
      }
    }
  }

  override onDestroy(): any {
    super.onDestroy();
    this.siteSelectionExpression && this.siteSelectionExpression.removeChangeListener(bind(this, this.#siteSelectionChanged));
  }

  getSearchSuggestionsDataProxy(config: Config<TaxonomySearchField>): AjaxProxy {
    if (!this.#httpProxy) {
      const reader = Config(JsonReader);
      reader.rootProperty = TaxonomySearchFieldBase.#NODES;

      //noinspection JSUnusedGlobalSymbols
      this.#httpProxy = Ext.create(AbortingAjaxProxy, {
        url: RemoteService.calculateRequestURI("taxonomies/find?" + TaxonomySearchFieldBase.#getTaxonomyIdParam(TaxonomySearchFieldBase.#getTaxonomyId(config)) + this.#getSiteParam(config.siteSelectionExpression)),
        reader: reader,
      });
    }

    return this.#httpProxy;
  }

  static #getTaxonomyId(config: Config<TaxonomySearchField>): string {
    if (config.taxonomyIdExpression === undefined) {
      return "";
    }

    return config.taxonomyIdExpression.getValue();
  }

  #siteSelectionChanged(): void {
    this.reset();
    let taxonomyId = "";
    if (this.taxonomyIdExpression) {
      taxonomyId = this.taxonomyIdExpression.getValue();
    }

    const proxy = as(this.getStore(), JsonStore).getProxy();
    if (is(proxy, AjaxProxy) && proxy.isInstance) {
      proxy.setUrl(RemoteService.calculateRequestURI("taxonomies/find?" + TaxonomySearchFieldBase.#getTaxonomyIdParam(taxonomyId) + this.#getSiteParam(this.siteSelectionExpression)));
    }
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Creates the HTML that is displayed for the search hits.
   */
  protected static renderHTML(component: any, record: Model): string {
    if (record.data.path) {
      const nodes: Array<any> = record.data.path.nodes;
      const renderer = TaxonomyRenderFactory.createSearchComboRenderer(nodes, record.data[TaxonomyNode.PROPERTY_REF]);
      renderer.doRender();
      return renderer.getHtml();
    }
    return null;
  }

  #isValidTagPrefix(v: string): boolean {
    return (!v || this.getStore().getCount() > 0);
  }

  protected tagPrefixValidValidator(): any {
    let value = this.getValue();
    if (value === null) {
      return true;
    }

    if (Array.isArray(value)) {
      if (value.length === 0) {
        return true;
      }
      value = value[0];
    }

    if (!this.#valueManuallyChanged
            || value === this.#cachedValue
            || (this.minChars && value && as(value, String).length < this.minChars)
            || this.#isValidTagPrefix(value)) {
      return true;
    } else {
      return TaxonomyStudioPlugin_properties.TaxonomySearch_no_hit;
    }
  }

  /**
   * The on focus event handler for the textfield/combo, resets the status of the field.
   */
  doFocus(): void {
    this.lastQuery = undefined;
    if (!this.#resetOnBlur) {
      as(this.getStore(), JsonStore).load({});
      if (this.getValue()) {
        this.#cachedValue = this.getValue();
      }
    } else {
      this.setValue("");
    }
  }

  /**
   * Appends the taxonomy id param to the search query if set.
   * @return
   */
  static #getTaxonomyIdParam(taxId: string): string {
    if (taxId) {
      return "taxonomyId=" + taxId;
    }
    return "";
  }

  /**
   * Returns the site param if there is a site selected.
   * @return
   */
  #getSiteParam(siteVE: ValueExpression): string {
    if (siteVE && siteVE.getValue()) {
      return "&site=" + siteVE.getValue();
    }
    return "";
  }

  /**
   * Sets the selected path as string of resets the textfield after selection.
   * @param selection
   */
  #setSelectionString(selection: any): void {
    if (this.#showSelectionPath) {
      this.setValue(selection);
    } else {
      this.setValue("");
    }
  }

  //not static
  //noinspection JSMethodCanBeStatic
  getEmptyTextText(config: Config<TaxonomySearchField>): string {
    if (config.emptyText) {
      return asConfig(this).emptyText;
    }

    if (config.bindTo) {
      return TaxonomyStudioPlugin_properties.TaxonomySearch_empty_linklist_text;
    }
    return TaxonomyStudioPlugin_properties.TaxonomySearch_empty_search_text;
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Handler function for node selection.
   */
  #onNodeSelection(combo: TaxonomySearchField, record: Model | Model[]): void {
    if (!record) {
      this.setValue("");
      return;
    }

    let records = [];
    if (Array.isArray(record)) {
      records = record;
    } else {
      records.push(record);
    }

    for (let i = 0; i < records.length; i++) {
      const r = records[i];
      // Harden for possibly malformed 'select' events.
      if (!r || !r.data || !r.data[TaxonomyNode.PROPERTY_REF]) {
        this.setValue("");
        return;
      }
      const content = as(beanFactory._.getRemoteBean(r.data[TaxonomyNode.PROPERTY_REF]), Content);
      content.load((c: Content): void => {
        this.#setSelectionString(r.data.name);
        this.#cachedValue = r.data.name;
        const path = new TaxonomyNodeList(r.data.path.nodes);
        this.#searchResultExpression.setValue(path);
        this.setValue("");
      });
    }
  }

  protected getFilterFn(linkListWrapper: ILinkListWrapper): AnyFunction {
    return (record: Model): boolean => {
      const componentId: string = record.data[TaxonomyNode.PROPERTY_REF];
      return linkListWrapper.getLinks().every((content: Content): boolean =>
        componentId !== content.getUriPath(),
      );
    };
  }
}

export default TaxonomySearchFieldBase;
