import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ActionConfigUtil from "@coremedia/studio-client.ext.cap-base-components/actions/ActionConfigUtil";
import Action from "@jangaroo/ext-ts/Action";
import Component from "@jangaroo/ext-ts/Component";
import { bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ManagementCenterUtil from "../mgmtcenter/ManagementCenterUtil";
import OpenManagementCenterAction from "./OpenManagementCenterAction";

interface OpenManagementCenterActionBaseConfig extends Config<Action> {
}

/**
 * This action is intended to be used from within EXML, only.
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 * @deprecated
 */
class OpenManagementCenterActionBase extends Action {
  declare Config: OpenManagementCenterActionBaseConfig;

  #disabledExpression: ValueExpression = null;

  /**
   * @param config the configuration object
   */
  constructor(config: Config<OpenManagementCenterAction> = null) {
    super(cast(OpenManagementCenterAction, ActionConfigUtil.extendConfiguration(resourceManager.getResourceBundle(null, LivecontextStudioPlugin_properties).content, config, "openManagementCenter",
      {
        handler: (): void =>
          ManagementCenterUtil.openManagementCenterView(),
      })));
    this.#disabledExpression = ValueExpressionFactory.createFromFunction(bind(this, this.#calculateDisabled));
    this.#disabledExpression.addChangeListener(bind(this, this.#updateDisabledStatus));
    this.#updateDisabledStatus();

  }

  override addComponent(comp: Component): void {
    super.addComponent(comp);
    //broadcast the disable state after the add of a component
    this.#updateDisabledStatus();
  }

  #updateDisabledStatus(): void {
    const value = this.#disabledExpression.getValue();
    const disabled: boolean = value === undefined || value;

    this.setDisabled(disabled);
  }

  #calculateDisabled(): boolean {
    if (!ManagementCenterUtil.isSupportedBrowser()) {
      return true;
    }

    return !ManagementCenterUtil.getUrl();
  }

}

export default OpenManagementCenterActionBase;
