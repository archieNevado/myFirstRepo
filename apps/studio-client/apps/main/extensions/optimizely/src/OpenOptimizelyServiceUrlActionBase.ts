import Ext from "@jangaroo/ext-ts";
import Action from "@jangaroo/ext-ts/Action";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import OpenOptimizelyServiceUrlAction from "./OpenOptimizelyServiceUrlAction";

interface OpenOptimizelyServiceUrlActionBaseConfig extends Config<Action> {
}

class OpenOptimizelyServiceUrlActionBase extends Action {
  declare Config: OpenOptimizelyServiceUrlActionBaseConfig;

  static #url: string = "https://www.optimizely.com/";

  /**
   * @cfg {String} url to the home page.
   *
   * @param config the config object
   */
  constructor(config: Config<OpenOptimizelyServiceUrlAction> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    (config as unknown)["handler"] = bind(this$, this$.#openInBrowser);
    if (config["url"]) {
      OpenOptimizelyServiceUrlActionBase.#url = config["url"];
    }
    super(Config(Action, Ext.apply({}, config, { handler: bind(this$, this$.#openInBrowser) })));
    this.setDisabled(false);
  }

  /**
   * The action opens the url in a new browser.
   */
  #openInBrowser(): void {
    const urlToOpen = OpenOptimizelyServiceUrlActionBase.#url;
    const wname = "Optimizely";
    const wfeatures = "menubar=yes,resizable=yes,scrollbars=yes,status=yes,location=yes";
    window.open(urlToOpen, wname, wfeatures);
  }

}

export default OpenOptimizelyServiceUrlActionBase;
