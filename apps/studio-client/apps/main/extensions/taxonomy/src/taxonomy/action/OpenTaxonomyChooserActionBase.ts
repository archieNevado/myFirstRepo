import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import AccessControlUtil from "@coremedia/studio-client.ext.cap-base-components/util/AccessControlUtil";
import DependencyTrackedAction from "@coremedia/studio-client.ext.ui-components/actions/DependencyTrackedAction";
import Ext from "@jangaroo/ext-ts";
import { as, bind, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomySelectionWindow from "../chooser/TaxonomySelectionWindow";
import OpenTaxonomyChooserAction from "./OpenTaxonomyChooserAction";

interface OpenTaxonomyChooserActionBaseConfig extends Config<DependencyTrackedAction>, Partial<Pick<OpenTaxonomyChooserActionBase,
  "bindTo" |
  "forceReadOnlyValueExpression" |
  "siteSelectionExpression" |
  "taxonomyIdExpression"
>> {
}

/**
 * Shows the dialog for choosing taxonomies for a linklist property.
 */
class OpenTaxonomyChooserActionBase extends DependencyTrackedAction {
  declare Config: OpenTaxonomyChooserActionBaseConfig;

  #propertyValueExpression: ValueExpression = null;

  #singleSelection: boolean = false;

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  siteSelectionExpression: ValueExpression = null;

  taxonomyIdExpression: ValueExpression = null;

  /**
   * @param config
   */
  constructor(config: Config<OpenTaxonomyChooserAction> = null) {
    super((()=>{
      this.#propertyValueExpression = config.propertyValueExpression;
      this.#singleSelection = config.singleSelection;
      this.bindTo = config.bindTo;
      this.forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;
      this.siteSelectionExpression = config.siteSelectionExpression;
      this.taxonomyIdExpression = config.taxonomyIdExpression;
      config.handler = bind(this, this.#showChooser);
      return config;
    })());
  }

  protected override calculateDisabled(): boolean {
    if (this.bindTo && is(this.bindTo.getValue(), Content) && this.forceReadOnlyValueExpression) {
      return as(this.bindTo.getValue(), Content).isCheckedOutByOther() || AccessControlUtil.isReadOnly(this.bindTo.getValue()) || this.forceReadOnlyValueExpression.getValue();
    }
    return false;
  }

  #showChooser(): void {
    const dialog = as(Ext.getCmp(TaxonomySelectionWindow.ID), TaxonomySelectionWindow);
    if (dialog && dialog.rendered) {
      dialog.focus();
    } else {
      const taxChooser = new TaxonomySelectionWindow(Config(TaxonomySelectionWindow, {
        taxonomyIdExpression: this.taxonomyIdExpression,
        siteSelectionExpression: this.siteSelectionExpression,
        singleSelection: this.#singleSelection,
        bindTo: this.bindTo,
        propertyValueExpression: this.#propertyValueExpression,
      }));
      taxChooser.show();
    }
  }

}

export default OpenTaxonomyChooserActionBase;
