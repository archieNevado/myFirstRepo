import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CommerceObjectsDisplayFieldBase from "./CommerceObjectsDisplayFieldBase";

interface CommerceObjectsDisplayFieldConfig extends Config<CommerceObjectsDisplayFieldBase>, Partial<Pick<CommerceObjectsDisplayField,
  "personaContent" |
  "catalogObjectIdListName" |
  "invalidMessage" |
  "emptyMessage"
>> {
}

class CommerceObjectsDisplayField extends CommerceObjectsDisplayFieldBase {
  declare Config: CommerceObjectsDisplayFieldConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.p13n.studio.config.commerceObjectsLabel";

  constructor(config: Config<CommerceObjectsDisplayField> = null) {
    super((()=> ConfigUtils.apply(Config(CommerceObjectsDisplayField, {

      plugins: [
        Config(BindPropertyPlugin, {
          componentProperty: "value",
          bindTo: this.getCommerceObjectsExpression(),
        }),
      ],

    }), config))());
  }

  /**
   * The content (document) of the persona
   */
  personaContent: Content = null;

  catalogObjectIdListName: string = null;

  invalidMessage: string = null;

  emptyMessage: string = null;
}

export default CommerceObjectsDisplayField;
