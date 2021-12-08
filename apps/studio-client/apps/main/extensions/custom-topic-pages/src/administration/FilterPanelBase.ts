import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Container from "@jangaroo/ext-ts/container/Container";
import Event from "@jangaroo/ext-ts/event/Event";
import Field from "@jangaroo/ext-ts/form/field/Field";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";

interface FilterPanelBaseConfig extends Config<Container>, Partial<Pick<FilterPanelBase,
  "filterExpression" |
  "applyFilterFunction"
>> {
}

/**
 * The super class of the filter search text field.
 */
class FilterPanelBase extends Container {
  declare Config: FilterPanelBaseConfig;

  filterExpression: ValueExpression = null;

  applyFilterFunction: AnyFunction = null;

  constructor(config: Config<FilterPanelBase> = null) {
    super(config);
    this.applyFilterFunction = config.applyFilterFunction;
  }

  /**
   * Executed when the user presses the enter key of the search area.
   * @param field The field the event was triggered from.
   * @param e The key event.
   */
  protected applyFilterInput(field: Field, e: Event): void {
    if (e.getKey() === Event.ENTER) {
      this.applyFilterFunction();
      e.stopEvent();
    }
  }
}

export default FilterPanelBase;
