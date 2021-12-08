import Bean from "@coremedia/studio-client.client-core/data/Bean";
import Calendar from "@coremedia/studio-client.client-core/data/Calendar";
import PropertyChangeEvent from "@coremedia/studio-client.client-core/data/PropertyChangeEvent";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import StatefulRadio from "@coremedia/studio-client.ext.ui-components/components/StatefulRadio";
import HighlightableMixin from "@coremedia/studio-client.ext.ui-components/mixins/HighlightableMixin";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Component from "@jangaroo/ext-ts/Component";
import Events from "@jangaroo/ext-ts/Events";
import ContainerLayout from "@jangaroo/ext-ts/layout/container/Container";
import { as, bind, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import ExternallyVisibleDateForm from "./ExternallyVisibleDateForm";

interface ExternallyVisibleDateFormBaseEvents extends Events<PropertyFieldGroup>, Events<HighlightableMixin> {

  /**
   * Fires after the externally display date has changed.
   */
  externallyDisplayedDateChanged?(): any;
}

interface ExternallyVisibleDateFormBaseConfig extends Config<PropertyFieldGroup>, Config<HighlightableMixin> {
  listeners?: ExternallyVisibleDateFormBaseEvents;
}

// NOSONAR - no type

class ExternallyVisibleDateFormBase extends PropertyFieldGroup {
  declare Config: ExternallyVisibleDateFormBaseConfig;

  protected static readonly PUBLICATION_DATE_RADIO_ITEM_ID: string = "publicationDate";

  protected static readonly OWN_DATE_RADIO_ITEM_ID: string = "ownDate";

  static readonly #FOCUS: string = "focus";

  static readonly #BLUR: string = "blur";

  model: Bean = null;

  modelValueExpression: ValueExpression = null;

  constructor(config: Config<ExternallyVisibleDateForm> = null) {
    super(config);
  }

  protected override afterLayout(layout: ContainerLayout): void {
    super.afterLayout(layout);

    // pass the focus and blur events for PDE
    const publicationDateRatioBox = this.#getPublicationDateRadioBox();
    if (publicationDateRatioBox) {
      this.mon(publicationDateRatioBox, ExternallyVisibleDateFormBase.#FOCUS, (comp: Component): void => {
        if (comp.xtype === StatefulRadio.xtype) {
          this.fireEvent(ExternallyVisibleDateFormBase.#FOCUS);
        }
      });
      this.mon(publicationDateRatioBox, ExternallyVisibleDateFormBase.#BLUR, (comp: Component): void => {
        if (comp.xtype === StatefulRadio.xtype) {
          this.fireEvent(ExternallyVisibleDateFormBase.#BLUR);
        }
      });
    }

    const ownDateRatioBox = this.#getOwnDateRadioBox();
    if (ownDateRatioBox) {
      this.mon(ownDateRatioBox, ExternallyVisibleDateFormBase.#FOCUS, (comp: Component): void => {
        if (comp.xtype === StatefulRadio.xtype) {
          this.fireEvent(ExternallyVisibleDateFormBase.#FOCUS);
        }
      });
      this.mon(ownDateRatioBox, ExternallyVisibleDateFormBase.#BLUR, (comp: Component): void => {
        if (comp.xtype === StatefulRadio.xtype) {
          this.fireEvent(ExternallyVisibleDateFormBase.#BLUR);
        }
      });
    }
  }

  #externallyDisplayDateChangeListener(event: PropertyChangeEvent): void {
    this.getModel().get("properties").set("innerExternallyDisplayedDate", event.newValue);
    this.fireEvent("externallyDisplayedDateChanged");
  }

  #innerExternallyDisplayedDateListener(event: PropertyChangeEvent): void {
    this.getModel().set("externallyDisplayDate", event.newValue);
    if (event.newValue === null) {
      this.getModel().set("innerUseCustomExternalDisplayedDate", false);
    } else {
      this.getModel().set("innerUseCustomExternalDisplayedDate", true);
    }
  }

  #innerUseCustomExternalDisplayedDateListener(event: PropertyChangeEvent): void {
    if (event.newValue === false) {
      this.getModel().set("archivedDisplayedDate", this.getModel().get("externallyDisplayDate"));
      this.getModel().set("externallyDisplayDate", null);
    }
    if (event.newValue === true && this.getModel().get("archivedDisplayedDate")) {
      this.getModel().set("externallyDisplayDate", this.getModel().get("archivedDisplayedDate"));
    }
  }

  protected override onDestroy(): void {
    this.getModel().removePropertyChangeListener("externallyDisplayDate", bind(this, this.#externallyDisplayDateChangeListener));
    this.getModel().get("properties").removePropertyChangeListener("innerExternallyDisplayedDate", bind(this, this.#innerExternallyDisplayedDateListener));
    this.getModel().removePropertyChangeListener("innerUseCustomExternalDisplayedDate", bind(this, this.#innerUseCustomExternalDisplayedDateListener));

    super.onDestroy();
  }

  getModel(): Bean {
    if (!this.model) {
      this.model = beanFactory._.createLocalBean();
      const innerModel = beanFactory._.createLocalBean();
      innerModel.set("innerExternallyDisplayedDate", null);
      this.model.set("properties", innerModel);

      this.model.addPropertyChangeListener("externallyDisplayDate", bind(this, this.#externallyDisplayDateChangeListener));
      innerModel.addPropertyChangeListener("innerExternallyDisplayedDate", bind(this, this.#innerExternallyDisplayedDateListener));
      this.model.addPropertyChangeListener("innerUseCustomExternalDisplayedDate", bind(this, this.#innerUseCustomExternalDisplayedDateListener));
    }
    return this.model;
  }

  getModelExpression(): ValueExpression {
    if (!this.modelValueExpression) {
      this.modelValueExpression = ValueExpressionFactory.createFromValue(this.getModel());
    }
    return this.modelValueExpression;
  }

  static toValue(value: string): boolean {
    return value === "ownDate";
  }

  setExternallyDisplayedDate(displayedDate: Calendar): void {
    this.getModel().set("externallyDisplayDate", displayedDate);
  }

  getExternallyDisplayedDate(): Calendar {
    return this.getModel().get("externallyDisplayDate");
  }

  #getPublicationDateRadioBox(): StatefulRadio {
    return as(this.queryById(ExternallyVisibleDateFormBase.PUBLICATION_DATE_RADIO_ITEM_ID), StatefulRadio);
  }

  #getOwnDateRadioBox(): StatefulRadio {
    return as(this.queryById(ExternallyVisibleDateFormBase.OWN_DATE_RADIO_ITEM_ID), StatefulRadio);
  }

  override focus(selectText?: any, delay?: any, callback: AnyFunction = null, scope: AnyFunction = null): this {
    // PDE: if date gets focused in preview, we will have to focus the radiobox as well
    const radio = this.#getPublicationDateRadioBox() && this.#getPublicationDateRadioBox().getValue() ? this.#getPublicationDateRadioBox() : this.#getOwnDateRadioBox();
    if (radio) {
      radio.focus(selectText, delay);
    }
    return this;
  }

}

interface ExternallyVisibleDateFormBase extends HighlightableMixin{}

mixin(ExternallyVisibleDateFormBase, HighlightableMixin);

export default ExternallyVisibleDateFormBase;
