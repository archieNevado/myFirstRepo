import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import ProductImpl from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductImpl";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import QuickCreateDialog from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateDialog";
import ProcessingData from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/processing/ProcessingData";
import ContentCreationUtil from "@coremedia/studio-client.main.editor-components/sdk/util/ContentCreationUtil";
import Ext from "@jangaroo/ext-ts";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import Window from "@jangaroo/ext-ts/window/Window";
import { as, bind, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import joo from "@jangaroo/runtime/joo";
import trace from "@jangaroo/runtime/trace";
import CreateCatalogObjectDocumentAction from "./CreateCatalogObjectDocumentAction";
import CreateCatalogObjectDocumentActionBase from "./CreateCatalogObjectDocumentActionBase";

interface CreateProductTeaserActionConfig extends Config<CreateCatalogObjectDocumentAction> {
}

class CreateProductTeaserAction extends CreateCatalogObjectDocumentAction {
  declare Config: CreateProductTeaserActionConfig;

  constructor(config: Config<CreateProductTeaserAction> = null) {
    super(ConfigUtils.apply(Config(CreateProductTeaserAction, {
      actionName: "createProductTeaser",
      contentType: "CMProductTeaser",
      catalogObjectType: ProductImpl,

    }), config));
  }

  protected override isCorrectType(catalogObject: CatalogObject): boolean {
    //check the product type so that a product variant is also of a correct type.
    return is(catalogObject, Product);
  }

  protected override isDisabledFor(catalogObjects: Array<any>): boolean {
    const myItems = catalogObjects.filter(bind(this, this.isCorrectType));
    return myItems.length < 1;
  }

  protected override myHandler(): void {
    const repo = session._.getConnection().getContentRepository();
    const myContentType = repo.getContentType(this.getContentType());
    const myItems = this.getCatalogObjects().filter(bind(this, this.isCorrectType));

    this.#showCreateProductTeaserDialog(myItems, myContentType);
  }

  #getLocale(catalogObject: CatalogObject): string {
    const site = editorContext._.getSitesService().getSite(catalogObject.getSiteId());
    let locale: string;
    if (site) {
      locale = site.getLocale().getLanguageTag();
    } else {
      locale = joo.localeSupport.getLocale();
    }
    return locale;
  }

  #showCreateProductTeaserDialog(products: Array<any>, myContentType: ContentType): void {
    const catalogObject: CatalogObject = products.shift();
    const locale = this.#getLocale(catalogObject);
    //create the dialog
    const dialogConfig = Config(QuickCreateDialog);
    dialogConfig.contentType = myContentType.getName();
    dialogConfig.model = new ProcessingData();
    dialogConfig.model.set(CreateCatalogObjectDocumentActionBase.EXTERNAL_ID_PROPERTY, catalogObject.getId());
    dialogConfig.model.set(ProcessingData.NAME_PROPERTY, catalogObject.getName());
    dialogConfig.inheritEditors = this.inheritEditors;
    dialogConfig.onSuccess = ((createdTeaser: Content, data: ProcessingData): void => {
      editorContext._.getContentTabManager().openDocument(createdTeaser);
      const productTeaserSelectedPath = createdTeaser.getParent();
      this.#createRemainingProductTeasers(products, myContentType, productTeaserSelectedPath, locale);
      trace("[INFO] created ", products.length + 1, " teasers in folder ", productTeaserSelectedPath.getPath());
    });
    const dialog = as(ComponentManager.create(dialogConfig, "window"), Window);
    dialog.show();
  }

  #createRemainingProductTeasers(products: Array<any>, myContentType: ContentType, folder: Content, locale: string): void {
    products.forEach((catalogObject: CatalogObject): void => {
      const externalId = catalogObject.getId();
      const preferredName = catalogObject.getName();
      const properties: Record<string, any> = {
        externalId: externalId,
        locale: locale,
      };
      ContentCreationUtil.createContent(folder, false, true, preferredName, myContentType, Ext.emptyFn, Ext.emptyFn, properties);
    });
  }
}

export default CreateProductTeaserAction;
