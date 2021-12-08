import OpenQuickCreateAction from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/OpenQuickCreateAction";
import QuickCreateDialog from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateDialog";
import ProcessingData from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/processing/ProcessingData";
import Ext from "@jangaroo/ext-ts";
import Component from "@jangaroo/ext-ts/Component";
import Window from "@jangaroo/ext-ts/window/Window";
import URIUtils from "@jangaroo/jangaroo-net/URIUtils";
import Config from "@jangaroo/runtime/Config";

class OpenCreateExternalPageDialogActionBase extends OpenQuickCreateAction {

  static readonly EXTERNAL_ID_PROPERTY: string = "externalId";

  static readonly EXTERNAL_URI_PATH_PROPERTY: string = "externalUriPath";

  static readonly KNOWN_NON_SEO_PARAMS: Array<any> = ["storeId", "catalogId", "langId"];

  #data: any = null;

  constructor(config: Config<OpenQuickCreateAction> = null) {
    super(config);
    this.#data = undefined;
  }

  protected override getDialogConfig(trigger: Component): Config<Window> {
    //create the dialog
    const dialogConfig = Config(QuickCreateDialog, Ext.apply({}, Ext.apply({}, this.initialConfig)));
    dialogConfig.model = new ProcessingData();
    dialogConfig.model.set(OpenCreateExternalPageDialogActionBase.EXTERNAL_ID_PROPERTY, this.#data.pageId);
    dialogConfig.model.set(ProcessingData.NAME_PROPERTY, this.#data.pageId);
    const previewUrl: string = this.#data.shopUrl;
    if (previewUrl) {
      const uri = URIUtils.parse(previewUrl);
      if (!OpenCreateExternalPageDialogActionBase.#isSeoUrl(previewUrl)) {
        const path: string = uri.path.split("/").pop();
        const query = OpenCreateExternalPageDialogActionBase.#replaceKnownQueryParameters(uri.query);
        dialogConfig.model.set(OpenCreateExternalPageDialogActionBase.EXTERNAL_URI_PATH_PROPERTY, path + "?" + query);
      }
    }

    return dialogConfig;
  }

  setData(data: Array<any>) {
    if (Ext.isEmpty(data)) {
      this.#data = undefined;
    } else {
      this.#data = data[0];
    }
  }

  protected override calculateDisabled(): boolean {
    if (this.#data === undefined) {
      return undefined;
    }

    return !this.#data.shopUrl || !this.#data.pageId;
  }

  static #replaceKnownQueryParameters(queryStr: string): string {
    const queryParamMap = Ext.urlDecode(queryStr);
    const result = [];
    for (const key in queryParamMap) {
      if (OpenCreateExternalPageDialogActionBase.KNOWN_NON_SEO_PARAMS.indexOf(key) > -1) {
        result.push(key + "={" + key + "}");
      } else {
        result.push(key + "=" + queryParamMap[key]);
      }
    }
    return result.join("&");
  }

  static #isSeoUrl(url: string): boolean {
    return OpenCreateExternalPageDialogActionBase.KNOWN_NON_SEO_PARAMS.every((s: string): boolean =>
      url.indexOf(s) < 0,
    );
  }

}

export default OpenCreateExternalPageDialogActionBase;
