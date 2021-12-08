import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import augmentationService from "@coremedia-blueprint/studio-client.main.ec-studio/augmentation/augmentationService";
import ActionConfigUtil from "@coremedia/studio-client.ext.cap-base-components/actions/ActionConfigUtil";
import QuickCreateDialog from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateDialog";
import ProcessingData from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/processing/ProcessingData";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import Window from "@jangaroo/ext-ts/window/Window";
import { as, bind } from "@jangaroo/runtime";
import Class from "@jangaroo/runtime/Class";
import Config from "@jangaroo/runtime/Config";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import CreateCatalogObjectDocumentAction from "./CreateCatalogObjectDocumentAction";
import LiveContextCatalogObjectAction from "./LiveContextCatalogObjectAction";

interface CreateCatalogObjectDocumentActionBaseConfig extends Config<LiveContextCatalogObjectAction> {
}

/**
 * This action is intended to be used from within EXML, only.
 *
 */
class CreateCatalogObjectDocumentActionBase extends LiveContextCatalogObjectAction {
  declare Config: CreateCatalogObjectDocumentActionBaseConfig;

  static readonly EXTERNAL_ID_PROPERTY: string = "externalId";

  #contentType: string = null;

  #catalogObjectType: Class = null;

  #inheritEditors: boolean = false;

  /**
   * @param config the configuration object
   */
  constructor(config: Config<CreateCatalogObjectDocumentAction> = null) {
    super((()=>ActionConfigUtil.extendConfiguration(resourceManager.getResourceBundle(null, LivecontextStudioPlugin_properties).content, config, config.actionName, { handler: bind(this, this.myHandler) }))());
    this.#contentType = config.contentType;
    this.#catalogObjectType = config.catalogObjectType;
    this.#inheritEditors = config.inheritEditors;
  }

  protected override isDisabledFor(catalogObjects: Array<any>): boolean {
    //the action should be enabled only if there is only one catalog object and it is a correct configured type
    if (catalogObjects.length !== 1) {
      return true;
    }
    const catalogObject: CatalogObject = catalogObjects[0];
    if (!(this.isCorrectType(catalogObject))) {
      return true;
    }
    //check if the catalog object has already an associated content
    if (augmentationService.getContent(catalogObject)) {
      return true;
    }

    return super.isDisabledFor(catalogObjects);
  }

  protected override isHiddenFor(catalogObjects: Array<any>): boolean {
    return super.isHiddenFor(catalogObjects) || this.isDisabledFor(catalogObjects);
  }

  protected isCorrectType(catalogObject: CatalogObject): boolean {
    return Object.getPrototypeOf(catalogObject) === this.#catalogObjectType.prototype;
  }

  protected myHandler(): void {
    const catalogObject: CatalogObject = this.getCatalogObjects()[0];
    if (this.isCorrectType(catalogObject)) {
      //create the dialog
      const dialogConfig = Config(QuickCreateDialog);
      dialogConfig.contentType = this.getContentType();
      dialogConfig.model = new ProcessingData();
      dialogConfig.model.set(CreateCatalogObjectDocumentActionBase.EXTERNAL_ID_PROPERTY, catalogObject.getId());
      dialogConfig.model.set(ProcessingData.NAME_PROPERTY, catalogObject.getName());
      dialogConfig.inheritEditors = this.#inheritEditors;

      const dialog = as(ComponentManager.create(dialogConfig, "window"), Window);
      dialog.show();
    }
  }

  protected getContentType(): string {
    return this.#contentType;
  }
}

export default CreateCatalogObjectDocumentActionBase;
