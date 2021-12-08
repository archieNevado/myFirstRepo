import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import StandAloneDocumentViewBase from "@coremedia/studio-client.main.editor-components/sdk/premular/StandAloneDocumentViewBase";
import TabbedDocumentFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/TabbedDocumentFormDispatcher";
import FitLayout from "@jangaroo/ext-ts/layout/container/Fit";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";

interface TaxonomyStandAloneDocumentViewConfig extends Config<StandAloneDocumentViewBase>, Partial<Pick<TaxonomyStandAloneDocumentView,
  "bindTo" |
  "forceReadOnlyValueExpression"
>> {
}

class TaxonomyStandAloneDocumentView extends StandAloneDocumentViewBase {
  declare Config: TaxonomyStandAloneDocumentViewConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyStandAloneDocumentView";

  bindTo: ValueExpression = null;

  forceReadOnlyValueExpression: ValueExpression = null;

  constructor(config: Config<TaxonomyStandAloneDocumentView> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomyStandAloneDocumentView, {
      scrollable: true,

      items: [
        Config(TabbedDocumentFormDispatcher, {
          itemId: "documentFormDispatcher",
          focusForwarder: this.getFocusForwarder(),
          propertyFieldRegistry: this.getPropertyFieldRegistry(),
          bindTo: config.bindTo,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
        }),
      ],
      layout: Config(FitLayout),
    }), config))());
  }
}

export default TaxonomyStandAloneDocumentView;
