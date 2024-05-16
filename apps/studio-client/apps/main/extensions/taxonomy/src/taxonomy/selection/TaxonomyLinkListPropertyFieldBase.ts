import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import LinkListBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/LinkListBEMEntities";
import HidableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HidableMixin";
import ILinkListWrapper from "@coremedia/studio-client.link-list-models/ILinkListWrapper";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import ContentLinkListWrapper from "@coremedia/studio-client.main.editor-components/sdk/util/ContentLinkListWrapper";
import PropertyEditorUtil from "@coremedia/studio-client.main.editor-components/sdk/util/PropertyEditorUtil";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import { as, bind, is, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyUtil from "../TaxonomyUtil";
import TaxonomyLinkListGridPanel from "./TaxonomyLinkListGridPanel";
import TaxonomySearchField from "./TaxonomySearchField";

interface TaxonomyLinkListPropertyFieldBaseConfig extends Config<FieldContainer>, Config<HidableMixin>, Partial<Pick<TaxonomyLinkListPropertyFieldBase,
  "taxonomyIdExpression" |
  "bindTo" |
  "propertyName" |
  "maxCardinality" |
  "linkType" |
  "forceReadOnlyValueExpression" |
  "taxonomyLinkListWrapper" |
  "hideText"
>> {
}

class TaxonomyLinkListPropertyFieldBase extends FieldContainer {
  declare Config: TaxonomyLinkListPropertyFieldBaseConfig;

  protected static readonly GRID_PANEL_ITEM_ID: string = "gridPanel";

  protected static readonly DELETE_BUTTON_ITEM_ID: string = "delete";

  protected static readonly TAXONOMY_SEARCH_FIELD_ITEM_ID: string = "taxonomySearchField";

  protected static readonly OPEN_TAXONOMY_CHOOSER_BUTTON_ITEM_ID: string = "openTaxonomyChooserButton";

  /**
   * the id of the taxonomy whose tree is used to add items from.
   */
  taxonomyIdExpression: ValueExpression = null;

  bindTo: ValueExpression = null;

  /**
   * The property name that is edited
   */
  propertyName: string = null;

  /**
   * Optional. The maximum cardinality that the link list property should hold.
   If not specified the maximum cardinality of the property descriptor of the link list property will be applied.
   */
  maxCardinality: int = 0;

  /**
   * The allowed type of links is usually derived from the link property descriptor found through bindTo and propertyName,
   * but to override this or provide an initial value for link properties in Structs that are created by this
   * property field, you may specify a custom link type.
   */
  linkType: string = null;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  /**
   * Optional custom link list wrapper to allow a different persistence logic for this link list.
   */
  taxonomyLinkListWrapper: ILinkListWrapper = null;

  #linkListWrapper: ILinkListWrapper = null;

  #searchResultExpression: ValueExpression = null;

  #siteSelectionExpression: ValueExpression = null;

  #gridPanel: TaxonomyLinkListGridPanel = null;

  #searchField: TaxonomySearchField = null;

  #selectedValuesVE: ValueExpression = null;

  #selectedPositionsVE: ValueExpression = null;

  #modifierVE: ValueExpression = null;

  constructor(config: Config<TaxonomyLinkListPropertyFieldBase> = null) {
    super(config);

    this.#gridPanel = as(this.queryById(TaxonomyLinkListPropertyFieldBase.GRID_PANEL_ITEM_ID), TaxonomyLinkListGridPanel);
    this.#searchField = as(this.queryById(TaxonomyLinkListPropertyFieldBase.TAXONOMY_SEARCH_FIELD_ITEM_ID), TaxonomySearchField);

    this.getSearchResultExpression().addChangeListener(bind(this, this.#searchResultChanged));
  }

  protected getLinkListWrapper(config: Config<TaxonomyLinkListPropertyFieldBase>): ILinkListWrapper {
    if (!this.#linkListWrapper) {
      if (config.taxonomyLinkListWrapper) {
        this.#linkListWrapper = config.taxonomyLinkListWrapper;
        return this.#linkListWrapper;
      }

      const linkListWrapperCfg = Config<ContentLinkListWrapper>({});
      linkListWrapperCfg.bindTo = config.bindTo;
      linkListWrapperCfg.propertyName = config.propertyName;
      linkListWrapperCfg.linkTypeName = config.linkType;
      linkListWrapperCfg.maxCardinality = config.maxCardinality;
      linkListWrapperCfg.readOnlyVE = PropertyEditorUtil.createReadOnlyValueExpression(config.bindTo, config.forceReadOnlyValueExpression);
      this.#linkListWrapper = new ContentLinkListWrapper(linkListWrapperCfg);
    }
    return this.#linkListWrapper;
  }

  protected getModifierVE(config: Config<TaxonomyLinkListPropertyFieldBase>): ValueExpression {
    if (!this.#modifierVE) {
      this.#modifierVE = ValueExpressionFactory.createFromFunction((): Array<any> => {
        //noinspection JSMismatchedCollectionQueryUpdate
        const links = this.getLinkListWrapper(config).getLinks();
        if (links === undefined) {
          return undefined;
        }

        const modifiers = [];
        if (links.length === 0) {
          modifiers.push(LinkListBEMEntities.MODIFIER_EMPTY);
        }
        return modifiers;
      });
    }
    return this.#modifierVE;
  }

  /**
   * Returns the value expression for the site the taxonomy link list is working on.
   * The site is calculated from the content path. By setting a value, the REST backend looks up
   * if there is a site depending taxonomy for the given taxonomyId, otherwise the global taxonomy is used.
   * @return
   */
  protected getSiteSelectionExpression(bindTo: ValueExpression): ValueExpression {
    if (!this.#siteSelectionExpression) {
      this.#siteSelectionExpression = ValueExpressionFactory.create("site", beanFactory._.createLocalBean());
      const content = as(bindTo.getValue(), Content);
      const siteId = editorContext._.getSitesService().getSiteIdFor(content);
      if (content && !content.getPath()) {
        ValueExpressionFactory.create("path", content).loadValue((): void => {
          this.#siteSelectionExpression.setValue(siteId);
        });
      } else {
        this.#siteSelectionExpression.setValue(siteId);
      }
    }
    return this.#siteSelectionExpression;
  }

  /**
   * Fired when the field is used inside a property editor
   * and the user has selected an entry that should be added
   * to the taxonomy link list.
   */
  #searchResultChanged(): void {
    const selection: TaxonomyNodeList = this.#searchResultExpression.getValue();
    if (selection) {
      const content = session._.getConnection().getContentRepository().getContent(selection.getLeafRef());
      //do create the property expression here! see BARBUDA-1805
      const propertyValueExpression = ValueExpressionFactory.create<Content[]>("properties." + this.propertyName, this.bindTo.getValue());
      const taxonomies = propertyValueExpression.getValue();
      for (let i = 0; i < taxonomies.length; i++) {
        const child: Content = taxonomies[i];
        //check if node has already been added
        if (TaxonomyUtil.parseRestId(child) === selection.getLeafRef()) {
          return;
        }
      }
      const newTaxonomies = [];
      for (let j = 0; j < taxonomies.length; j++) {
        newTaxonomies.push(taxonomies[j]);
      }
      newTaxonomies.push(content);
      propertyValueExpression.setValue(newTaxonomies);
      this.#searchField.focus();
      this.getSelectedValuesVE().setValue([content]);
    }
  }

  protected getSelectedValuesVE(): ValueExpression {
    if (!this.#selectedValuesVE) {
      this.#selectedValuesVE = ValueExpressionFactory.createFromValue([]);
    }
    return this.#selectedValuesVE;
  }

  protected getSelectedPositionsVE(): ValueExpression {
    if (!this.#selectedPositionsVE) {
      this.#selectedPositionsVE = ValueExpressionFactory.createFromValue([]);
    }
    return this.#selectedPositionsVE;
  }

  protected getSearchResultExpression(): ValueExpression {
    if (!this.#searchResultExpression) {
      this.#searchResultExpression = ValueExpressionFactory.createFromValue([]);
    }
    return this.#searchResultExpression;
  }

  protected handleDropAreaDrop(contents: Array<any>): void {
    for (const item of contents as Content[]) {
      for (const link of this.#linkListWrapper.getLinks() as Content[]) {
        if (item.getId() === link.getId()) {
          return;
        }
      }
    }

    this.#linkListWrapper.addLinks(contents);
    this.#selectedValuesVE.setValue(contents);
  }

  protected getSiteId(bindTo: ValueExpression): string {
    if (is(bindTo.getValue(), Content)) {
      return editorContext._.getSitesService().getSiteIdFor(bindTo.getValue());
    }
    return null;
  }

  /** @private */
  set hideText(newHideText: string) {
    // The hideText is determined by the getter. Nothing to do.
  }

  /** @inheritDoc */
  get hideText(): string {
    return this.getFieldLabel();
  }

}

interface TaxonomyLinkListPropertyFieldBase extends HidableMixin{}

mixin(TaxonomyLinkListPropertyFieldBase, HidableMixin);

export default TaxonomyLinkListPropertyFieldBase;
