import createComponentSelector from "@coremedia/studio-client.ext.ui-components/util/createComponentSelector";
import messageService from "@coremedia/studio-client.main.editor-components/sdk/messageService";
import Premular from "@coremedia/studio-client.main.editor-components/sdk/premular/Premular";
import PreviewIFrame from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewIFrame";
import PreviewMessageTypes from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewMessageTypes";
import PreviewPanel from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanel";
import Component from "@jangaroo/ext-ts/Component";
import StringUtil from "@jangaroo/ext-ts/String";
import Container from "@jangaroo/ext-ts/container/Container";
import Field from "@jangaroo/ext-ts/form/field/Field";
import AbstractPlugin from "@jangaroo/ext-ts/plugin/Abstract";
import { as, bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import PersonalizationPlugIn_properties from "./PersonalizationPlugIn_properties";
import SearchValidator from "./SearchValidator";

interface SearchValidatorBaseConfig extends Config<AbstractPlugin> {
}

/**
 * The field that uses this plugin retrieves the status of the search from the preview panel and adapts
 * its validation state and tooltip accordingly, thus providing better error feedback to the Studio user.
 */
class SearchValidatorBase extends AbstractPlugin {
  declare Config: SearchValidatorBaseConfig;

  // name of the data attribute used in the preview page to store the search message
  static readonly #P13N_SEARCHSTATUS_DATA_ATTRIBUTE: string = "cm-personalization-editorplugin-searchstatus";

  #prevPanel: Container = null;

  #field: Field = null;

  #comp: Component = null;

  constructor(config: Config<SearchValidator> = null) {
    super(config);
  }

  override init(component: Component): void {
    this.#field = as(component, Field);
    this.#comp = component;
    this.#comp.addListener("afterrender", bind(this, this.onAfterrender));
  }

  /**
   * Performed on the afterrender event. Does stuff that requires a rendered component.
   */
  onAfterrender(): void {
    this.#prevPanel = this.#findPreviewPanel();
    this.#prevPanel.addListener("previewUrl", bind(this, this.onPreviewUrlChange));
  }

  /**
   * Find the preview panel in the premular this field is placed in. Make sure this is only
   * called on a rendered component.
   *
   * @return the preview panel
   *
   * @throws Error if the preview panel cannot be found
   */
  #findPreviewPanel(): Container {
    if (!this.#comp.rendered) {
      throw new Error("findPreviewPanel must only be called on a rendered component");
    }

    const prem = as(this.#comp.findParentByType(Premular.xtype), Container);
    if (prem) {
      const preview = as(prem.down(createComponentSelector()._xtype(PreviewPanel.xtype).build()), Container);
      if (preview) {
        return preview;
      }
    }
    // didn't found the preview
    throw new Error("unable to locate Preview Panel. Has this component been rendered already?");
  }

  /**
   * Called when the contents of the preview change. Retrieves the search status object from the preview and
   * adapts the state of this field if necessary.
   *
   * @param event the 'previewUrl changed' event
   */
  onPreviewUrlChange(oldValue: string, newValue: string): void {
    if (newValue) {
      // retrieve the search message from the preview
      const targetWindow = cast(PreviewIFrame, this.#prevPanel.down(createComponentSelector()._xtype(PreviewIFrame.xtype).build())).getContentWindow();
      const messageBody: Record<string, any> = { dataAttributeName: SearchValidatorBase.#P13N_SEARCHSTATUS_DATA_ATTRIBUTE };
      messageService.sendMessage(targetWindow, PreviewMessageTypes.RETRIEVE_DATA_ATTRIBUTE, messageBody, (responseBody: any): void => {
        const searchStatus = as(responseBody.value, Array);

        if (searchStatus && searchStatus.length > 0) {
          this.#field["validator"] = ((value: any): any =>
            SearchValidatorBase.#toTooltip(searchStatus[0])
          );
          this.#field.validate();
        } else {
          this.#field["validator"] = ((value: any): any =>
            true
          );
          this.#field.validate();
        }
      }, this.#prevPanel);
    }
  }

  /**
   * Converts the supplied status object into a string to be shown in a tooltip.
   *
   * @param status the object representing the search status
   *
   * @return tooltip representing the search status
   */
  static #toTooltip(status: any): string {
    const code: string = status["code"];
    let msg = PersonalizationPlugIn_properties[code];
    if (!msg) {
      msg = status["msg"];
    }
    switch (code) {
    case "ARGUMENT_VALUE":
    case "ARGUMENT_UNKNOWN":
    case "ARGUMENT_SYNTAX":
    case "ARGUMENT_MISSING":
      return StringUtil.format(msg, status["func"], status["param"], status["msg"]);
    case "FUNCTION_EVALUATION":
    case "FUNCTION_UNKNOWN":
      return StringUtil.format(msg, status["func"], status["msg"]);
    case "GENERAL":
    default:
      return StringUtil.format(msg, status["msg"]);
    }
  }
}

export default SearchValidatorBase;
