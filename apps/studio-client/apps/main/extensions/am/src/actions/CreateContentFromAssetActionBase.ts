import BlueprintDocumentTypes_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintDocumentTypes_properties";
import StringPropertyDescriptor from "@coremedia/studio-client.cap-rest-client/common/descriptors/StringPropertyDescriptor";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentCreateResult from "@coremedia/studio-client.cap-rest-client/content/ContentCreateResult";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import Blob from "@coremedia/studio-client.client-core/data/Blob";
import Calendar from "@coremedia/studio-client.client-core/data/Calendar";
import RemoteBeanUtil from "@coremedia/studio-client.client-core/data/RemoteBeanUtil";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ActionConfigUtil from "@coremedia/studio-client.ext.cap-base-components/actions/ActionConfigUtil";
import ContentAction from "@coremedia/studio-client.ext.cap-base-components/actions/ContentAction";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import QuickCreateDialog from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateDialog";
import ProcessingData from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/processing/ProcessingData";
import ComponentManager from "@jangaroo/ext-ts/ComponentManager";
import { as, bind, cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import int from "@jangaroo/runtime/int";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import { AnyFunction } from "@jangaroo/runtime/types";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetConstants from "../AssetConstants";
import CreateContentFromAssetAction from "./CreateContentFromAssetAction";

interface CreateContentFromAssetActionBaseConfig extends Config<ContentAction> {
}

class CreateContentFromAssetActionBase extends ContentAction {
  declare Config: CreateContentFromAssetActionBaseConfig;

  #assetContentType: string = null;

  #targetContentType: string = null;

  #sourceRenditionProperty: string = null;

  #targetRenditionProperty: string = null;

  #targetAssetLinkProperty: string = null;

  #targetCopyrightProperty: string = null;

  #targetValidToProperty: string = null;

  #sourceThumbnailProperty: string = null;

  #targetThumbnailContentType: string = null;

  #targetThumbnailProperty: string = null;

  #targetLinkedThumbnailProperty: string = null;

  #targetThumbnailAssetLinkProperty: string = null;

  constructor(config: Config<CreateContentFromAssetAction> = null) {

    super((()=>{
      this.#assetContentType = config.assetContentType;
      this.#targetContentType = config.targetContentType;
      this.#sourceRenditionProperty = config.sourceRenditionProperty;
      this.#targetRenditionProperty = config.targetRenditionProperty;
      this.#targetAssetLinkProperty = config.targetAssetLinkProperty;
      this.#targetCopyrightProperty = config.targetCopyrightProperty;
      this.#targetValidToProperty = config.targetValidToProperty;
      this.#targetThumbnailProperty = config.targetThumbnailProperty;
      this.#targetThumbnailContentType = config.targetThumbnailContentType;
      this.#sourceThumbnailProperty = config.sourceThumbnailProperty;
      this.#targetLinkedThumbnailProperty = config.targetLinkedThumbnailProperty;
      this.#targetThumbnailAssetLinkProperty = config.targetThumbnailAssetLinkProperty;
      return ActionConfigUtil.extendConfiguration(
        resourceManager.getResourceBundle(null, AMStudioPlugin_properties).content,
        config,
        "create" + this.#targetContentType + "From" + this.#assetContentType,
        { handler: bind(this, this.#createContentsFromAssets) });
    })());
  }

  protected override isHiddenFor(contents: Array<any>): boolean {
    if (!contents || contents.length === 0) {
      return true;
    }

    return contents.some((content: Content): boolean =>
      !content.getState().readable || !content.isDocument() ||
             !content.getType() ||
             !content.getType().isSubtypeOf(this.#assetContentType),
    );
  }

  protected override isDisabledFor(contents: Array<any>): boolean {
    if (!contents || contents.length === 0) {
      return true;
    }

    return contents.some((content: Content): boolean =>{
      const assetProperties = content.getProperties();
      if (!assetProperties) {
        return true;
      }
      return assetProperties.get(this.#sourceRenditionProperty) === null;
    });
  }

  #createContentsFromAssets(): void {
    const contents: Array<any> = this.getContents();

    if (this.isDisabledFor(contents)) {
      return;
    }

    this.#createSingleContent(contents, 0);
  }

  #createSingleContent(contents: Array<any>, i: int): void {
    if (i >= contents.length) {
      return;
    }

    const asset: Content = contents[i];
    const quickCreateConfig = Config(QuickCreateDialog);
    quickCreateConfig.bindTo = this.getValueExpression();
    quickCreateConfig.openInTab = false;
    quickCreateConfig.contentType = this.#targetContentType;
    quickCreateConfig.onSuccess = ((createdContent: Content, data: ProcessingData, callback: AnyFunction): void => {
      const metadataStruct: Struct = asset.getProperties().get(AssetConstants.PROPERTY_ASSET_METADATA);
      RemoteBeanUtil.loadAll((): void => {
        createdContent.getProperties().set(this.#targetAssetLinkProperty, [asset]);
        createdContent.getProperties().set(this.#targetRenditionProperty, asset.getProperties().get(this.#sourceRenditionProperty));
        if (this.#targetCopyrightProperty) {
          const copyright: string = metadataStruct.get(AssetConstants.PROPERTY_ASSET_METADATA_COPYRIGHT);
          if (copyright) {
            const descriptor = as(createdContent.getType().getDescriptor(this.#targetCopyrightProperty), StringPropertyDescriptor);
            if (descriptor) {
              createdContent.getProperties().set(this.#targetCopyrightProperty, copyright.substr(0, descriptor.length));
            }
          }
        }
        if (this.#targetValidToProperty) {
          const expirationDate: Calendar = metadataStruct.get(AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE);
          if (expirationDate) {
            const validToDescriptor = createdContent.getType().getDescriptor(this.#targetValidToProperty);
            if (validToDescriptor) {
              createdContent.getProperties().set(this.#targetValidToProperty, expirationDate);
            }
          }
        }
        if (this.#targetThumbnailProperty && this.#sourceThumbnailProperty) {
          const thumbnail: Blob = asset.getProperties().get(this.#sourceThumbnailProperty);
          if (thumbnail) {
            const contentType = session._.getConnection().getContentRepository().getContentType(this.#targetThumbnailContentType);
            const thumbnailProperties: Record<string, any> = {};
            thumbnailProperties[this.#targetThumbnailProperty] = thumbnail;
            thumbnailProperties[this.#targetThumbnailAssetLinkProperty] = [asset];
            contentType.createWithProperties(
              createdContent.getParent(),
              createdContent.getName() + " Thumbnail",
              thumbnailProperties,
              (result: ContentCreateResult): void => {
                const documentsToOpen = [];
                if (result.createdContent) {
                  createdContent.getProperties().set(this.#targetLinkedThumbnailProperty, [result.createdContent]);
                  documentsToOpen.push(result.createdContent);
                }
                documentsToOpen.push(createdContent);
                editorContext._.getContentTabManager().openDocuments(documentsToOpen);
                callback();
                this.#createSingleContent(contents, i + 1);
              });
          } else {
            editorContext._.getContentTabManager().openDocument(createdContent);
            callback();
            this.#createSingleContent(contents, i + 1);
          }
        } else {
          editorContext._.getContentTabManager().openDocument(createdContent);
          callback();
          this.#createSingleContent(contents, i + 1);
        }

      }, createdContent, metadataStruct);
    });
    quickCreateConfig.defaultNameExpression = ValueExpressionFactory.createFromFunction((): string =>
      asset.getName() + " " +
              (BlueprintDocumentTypes_properties[this.#targetContentType + "_text"] || this.#targetContentType),
    );

    const dialog = cast(QuickCreateDialog, ComponentManager.create(quickCreateConfig));
    dialog.show();
  }
}

export default CreateContentFromAssetActionBase;
