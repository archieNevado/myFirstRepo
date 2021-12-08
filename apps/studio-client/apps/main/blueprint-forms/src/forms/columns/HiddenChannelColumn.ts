import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconColumn from "@coremedia/studio-client.ext.ui-components/grid/column/IconColumn";
import Model from "@jangaroo/ext-ts/data/Model";
import Store from "@jangaroo/ext-ts/data/Store";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import Validation_properties from "../../Validation_properties";

interface HiddenChannelColumnConfig extends Config<IconColumn> {
}

/**
 * A column object that displays the hidden status of a content as an icon.
 * This column expects that a data field is defined, providing
 * the content's hiddenStatus.
 */

class HiddenChannelColumn extends IconColumn {
  declare Config: HiddenChannelColumnConfig;

  constructor(config: Config<HiddenChannelColumn> = null) {
    super(ConfigUtils.apply(Config(HiddenChannelColumn, {
      align: "center",
      stateId: HiddenChannelColumn.STATUS_ID,
      dataIndex: HiddenChannelColumn.STATUS_ID,
    }), config));
  }

  static readonly STATUS_ID: string = "hidden";

  protected override calculateIconCls(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store): string {
    if (value) {
      return CoreIcons_properties.hidden_channel;
    }
    return "";
  }

  protected override calculateToolTipText(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store): string {
    if (value) {
      return Validation_properties.CMNavigation_hidden_text;
    }
  }

  protected override calculateIconText(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store): string {
    if (value) {
      return Validation_properties.CMNavigation_hidden_text;
    }
  }
}

export default HiddenChannelColumn;
