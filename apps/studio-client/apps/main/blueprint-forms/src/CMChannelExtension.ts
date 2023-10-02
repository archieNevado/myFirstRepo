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
 * Extension, the enhances the QuickCreateDialog for CMChannel with the parent navigation editor
 */
class CMChannelExtension {

  //content and custom properties for quick create dialog
  static readonly PARENT_PROPERTY: string = "parentChannel";

  static readonly CHILDREN_PROPERTY: string = "children";

  static readonly CONTENT_TYPE_PAGE: string = "CMChannel";

  static register(contentType: string): void {
    /**
     * Apply custom properties for CMChannel
     */
    QuickCreate.addQuickCreateDialogProperty(contentType, CMChannelExtension.PARENT_PROPERTY,
      (data: ProcessingData, properties: any): Component =>
        CMChannelExtension.#createComponent(data, properties, contentType),
    );

    QuickCreate.addSuccessHandler(contentType, CMChannelExtension.#process);
  }

  /**
   * Creates the UI Component for the Quick Creation Dialog
   * @param data the Data Process Object
   * @param properties The properties of the bound object
   * @param contentType The contentType of the document to be created
   * @return the UI Component
   */
  static #createComponent(data: ProcessingData, properties: any, contentType: string): Component {
    let c: Content = null;
    if (properties.bindTo) {
      c = properties.bindTo.getValue();
    }
    if (c && c.getType().isSubtypeOf(contentType)) {
      data.set(CMChannelExtension.PARENT_PROPERTY, c);
      ValueExpressionFactory.create(ContentPropertyNames.PATH, c.getParent()).loadValue((path: string): void => {
        data.set(ProcessingData.FOLDER_PROPERTY, path);
      });
    }
    properties.label = QuickCreate_properties.parent_label;
    properties.doctype = contentType;
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
    const parentContent: Content = data.get(CMChannelExtension.PARENT_PROPERTY);
    content.getProperties().set("title", content.getName());

    if (parentContent) {
      CMChannelExtension.#linkToList(parentContent, content, CMChannelExtension.CHILDREN_PROPERTY, data, (): void => {
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

export default CMChannelExtension;
