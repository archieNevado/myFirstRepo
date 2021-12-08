import UserUtil from "@coremedia/studio-client.cap-base-models/util/UserUtil";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";
import User from "@coremedia/studio-client.cap-rest-client/user/User";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import StudioDialog from "@coremedia/studio-client.ext.base-components/dialogs/StudioDialog";
import StudioConfigurationUtil from "@coremedia/studio-client.ext.cap-base-components/util/config/StudioConfigurationUtil";
import StatefulTextField from "@coremedia/studio-client.ext.ui-components/components/StatefulTextField";
import ValidationState from "@coremedia/studio-client.ext.ui-components/mixins/ValidationState";
import ValidationStateMixin from "@coremedia/studio-client.ext.ui-components/mixins/ValidationStateMixin";
import NavigationLinkField_properties from "@coremedia/studio-client.main.bpbase-studio-components/navigationlink/NavigationLinkField_properties";
import FolderCreationResult from "@coremedia/studio-client.main.editor-components/sdk/components/folderprompt/FolderCreationResult";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import FolderChooserListView from "@coremedia/studio-client.main.editor-components/sdk/folderchooser/FolderChooserListView";
import ContentCreationUtil from "@coremedia/studio-client.main.editor-components/sdk/util/ContentCreationUtil";
import MessageBoxUtil from "@coremedia/studio-client.main.editor-components/sdk/util/MessageBoxUtil";
import Ext from "@jangaroo/ext-ts";
import StringUtil from "@jangaroo/ext-ts/String";
import { as, asConfig, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import trace from "@jangaroo/runtime/trace";
import { AnyFunction } from "@jangaroo/runtime/types";
import CreateFromTemplateDialog from "./CreateFromTemplateDialog";
import CreateFromTemplateProcessor from "./CreateFromTemplateProcessor";
import CreateFromTemplateStudioPluginSettings_properties from "./CreateFromTemplateStudioPluginSettings_properties";
import CreateFromTemplateStudioPlugin_properties from "./CreateFromTemplateStudioPlugin_properties";
import ProcessingData from "./model/ProcessingData";

interface CreateFromTemplateDialogBaseConfig extends Config<StudioDialog> {
}

/**
 * The base class of the create from template dialog creates.
 */
class CreateFromTemplateDialogBase extends StudioDialog {
  declare Config: CreateFromTemplateDialogBaseConfig;

  #_disabledValueExpression: ValueExpression = null;

  #_model: ProcessingData = null;

  #_errorMessages: Record<string, any> = {};

  #_baseFolderEditorialVE: ValueExpression = null;

  #_baseFolderNavigationVE: ValueExpression = null;

  #_folderValueExpression: ValueExpression = null;

  #_editorialFolderValueExpression: ValueExpression = null;

  #_nameField: StatefulTextField = null;

  static readonly NAME_FIELD_ID: string = "nameField";

  static readonly TEMPLATE_CHOOSER_FIELD_ID: string = "templateChooserField";

  static readonly PARENT_PAGE_FIELD_ID: string = "parentPageFieldId";

  static readonly BASE_FOLDER_CHOOSER_ID: string = "baseFolderChooser";

  static readonly CONTENT_BASE_FOLDER_CHOOSER_ID: string = "contentBaseFolderChooser";

  static readonly EDITOR_CONTAINER_ITEM_ID: string = "editorContainer";

  constructor(config: Config<CreateFromTemplateDialog> = null) {
    super(config);
    this.#_errorMessages[CreateFromTemplateDialogBase.NAME_FIELD_ID] = CreateFromTemplateStudioPlugin_properties.name_not_valid_value;
    this.#_errorMessages[CreateFromTemplateDialogBase.TEMPLATE_CHOOSER_FIELD_ID] = CreateFromTemplateStudioPlugin_properties.template_chooser_empty_text;
    this.#_errorMessages[CreateFromTemplateDialogBase.BASE_FOLDER_CHOOSER_ID] = CreateFromTemplateStudioPlugin_properties.page_folder_combo_validation_message;
  }

  // The height is set when a studio user resize the window manually. Since the displayed tab can change while the window is hidden,
  // we need to reset the height when it is shown again. Otherwise the auto-resizing functionality will not work anymore.
  // The maxHeight has to be set explicit to ensure correct window scrolling for many list items.
  protected override onShow(animateTarget?: any, callback: AnyFunction = null, scope: any = null): void {
    asConfig(this).height = null;
    asConfig(this).maxHeight = Ext.getBody().getHeight() - this.getHeader().getHeight();
    super.onShow(animateTarget, callback, scope);
  }

  protected override afterRender(): void {
    super.afterRender();

    this.#_nameField = as(this.queryById(CreateFromTemplateDialogBase.NAME_FIELD_ID), StatefulTextField);
    this.#_nameField.on("blur", bind(this, this.#validateForm));

    this.getModel().set(ProcessingData.FOLDER_PROPERTY, []);
    this.getModel().set(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property, []);

    this.getBaseFolderVE().loadValue((path: string): void => {
      this.#setBaseFolderInModel(path);
      this.getBaseFolderVE().addChangeListener(bind(this, this.#loadAndSetBaseFolder));
    });

    this.getContentBaseFolderVE().loadValue((path: string): void => {
      this.#setContentBaseFolderInModel(path);
      this.getContentBaseFolderVE().addChangeListener(bind(this, this.#loadAndSetContentBaseFolder));
    });

    this.#validateForm();
  }

  #loadAndSetBaseFolder(): void {
    this.getBaseFolderVE().loadValue((path: string): void =>
      this.#setBaseFolderInModel(path),
    );
  }

  #loadAndSetContentBaseFolder(): void {
    this.getContentBaseFolderVE().loadValue((path: string): void =>
      this.#setContentBaseFolderInModel(path),
    );
  }

  #setBaseFolderInModel(path: string): void {
    if (path) {
      const baseFolder = editorContext._.getSession().getConnection().getContentRepository().getChild(path);
      if (baseFolder) {
        this.getModel().set(ProcessingData.FOLDER_PROPERTY, [baseFolder]);
      }
    }
  }

  #setContentBaseFolderInModel(path: string): void {
    if (path) {
      const contentBaseFolder = editorContext._.getSession().getConnection().getContentRepository().getChild(path);
      if (contentBaseFolder) {
        this.getModel().set(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property, [contentBaseFolder]);
      }
    }
  }

  protected getContentType(contentypeName: string): ContentType {
    return editorContext._.getSession().getConnection().getContentRepository().getContentType(contentypeName);
  }

  protected override onDestroy(): void {
    this.#_model.removeValueChangeListener(bind(this, this.#validateForm));
    this.getBaseFolderVE().removeChangeListener(bind(this, this.#loadAndSetBaseFolder));
    this.getContentBaseFolderVE().removeChangeListener(bind(this, this.#loadAndSetContentBaseFolder));
    super.onDestroy();
  }

  /**
   * Creates the model that is used for this dialog.
   * @return
   */
  protected getModel(): ProcessingData {
    if (!this.#_model) {
      this.#_model = new ProcessingData();

      //pre-fill default values
      const site = editorContext._.getSitesService().getPreferredSite();
      if (site) {
        const root = site.getSiteRootDocument();
        const property = CreateFromTemplateStudioPluginSettings_properties.parent_property;
        this.#_model.set(property, root);
      }

      this.#_model.addValueChangeListener(bind(this, this.#validateForm));
    }
    return this.#_model;
  }

  #validateForm(): void {
    this.getDisabledValueExpression().setValue(false);

    //we can use the field validator since after the field becomes invalid, no event is fired to correct the value of the bound value expression
    this.#validateAsync(this.#_nameField, bind(this, this.nameValidator));
    this.#validate(this.queryById(CreateFromTemplateDialogBase.CONTENT_BASE_FOLDER_CHOOSER_ID), bind(this, this.contentBaseFolderValidator));
    this.#validate(this.queryById(CreateFromTemplateDialogBase.BASE_FOLDER_CHOOSER_ID), bind(this, this.baseFolderValidator));
    this.#validate(this.queryById(CreateFromTemplateDialogBase.TEMPLATE_CHOOSER_FIELD_ID));
  }

  #validate(editor: any, validatorFunction: AnyFunction = null): void {
    if (editor) {
      validatorFunction = validatorFunction || editor.initialConfig["validate"];
      if (validatorFunction) {
        const result: boolean = validatorFunction(editor);
        this.#applyValidationResult(editor, result);
      }
    }
  }

  #validateAsync(editor: any, validator: AnyFunction): void {
    if (editor) {
      if (validator) {
        validator.call(null, (errorMessage: string): void =>
          this.#applyValidationResult(editor, !(errorMessage && errorMessage.length > 0), errorMessage),
        );
      }
    }
  }

  #applyValidationResult(editor: any, result: boolean, errorMessage: string = null): void {
    const statefulEditor = as(editor, ValidationStateMixin);
    let errorMsg: string = errorMessage ? errorMessage : this.#_errorMessages[editor.itemId];
    if (!errorMsg) {
      errorMsg = CreateFromTemplateStudioPlugin_properties.template_create_missing_value;
    }
    if (!result) {
      if (statefulEditor) {
        statefulEditor.validationState = ValidationState.ERROR;
        statefulEditor.validationMessage = errorMsg;
      }
      this.getDisabledValueExpression().setValue(true);
    } else {
      if (statefulEditor) {
        statefulEditor.validationState = null;
        statefulEditor.validationMessage = null;
      }
    }
  }

  /**
   * Invokes the post processing and closes the dialog
   */
  protected handleSubmit(): void {
    const data = this.getModel();
    const folder: Content = data.get(ProcessingData.FOLDER_PROPERTY)[0];
    const path = data.getExtendedPath(folder);
    const parent: Content = data.get(CreateFromTemplateStudioPluginSettings_properties.parent_property);

    if (!parent) {
      MessageBoxUtil.showConfirmation(CreateFromTemplateStudioPlugin_properties.text,
        CreateFromTemplateStudioPlugin_properties.no_parent_page_selected_warning,
        CreateFromTemplateStudioPlugin_properties.no_parent_page_selected_warning_buttonText,
        (buttonId: string): void => {
          if (buttonId === "ok") {
            this.#doCreation(path);
          }
        });
    } else {
      parent.invalidate((): void => {
        if (parent.isCheckedOutByOther()) {
          parent.getEditor().load((user: User): void => {
            const msg = StringUtil.format(NavigationLinkField_properties.layout_error_msg, UserUtil.convertDisplayName(user));
            MessageBoxUtil.showError(NavigationLinkField_properties.layout_error, msg);
          });
        } else {
          this.#doCreation(path);
        }
      });
    }
  }

  /**
   * Performs the creation of the content
   * @param path the navigation path
   */
  #doCreation(path: string): void {
    //first ensure that all folders exist
    const data = this.getModel();

    const editorialFolder: Content = data.get(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property)[0];
    const editorialFolderName = data.getExtendedPath(editorialFolder);
    ContentCreationUtil.createRequiredSubfolders(path, (result: FolderCreationResult): void => {
      if (result.success) {
        const navigationFolder = result.baseFolder;
        ContentCreationUtil.createRequiredSubfolders(editorialFolderName, (editorialResult: FolderCreationResult): void => {
          if (editorialResult.success) {
            this.destroy();

            //apply the folder instance to the processing data
            data.set(ProcessingData.FOLDER_PROPERTY, navigationFolder);
            CreateFromTemplateProcessor.process(data, (): void => {
              trace("INFO", "Finished create from template");
              const content = data.getContent();
              const initializer = editorContext._.lookupContentInitializer(content.getType());
              if (initializer) {
                initializer(content);
              }

              const parent: Content = data.get(CreateFromTemplateStudioPluginSettings_properties.parent_property);
              if (parent) {
                parent.invalidate((): void => {
                  editorContext._.getContentTabManager().openDocument(parent);
                  this.#openNewPageInTab(data);
                });
              } else {
                this.#openNewPageInTab(data);
              }
            });
          } else {
            MessageBoxUtil.showError(CreateFromTemplateStudioPlugin_properties.text,
              CreateFromTemplateStudioPlugin_properties.editor_folder_could_not_create_message);
            editorialResult.remoteError.setHandled(true);
          }
        });
      } else {
        MessageBoxUtil.showError(CreateFromTemplateStudioPlugin_properties.text,
          CreateFromTemplateStudioPlugin_properties.page_folder_could_not_create_message);
        result.remoteError.setHandled(true);
      }
    });
  }

  #openNewPageInTab(data: ProcessingData): void {
    const newPage = data.getContent();
    newPage.invalidate((): void =>
      editorContext._.getContentTabManager().openDocument(newPage),
    );
  }

  protected getDisabledValueExpression(): ValueExpression {
    if (!this.#_disabledValueExpression) {
      this.#_disabledValueExpression = ValueExpressionFactory.create("disabled", beanFactory._.createLocalBean());
      this.#_disabledValueExpression.setValue(true);
    }
    return this.#_disabledValueExpression;
  }

  protected nameValidator(callback: AnyFunction): void {
    const repository = session._.getConnection().getContentRepository();
    const name: string = this.getModel().get(ProcessingData.NAME_PROPERTY);
    if (!(name && repository.isValidName(this.getModel().get(ProcessingData.NAME_PROPERTY)))) {
      callback(this.#_errorMessages[CreateFromTemplateDialogBase.NAME_FIELD_ID]);
      return;
    }

    let folder: Content | Content[] = this.getModel().get(ProcessingData.FOLDER_PROPERTY);
    if (Array.isArray(folder)) {
      folder = folder[0];
    }
    if (folder && folder.getPath().length > 0) {
      const createFolderPath = folder.getPath() + "/" + name;
      session._.getConnection().getContentRepository().getChild(createFolderPath, (c: Content): void => {
        if (c) {
          callback(this.#_errorMessages[CreateFromTemplateDialogBase.BASE_FOLDER_CHOOSER_ID]);
        } else {
          callback("");
        }
      });
    } else {
      callback("");
    }
  }

  protected templateChooserNonEmptyValidator(): boolean {
    const ve = ValueExpressionFactory.create(
      CreateFromTemplateStudioPluginSettings_properties.template_property, this.getModel());
    return ve && ve.getValue() && as(ve.getValue(), Array).length > 0;
  }

  protected contentBaseFolderValidator(folderChooserListView: FolderChooserListView): boolean {
    let folder: Content | Content[] = this.getModel().get(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property);
    if (Array.isArray(folder)) {
      folder = folder[0];
    }
    return folder && folder.getPath() && folder.getPath().length > 0;
  }

  protected baseFolderValidator(): boolean {
    let folder: Content | Content[] = this.getModel().get(ProcessingData.FOLDER_PROPERTY);
    if (Array.isArray(folder)) {
      folder = folder[0];
    }
    return folder && folder.getPath() && folder.getPath().length > 0;
  }

  protected getContentBaseFolderVE(): ValueExpression {
    if (!this.#_baseFolderEditorialVE) {
      this.#_baseFolderEditorialVE = ValueExpressionFactory.createFromFunction(bind(this, this.#baseFolderEditorialCalculation));
    }
    return this.#_baseFolderEditorialVE;
  }

  protected getBaseFolderVE(): ValueExpression {
    if (!this.#_baseFolderNavigationVE) {
      this.#_baseFolderNavigationVE = ValueExpressionFactory.createFromFunction(bind(this, this.#baseFolderNavigationCalculation));
    }
    return this.#_baseFolderNavigationVE;
  }

  protected getNavigationFolders(): Array<any> {
    const baseFolder = this.#baseFolderNavigationCalculation();
    if (baseFolder) {
      return [baseFolder];
    }
    return [];
  }

  protected getEditorialFolders(): Array<any> {
    const baseFolder = this.#baseFolderEditorialCalculation();
    if (baseFolder) {
      return [baseFolder];
    }
    return [];
  }

  #baseFolderNavigationCalculation(): string {
    return this.#baseFolderCalculation("paths.navigation", CreateFromTemplateDialogBase.getNavigationFolderFallback);
  }

  #baseFolderEditorialCalculation(): string {
    return this.#baseFolderCalculation("paths.editorial", CreateFromTemplateDialogBase.getEditorialFolderFallback);
  }

  #baseFolderCalculation(configuration: string, fallback: AnyFunction): string {
    let retPath = CreateFromTemplateDialogBase.#baseFolderCalculationRaw(configuration, fallback);
    if (retPath === undefined) {
      return undefined;
    }

    const diffSelectedParentPageAndNavigationPath = this.#getDiffNavigationFolderParentFolder();
    if (diffSelectedParentPageAndNavigationPath === undefined) {
      return undefined;
    }

    if (diffSelectedParentPageAndNavigationPath) {
      retPath += "/" + diffSelectedParentPageAndNavigationPath;
    }

    return retPath;
  }

  static #baseFolderCalculationRaw(configuration: string, fallback: AnyFunction): string {
    const folder: Content = StudioConfigurationUtil.getConfiguration("Content Creation", configuration);

    if (folder === undefined) {
      return undefined;
    }

    if (folder === null) {
      return fallback();
    } else {
      return folder.getPath();
    }
  }

  #getDiffNavigationFolderParentFolder(): string {
    const folderNavigation = CreateFromTemplateDialogBase.#baseFolderCalculationRaw("paths.navigation", CreateFromTemplateDialogBase.getNavigationFolderFallback);
    if (folderNavigation === undefined) {
      return undefined;
    }
    const parent: Content = this.getModel().get(CreateFromTemplateStudioPluginSettings_properties.parent_property);
    if (!parent) {
      return null;
    }

    const parentFolder = parent.getParent();

    if (parentFolder === undefined) {
      return undefined;
    }

    const parentFolderPath = parentFolder.getPath();
    if (parentFolderPath === undefined) {
      return undefined;
    }

    if (parentFolderPath.substr(0, folderNavigation.length) === folderNavigation) {
      return parentFolderPath.substr(folderNavigation.length + 1);
    } else {
      return null;
    }
  }

  protected static getNavigationFolderFallback(): string {
    return CreateFromTemplateDialogBase.getFolderFallback(CreateFromTemplateStudioPluginSettings_properties.doctype);
  }

  protected static getEditorialFolderFallback(): string {
    return CreateFromTemplateDialogBase.getFolderFallback("CMArticle");
  }

  protected static getFolderFallback(docType: string): string {
    const folder: Content = StudioConfigurationUtil.getConfiguration("Content Creation", "paths." + docType);
    if (folder === undefined) {
      return undefined;
    }
    if (folder) {
      return folder.getPath();
    }
    // fallback to preferred site's root folder if nothing is set in content creation
    const site = editorContext._.getSitesService().getPreferredSite();
    if (site && site.getSiteRootFolder()) {
      return site.getSiteRootFolder().getPath();
    }

    return null;
  }

  getFolderValueExpression(): ValueExpression {
    if (!this.#_folderValueExpression) {
      this.#_folderValueExpression = ValueExpressionFactory.create(ProcessingData.FOLDER_PROPERTY, this.getModel());
    }
    return this.#_folderValueExpression;
  }

  getEditorialFolderValueExpression(): ValueExpression {
    if (!this.#_editorialFolderValueExpression) {
      this.#_editorialFolderValueExpression = ValueExpressionFactory.create(CreateFromTemplateStudioPluginSettings_properties.editorial_folder_property, this.getModel());
    }
    return this.#_editorialFolderValueExpression;
  }

}

export default CreateFromTemplateDialogBase;
