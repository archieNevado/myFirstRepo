"use strict";

const inquirer = require("inquirer");
const path = require("path");
const cmLogger = require("@coremedia/cm-logger");
const { getWorkspaceConfig, getAvailableBricks } = require("@coremedia/tool-utils/workspace");

const args = require("../lib/args");
const {
  convertModuleName,
  isModuleNameInUse,
  createTheme,
} = require("@coremedia/module-creator");
const { PKG_NAME } = require("../lib/constants");

const command = "create-theme <name>";
const desc = "Create a blank, minimal theme";
const builder = yargs =>
  yargs
    .option("interactive", {
      alias: "I",
      default: false,
      describe: "Enable interactive mode for theme creation.",
      type: "boolean"
    })
    .option("verbose", {
      alias: "V",
      default: false,
      describe: "Enable verbose mode for more information output.",
      type: "boolean",
    })
    .epilogue(args.docs);

const handler = argv => {
  const log = cmLogger.getLogger({
    name: PKG_NAME,
    level: argv.verbose ? "debug" : "info",
  });

  let wsConfig;
  try {
    wsConfig = getWorkspaceConfig();
  } catch (error) {
    log.error(error.message);
    process.exit(1);
  }

  const themeName = convertModuleName(argv.name);
  if (!themeName) {
    log.error(
      "No valid theme name was provided. Only characters (A-Z, a-z), numbers (0-9) and hyphens (-) are allowed. Please try again."
    );
    process.exit(1);
  }

  const themePath = path.join(wsConfig.themesPath, `${themeName}-theme`);
  if (isModuleNameInUse(themePath)) {
    log.error(
      `The theme "${themeName}" already exists. Please choose another name.`
    );
    process.exit(1);
  }

  const availableBricks = getAvailableBricks();
  let bricksToActivate = [];
  let bricksToCommentOut = availableBricks;

  function doIt() {
    log.info(`Generating new theme "${themeName}".`);
    try {
      createTheme(wsConfig, themePath, themeName, bricksToActivate, bricksToCommentOut, log);
      log.success(`Done.`);
    } catch (e) {
      log.error(
              `An error occured while trying to create theme "${themeName}": ${
                      e.message
                      }`
      );
    }
  }

  if (argv.interactive) {

    const brickChoices = Object.keys(availableBricks).map(
      brickName => ({
        name: brickName.replace("@coremedia/brick-", ""),
        short: brickName.replace("@coremedia/brick-", ""),
        value: {
          [brickName]: availableBricks[brickName]
        },
      })
    );

    // Ask for bricks to auto activate
    inquirer
      .prompt([
        {
          type: "checkbox",
          name: "chosenBricks",
          message: "Which bricks should be activated:",
          choices: brickChoices,
          default: []
        },
        {
          type: "confirm",
          name: "commentOutBricks",
          message: "Should non-activated bricks be passed as commented out dependencies?",
          default: false
        }
      ])
            .then(args => {
              bricksToActivate = args.chosenBricks.reduce(
                      (aggregator, newValue) => ({
                        ...aggregator,
                        ...newValue
                      }),
                      {}
              );
              if (args.commentOutBricks) {
                bricksToCommentOut = Object.keys(availableBricks).filter(
                        brickName => !(brickName in bricksToActivate)
                ).reduce(
                        (aggregator, brickName) => ({
                          ...aggregator,
                          [brickName]: availableBricks[brickName]
                        }),
                        {}
                );
              } else {
                bricksToCommentOut = {};
              }
              doIt();
            });


  } else {
    doIt();
  }
};

module.exports = {
  command,
  desc,
  builder,
  handler,
};
