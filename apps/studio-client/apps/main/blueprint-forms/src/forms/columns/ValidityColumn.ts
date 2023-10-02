import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Calendar from "@coremedia/studio-client.client-core/data/Calendar";
import ObjectUtils from "@coremedia/studio-client.client-core/util/ObjectUtils";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconColumn from "@coremedia/studio-client.ext.ui-components/grid/column/IconColumn";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import DateUtil from "@jangaroo/ext-ts/Date";
import Model from "@jangaroo/ext-ts/data/Model";
import Store from "@jangaroo/ext-ts/data/Store";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";
import Validation_properties from "../../Validation_properties";

interface ValidityColumnConfig extends Config<IconColumn> {
}

/**
 * A column object that displays the validity status of a content as an icon.
 * This column expects that a data field is defined, providing
 * the content's visibilityStatus.
 */

class ValidityColumn extends IconColumn {
  declare Config: ValidityColumnConfig;

  constructor(config: Config<ValidityColumn> = null) {
    super(ConfigUtils.apply(Config(ValidityColumn, {
      header: BlueprintDocumentTypes_properties.CMLinkable_validity_text,
      align: "center",
      stateId: ValidityColumn.STATUS_ID,
      dataIndex: ValidityColumn.STATUS_ID,
    }), config));
  }

  static readonly STATUS_ID: string = "validityStatus";

  static readonly #INVALID_FUTURE: string = "invalid-future";

  static readonly #INVALID_PAST: string = "invalid-past";

  static readonly #DATE_FORMAT: string = Editor_properties.dateFormat;

  protected override calculateIconCls(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store): string {
    if (value && value.clsName) {
      if (value.clsName == ValidityColumn.#INVALID_PAST) {
        return CoreIcons_properties.not_valid_anymore;
      } else if (value.clsName == ValidityColumn.#INVALID_FUTURE) {
        return CoreIcons_properties.not_valid_yet;
      }
    }
    return "";
  }

  protected override calculateToolTipText(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store): string {
    if (value) {
      if (value.clsName == ValidityColumn.#INVALID_PAST) {
        return Validation_properties.ValidationStatus_not_valid_anymore + " " + value.dateString;
      } else if (value.clsName == ValidityColumn.#INVALID_FUTURE) {
        return Validation_properties.ValidationStatus_will_be_active + " " + value.dateString;
      }
    }
    return "";
  }

  protected override calculateIconText(value: any, metadata: any, record: Model, rowIndex: number, colIndex: number, store: Store): string {
    return this.calculateToolTipText(value, metadata, record, rowIndex, colIndex, store);
  }

  static convert(value: string, content: Content): any {
    const validFrom: Calendar = ObjectUtils.getPropertyAt(content, "properties.validFrom", null);
    const validTo: Calendar = ObjectUtils.getPropertyAt(content, "properties.validTo", null);

    if (validFrom === undefined || validTo === undefined) {
      return undefined;
    }

    let clsName = "";
    let dateString: string;

    if (!validFrom && validTo && ValidityColumn.#isBeforeToday(validTo)) {
      clsName = ValidityColumn.#INVALID_PAST;
      dateString = DateUtil.format(validTo.getDate(), ValidityColumn.#DATE_FORMAT);
    } else if (validFrom && !validTo && !ValidityColumn.#isBeforeToday(validFrom)) {
      clsName = ValidityColumn.#INVALID_FUTURE;
      dateString = DateUtil.format(validFrom.getDate(), ValidityColumn.#DATE_FORMAT);
    } else if (validTo && ValidityColumn.#isBeforeToday(validTo)) {
      clsName = ValidityColumn.#INVALID_PAST;
      dateString = DateUtil.format(validTo.getDate(), ValidityColumn.#DATE_FORMAT);
    } else if (validTo && validFrom && !ValidityColumn.#isBeforeToday(validFrom) && !ValidityColumn.#isBeforeToday(validTo)) {
      clsName = ValidityColumn.#INVALID_FUTURE;
      dateString = DateUtil.format(validFrom.getDate(), ValidityColumn.#DATE_FORMAT);
    }
    return {
      clsName: clsName,
      dateString: dateString,
    };
  }

  static #isBeforeToday(date: Calendar): boolean {
    const today = new Date();
    return date.getDate().getTime() < today.getTime();
  }

  static #isOlderThan(date: Calendar, than: Calendar): boolean {
    return date.getDate().getTime() < than.getDate().getTime();
  }
}

export default ValidityColumn;
