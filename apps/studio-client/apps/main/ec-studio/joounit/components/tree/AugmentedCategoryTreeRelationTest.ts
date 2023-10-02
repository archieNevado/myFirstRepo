import CategoryImpl from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CategoryImpl";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import SitesService from "@coremedia/studio-client.multi-site-models/SitesService";
import { as, bind, mixin } from "@jangaroo/runtime";
import { AnyFunction } from "@jangaroo/runtime/types";
import augmentedCategoryTreeRelation from "../../../src/tree/augmentedCategoryTreeRelation";
import AbstractCatalogStudioTest from "../../AbstractCatalogStudioTest";

class AugmentedCategoryTreeRelationTest extends AbstractCatalogStudioTest {

  #siteRootDocument: RemoteBean = null;

  #rootCategoryDocument: RemoteBean = null;

  #topCategory: CategoryImpl = null;

  #topCategoryDocument: RemoteBean = null;

  #leafCategoryDocument: RemoteBean = null;

  editorContext_getSitesService: AnyFunction = null;

  override setUp(): void {
    super.setUp();

    EditorContextImpl.initEditorContext();

    const editorContext = EditorContextImpl.getInstance();
    this.editorContext_getSitesService = bind(editorContext, editorContext.getSitesService);
    editorContext.getSitesService = bind(this, this.#getSitesService);

    this.#siteRootDocument = beanFactory._.getRemoteBean(AbstractCatalogStudioTest.SITE_ROOT_DOCUMENT_ID);
    this.#rootCategoryDocument = beanFactory._.getRemoteBean(AbstractCatalogStudioTest.ROOT_CATEGORY_DOCUMENT_ID);
    this.#topCategory = as(beanFactory._.getRemoteBean(AbstractCatalogStudioTest.TOP_CATEGORY_ID), CategoryImpl);
    this.#topCategoryDocument = beanFactory._.getRemoteBean(AbstractCatalogStudioTest.TOP_CATEGORY_DOCUMENT_ID);
    this.#leafCategoryDocument = beanFactory._.getRemoteBean(AbstractCatalogStudioTest.LEAF_CATEGORY_DOCUMENT_ID);
  }

  override tearDown(): void {
    super.tearDown();
    editorContext._.getSitesService = this.editorContext_getSitesService;
  }

  testGetChildrenOfLeafCategoryDocument(): void {
    //TODO: nothing to test as augmentedCategoryTreeRelation#getChildrenOf is not implemented yet.
  }

  testGetParentUncheckedOfLeafCategoryDocument(): void {
    this.chain(
      this.#waitForTheUncheckedParentOfLeafCategoryDocumentToBeRootCategoryDocument(),
      this.#augmentTopCategory(),
      this.#waitForTheUncheckedParentOfLeafCategoryDocumentToBeTopCategoryDocument(),
    );
  }

  #waitForTheUncheckedParentOfLeafCategoryDocumentToBeRootCategoryDocument(): Step {
    return new Step("wait for the unchecked parent of the leaf category document to be the category root document",
      (): boolean =>
        this.#rootCategoryDocument === augmentedCategoryTreeRelation.getParentUnchecked(this.#leafCategoryDocument),
    );
  }

  #augmentTopCategory(): Step {
    return new Step("Augment the top category",
      (): boolean => true,
      (): void => {
        this.#topCategory.getContent = ((): RemoteBean =>
          this.#topCategoryDocument
        );
      });
  }

  #waitForTheUncheckedParentOfLeafCategoryDocumentToBeTopCategoryDocument(): Step {
    return new Step("wait for the unchecked parent of the leaf category document to be the top category document",
      (): boolean =>
        this.#topCategoryDocument === augmentedCategoryTreeRelation.getParentUnchecked(this.#leafCategoryDocument),
    );
  }

  testIsRootOfSiteRootDocument(): void {
    this.waitUntil("wait for the site root document to be evaluated to be root",
      (): boolean =>
        augmentedCategoryTreeRelation.isRoot(this.#siteRootDocument),

    );

  }

  testIsNotRootOfRootCategoryDocument(): void {
    this.waitUntil("wait for the root category document to be evaluated not to be root",
      (): boolean =>
        !augmentedCategoryTreeRelation.isRoot(this.#rootCategoryDocument),

    );

  }

  #getSitesService(): SitesService {
    return Object.setPrototypeOf({
      "getSiteRootDocument": (siteId: string): any =>
        this.#siteRootDocument
      ,
      "getSiteIdFor": (content: Content): string =>
        AbstractCatalogStudioTest.HELIOS_SITE_ID,

    }, mixin(class {}, SitesService).prototype);
  }

}

export default AugmentedCategoryTreeRelationTest;
