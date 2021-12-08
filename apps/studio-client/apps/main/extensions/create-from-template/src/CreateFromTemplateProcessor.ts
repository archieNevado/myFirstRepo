import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import BulkOperationResultItem from "@coremedia/studio-client.cap-rest-client/content/results/BulkOperationResultItem";
import CopyResult from "@coremedia/studio-client.cap-rest-client/content/results/CopyResult";
import FlushResult from "@coremedia/studio-client.client-core/data/FlushResult";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import ContentCreationUtil from "@coremedia/studio-client.main.editor-components/sdk/util/ContentCreationUtil";
import { as } from "@jangaroo/runtime";
import int from "@jangaroo/runtime/int";
import trace from "@jangaroo/runtime/trace";
import { AnyFunction } from "@jangaroo/runtime/types";
import CreateFromTemplateStudioPluginSettings_properties from "./CreateFromTemplateStudioPluginSettings_properties";
import PageTemplate from "./model/PageTemplate";
import ProcessingData from "./model/ProcessingData";

/**
 * Post processor implementation for CMChannel instances.
 */
class CreateFromTemplateProcessor {

  static process(data: ProcessingData, callback: AnyFunction): void {
    const template: Array<any> = data.get(CreateFromTemplateStudioPluginSettings_properties.template_property);

    CreateFromTemplateProcessor.#convertToPageTemplate(template[0], (pageTemplate: PageTemplate): void => {
      data.set(CreateFromTemplateStudioPluginSettings_properties.template_property, pageTemplate);
      if (pageTemplate) {
        CreateFromTemplateProcessor.#copyTemplateFiles(data, (contents: Array<any>): void =>
          CreateFromTemplateProcessor.#initializeNewlyCreatedContents(contents, (): void =>
            CreateFromTemplateProcessor.#renameTemplateChannel(data, (): void =>
              CreateFromTemplateProcessor.#deleteTemplateSymbols(data, (): void =>
                CreateFromTemplateProcessor.#movePageToNavigation(data, (channel: Content): void =>
                  CreateFromTemplateProcessor.#linkToList(data, channel, (): void => {
                    callback.call(null);
                  }),
                ),
              ),
            ),
          ),
        );
      } else {
        const folder = data.getFolder();
        const ct = session._.getConnection().getContentRepository().getContentType(CreateFromTemplateStudioPluginSettings_properties.doctype);
        ContentCreationUtil.createContent(folder, true, false, data.getName(), ct, (createdContent: Content): void => {
          data.setContent(createdContent);
          callback.call(null);
        });
      }
    });
  }

  static #convertToPageTemplate(templateSymbol: Content, callback: AnyFunction): void {
    const folder = templateSymbol.getParent();
    folder.load((): void =>{
      const pageTemplate = new PageTemplate(folder, templateSymbol);
      const templateChannelDocType = session._.getConnection().getContentRepository().getContentType(CreateFromTemplateStudioPluginSettings_properties.doctype);

      const children = folder.getChildDocuments();
      let count: int = children.length;
      for (let i = 0; i < children.length; i++) {
        const child: Content = children[i];
        child.load((loadedChild: Content): void => {
          count--;
          const type = loadedChild.getType();
          if (loadedChild.getId() !== pageTemplate.getDescriptor().getId()) {
            if (type.isSubtypeOf(templateChannelDocType)) {
              pageTemplate.setPage(loadedChild);
            }
          }
          if (count === 0) {
            callback.call(null, pageTemplate);
          }
        });
      }
    });
  }

  static #initializeNewlyCreatedContents(contents: Array<any>, callback: AnyFunction): void {
    if (!contents) {
      callback.call(null);
      return;
    }
    contents.forEach((content: Content): void => {
      const initializer = editorContext._.lookupContentInitializer(content.getType());
      if (initializer) {
        initializer(content);
      }
    });

    callback.call(null);
  }

  /**
   * Renames the template channel document after is has been copied to the new
   * editorial folder. The channel is copied afterwards...
   * @param data
   * @param callback
   */
  static #renameTemplateChannel(data: ProcessingData,
    callback: AnyFunction): void {
    const initializer: PageTemplate = data.get(CreateFromTemplateStudioPluginSettings_properties.template_property);
    const folder: Content = data.get(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property);
    folder.getChild(initializer.getPage().getName(), (copiedChannel: Content): void =>
      copiedChannel.rename(data.getName(), (): void => {
        trace("INFO", "Renamed template channel \"" + initializer.getPage().getName() + "\" to \"" + data.getName() + "\"");
        callback.call(null);
      }),
    );
  }

  /**
   * Deletes the symbol document from the copied template folder.
   * @param data
   * @param callback
   */
  static #deleteTemplateSymbols(data: ProcessingData,
    callback: AnyFunction): void {
    const targetEditorialFolder: Content = data.get(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property);
    targetEditorialFolder.invalidate((): void => {
      const children = targetEditorialFolder.getChildDocuments();
      let callbackCount: int = children.length;
      const symbols = [];
      for (let i = 0; i < children.length; i++) {
        const child: Content = children[i];
        child.load((c: Content): void => {
          callbackCount--;
          if (c.getName().indexOf(CreateFromTemplateStudioPluginSettings_properties.template_descriptor_name) === 0
                  && c.getType().getName() === CreateFromTemplateStudioPluginSettings_properties.template_descriptor_type) {
            symbols.push(c);
          }
          if (callbackCount === 0) {
            CreateFromTemplateProcessor.#deleteDescriptors(symbols, targetEditorialFolder, callback);
          }
        });
      }
      if (children.length === 0) {
        callback.call(null);
      }
    });
  }

  static #deleteDescriptors(symbols: Array<any>, targetEditorialFolder: Content, callback: AnyFunction): void {
    trace("INFO", "Deleting " + symbols.length + " descriptors");
    let callbackCount: int = symbols.length;
    for (let i = 0; i < symbols.length; i++) {
      symbols[i].doDelete((): void => {
        trace("INFO", "Deleted template descriptor from new editorial folder");
        callbackCount--;
        if (callbackCount === 0) {
          targetEditorialFolder.invalidate((): void => {
            callback.call(null);
          });
        }
      });
    }
    if (symbols.length === 0) {
      callback.call(null);
    }
  }

  /**
   * Moves the copied channel document from the editorial folder to the navigation/selected folder.
   * @param data
   * @param callback
   */
  static #movePageToNavigation(data: ProcessingData, callback: AnyFunction): void {
    //resolved target name and folder
    const name = data.getName();
    const sourceFolder = data.getFolder();
    const targetEditorialFolder: Content = data.get(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property);
    targetEditorialFolder.getChild(name, (channel: Content): void =>
      channel.moveTo(sourceFolder, (result: FlushResult): void => {
        const movedChannel = as(result.remoteBean, Content);
        movedChannel.invalidate((): void => {
          const channelName = movedChannel.getName();
          movedChannel.getProperties().set("title", channelName);
          movedChannel.getProperties().set("segment", channelName);
          movedChannel.flush();

          ValueExpressionFactory.create(ContentPropertyNames.PATH, movedChannel).loadValue((path: string): void => {
            trace("INFO", "Moved \"" + channel.getPath() + "\" to \"" + path + "\" (" + movedChannel + ")");
            data.setContent(movedChannel);

            //reload folder and invoke callback
            const editorialFolder: Content = data.get(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property);
            //invalidate editorial folder to show that document has been removed and...
            editorialFolder.invalidate((): void =>
              //...invalidate the target folder to show the moved document.
              sourceFolder.invalidate((): void => {
                callback.call(null, movedChannel);
              }),

            );
          });
        });
      }),
    );
  }

  /**
   * Copies the files of the template to the corresponding editorial folder.
   * @param data
   * @param callback
   */
  static #copyTemplateFiles(data: ProcessingData, callback: AnyFunction): void {
    const initializer: PageTemplate = data.get(CreateFromTemplateStudioPluginSettings_properties.template_property);
    //resolve the target folder for the template files
    const folder: Content = data.get(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property)[0];
    const folderName = data.getExtendedPath(folder);
    //create the target folder...
    trace("INFO", "Copying template files to new editorial folder \"" + folderName + "\"");
    session._.getConnection().getContentRepository().getChild(folderName, (folder: Content): void => {
      data.set(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property, folder);
      //...and copy the files.
      let toBeCopied = initializer.getFolder().getChildren();
      CreateFromTemplateProcessor.#loadContents(toBeCopied, (): void => {
        toBeCopied = toBeCopied.filter(CreateFromTemplateProcessor.#notCMSymbol);
        session._.getConnection().getContentRepository().copyRecursivelyTo(toBeCopied, folder, (result: CopyResult): void => {
          if (result.successful) {
            const contents = result.results.map(CreateFromTemplateProcessor.#toContent);
            callback(contents);
          } else {
            trace("[WARN]", "Template copy failed: " + result.error.errorName);
            callback.call(null);
          }
        });
      });
    });
  }

  static #toContent(resultItem: BulkOperationResultItem): Content {
    return resultItem.content;
  }

  /**
   * Ensures that all items of the given content array are loaded.
   * The method is used to skip asynchronous calls afterwards.
   * @param items An array filled with content items.
   * @param callback Called when all items have been loaded.
   */
  static #loadContents(items: Array<any>, callback: AnyFunction): void {
    if (!items || items.length === 0) {
      callback.call(null);
    }
    let index: int = items.length;
    for (let i = 0; i < items.length; i++) {
      const c: Content = items[i];
      c.load((): void => {
        index--;
        if (index === 0) {
          callback.call(null);
        }
      });
    }
  }

  /**
   * Links the newly created channel to the navigation hierarchy.
   */
  static #linkToList(data: ProcessingData,
    channel: Content,
    callback: AnyFunction): void {
    const parentContent: Content = data.get(CreateFromTemplateStudioPluginSettings_properties.parent_property);
    if (parentContent) {
      parentContent.load((): void => {
        let children: Array<any> = parentContent.getProperties().get(CreateFromTemplateStudioPluginSettings_properties.children_property);
        if (!children) {
          children = [];
        }
        if (children.indexOf(channel) === -1) { //maybe the dialog is linking too.
          children = children.concat(channel);
          parentContent.getProperties().set(CreateFromTemplateStudioPluginSettings_properties.children_property, children);
          data.addAdditionalContent(parentContent);
        }
        callback.call(null);
      });
    } else {
      callback.call(null);
    }
  }

  /**
   * Filter symbols out of the list of files to be copied.
   * @param loadedContent the folder content
   * @return true if the given content is not a CMSymbol
   */
  static #notCMSymbol(loadedContent: Content): boolean {
    return loadedContent.getType() && !loadedContent.getType().isSubtypeOf(CreateFromTemplateStudioPluginSettings_properties.template_descriptor_type);
  }
}

export default CreateFromTemplateProcessor;
