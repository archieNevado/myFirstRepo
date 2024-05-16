import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import EditorContextImpl from "@coremedia/studio-client.main.editor-components/sdk/EditorContextImpl";
import CollectionViewModelAction from "@coremedia/studio-client.main.editor-components/sdk/actions/CollectionViewModelAction";
import CollectionViewModel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionViewModel";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Store from "@jangaroo/ext-ts/data/Store";
import Assert from "@jangaroo/joounit/flexunit/framework/Assert";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import AbstractLiveContextStudioTest from "../AbstractLiveContextStudioTest";

class CollectionViewModelActionTest extends AbstractLiveContextStudioTest {
  static readonly #MODE_PROPERTY: string = CollectionViewModel.MODE_PROPERTY;

  static readonly #REPOSITORY_MODE: string = CollectionViewModel.REPOSITORY_MODE;

  static readonly #SEARCH_MODE: string = CollectionViewModel.SEARCH_MODE;

  #repositoryAction: CollectionViewModelAction = null;

  #searchAction: CollectionViewModelAction = null;

  #getPreferredSite: AnyFunction = null;

  #preferredSiteExpression: ValueExpression = null;

  override setUp(): void {
    super.setUp();

    this.#preferredSiteExpression = ValueExpressionFactory.create("site", beanFactory._.createLocalBean({ site: "HeliosSiteId" }));
    this.#getPreferredSite = editorContext._.getSitesService().getPreferredSiteId;
    editorContext._.getSitesService().getPreferredSiteId = ((): string =>
      this.#preferredSiteExpression.getValue()
    );

    this.#repositoryAction = new CollectionViewModelAction(
      Config(CollectionViewModelAction, {
        property: CollectionViewModel.MODE_PROPERTY,
        value: CollectionViewModelActionTest.#REPOSITORY_MODE,
      }));

    this.#searchAction = new CollectionViewModelAction(
      Config(CollectionViewModelAction, {
        property: CollectionViewModelActionTest.#MODE_PROPERTY,
        value: CollectionViewModelActionTest.#SEARCH_MODE,
      }));
  }

  override tearDown(): void {
    super.tearDown();
    editorContext._.getSitesService().getPreferredSiteId = this.#getPreferredSite;
  }

  //noinspection JSUnusedGlobalSymbols
  testDefault(): void {
    this.chain(this.#waitForDefault());
  }

  //noinspection JSUnusedGlobalSymbols
  testInCmsToSearch(): void {
    this.chain(this.#switchToSearch(),
      this.#waitForCmsSearch());
  }

  //noinspection JSUnusedGlobalSymbols
  testInCmsToRepository(): void {
    this.chain(this.#switchToSearch(),
      this.#waitForCmsSearch(),
      this.#switchToRepository(),
      this.#waitForDefault());
  }

  //noinspection JSUnusedGlobalSymbols
  testInCatalogToSearch(): void {
    this.chain(this.#switchToCatalog(),
      this.#waitForCatalogRepository(),
      this.#switchToSearch(),
      this.#waitForCatalogSearch());
  }

  //noinspection JSUnusedGlobalSymbols
  testInCatalogToRepository(): void {
    this.chain(this.#switchToCatalog(),
      this.#switchToSearch(),
      this.#waitForCatalogSearch(),
      this.#switchToRepository(),
      this.#waitForCatalogRepository());
  }

  //noinspection JSUnusedGlobalSymbols
  testInRepositoryToCatalog(): void {
    this.chain(this.#switchToCatalog(),
      this.#waitForCatalogRepository());
  }

  //noinspection JSUnusedGlobalSymbols
  testInRepositoryToCms(): void {
    this.chain(this.#switchToCatalog(),
      this.#waitForCatalogRepository(),
      this.#waitForDefault());
  }

  //noinspection JSUnusedGlobalSymbols
  testInSearchToCatalog(): void {
    this.chain(this.#switchToSearch(),
      this.#waitForCmsSearch(),
      this.#switchToCatalog(),
      this.#waitForCatalogSearch());
  }

  //noinspection JSUnusedGlobalSymbols
  testInSearchToCms(): void {
    this.chain(this.#switchToSearch(),
      this.#switchToCatalog(),
      this.#waitForCatalogSearch(),
      this.#waitForCmsSearch());
  }

  /**
   *  Waiting and Testing Steps
   */

  #waitForDefault(): Step {
    return new Step("wait for default repository cms and default mode repository.",
      (): boolean =>
        CollectionViewModelActionTest.getCollectionViewState().get(CollectionViewModelActionTest.#MODE_PROPERTY) === CollectionViewModelActionTest.#REPOSITORY_MODE
      ,
      (): void => {
        Assert.assertTrue(this.#repositoryAction.isPressed());
        Assert.assertFalse(this.#searchAction.isPressed());
      });
  }

  #waitForCmsSearch(): Step {
    return new Step("wait for repository cms and search mode.",
      (): boolean =>
        CollectionViewModelActionTest.getCollectionViewState().get(CollectionViewModelActionTest.#MODE_PROPERTY) === CollectionViewModelActionTest.#SEARCH_MODE
      ,
      (): void => {
        Assert.assertFalse(this.#repositoryAction.isPressed());
        Assert.assertTrue(this.#searchAction.isPressed());
      });
  }

  #waitForCatalogRepository(): Step {
    return new Step("wait for repository catalog and mode repository.",
      (): boolean =>
        CollectionViewModelActionTest.getCollectionViewState().get(CollectionViewModelActionTest.#MODE_PROPERTY) === CollectionViewModelActionTest.#REPOSITORY_MODE
      ,
      (): void => {
        Assert.assertTrue(this.#repositoryAction.isPressed());
        Assert.assertFalse(this.#searchAction.isPressed());
      });
  }

  #waitForCatalogSearch(): Step {
    return new Step("wait for repository catalog and mode search.",
      (): boolean =>
        CollectionViewModelActionTest.getCollectionViewState().get(CollectionViewModelActionTest.#MODE_PROPERTY) === CollectionViewModelActionTest.#SEARCH_MODE
      ,
      (): void => {
        Assert.assertFalse(this.#repositoryAction.isPressed());
        Assert.assertTrue(this.#searchAction.isPressed());
      });
  }

  /**
   * Action Steps
   */

  #switchToRepository(): Step {
    return new Step("switch to repository.",
      (): boolean =>
        true
      ,
      (): void =>
        this.#repositoryAction.execute(),
    );
  }

  #switchToSearch(): Step {
    return new Step("switch to search.",
      (): boolean =>
        true
      ,
      (): void =>
        this.#searchAction.execute(),
    );
  }

  #switchToCatalog(): Step {
    return new Step("switch to catalog.",
      (): boolean =>
        true
      ,
      (): void => {
        const store: Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
        CollectionViewModelActionTest.getCollectionViewState().set(CollectionViewModel.FOLDER_PROPERTY, store);
      });
  }

  static getCollectionViewState(): Bean {
    return cast(EditorContextImpl, editorContext._).getCollectionViewModel().getMainStateBean();
  }

}

export default CollectionViewModelActionTest;
