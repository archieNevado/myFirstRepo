import { LinkAttributesConfig } from "@coremedia/ckeditor5-link-common";


/**
 * Configuration that holds all link-related attributes, that are not
 * covered yet by any plugin.
 *
 * Similar to GHS/GRS, they are just registered as being _valid_ **and**
 * (this is important) register them to belong to a link element, which again
 * ensures, that they are removed on remove-link, that cursor positioning
 * handles them correctly, etc.
 *
 * Note that if you add a plugin supporting any of these attributes, you may
 * want to move the registration process to plugin initialization phase
 * instead. Example:
 *
 * ```typescript
 * import { getLinkAttributes, LinkAttributes }
 *   from "@coremedia/ckeditor5-link-common/LinkAttributes"
 *
 * getLinkAttributes(editor)?
 *   .registerAttribute({ view: "title", model: "linkTitle" });
 * ```
 */
export const linkAttributesConfig: LinkAttributesConfig = {
  attributes: [
    {
      // Data-Processing maps xlink:title to title by default in data view as
      // well as in editing view.
      view: "title",
      // To benefit from CKEditor 5 Link Plugin on some clean-up tasks, it is
      // the best practice to prefix representation of attribute in model layer
      // with `link*`.
      model: "linkTitle",
    },
    {
      // Data-Processing maps xlink:actuate to data-xlink-actuate by default in
      // data view as well as in editing view.
      view: "data-xlink-actuate",
      model: "linkActuate",
    },
  ],
};
