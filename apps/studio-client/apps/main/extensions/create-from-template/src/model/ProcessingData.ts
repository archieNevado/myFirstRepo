import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import BeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanImpl";

/**
 * Data wrapper that contains all user input of the dialog.
 */
class ProcessingData extends BeanImpl {
  //mandatory dialog properties
  static readonly FOLDER_PROPERTY: string = "folder";

  static readonly NAME_PROPERTY: string = "name";

  //dialog properties
  static readonly SKIP_INITIALIZERS: string = "skipInitializers";

  #content: Content = null;

  //property is used to remember all documents that have been touched.
  #additionalContent: Array<any> = [];

  constructor() {
    super();
  }

  doSkipInitializers(): boolean {
    return this.get(ProcessingData.SKIP_INITIALIZERS);
  }

  addAdditionalContent(c: Content): void {
    if (this.#additionalContent.indexOf(c) === -1) {
      this.#additionalContent.push(c);
    }
  }

  /**
   * Returns the folder the content has been created into.
   * @return
   */
  getFolder(): Content {
    return this.get(ProcessingData.FOLDER_PROPERTY);
  }

  getName(): string {
    return this.get(ProcessingData.NAME_PROPERTY);
  }

  setContent(c: Content): void {
    this.#content = c;
  }

  getContent(): Content {
    return this.#content;
  }

  getExtendedPath(content: Content): string {
    return this.getName() ? content.getPath() + "/" + this.getName() : content.getPath();
  }

  override toString(): string {
    const value = "Processing Data: " + this.#content + ", skipInitializers:" + this.doSkipInitializers()
            + ", additionalContent:" + this.#additionalContent.length;
    return value;
  }
}

export default ProcessingData;
