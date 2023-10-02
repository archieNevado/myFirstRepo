import FolderContentContainer from "@coremedia/studio-client.main.editor-components/sdk/collectionview/FolderContentContainer";
import ICollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/ICollectionView";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AssetCollectionViewExtension from "../AssetCollectionViewExtension";

interface AssetRepositoryListContainerConfig extends Config<FolderContentContainer>, Partial<Pick<AssetRepositoryListContainer,
  "selectionHolder"
>> {
}

class AssetRepositoryListContainer extends FolderContentContainer {
  declare Config: AssetRepositoryListContainerConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.assetRepositoryListContainer";

  static readonly ITEM_ID: string = "assetRepositoryListContainer";

  constructor(config: Config<AssetRepositoryListContainer> = null) {
    super(ConfigUtils.apply(Config(AssetRepositoryListContainer, {
      instanceName: AssetCollectionViewExtension.INSTANCE_NAME,
      itemId: AssetRepositoryListContainer.ITEM_ID,
      selectedFolderValueExpression: config.selectionHolder.getSelectedFolderValueExpression(),
      selectedRepositoryItemsValueExpression: config.selectionHolder.getSelectedRepositoryItemsValueExpression(),
      createdContentValueExpression: config.selectionHolder.getCreatedContentValueExpression(),

    }), config));
  }

  selectionHolder: ICollectionView = null;
}

export default AssetRepositoryListContainer;
