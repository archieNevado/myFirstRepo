import path from "path";
import JSON5 from "json5";
import { isValidIdentifier } from "@babel/types";
import { dashCaseToCamelCase, IContentTypeLocalization, IPropertyLocalization } from "../utils";

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
  if (path.endsWith(".ts")) {
    path = path.substring(0, path.lastIndexOf(".ts"));
  }
  return path || ".";
}

function getMemberAccess(object: string, member: string): string {
  return `${object}${isValidIdentifier(member) ? `.${member}` : `[${JSON.stringify(member)}]`}`
}

function getPropertiesAccess(localization:IContentTypeLocalization | IPropertyLocalization, propertiesImport: string, prefix: string):Record<string, any> {
  const result:Record<string, any> = {};
  ["displayName", "description", "emptyText"].forEach(prop => {
    if (typeof localization[prop] === "string") {
      result[prop] = getMemberAccess(propertiesImport, prefix + prop);
    }
  });
  if (localization.svgIcon) {
    result["svgIcon"] = dashCaseToCamelCase(localization.svgIcon);
  }
  Object.entries(localization.properties || {}).forEach(([propertyName, propertyLocalization]) => {
    const propertyPrefix = prefix + propertyName + "_";
    result.properties = result.properties || {};

    const propertyName_ = isValidIdentifier(propertyName) ? propertyName : JSON.stringify(propertyName);
    if (typeof propertyLocalization === "string") {
      result.properties[propertyName_] = getMemberAccess(propertiesImport, propertyPrefix + "displayName");
    } else {
      result.properties[propertyName_] = getPropertiesAccess(propertyLocalization, propertiesImport, propertyPrefix);
    }
  });
  return result;
}

export default (propertiesTsFilePath: string, localization:Record<string, IContentTypeLocalization>) => {
  let propertiesImport = path.basename(propertiesTsFilePath, ".ts");
  return `import ${propertiesImport} from "${normalizePathForImport(propertiesTsFilePath)}";
import contentTypeLocalizationRegistry from "@coremedia/studio-client.cap-base-models/content/contentTypeLocalizationRegistry";
${Object.values(localization)
    .map((contentTypeLocalization) => contentTypeLocalization.svgIcon)
    .filter((value, index, array) => !!value && array.indexOf(value) === index)
    .map((svgIcon) =>
    `import ${dashCaseToCamelCase(svgIcon)} from "./icons/${svgIcon}.svg";`).join("\n")}
${Object.entries(localization).map(([docTypeName, contentTypeLocalization]) => `
contentTypeLocalizationRegistry.addLocalization(${JSON.stringify(docTypeName)}, ${JSON5.stringify(getPropertiesAccess(contentTypeLocalization, propertiesImport, docTypeName + "_"), {replacer: null, space: 2, quote: "'"}).replace(/'/g, "")});`).join("\n")}
`;
};
