import ImageUtil from "@coremedia/studio-client.cap-base-models/util/ImageUtil";
import UserUtil from "@coremedia/studio-client.cap-base-models/util/UserUtil";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Logger from "@coremedia/studio-client.client-core-impl/logging/Logger";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import Blob from "@coremedia/studio-client.client-core/data/Blob";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BEMBlock from "@coremedia/studio-client.ext.ui-components/models/bem/BEMBlock";
import BEMElement from "@coremedia/studio-client.ext.ui-components/models/bem/BEMElement";
import ContentLocalizationUtil from "@coremedia/studio-client.main.bpbase-studio-components/localization/ContentLocalizationUtil";
import BeanListChooser from "@coremedia/studio-client.main.editor-components/sdk/components/BeanListChooser";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import PathFormatter from "@coremedia/studio-client.main.editor-components/sdk/util/PathFormatter";
import Ext from "@jangaroo/ext-ts";
import XTemplate from "@jangaroo/ext-ts/XTemplate";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import CreateFromTemplateStudioPluginSettings_properties from "./CreateFromTemplateStudioPluginSettings_properties";

interface TemplateBeanListChooserBaseConfig extends Config<BeanListChooser>, Partial<Pick<TemplateBeanListChooserBase,
  "configPaths" |
  "validate"
>> {
}

/**
 * Base class of the template chooser:
 * Loads all template folders and their descriptions.
 */
class TemplateBeanListChooserBase extends BeanListChooser {
  declare Config: TemplateBeanListChooserBaseConfig;

  static readonly TEMPLATE_BEAN_LIST_CHOOSER_BLOCK: BEMBlock = new BEMBlock("cm-template-bean-list-chooser");

  static readonly TEMPLATE_BEAN_LIST_CHOOSER_ELEMENT_ITEM: BEMElement = TemplateBeanListChooserBase.TEMPLATE_BEAN_LIST_CHOOSER_BLOCK.createElement("item");

  static readonly TEMPLATE_BEAN_LIST_CHOOSER_ITEM_BLOCK: BEMBlock = new BEMBlock("cm-template-bean-list-chooser-item");

  static readonly TEMPLATE_BEAN_LIST_CHOOSER_ITEM_ELEMENT_TEXT: BEMElement = TemplateBeanListChooserBase.TEMPLATE_BEAN_LIST_CHOOSER_ITEM_BLOCK.createElement("text");

  static readonly #CONTENT_ITEM_SELECTOR_EXPRESSION: string = TemplateBeanListChooserBase.TEMPLATE_BEAN_LIST_CHOOSER_ITEM_BLOCK.getCSSSelector();

  /**
   * The paths to look for page templates. Typically a global path, a site specific path and the users home folder.
   */
  configPaths: string = null;

  /**
   * Points to function, which validates this editor. This could be a no empty selection validation.
   */
  validate: AnyFunction = null;

  static #xTemplate: XTemplate = new XTemplate(
    "<tpl for=\".\">",
    "<div class=\"" + TemplateBeanListChooserBase.TEMPLATE_BEAN_LIST_CHOOSER_ITEM_BLOCK + " " + TemplateBeanListChooserBase.TEMPLATE_BEAN_LIST_CHOOSER_ELEMENT_ITEM +
          "\" data-qtip=\"{description}\">",
    "<img src=\"{iconUri}\" loading=\"lazy\"/>",
    "<p class=\"" + TemplateBeanListChooserBase.TEMPLATE_BEAN_LIST_CHOOSER_ITEM_ELEMENT_TEXT + "\">{description}</p>",
    "</div>",
    "</tpl>");

  constructor(config: Config<TemplateBeanListChooserBase> = null) {
    config.template = TemplateBeanListChooserBase.getXTemplateForRendering();
    config.itemSelector = TemplateBeanListChooserBase.getContentItemSelector();
    super((()=>{
      config.beanList = this.getTemplates();
      config.cls = TemplateBeanListChooserBase.TEMPLATE_BEAN_LIST_CHOOSER_BLOCK.getCSSClass();
      return config;
    })());
  }

  /**
   * This function is designed for multiple executions to evaluate all templates. As this is a FunctionValueExpression,
   * the inner function will always be reinvoked, if a collected dependency will be invalided.
   * @return a ValueExpressoin representing the founded templates to choose from
   */
  getTemplates(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): Array<any> => { // the inner function for
      // the FunctionValueExpression

      // build up an array with all paths including the user home directory
      let paths = this.configPaths.split(",");
      paths = paths.concat(UserUtil.getHome().getPath() + "/" +
              CreateFromTemplateStudioPluginSettings_properties.template_folder_fragment +
              "/" + CreateFromTemplateStudioPluginSettings_properties.doctype);

      const contentListResult = [];
      const contentRepo = session._.getConnection().getContentRepository();

      // Method returns undefined until this flag is true at the end of the function.
      let ready = true;

      // iterate over each path
      paths.forEach((path: string): void => {
        path = PathFormatter.formatSitePath(path);
        if (!path) { //maybe null if the active site is not set
          return;
        }

        let currentNode = contentRepo.getRoot();
        let pathSegments = path.split("/");
        pathSegments = pathSegments.splice(1, pathSegments.length);
        let validPath = true;

        // because this method has to be completely synchronous, we have to iterate over each path fragment instead of
        // use contentRepo.getChildren(...), because this call is asynchronous.
        pathSegments.forEach((segment: string): void => {
          if (currentNode && currentNode.getChildrenByName()) { // currentNode is loaded
            if (segment !== "") {
              if (!currentNode.getChildrenByName()[segment]) {
                validPath = false;
              } else {
                currentNode = currentNode.getChildrenByName()[segment];
              }
            }
          } else { // currentNode is not loaded
            currentNode = undefined;
          }
        });

        // If the given path exists...
        if (validPath) {
          if (currentNode && currentNode.getChildrenByName()) { // currentNode is loaded
            const children = currentNode.getChildren();
            //this is the type of channel to be found in a template folder so that the folder is used as template.
            const templateChannelDocType = session._.getConnection().getContentRepository().getContentType(CreateFromTemplateStudioPluginSettings_properties.doctype);

            // Iterator over each children of the folder
            children.forEach((templateFolder: Content): void => {
              if (templateFolder.isFolder()) {
                if (templateFolder && templateFolder.getChildrenByName()) { // template folder is loaded

                  // Check, if there is a content (CMSymbol) with the predefined descriptor name
                  const c: Content = templateFolder.getChildrenByName()
                    [CreateFromTemplateStudioPluginSettings_properties.template_descriptor_name];
                  if (c) {
                    c.load(); // load content asychronously, but ignore callback
                    if (c.getProperties()) { // but get a dependency on properties to force reevaluation of this
                      // ValueExpression
                      // verify that there is a template page next to the descriptor, otherwise ignore this descriptor
                      let pageFound = false;
                      const templateFolderChildren = templateFolder.getChildDocuments();
                      templateFolderChildren.forEach((folderChild: Content): void => {
                        folderChild.load();
                        if (folderChild.getProperties()) {
                          if (folderChild.getType().isSubtypeOf(templateChannelDocType)) {
                            pageFound = true;
                          }
                        }
                      });
                      // Check, if there exists a localized variant of this Content (CMSymbol)
                      if (pageFound) {
                        const locContent = ContentLocalizationUtil.getLocalizedContentSync(c);
                        if (locContent !== undefined) {
                          // If content is not already part of the result list
                          if (contentListResult.indexOf(locContent) === -1) {
                            // add it
                            contentListResult.push(locContent);
                          }
                        } else { // Descriptor is not loaded yet
                          ready = false;
                        }
                      }
                    } else { // Content is not loaded yet
                      ready = false;
                    }
                  }
                } else { // Template Folder is not loaded yet
                  ready = false;
                }
              }
            });
          } else { // path is not loaded yet
            ready = false;
          }
        } else {
          Logger.debug(TemplateBeanListChooserBase + ": No valid documents are found in configured path '" + path + "'.");
        }
      });
      if (ready) {
        // All contents should be loaded by now, adding a comparator does not cause the FVE to fire again.
        return contentListResult.sort(bind(this, this.comparator));
      } else {
        // Make sure that the contentList remains undefined until it is complete.
        return undefined;
      }
    });
  }

  /////////////////////////////////////////////////////////////////////////////////////
  // Functions for evaluate values for dataview store converters
  /////////////////////////////////////////////////////////////////////////////////////
  static getXTemplateForRendering(): XTemplate {
    return TemplateBeanListChooserBase.#xTemplate;
  }

  computeIconURL(name: string, content: Content): string {
    if (content && content.getProperties()) {
      const imageBlob: Blob = content.getProperties().get("icon");
      if (imageBlob) {
        const size = CreateFromTemplateStudioPluginSettings_properties.template_icon_size;
        return editorContext._.getThumbnailUri(content, ImageUtil.getCroppingOperation(Number(size), Number(size)));
      }
    }
    return Ext.BLANK_IMAGE_URL;
  }

  getDescription(name: string, content: Content): string {
    if (content && content.getProperties()) {

      let description: string = content.getProperties().get("description");
      if (!description || description.length === 0) {
        description = name;
      }
      return description;
    }
    return "";
  }

  /**
   * Comparator to sort the result list of templates, when this list is completely built
   * @param val1 one Bean
   * @param val2 another Bean
   * @return the compare result
   */
  comparator(val1: Bean, val2: Bean): number {
    return this.getDescription(val1.get("name"), as(val1, Content)).localeCompare(this.getDescription(val2.get("name"), as(val2, Content)));
  }

  /**
   * Returns the itemSelector value matching the content template.
   *
   * @return default itemSelector value
   */
  static getContentItemSelector(): string {
    return TemplateBeanListChooserBase.#CONTENT_ITEM_SELECTOR_EXPRESSION;
  }

}

export default TemplateBeanListChooserBase;
