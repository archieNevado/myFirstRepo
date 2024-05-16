import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import augmentationService from "@coremedia-blueprint/studio-client.main.ec-studio/augmentation/augmentationService";
import jobService from "@coremedia/studio-client.cap-rest-client/common/jobService";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Right from "@coremedia/studio-client.cap-rest-client/content/authorization/Right";
import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteBeanUtil from "@coremedia/studio-client.client-core/data/RemoteBeanUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import ContentCreationUtil from "@coremedia/studio-client.main.editor-components/sdk/util/ContentCreationUtil";
import Ext from "@jangaroo/ext-ts";
import { as, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import AugmentationJob from "../job/AugmentationJob";
import AugmentCategoryAction from "./AugmentCategoryAction";
import CreateCatalogObjectDocumentAction from "./CreateCatalogObjectDocumentAction";

interface AugmentCatalogObjectActionBaseConfig extends Config<CreateCatalogObjectDocumentAction> {
}

/**
 * This action is intended to be used from within EXML, only.
 *
 */
class AugmentCatalogObjectActionBase extends CreateCatalogObjectDocumentAction {
  declare Config: AugmentCatalogObjectActionBaseConfig;

  /**
   * @param config the configuration object
   */
  constructor(config: Config<AugmentCategoryAction> = null) {
    super(config);
  }

  #isDisabledForCatalogObject(catalogObjectCandidate: any): boolean {
    const catalogObject = as(catalogObjectCandidate, CatalogObject);
    if (!catalogObject) {
      return true;
    }

    if (!(this.isCorrectType(catalogObject))) {
      return true;
    }

    const siteId = catalogObject.getSiteId();
    const site = editorContext._.getSitesService().getSite(siteId);
    if (!site) {
      return true;
    }

    const siteRootFolder = site.getSiteRootFolder();
    const repository = siteRootFolder.getRepository();
    const accessControl = repository.getAccessControl();
    return !RemoteBeanUtil.isAccessible(siteRootFolder) ||
      !accessControl.mayPerformForType(siteRootFolder, repository.getContentType(this.getContentType()), Right.WRITE);
  }

  #augmentAndOpenCatalogObject(catalogObject: CatalogObject) {
    if (!this.isCorrectType(catalogObject) || augmentationService.getContent(catalogObject)) {
      return;
    }

    const augmentCommerceBeanUri = catalogObject.getStore().getUriPath() + "/augment";
    const remoteServiceMethod = new RemoteServiceMethod(augmentCommerceBeanUri, "POST", true);
    remoteServiceMethod.request({ $Ref: catalogObject.getUriPath() }, (response => {
      if (!response.success) {
        return;
      }
      const content = cast(Content, response.getResponseJSON());
      content.load((): void => {
        ContentCreationUtil.initialize(content);
        editorContext._.getWorkAreaTabManager().replaceTab(catalogObject, content);
      });
    }));
  }

  protected override isDisabledFor(catalogObjects: Array<any>): boolean {
    const notAugmentedObjects = catalogObjects.filter(catalogObject => !augmentationService.getContent(catalogObject));
    if (notAugmentedObjects.length === 0) {
      return true;
    }
    return notAugmentedObjects.some(catalogObject => this.#isDisabledForCatalogObject(catalogObject));
  }

  protected override myHandler(): void {
    const catalogObjects = this.getCatalogObjects();
    if (catalogObjects.length === 1) {
      this.#augmentAndOpenCatalogObject(catalogObjects[0]);
    } else {
      for (const catalogObject of catalogObjects) {
        if (augmentationService.getContent(catalogObject)) {
          continue;
        }
        // targetFolder is relevant only for product variants
        const augmentationJob = new AugmentationJob(catalogObject, undefined);
        jobService._.executeJob(augmentationJob, Ext.emptyFn, Ext.emptyFn);
      }
    }
  }
}

export default AugmentCatalogObjectActionBase;
