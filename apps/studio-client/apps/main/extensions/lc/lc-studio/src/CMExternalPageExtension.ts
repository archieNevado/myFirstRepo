import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import NavigationLinkFieldWrapper from "@coremedia/studio-client.main.bpbase-studio-components/navigationlink/NavigationLinkFieldWrapper";
import QuickCreate from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreate";
import QuickCreate_properties from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreate_properties";
import ProcessingData from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/processing/ProcessingData";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";

/**
 * Extension, the enhances the QuickCreateDialog for CMExternalPage with the parent navigation, externalId and
 * externalUri editors
 */
class CMExternalPageExtension {
  //content and custom properties for quick create dialog
  static readonly PARENT_PROPERTY: string = "parentChannel";

  static readonly CHILDREN_PROPERTY: string = "children";

  static readonly CONTENT_TYPE_PAGE: string = "CMExternalPage";

  static register(docType: string): void {
    /**
     * Apply custom properties for CMExternalPage
     */
    QuickCreate.addQuickCreateDialogProperty(docType, CMExternalPageExtension.PARENT_PROPERTY, CMExternalPageExtension.#getCreateComponent(docType), 0);

    QuickCreate.addSuccessHandler(docType, CMExternalPageExtension.#process);
  }

  static #getCreateComponent(docType: string): AnyFunction {
    return (data: ProcessingData, properties: any): Component =>
      CMExternalPageExtension.#createComponent(data, properties, docType)
    ;
  }

  /**
   * Creates the UI Component for the Quick Creation Dialog
   * @param data the Data Process Object
   * @param properties The properties of the bound object
   * @param docType
   * @return the UI Component
   */
  static #createComponent(data: ProcessingData, properties: any, docType: string): Component {
    let c: Content = null;
    if (properties.bindTo) {
      c = properties.bindTo.getValue();
    }
    if (c && c.getType().getName() === docType) {
      data.set(CMExternalPageExtension.PARENT_PROPERTY, c);
      ValueExpressionFactory.create(ContentPropertyNames.PATH, c).loadValue((path: string): void => {
        data.set(ProcessingData.FOLDER_PROPERTY, path);
      });
    }
    properties.label = QuickCreate_properties.parent_label;
    properties.doctype = CMExternalPageExtension.CONTENT_TYPE_PAGE;
    return new NavigationLinkFieldWrapper(Config(NavigationLinkFieldWrapper, properties));
  }

  /**
   * Adds a hook for processing the creation of CMChannel
   * @param content the content to created
   * @param data the processing data with varios informations
   * @param callback the function to call after processing
   */
  static #process(content: Content, data: ProcessingData, callback: AnyFunction): void {

    //parent property is read from a link list, so resolve value from array
    const parentContent: Content = data.get(CMExternalPageExtension.PARENT_PROPERTY);
    content.getProperties().set("title", content.getName());

    if (parentContent) {
      CMExternalPageExtension.#linkToList(parentContent, content, CMExternalPageExtension.CHILDREN_PROPERTY, data, (): void => {
        callback.call(null);
      });
    } else {
      callback.call(null);
    }
  }

  static #linkToList(parentContent: Content, content: Content, property: string, data: ProcessingData, callback: AnyFunction): void {
    if (parentContent) {
      parentContent.load((): void => {
        let children: Array<any> = parentContent.getProperties().get(property);
        if (!children) {
          children = [];
        }
        if (children.indexOf(content) === -1) { //maybe the dialog is linking too.
          children = children.concat(content);
          parentContent.getProperties().set(property, children);
          data.addAdditionalContent(parentContent);
        }
        callback.call(null);
      });
    } else {
      callback.call(null);
    }
  }

}

export default CMExternalPageExtension;
