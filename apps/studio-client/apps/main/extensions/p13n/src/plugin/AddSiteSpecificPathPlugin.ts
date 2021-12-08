import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import RemoteBean from "@coremedia/studio-client.client-core/data/RemoteBean";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import PersonaSelector from "@coremedia/studio-client.main.cap-personalization-ui/persona/selector/PersonaSelector";
import PathFormatter from "@coremedia/studio-client.main.editor-components/sdk/util/PathFormatter";
import Component from "@jangaroo/ext-ts/Component";
import AbstractPlugin from "@jangaroo/ext-ts/plugin/Abstract";
import { as, bind, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import joo from "@jangaroo/runtime/joo";
import trace from "@jangaroo/runtime/trace";
import { AnyFunction } from "@jangaroo/runtime/types";
import Addsitespecificpath from "./Addsitespecificpath";

interface AddSiteSpecificPathPluginConfig extends Config<AbstractPlugin> {
}

/**
 * Plugin that adds a site specific path containing a placeholder to a {@link PersonaSelector}.
 */
class AddSiteSpecificPathPlugin extends AbstractPlugin {
  declare Config: AddSiteSpecificPathPluginConfig;

  #groupHeaderLabel: string = null;

  #path: string = null;

  #personaSelector: PersonaSelector = null;

  #entityExpression: ValueExpression = null;

  static #sitePathFormatters: Array<any>/*<Function>*/ = [AddSiteSpecificPathPlugin.#formatSitePathFromContent];

  /**
   * @cfg {String} relativePath relative path within the users home folder that will be added to a 'PersonaSelector'
   * @cfg {String} groupHeaderLabel optional suffix that will be appended to the labels of the content objects
   *  retrieved from the path
   *
   * @param config the config object
   */
  constructor(config: Config<Addsitespecificpath> = null) {
    super((()=>{
      this.#groupHeaderLabel = config.groupHeaderLabel;
      this.#entityExpression = config.activeContentValueExpression;
      this.#path = config.path;
      return config;
    })());
  }

  static addSitePathFormatter(sitePathFormatter: AnyFunction): void {
    AddSiteSpecificPathPlugin.#sitePathFormatters = AddSiteSpecificPathPlugin.#sitePathFormatters.concat(sitePathFormatter);
  }

  override init(component: Component): void {
    if (!is(component, PersonaSelector)) {
      throw Error("plugin is only applicable to components of type 'PersonaSelector'");
    }
    this.#personaSelector = as(component, PersonaSelector);
    this.#personaSelector.mon(this.#personaSelector, "afterrender", (): void =>
      this.#personaSelector.contentValueExpression.addChangeListener(bind(this, this.#loadPersonasFromSitePath)),
    );

    this.#personaSelector.addListener("beforedestroy", () => {
      this.#personaSelector.contentValueExpression && this.#personaSelector.contentValueExpression.removeChangeListener(bind(this, this.#loadPersonasFromSitePath));
    });
  }

  #loadPersonasFromSitePath(): void {
    for (const sitePathFormatter of AddSiteSpecificPathPlugin.#sitePathFormatters as AnyFunction[]) {
      sitePathFormatter.call(null, this.#path, this.#entityExpression, bind(this, this.#doLoadPersonas));
    }
  }

  static #formatSitePathFromContent(path: string, entityExpression: ValueExpression, callback: AnyFunction): void {
    entityExpression.loadValue((entity: any): void => {
      if (is(entity, Content)) {
        entityExpression.extendBy(ContentPropertyNames.PATH).loadValue((): void => {
          const selectedSitePath = PathFormatter.formatSitePath(path, cast(Content, entity));
          callback.call(null, selectedSitePath, entity);
        });
      }
    });
  }

  #doLoadPersonas(selectedSitePath: string, currentBean: RemoteBean): void {
    if (selectedSitePath) {
      session._.getConnection().getContentRepository().getRoot().getChild(selectedSitePath, (content: Content, cPath: string): void => {
        if (content && this.#personaSelector) {
          if (currentBean && this.#personaSelector.contentValueExpression && this.#personaSelector.contentValueExpression.getValue() && currentBean.getUri() === this.#personaSelector.contentValueExpression.getValue().getUri()) {
            this.#personaSelector.clearSiteSpecificPaths();
            this.#personaSelector.addSiteSpecificPath(selectedSitePath, this.#groupHeaderLabel);
            if (joo.debug) {
              trace("[INFO]", "added persona lookup path", selectedSitePath, "with label", this.#groupHeaderLabel);
            }
          }
        } else if (joo.debug) {
          trace("[INFO]", "no lookup path for persona added. no folder available at path", selectedSitePath);
        }
      });
    } else if (joo.debug) {
      trace("[INFO]", "no lookup path for persona added. selected site path is null");
    }
  }
}

export default AddSiteSpecificPathPlugin;
