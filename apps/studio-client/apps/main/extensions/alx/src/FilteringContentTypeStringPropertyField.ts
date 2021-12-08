import session from "@coremedia/studio-client.cap-rest-client/common/session";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";
import ContentTypeStringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/ContentTypeStringPropertyField";
import Model from "@jangaroo/ext-ts/data/Model";
import Store from "@jangaroo/ext-ts/data/Store";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import { AnyFunction } from "@jangaroo/runtime/types";

interface FilteringContentTypeStringPropertyFieldConfig extends Config<ContentTypeStringPropertyField>, Partial<Pick<FilteringContentTypeStringPropertyField,
  "baseType"
>> {
}

/**
 * A contentTypeStringPropertyField instance that restricts the list of available content types to those matching 'filterFunction'
 */
class FilteringContentTypeStringPropertyField extends ContentTypeStringPropertyField {
  declare Config: FilteringContentTypeStringPropertyFieldConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.analytics.filteringContentTypeStringPropertyField";

  #contentType: ContentType = null;

  #filterFunction: AnyFunction = null;

  #storeFilterer: AnyFunction = null;

  /**
   * The name of the base content type to restrict the combo box to
   */
  baseType: string = null;

  constructor(config: Config<FilteringContentTypeStringPropertyField> = null) {
    super((()=>{
      this.#contentType = session._.getConnection().getContentRepository().getContentType(config.baseType);
      this.#filterFunction = ((record: Model): any =>
        session._.getConnection().getContentRepository().getContentType(String(record.get("name"))).isSubtypeOf(this.#contentType)
      );
      this.#storeFilterer = ((): void => {
        const store = cast(Store, this.getStore());
        store.suspendEvents(false);
        store.filterBy(this.#filterFunction);
        store.resumeEvents();
      });
      config = ConfigUtils.apply({ baseType: "_Document" }, config);
      return ConfigUtils.apply(Config(FilteringContentTypeStringPropertyField, {

        listeners: {
          afterRender: (): void =>{
            this.getStore().addListener("datachanged", this.#storeFilterer);
            this.#storeFilterer();
          },
        },

      }), config);
    })());
  }
}

export default FilteringContentTypeStringPropertyField;
