import DateColumn from "@jangaroo/ext-ts/grid/column/Date";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetConstants from "../AssetConstants";
import BaseModels_properties from "@coremedia/studio-client.base-models/BaseModels_properties";

interface ExpirationDateColumnConfig extends Config<DateColumn> {
}

class ExpirationDateColumn extends DateColumn {
  declare Config: ExpirationDateColumnConfig;

  constructor(config: Config<ExpirationDateColumn> = null) {
    super(ConfigUtils.apply(Config(ExpirationDateColumn, {
      header: AMStudioPlugin_properties.Column_ExpirationDate_text,
      width: 126,
      stateId: AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE,
      format: BaseModels_properties.dateFormat,
      dataIndex: AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE,
      ...{ sortField: AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE },
      sortable: true,

    }), config));
  }
}

export default ExpirationDateColumn;
