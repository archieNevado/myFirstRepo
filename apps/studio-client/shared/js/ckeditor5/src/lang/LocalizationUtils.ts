/**
 * The type of entries, added to the localization table.
 */
export type LocalizationTable = {
  [key: string]: {
    [key: string]: string;
  };
};

/**
 * Localization class for localizations inside this ckeditor5 package.
 * CKEditor5 plugins usually come with their own localizations.
 * For texts, directly added to the editors configuration however,
 * there is no convenient way to apply localized text. This utility
 * helps to deal with that.
 *
 * Usage:
 * Simply add a localization for an input string under the desired language.
 *
 * @example:
 * localization.add({
 *   "de": {
 *     "hello world": "Hallo Welt"
 *   }
 * })
 *
 * You can then use the localize function to display your text:
 *
 * @example:
 * console.log(localize("hello world", language));
 */
class Localization {
  readonly #table: LocalizationTable = {};

  add(table: LocalizationTable): void {
    Object.keys(table).forEach(key => {
      this.#table[key] = {...this.#table[key], ...table[key]}
    })
  }

  getEntries(): LocalizationTable {
    return this.#table;
  }
}

export const localization = new Localization();

/**
 * This is the main function to be used to localize text strings.
 *
 * @param toLocalize - the default string to display if there is no localization for the given language
 * @param language - the language
 * @returns the localized string
 */
export const localize = (toLocalize: string, language: string): string => {
  if (localization.getEntries().hasOwnProperty(language)) {
    const localizationTable = localization.getEntries()[language];
    if (localizationTable.hasOwnProperty(toLocalize)) {
      return localizationTable[toLocalize];
    }
  }
  return toLocalize;
}
