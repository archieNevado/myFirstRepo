import JSON5 from "json5";
import { isValidIdentifier } from "@babel/types";
import { IContentTypeLocalization, IPropertyLocalization } from "../utils";

function normalizePathSeparators(path: string): string {
  return path.replace(/\\/g, "/");
}

function removeTrailingDirectorySeparator(path: string): string {
  path = normalizePathSeparators(path);
  if (path.endsWith("/")) {
    return path.substr(0, path.length - 1);
  }
  return path;
}

export function normalizePathForImport(path: string): string {
  path = normalizePathSeparators(path);
  if (!path.startsWith(".")) {
    path = "./" + path;
  }
  path = removeTrailingDirectorySeparator(path);
  return path || ".";
}

function getFlatObject(localization:IContentTypeLocalization | IPropertyLocalization, prefix: string):Record<string, string> {
  const result:Record<string, string> = {};
  ["displayName", "description", "emptyText"].forEach(prop => {
    if (typeof localization[prop] === "string") {
      result[prefix + prop] = localization[prop];
    }
  });
  Object.entries(localization.properties || {}).forEach(([propertyName, propertyLocalization]) => {
    const propertyPrefix = prefix + propertyName + "_";
    if (typeof propertyLocalization === "string") {
      result[propertyPrefix + "displayName"] = propertyLocalization;
    } else {
      Object.assign(result, getFlatObject(propertyLocalization, propertyPrefix));
    }
  });
  return result;
}

const defaultPropertiesFile = (baseName: string, keysAndValues:Record<string, string>) => {
  return `interface ${baseName} {
${Object.keys(keysAndValues).map(key => `  ${isValidIdentifier(key) ? key : `"${key}"`}: string;`).join("\n")}
}

const ${baseName}: ${baseName} = ${JSON5.stringify(keysAndValues, {replacer: null, space: 2, quote: '"'})};

export default ${baseName};
`;
};

const localizedPropertiesFile = (locale:string, baseName: string, keysAndValues:Record<string, string>) => `import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";
import ${baseName} from "./${baseName}";

/**
 * Overrides of ResourceBundle "${baseName}" for locale "${locale}".
 * @see ${baseName}
 */
ResourceBundleUtil.override(${baseName}, ${JSON5.stringify(keysAndValues, {replacer: null, space: 2, quote: '"'})});
`;

export default (locale, baseName: string, localization:Record<string, IContentTypeLocalization>) => {
  const keysAndValues = Object.entries(localization).reduce((aggregator, [contentType, localization]) => {
    return Object.assign(aggregator, getFlatObject(localization, contentType + "_"));
  }, {});
  if (locale === "en") {
    return defaultPropertiesFile(baseName, keysAndValues);
  }
  return localizedPropertiesFile(locale, baseName, keysAndValues);
};
