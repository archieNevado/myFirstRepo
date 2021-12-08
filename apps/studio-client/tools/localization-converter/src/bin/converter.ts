#! /usr/bin/env node

import * as fsExtra from "fs-extra";
import * as yargs from "yargs";
import { NodePath, parseSync, traverse } from "@babel/core";
import { isIdentifier, isMemberExpression, isStringLiteral, ObjectProperty } from "@babel/types";
import findUp from "find-up";
import path from "path";
import jsYaml from "js-yaml";
import initTemplate from "../templates/initTemplate";
import propertiesTemplate from "../templates/propertiesTemplate";
import jangarooConfig from "../templates/jangarooConfig";
import custom from "../templates/custom";
import { downloadAndMergeIcons } from "@coremedia/studio-client.tools.svg-icon-merger";
import {
  dashCaseToCamelCase,
  firstLetterUpperCase,
  IContentTypeLocalization,
  IPropertyLocalization,
  ITypeLocalization
} from "../utils";

function getLocalizedPropertiesTs(defaultPropertiesTs, locale) {
  if (locale === "en") {
    return defaultPropertiesTs;
  }
  return path.join(path.dirname(defaultPropertiesTs), `${path.basename(defaultPropertiesTs, "_properties.ts")}_${locale}_properties.ts`);
}

function createPackageJson(basePackageJson: Record<string, string>, jangarooCoreVersion: string, name: string, dependencies: any, coremedia: any) {
  return {
    name: name,
    version: basePackageJson.version,
    author: basePackageJson.author,
    license: basePackageJson.license,
    dependencies,
    devDependencies: {
      "@jangaroo/build": jangarooCoreVersion,
      "@jangaroo/core": jangarooCoreVersion,
      "@jangaroo/publish": jangarooCoreVersion,
      "eslint": basePackageJson.devDependencies["eslint"],
      "rimraf": basePackageJson.devDependencies["rimraf"],
    },
    scripts: {
      "clean": "rimraf ./dist && rimraf ./build",
      "build": "jangaroo build",
      "watch": "jangaroo watch",
      "publish": "jangaroo publish",
      "lint": "eslint --fix \"src/**/*.ts\""
    },
    exports: {
      "./*": {
        "types": "./src/*.ts",
        "default": "./dist/src/*.js"
      }
    },
    coremedia,
    publishConfig: {
      "directory": "dist",
      "exports": {
        "./*": {
          "types": "./src/*.d.ts",
          "default": "./src/*.js"
        }
      }
    }
  };
}

function addDependency(packageJson: Record<string, any>, dependencyName: string, dependencyVersion: string) {
  packageJson.dependencies[dependencyName] = dependencyVersion;
  packageJson.dependencies = Object.fromEntries(Object.entries(packageJson.dependencies).sort(([packageNameA], [packageNameB]) => packageNameA.localeCompare(packageNameB)));
}

yargs
  .command(<yargs.CommandModule<{}, {
    defaultPropertiesFilePaths: any, // string[] is not supported for positionals
    sharedPackageBaseName: string,
    additionalLocales: string[],
    isBlueprint: boolean,
    coreVersion: string,
  }>>{
    command: "$0 <defaultPropertiesFilePaths..>",
    builder: yargs => yargs.positional(
      "defaultPropertiesFilePaths", {
        type: "string",
        describe: "The base path to a properties files of the default locale.",
        coerce: (defaultPropertiesFilePaths:string[]):string[] => {
          if (defaultPropertiesFilePaths.length === 0) {
            throw new Error("No properties files provided!");
          }
          let packageJsonPath: string = undefined;
          defaultPropertiesFilePaths.forEach(defaultPropertiesFilePath => {
            if (!fsExtra.existsSync(defaultPropertiesFilePath) || !fsExtra.statSync(defaultPropertiesFilePath).isFile()) {
              throw new Error("Invalid properties file " + defaultPropertiesFilePath);
            }
            const currentPackageJsonPath = findUp.sync("package.json", {
              cwd: path.dirname(defaultPropertiesFilePath),
            });
            if (!currentPackageJsonPath) {
              throw new Error("Properties file must be inside a package " + defaultPropertiesFilePath);
            }
            if (packageJsonPath && currentPackageJsonPath !== packageJsonPath) {
              throw new Error(`All given properties files have to be inside the same package. Found ${packageJsonPath} and ${currentPackageJsonPath}.`)
            }
            if (!packageJsonPath) {
              packageJsonPath = currentPackageJsonPath;
              if (!findUp.sync("pnpm-workspace.yaml", { cwd: path.dirname(packageJsonPath) })) {
                throw new Error(`Given properties file is not inside a pnpm workspace:  ${defaultPropertiesFilePath}`);
              }
            }
          });
          return defaultPropertiesFilePaths;
        },
      },
    ).option(
      "sharedPackageBaseName", {
        type: "string",
        describe: "Only used and required if provided properties files are not in an extension.",
        default: undefined,
      }
    ).option(
      "additionalLocales", {
        type: "array",
        describe: "The additional locales to consider",
        default: ["de", "ja"],
      },
    ).option(
      "isBlueprint", {
        type: "boolean",
        describe: "Internal: Specifies if the workspace is a blueprint",
        default: true,
      }
    ).option(
      "coreVersion", {
        type: "string",
        describe: "Use this option to explicitly define the version of the used CoreMedia studio-client core. If none is provided the version is derived from the dependencies of the packages containing the given properties files.",
        default: undefined,
      }
    ).option(
      "jangarooNpmVersion", {
        type: "string",
        describe: "Use this option to explicitly define the version of the used Jangaroo NPM. If none is provided the version is derived from the dependencies of the packages containing the given properties files.",
        default: undefined,
      }
    ),
    handler: ({ defaultPropertiesFilePaths, sharedPackageBaseName, additionalLocales, isBlueprint, coreVersion, jangarooNpmVersion }) => {
      // Only internal feature for now. Set to false for external use.
      const handleIcons: boolean = false;

      const localizationByLocale:Record<string, Record<string, IContentTypeLocalization>> = {};

      // sadly @types/yargs does not support positionals are arrays so we need to hard cast it
      (defaultPropertiesFilePaths as string[]).forEach(defaultPropertiesFilePath => {
        const propertiesByLocale = new Map<string, string>();
        ["en"].concat(additionalLocales).forEach(locale => {
          const propertiesFilePath = getLocalizedPropertiesTs(defaultPropertiesFilePath, locale);
          propertiesByLocale.set(locale, fsExtra.existsSync(propertiesFilePath) ? fsExtra.readFileSync(propertiesFilePath).toString() : "");
        });

        [...propertiesByLocale.entries()].forEach(([locale, properties]) => {
          localizationByLocale[locale] = {};
          const localization = localizationByLocale[locale];
          // noinspection JSUnusedGlobalSymbols
          traverse(parseSync(properties), {
            ObjectProperty(path: NodePath<ObjectProperty>): void {
              const node = path.node;
              if (!isIdentifier(node.key) && !isStringLiteral(node.key)) {
                return;
              }
              if (!isStringLiteral(node.value) && !isMemberExpression(node.value)) {
                return;
              }
              const key = isIdentifier(node.key) ? node.key.name : node.key.value;
              const value = isStringLiteral(node.value) ? node.value.value : node.value.property["name"];
              const parts = key.split("_");
              if (parts.length === 1) {
                return;
              }
              const lastPart = parts.pop();
              if (lastPart !== "text" && lastPart !== "toolTip" && lastPart !== "emptyText" && lastPart !== "icon") {
                return;
              }
              const firstPart = parts.shift();
              if (!/^\w*$/.test(firstPart)) {
                return;
              }
              localization[firstPart] = localization[firstPart] || {
                displayName: undefined,
              };
              let entry:ITypeLocalization = localization[firstPart];
              if (parts.length > 0) {
                const propertyPath = parts.join("_").split(".");
                if (propertyPath.some(propertyName => !/^[\w-]*$/.test(propertyName))) {
                  return;
                }
                entry = propertyPath.reduce((entry, nextPropertyName) => {
                  entry.properties = entry.properties || {};
                  let property = entry.properties[nextPropertyName] || {};
                  if (!property) {
                    property = {};
                  }
                  if (typeof property === "string") {
                    property = {
                      displayName: property,
                    };
                  }
                  entry.properties[nextPropertyName] = property;
                  return property;
                }, entry);
              }
              switch (lastPart) {
                case "icon": {
                  if (handleIcons) {
                    entry.svgIcon = value.replace(/_/g, "-");
                  }
                  break;
                }
                case "toolTip":
                  entry.description = value;
                  break;
                case "emptyText":
                  if (parts.length > 0) {
                    (entry as IPropertyLocalization).emptyText = value;
                  }
                  break;
                default:
                  entry.displayName = value;
              }
            }
          });
        })
      });

      const propertyPackageJsonPath = findUp.sync("package.json", { cwd: path.dirname(defaultPropertiesFilePaths[0]) });
      const propertyPackageJson = JSON.parse(fsExtra.readFileSync(propertyPackageJsonPath).toString());
      propertyPackageJson.dependencies = propertyPackageJson.dependencies || {};

      // auto detect coreVersion and jangarooNpmVersion
      if (coreVersion === undefined) {
        coreVersion = Object.entries(propertyPackageJson.dependencies).filter(
          ([packageName]) => typeof packageName === "string" && packageName.startsWith("@coremedia/studio-client.")
        ).map(([, packageVersion]) => packageVersion + "")[0];
      }
      if (coreVersion === undefined) {
        throw new Error(`Could not determine the version of CoreMedia studio-client core! Please provide one using option "--coreVersion".`);
      }
      if (jangarooNpmVersion === undefined) {
        jangarooNpmVersion = Object.entries(propertyPackageJson.dependencies).filter(
          ([packageName]) => typeof packageName === "string" && packageName.startsWith("@jangaroo/runtime")
        ).map(([, packageVersion]) => packageVersion + "")[0];
      }
      if (jangarooNpmVersion === undefined) {
        throw new Error(`Could not determine the version of Jangaroo NPM! Please provide one using option "--jangarooNpmVersion".`);
      }

      // handle extensions
      const workspaceYamlPath = findUp.sync("pnpm-workspace.yaml", { cwd: path.dirname(propertyPackageJsonPath) });
      const workspacePath = path.dirname(workspaceYamlPath);

      let isExtension;
      const rootPackageJsonPath = path.join(workspacePath, "package.json");
      if (!fsExtra.existsSync(rootPackageJsonPath)) {
        throw new Error(`Could not find root package.json below ${rootPackageJsonPath}`);
      }
      const rootPackageJson = JSON.parse(fsExtra.readFileSync(rootPackageJsonPath).toString());
      const projectExtensionWorkspacePaths = [];
      if (Array.isArray(rootPackageJson.coremedia?.projectExtensionWorkspacePaths)) {
        projectExtensionWorkspacePaths.push(...rootPackageJson.coremedia?.projectExtensionWorkspacePaths);
      }
      projectExtensionWorkspacePaths.some(projectExtensionWorkspacePath => {
        const extensionsPath = path.join(workspacePath, projectExtensionWorkspacePath, "extensions");
        const relativePathInsideExtensions = path.relative(extensionsPath, propertyPackageJsonPath);
        if (!relativePathInsideExtensions.startsWith("..")) {
          isExtension = true;
          sharedPackageBaseName = relativePathInsideExtensions.split(path.sep)[0];
          return true;
        }
      })

      if (!sharedPackageBaseName) {
        throw new Error(`Please provide option "--sharedPackageBaseName" as properties files are not inside an extension and otherwise no proper name can be evaluated for the shared package!`);
      }

      const sharedPackagePathSegments: string[] = ["shared", "js"];
      if (!isBlueprint) {
        sharedPackagePathSegments.push("blueprint");
      }
      if (isExtension) {
        // assuming that the extensions workspace path is listed in the root package.json
        const sharedPackageWorkspaceExtensionPath = sharedPackagePathSegments.join("/");
        if (!projectExtensionWorkspacePaths.some(projectExtensionWorkspacePath => projectExtensionWorkspacePath === sharedPackageWorkspaceExtensionPath)) {
          console.warn(`The path ${sharedPackageWorkspaceExtensionPath} is not listed in the "projectExtensionWorkspacePaths" entry of ${rootPackageJsonPath}. Please consider adding it if you want to use the extensions tool to activate the shared code package.`)
        }
        sharedPackagePathSegments.push("extensions");
        console.log(`Detected that the properties file is inside an extension named "${sharedPackageBaseName}". The shared package containing the localization will also be part of the extension.`);
      }

      sharedPackagePathSegments.push(sharedPackageBaseName);

      const sharedPackagePath = path.join(workspacePath, ...sharedPackagePathSegments);

      if (fsExtra.existsSync(sharedPackagePath)) {
        throw new Error(`The path ${sharedPackagePath} does already exist. Cannot create shared extension stuff.`);
      }

      const jangarooCoreVersion = propertyPackageJson.devDependencies["@jangaroo/core"];
      const sharedPackageJson = createPackageJson(propertyPackageJson, jangarooCoreVersion, "@coremedia-blueprint/studio-client." + sharedPackageBaseName, {
        "@coremedia/studio-client.cap-base-models": coreVersion,
        "@jangaroo/runtime": jangarooNpmVersion,
      }, undefined);

      console.log(`Creating shared package "${sharedPackageJson.name}" in "${sharedPackagePath}"...`)
      fsExtra.ensureDirSync(sharedPackagePath);
      fsExtra.writeFileSync(path.join(sharedPackagePath, "package.json"), JSON.stringify(sharedPackageJson, null, 2) + "\n");
      fsExtra.writeFileSync(path.join(sharedPackagePath, "jangaroo.config.js"), jangarooConfig(true));

      // create src dir
      let srcPath = path.join(sharedPackagePath, "src");
      fsExtra.ensureDirSync(srcPath);

      // custom type for svgs
      fsExtra.writeFileSync(path.join(srcPath, "custom.d.ts"), custom());

      if (handleIcons) {
        const iconNames = Object.values(localizationByLocale)
          .reduce((localizations, localizationForLocale) => localizations.concat(Object.values(localizationForLocale)), [])
          .reduce((icons, localization) => !!localization.svgIcon ? icons.concat([localization.svgIcon]) : icons, []);

        const iconsPath = path.join(srcPath, "icons");
        downloadAndMergeIcons(iconsPath, iconNames);
      }

      // write the properties and init.ts
      const propertiesName = `${firstLetterUpperCase(dashCaseToCamelCase(sharedPackageBaseName))}DocTypes_properties.ts`;
      Object.entries(localizationByLocale).forEach(([locale, localization]) => {
        let localizedPropertiesTs = getLocalizedPropertiesTs(propertiesName, locale);
        fsExtra.writeFileSync(path.join(srcPath, localizedPropertiesTs), propertiesTemplate(locale, path.basename(propertiesName, ".ts"), localization));
      });
      fsExtra.writeFileSync(path.join(srcPath, "init.ts"), initTemplate(propertiesName, localizationByLocale["en"]));

      if (!isExtension) {
        console.log(`Adding shared package to workspace found in ${workspaceYamlPath}...`)

        // add the newly create package to the workspace
        const workspace = jsYaml.load(fsExtra.readFileSync(path.join(workspaceYamlPath)).toString()) as { packages: string[] };

        workspace.packages = workspace.packages || [];
        if (workspace.packages.includes(sharedPackagePath)) {
          throw new Error(`The path ${sharedPackagePath} does already exist below the workspace. Couldn't add to pnpn-workspace.yaml`);
        }
        workspace.packages.push(path.relative(workspacePath, sharedPackagePath));
        workspace.packages.sort();

        fsExtra.writeFileSync(path.join(workspaceYamlPath), jsYaml.dump(workspace, {
          forceQuotes: true,
          quotingType: '"'
        }));

        console.log(`Adding dependency to shared package for package ${propertyPackageJson.name}...`);
        addDependency(propertyPackageJson, sharedPackageJson.name, sharedPackageJson.version);
        fsExtra.writeFileSync(propertyPackageJsonPath, JSON.stringify(propertyPackageJson, null, 2) + "\n");
      } else {
        projectExtensionWorkspacePaths.forEach(projectExtensionWorkspacePath => {
          const wsPathSegments = projectExtensionWorkspacePath.split("/");
          if (wsPathSegments.length < 2 || wsPathSegments[0] === "shared") {
            return;
          }
          const app = wsPathSegments[1];
          const extensionPackageJsonPathSegments = [workspacePath, "apps", app];
          if (!isBlueprint) {
            extensionPackageJsonPathSegments.push("blueprint");
          }
          extensionPackageJsonPathSegments.push("extensions", sharedPackageBaseName, "package.json");
          let extensionPackageJsonPath = extensionPackageJsonPathSegments.join("/");
          if (!path.relative(path.dirname(extensionPackageJsonPath), propertyPackageJsonPath).startsWith("..")) {
            extensionPackageJsonPath = propertyPackageJsonPath;
          }
          let extensionPackageJson;
          if (!fsExtra.existsSync(extensionPackageJsonPath)) {
            extensionPackageJson = createPackageJson(propertyPackageJson, jangarooCoreVersion, `@coremedia-blueprint/studio-client.${app}.${sharedPackageBaseName}`, {}, {
              "projectExtensionFor": `studio-client.${app}`,
            });

            const extensionPackagePath = path.dirname(extensionPackageJsonPath);
            console.log(`Creating extension package "${extensionPackageJson.name}" for app "${app}" in "${extensionPackagePath}"...`);
            fsExtra.ensureDirSync(extensionPackagePath);
            fsExtra.writeFileSync(extensionPackageJsonPath, JSON.stringify(extensionPackageJson, null, 2) + "\n");
            fsExtra.writeFileSync(path.join(extensionPackagePath, "jangaroo.config.js"), jangarooConfig());
          } else {
            extensionPackageJson = JSON.parse(fsExtra.readFileSync(extensionPackageJsonPath).toString());
          }
          // add dependency to shared package
          console.log(`Adding dependency to shared package in package ${extensionPackageJson.name}...`);
          addDependency(extensionPackageJson, sharedPackageJson.name, sharedPackageJson.version);
          fsExtra.writeFileSync(extensionPackageJsonPath, JSON.stringify(extensionPackageJson, null, 2) + "\n");
        });
      }
    }
  })
  .help()
  .demandCommand(1)
  .argv;
