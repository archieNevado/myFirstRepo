import CatalogLinkPropertyField from "@coremedia-blueprint/studio-client.main.ec-studio/components/link/CatalogLinkPropertyField";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface CatalogLinkPropertyFieldTestViewConfig extends Config<Viewport>, Partial<Pick<CatalogLinkPropertyFieldTestView,
  "bindTo" |
  "forceReadOnlyValueExpression"
>> {
}

class CatalogLinkPropertyFieldTestView extends Viewport {
  declare Config: CatalogLinkPropertyFieldTestViewConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.testhelper.config.catalogLinkPropertyFieldTestView";

  static readonly CATALOG_LINK_PROPERTY_FIELD_ITEM_ID: string = "catalogLinkPropertyField";

  constructor(config: Config<CatalogLinkPropertyFieldTestView> = null) {
    super(ConfigUtils.apply(Config(CatalogLinkPropertyFieldTestView, {

      items: [
        Config(CatalogLinkPropertyField, {
          itemId: CatalogLinkPropertyFieldTestView.CATALOG_LINK_PROPERTY_FIELD_ITEM_ID,
          bindTo: config.bindTo,
          propertyName: "externalId",
          emptyText: "irrelevant",
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
        }),
      ],

    }), config));
  }

  bindTo: ValueExpression = null;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;
}

export default CatalogLinkPropertyFieldTestView;
