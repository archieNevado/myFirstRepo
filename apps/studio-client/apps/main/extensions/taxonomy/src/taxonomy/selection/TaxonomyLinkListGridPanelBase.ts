import IdHelper from "@coremedia/studio-client.cap-rest-client/common/IdHelper";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import LinkListDropArea from "@coremedia/studio-client.ext.link-list-components/LinkListDropArea";
import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import SideButtonMixin from "@coremedia/studio-client.ext.ui-components/mixins/SideButtonMixin";
import ValidationStateMixin from "@coremedia/studio-client.ext.ui-components/mixins/ValidationStateMixin";
import BeanRecord from "@coremedia/studio-client.ext.ui-components/store/BeanRecord";
import createComponentSelector from "@coremedia/studio-client.ext.ui-components/util/createComponentSelector";
import ILinkListWrapper from "@coremedia/studio-client.link-list-models/ILinkListWrapper";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import Ext from "@jangaroo/ext-ts";
import Events from "@jangaroo/ext-ts/Events";
import StringUtil from "@jangaroo/ext-ts/String";
import DropTarget from "@jangaroo/ext-ts/dd/DropTarget";
import ScrollManager from "@jangaroo/ext-ts/dd/ScrollManager";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import { as, asConfig, bind, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyUtil from "../TaxonomyUtil";
import TaxonomyRenderFactory from "../rendering/TaxonomyRenderFactory";

interface TaxonomyLinkListGridPanelBaseConfig extends Config<GridPanel>, Config<ValidationStateMixin>, Config<SideButtonMixin>, Config<HidableMixin>, Partial<Pick<TaxonomyLinkListGridPanelBase,
  "linkListWrapper" |
  "readOnlyValueExpression" |
  "selectedPositionsExpression" |
  "selectedValuesExpression" |
  "bindTo" |
  "taxonomyIdExpression" |
  "selectionMode" |
  "removeCallback" |
  "hideText"
>> {
  listeners?: Events<GridPanel> & Events<ValidationStateMixin>;
}

/**
 * @private
 *
 * The application logic for a property field editor that edits
 * link lists. Links can be limited to documents of a given type.
 *
 * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
 * @see com.coremedia.cms.editor.sdk.premular.fields.LinkListPropertyField
 */
class TaxonomyLinkListGridPanelBase extends GridPanel {
  declare Config: TaxonomyLinkListGridPanelBaseConfig;

  linkListWrapper: ILinkListWrapper = null;

  readOnlyValueExpression: ValueExpression = null;

  /**
   * A ValueExpression whose value is set to the array of indexes of selected items.
   * The array is empty if nothing is selected. The change of the value doesn't update the selection of the grid.
   */
  selectedPositionsExpression: ValueExpression = null;

  /**
   * A ValueExpression whose value is set to the array of selected items.
   * The array is empty if nothing is selected.
   * The selection is updated by changing the value of this expression.
   */
  selectedValuesExpression: ValueExpression = null;

  /**
   * The premular content ValueExpression
   */
  bindTo: ValueExpression = null;

  /**
   * The taxonomy identifier configured on the server side.
   */
  taxonomyIdExpression: ValueExpression = null;

  selectionMode: "MULTI" | "SINGLE" = null;

  removeCallback: AnyFunction = null;

  #dropTarget: DropTarget = null;

  /**
   * @param config The configuration options. See the config class for details.
   *
   * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
   */
  constructor(config: Config<TaxonomyLinkListGridPanelBase> = null) {
    super(config);
    this.initValidationStateMixin();
    this.initSideButtonMixin();
    this.addListener("afterlayout", bind(this, this.#afterLayoutRefresh));

    this.on("validationStateChanged", bind(this, this.#onValidationChanged));
    this.on("validationMessageChanged", bind(this, this.#onValidationChanged));
    this.#onValidationChanged();
  }

  protected override afterRender(): void {
    super.afterRender();

    if (asConfig(this).scrollable) {
      ScrollManager.register(this.#getScrollerDom());

      this.addListener("beforedestroy", bind(this, this.#onBeforeDestroy), this, { single: true });
    }

    if (this.readOnlyValueExpression) {
      this.readOnlyValueExpression.addChangeListener(bind(this, this.#readOnlyChanged));
    }
  }

  #readOnlyChanged(): void {
    this.#refreshLinkList(true);
  }

  #onValidationChanged(): void {
    const viewValidation = as(asConfig(this).view, ValidationStateMixin);
    if (viewValidation) {
      viewValidation.validationState = this.validationState;
      viewValidation.validationMessage = this.validationMessage;
    }
    const dropArea = this.query(createComponentSelector()._xtype(LinkListDropArea.xtype).build())[0];
    if (dropArea) {
      const dropAreaValidation = as(dropArea, ValidationStateMixin);
      if (dropAreaValidation) {
        dropAreaValidation.validationState = this.validationState;
        dropAreaValidation.validationMessage = this.validationMessage;
      }
    }
  }

  #isWritable(): boolean {
    return !this.readOnlyValueExpression || !this.readOnlyValueExpression.getValue();
  }

  #onBeforeDestroy(): void {
    if (this.readOnlyValueExpression) {
      this.readOnlyValueExpression.removeChangeListener(bind(this, this.#readOnlyChanged));
    }

    // if we previously registered with the scroll manager, unregister
    // it (if we don't, it will lead to problems in IE)
    ScrollManager.unregister(this.#getScrollerDom());
  }

  /**
   * Return the DOM element associated with the scroller of the grid.
   * This method uses undocumented API.
   *
   * @return the DOM element
   */
  #getScrollerDom(): any {
    return this.getEl().dom;
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Displays each path item of a taxonomy
   * @param value
   * @param metaData
   * @param record
   *
   * @see http://docs.sencha.com/extjs/6.0.1-classic/Ext.grid.column.Column.html#cfg-renderer
   *
   * @return String
   */
  protected taxonomyRenderer(value: any, metaData: any, record: BeanRecord): string {
    const taxonomyId: string = this.taxonomyIdExpression.getValue();
    TaxonomyUtil.isEditable(taxonomyId, (editable: boolean): void => {
      if (editable) {
        let content: Content = null;
        if (this.bindTo && this.bindTo.getValue()) {
          content = this.bindTo.getValue();
        }

        TaxonomyUtil.loadTaxonomyPath(record, content, taxonomyId, (updatedRecord: BeanRecord): void => {
          //noinspection JSMismatchedCollectionQueryUpdate
          const links: Array<any> = this.linkListWrapper.getLinks() || [];
          const renderer = TaxonomyRenderFactory.createSelectedListRenderer(record.data.nodes, this.getId(), links.length > 3);
          renderer.setRenderControl(!this.readOnlyValueExpression || !this.readOnlyValueExpression.getValue());

          renderer.doRender((html: string): void => {
            if (record.data.html !== html) {
              record.data.html = html;
              record.commit(false);
            }
          });
        });
      } else {
        const msg = StringUtil.format(Editor_properties.Content_notReadable_text, IdHelper.parseContentId(record.getBean()));
        const html = "<img width=\"16\" height=\"16\" src=\"" + Ext.BLANK_IMAGE_URL + "\" loading=\"lazy\" data-qtip=\"\" />"
                + "<span>" + msg + "</span>";
        if (record.data.html !== html) {
          record.data.html = html;
          EventUtil.invokeLater((): void =>
            record.commit(false),
          );
        }
      }
    }, as(record.getBean(), Content));

    if (!record.data.html) {
      return "<div class='loading'>" + TaxonomyStudioPlugin_properties.TaxonomyLinkList_status_loading_text + "</div>";
    }
    return record.data.html;
  }

  #afterLayoutRefresh(): void {
    this.removeListener("afterlayout", bind(this, this.#afterLayoutRefresh));
    this.#refreshLinkList(false);
  }

  /**
   * Executes after layout, we have to refresh the HTML too.
   */
  #refreshLinkList(forceCommit: boolean = false): void {
    for (let i = 0; i < this.getStore().getCount(); i++) {
      this.getStore().getAt(i).data.html = null;
      if (forceCommit) {
        this.getStore().getAt(i).commit(false);
      }
    }
  }

  //noinspection JSUnusedGlobalSymbols
  /**
   * Removes the given taxonomy<br>
   * Used in TaxonomyRenderer#plusMinusClicked
   */
  plusMinusClicked(nodeRef: string): void {
    if (this.#isWritable()) {
      TaxonomyUtil.removeNodeFromSelection(this.linkListWrapper.getVE(), nodeRef, this.removeCallback);
    }
  }

  override onRemoved(destroying: boolean): void {
    this.#dropTarget && this.#dropTarget.unreg();
    super.onRemoved(destroying);
  }

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return as(this.getTitle(), String);
  }

}

interface TaxonomyLinkListGridPanelBase extends ValidationStateMixin, SideButtonMixin, HidableMixin{}

mixin(TaxonomyLinkListGridPanelBase, ValidationStateMixin, SideButtonMixin, HidableMixin);

export default TaxonomyLinkListGridPanelBase;
