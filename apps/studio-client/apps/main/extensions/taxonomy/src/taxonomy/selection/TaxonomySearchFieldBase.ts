import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import RemoteService from "@coremedia/studio-client.client-core-impl/data/impl/RemoteService";
import Logger from "@coremedia/studio-client.client-core-impl/logging/Logger";
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
import { as, asConfig, bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyRenderFactory from "../rendering/TaxonomyRenderFactory";
import TaxonomySearchComboRenderer from "../rendering/TaxonomySearchComboRenderer";
import AbortingAjaxProxy from "./AbortingAjaxProxy";
import TaxonomySearchField from "./TaxonomySearchField";

interface TaxonomySearchFieldBaseConfig extends Config<StatefulComboBox>, Partial<Pick<TaxonomySearchFieldBase,
  "taxonomyIdExpression" |
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

  #siteSelectionExpression: ValueExpression = null;

  #resetOnBlur: boolean = false;

  #valueManuallyChanged: boolean = false;

  #httpProxy: AjaxProxy = null;

  constructor(config: Config<TaxonomySearchField> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    this$.#siteSelectionExpression = config.siteSelectionExpression;
    if (this$.#siteSelectionExpression) {
      this$.#siteSelectionExpression.addChangeListener(bind(this$, this$.#siteSelectionChanged));
    }
    this$.#searchResultExpression = config.searchResultExpression;
    this$.#showSelectionPath = config.showSelectionPath;

    if (this$.#showSelectionPath === undefined) {
      this$.#showSelectionPath = true;
    }

    this$.#resetOnBlur = config.resetOnBlur;

    const superConfig = Config(TaxonomySearchField);
    super(Config(TaxonomySearchField, Ext.apply(superConfig, config)));

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

  getSearchSuggestionsDataProxy(config: Config<TaxonomySearchField>): AjaxProxy {
    if (!this.#httpProxy) {
      const reader = Config(JsonReader);
      reader.rootProperty = TaxonomySearchFieldBase.#NODES;

      //noinspection JSUnusedGlobalSymbols
      this.#httpProxy = Ext.create(AbortingAjaxProxy, {
        failure: (response: XMLHttpRequest): void =>
          Logger.info("Taxonomy search request failed:" + response.responseText)
        ,
        method: "GET",
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

    (asConfig((this.getStore() as JsonStore)).proxy as AjaxProxy).setUrl(RemoteService.calculateRequestURI("taxonomies/find?" + TaxonomySearchFieldBase.#getTaxonomyIdParam(taxonomyId) + this.#getSiteParam(this.#siteSelectionExpression)));
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
      const html = renderer.getHtml();
      return html;
    }
    return null;
  }

  #isValidTagPrefix(): boolean {
    return (!this.getValue() || this.getStore().getCount() > 0);
  }

  protected tagPrefixValidValidator(): any {
    if (!this.#valueManuallyChanged
            || this.getValue() === this.#cachedValue
            || (this.minChars && this.getValue() && (this.getValue() as String).length < this.minChars)
            || this.#isValidTagPrefix()) {
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
      (this.getStore() as JsonStore).load({});
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
  #onNodeSelection(combo: TaxonomySearchField, record: Model, index: number): void {
    // Harden for possibly malformed 'select' events.
    if (!record || !record.data || !record.data[TaxonomyNode.PROPERTY_REF]) {
      this.setValue("");
      return;
    }
    const content = as(beanFactory._.getRemoteBean(record.data[TaxonomyNode.PROPERTY_REF]), Content);
    content.load((c: Content): void => {
      this.#setSelectionString(record.data.name);
      this.#cachedValue = record.data.name;
      const path = new TaxonomyNodeList(record.data.path.nodes);
      this.#searchResultExpression.setValue(path);
      this.setValue("");
    });
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
