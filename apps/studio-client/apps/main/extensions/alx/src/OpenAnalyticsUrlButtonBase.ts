import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Component from "@jangaroo/ext-ts/Component";
import Button from "@jangaroo/ext-ts/button/Button";
import VTypes from "@jangaroo/ext-ts/form/field/VTypes";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import joo from "@jangaroo/runtime/joo";
import trace from "@jangaroo/runtime/trace";
import { AnyFunction } from "@jangaroo/runtime/types";
import AnalyticsStudioPluginBase from "./AnalyticsStudioPluginBase";

interface OpenAnalyticsUrlButtonBaseConfig extends Config<Button>, Partial<Pick<OpenAnalyticsUrlButtonBase,
  "windowName" |
  "serviceName"
>> {
}

class OpenAnalyticsUrlButtonBase extends Button {
  declare Config: OpenAnalyticsUrlButtonBaseConfig;

  static readonly WINDOW_FEATURES: string = "menubar=yes,resizable=yes,scrollbars=yes,status=yes,location=yes";

  static readonly #HOME_URL: string = "homeUrl";

  /** The name of the window to open. */
  windowName: string = null;

  /** The name of the service that this button is bound to */
  serviceName: string = null;

  readonly urlValueExpression: ValueExpression;

  constructor(config: Config<OpenAnalyticsUrlButtonBase> = null) {
    super(config);
    this.setHandler(bind(this, this.#_handler));
    this.initUrlValueExpression();
    this.on("afterrender", bind(this, this.#onAfterRender));
  }

  #onAfterRender(): void {
    OpenAnalyticsUrlButtonBase.bindDisable(this.urlValueExpression, this);
  }

  static bindDisable(valueExpression: ValueExpression, component: Component): void {
    function updateDisabled(): void {
      component.setDisabled(OpenAnalyticsUrlButtonBase.isNotUrlValue(valueExpression.getValue()));
    }

    valueExpression.addChangeListener(updateDisabled);
    component.on("destroy", (): void =>
      valueExpression.removeChangeListener(updateDisabled),
    );
    updateDisabled();
  }

  #_handler(): void {
    this.urlValueExpression.loadValue((): void => {
      window.open(this.urlValueExpression.getValue(), this.windowName, OpenAnalyticsUrlButtonBase.WINDOW_FEATURES);
    });
  }

  /**
   * Opens the current URL new browser window.
   */
  static openInBrowser(expr: ValueExpression, windowName: string): AnyFunction {
    return (): void => {
      const url = as(expr.getValue(), String);
      if (OpenAnalyticsUrlButtonBase.isNotUrlValue(url)) {
        if (joo.debug) {
          trace("[WARN] cannot open non URL value", url);
        }
      } else {
        window.open(url, windowName, OpenAnalyticsUrlButtonBase.WINDOW_FEATURES);
      }
    };
  }

  static isNotUrlValue(value: any): boolean {
    return !VTypes.url(value);
  }

  initUrlValueExpression(): void {
    (this as unknown)["urlValueExpression"] = AnalyticsStudioPluginBase.SETTINGS.extendBy("properties.settings", this.serviceName, OpenAnalyticsUrlButtonBase.#HOME_URL);
  }

}

export default OpenAnalyticsUrlButtonBase;
