import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CatalogObjectPropertyNames
  from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import AddSiteSpecificPathPlugin
  from "@coremedia-blueprint/studio-client.main.p13n-studio/plugin/AddSiteSpecificPathPlugin";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { bind, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import LivecontextP13NStudioPlugin from "./LivecontextP13NStudioPlugin";

interface LivecontextP13NStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class LivecontextP13NStudioPluginBase extends StudioPlugin {
  declare Config: LivecontextP13NStudioPluginBaseConfig;

  static readonly USER_SEGMENTS: string = "usersegments";

  constructor(config: Config<LivecontextP13NStudioPlugin> = null) {
    super(config);
  }

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);
    //add site formatter for the catalog object entity
    AddSiteSpecificPathPlugin.addSitePathFormatter(bind(this, this.#formatSitePathFromCatalogObject));
  }

  #formatSitePathFromCatalogObject(path: string, entityExpression: ValueExpression, callback: AnyFunction): void {
    entityExpression.loadValue((entity: any): void => {
      if (is(entity, CatalogObject)) {
        entityExpression.extendBy(CatalogObjectPropertyNames.STORE).loadValue((store: Store): void => {
          //value should be the store
          const sitesService = editorContext._.getSitesService();
          const site = sitesService.getSite(store.getSiteId());
          const folder = site.getSiteRootFolder();
          ValueExpressionFactory.createFromFunction((): string => {
            return folder.getPath();
          }).loadValue(() => {
            const selectedSitePath: string = site ? site.getSiteRootFolder().getPath() + "/" + path : null;
            callback.call(null, selectedSitePath, entity);
          });
        });
      }
    });
  }

  static getSegments(store: Store): Array<any> {
    const segments = store.getSegments();
    return segments && segments.getSegments();
  }

}

export default LivecontextP13NStudioPluginBase;
