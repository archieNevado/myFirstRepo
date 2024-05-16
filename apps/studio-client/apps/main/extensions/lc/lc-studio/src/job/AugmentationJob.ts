import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import ProductVariant from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/ProductVariant";
import Job from "@coremedia/studio-client.cap-rest-client/common/Job";
import JobContext from "@coremedia/studio-client.cap-rest-client/common/JobContext";
import JobExecutionError from "@coremedia/studio-client.cap-rest-client/common/JobExecutionError";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentType from "@coremedia/studio-client.cap-rest-client/content/ContentType";
import RemoteServiceMethod from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethod";
import RemoteServiceMethodResponse from "@coremedia/studio-client.client-core-impl/data/impl/RemoteServiceMethodResponse";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import ShowInRepositoryAction from "@coremedia/studio-client.ext.library-services-toolkit/actions/ShowInRepositoryAction";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import BackgroundJob from "@coremedia/studio-client.main.editor-components/sdk/jobs/BackgroundJob";
import ContentCreationUtil from "@coremedia/studio-client.main.editor-components/sdk/util/ContentCreationUtil";
import Ext from "@jangaroo/ext-ts";
import { as, cast, is, mixin } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import joo from "@jangaroo/runtime/joo";
import { AnyFunction } from "@jangaroo/runtime/types";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

class AugmentationJob implements Job, BackgroundJob {

  #catalogObject: CatalogObject = null;

  #targetFolder: Content = null;

  #augmentedObject: Content = null;

  constructor(catalogObject: CatalogObject, targetFolder: Content = null) {
    this.#catalogObject = catalogObject;
    this.#targetFolder = targetFolder;
  }

  execute(jobContext: JobContext): void {
    if (is(this.#catalogObject, ProductVariant)) {
      const externalId = this.#catalogObject.getId();
      const preferredName = this.#catalogObject.getName();
      const properties: Record<string, any> = {
        externalId: externalId,
        locale: AugmentationJob.#getLocale(this.#catalogObject),
      };
      ContentCreationUtil.createContent(this.#targetFolder, false, false, preferredName, AugmentationJob.getContentType("CMProductTeaser"), (content: Content): void => {
        this.#augmentedObject = content;
        jobContext.notifySuccess(content);
      }, Ext.emptyFn, properties);
    } else {
      const augmentCommerceBeanUri = this.#catalogObject.getStore().getUriPath() + "/augment";
      const remoteServiceMethod = new RemoteServiceMethod(augmentCommerceBeanUri, "POST", true);
      remoteServiceMethod.request({ $Ref: this.#catalogObject.getUriPath() }, (response: RemoteServiceMethodResponse): void => {
        if (response.success) {
          const content = cast(Content, response.getResponseJSON());
          this.#augmentedObject = content;
          content.load((): void => {
            ContentCreationUtil.initialize(content);
            jobContext.notifySuccess(content);
          });
        } else {
          jobContext.notifyError(new JobExecutionError("Augmentation failed"));
        }
      });
    }
  }

  requestAbort(jobContext: JobContext): void {
    // No
  }

  getNameExpression(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): string =>
      this.#catalogObject.getName(),
    );
  }

  getIconClsExpression(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): string => {
      if (is(this.#catalogObject, ProductVariant)) {
        return LivecontextStudioPlugin_properties.CMProductTeaser_icon;
      } else if (is(this.#catalogObject, Product)) {
        return LivecontextStudioPlugin_properties.CMExternalProduct_icon;
      } else {
        return LivecontextStudioPlugin_properties.CMExternalChannel_icon;
      }
    });
  }

  getErrorHandler(): AnyFunction {
    return null;
  }

  getSuccessHandler(): AnyFunction {
    return (): void => {
      const showInRepositoryAction = new ShowInRepositoryAction(Config(ShowInRepositoryAction, { contentValueExpression: ValueExpressionFactory.createFromValue(this.#augmentedObject) }));
      showInRepositoryAction.execute();
    };
  }

  static #getLocale(catalogObject: CatalogObject): string {
    const site = editorContext._.getSitesService().getSite(catalogObject.getSiteId());
    let locale: string;
    if (site) {
      locale = site.getLocale().getLanguageTag();
    } else {
      locale = joo.localeSupport.getLocale();
    }
    return locale;
  }

  static getContentType(contentType: string): ContentType {
    return as(session._.getConnection().getContentRepository().getContentType(contentType), ContentType);
  }
}
mixin(AugmentationJob, Job, BackgroundJob);

export default AugmentationJob;
