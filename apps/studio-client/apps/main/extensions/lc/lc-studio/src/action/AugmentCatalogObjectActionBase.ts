import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Right from "@coremedia/studio-client.cap-rest-client/content/authorization/Right";
import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteServiceMethodResponse from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethodResponse";
import RemoteBeanUtil from "@coremedia/studio-client.client-core/data/RemoteBeanUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import ContentCreationUtil from "@coremedia/studio-client.main.editor-components/sdk/util/ContentCreationUtil";
import { as, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
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

  protected override isDisabledFor(catalogObjects: Array<any>): boolean {
    let disabled = super.isDisabledFor(catalogObjects);
    if (!disabled && catalogObjects.length == 1) {
      const catalogObject = as(catalogObjects[0], CatalogObject);
      const siteId = catalogObject.getSiteId();
      const site = editorContext._.getSitesService().getSite(siteId);
      if (site) {
        const siteRootFolder = site.getSiteRootFolder();
        const repository = siteRootFolder.getRepository();
        const accessControl = repository.getAccessControl();
        disabled = !RemoteBeanUtil.isAccessible(siteRootFolder) ||
                 !accessControl.mayPerformForType(siteRootFolder, repository.getContentType(this.getContentType()), Right.WRITE);
      }
    }
    return disabled;
  }

  protected override myHandler(): void {
    const catalogObject: CatalogObject = this.getCatalogObjects()[0];
    if (this.isCorrectType(catalogObject)) {
      //call AugmentationService
      const augmentCommerceBeanUri = catalogObject.getStore().getUriPath() + "/augment";
      const remoteServiceMethod = new RemoteServiceMethod(augmentCommerceBeanUri, "POST", true);
      remoteServiceMethod.request({ $Ref: catalogObject.getUriPath() }, (response: RemoteServiceMethodResponse): void => {
        if (response.success) {
          const content = cast(Content, response.getResponseJSON());
          content.load((): void => {
            ContentCreationUtil.initialize(content);
            editorContext._.getWorkAreaTabManager().replaceTab(catalogObject, content);
          });
        }
      });
    }
  }
}

export default AugmentCatalogObjectActionBase;
