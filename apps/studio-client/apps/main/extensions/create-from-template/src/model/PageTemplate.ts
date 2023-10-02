import Content from "@coremedia/studio-client.cap-rest-client/content/Content";

/**
 * Data wrapper that contains all information about a template.
 */
class PageTemplate {
  #descriptor: Content = null;

  #folder: Content = null;

  #page: Content = null;

  constructor(folder: Content, descriptor: Content) {
    this.#descriptor = descriptor;
    this.#folder = folder;
  }

  getDescriptor(): Content {
    return this.#descriptor;
  }

  /**
   * Sets the page document this initializer is working on.
   * @param page
   */
  setPage(page: Content): void {
    this.#page = page;
  }

  getPage(): Content {
    return this.#page;
  }

  getFolder(): Content {
    return this.#folder;
  }

  toString(): string {
    return "PagegridInitializer '" + this.#folder.getName() + "'";
  }
}

export default PageTemplate;
