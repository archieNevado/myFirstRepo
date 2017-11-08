#!/usr/bin/env node

'use strict';

// imports
const shell = require('shelljs');
const fs = require('fs');
const path = require('path');
const chalk = require('chalk');
const theme = require('./lib/themeDefaults');

// check node version
const currentNodeVersion = process.versions.node;
const semver = currentNodeVersion.split('.');
const major = semver[0];
if (major < 6) {
  shell.echo(chalk.red(
          'You are running Node ' + currentNodeVersion + '.\n' +
          'CoreMedia Frontend Workspace requires Node 6 or higher. \n' +
          'Please update your version of Node.'
  ));
  shell.exit(1);
}

// check themeName
let themeName = process.argv[2];
if (typeof themeName !== 'string' || themeName === "") {
  shell.echo(chalk.red('Error: No theme name was provided. Please add a name.'));
  shell.exit(1);
}
// convert to lowercase and remove special characters
themeName = themeName.trim().toLowerCase().replace(/[^a-z0-9\-]/g, '');
// check themeName again
if (typeof themeName !== 'string' || themeName === "") {
  shell.echo(chalk.red('Error: No valid theme name was provided. Please try again with another name.'));
  shell.exit(1);
}

// check for existing themes
shell.cd(`themes`);
shell.ls().forEach(element => {
  if (element === themeName) {
    shell.echo(chalk.red(`Error: The folder "${themeName}" already exists. Please choose another name.`));
    shell.exit(1);
  }
});

// creating file
const createFile = function (dir, content) {
  fs.writeFile(dir, content, function (err) {
    if (err) {
      shell.echo(chalk.red(err));
      shell.exit(1);
    }
  });
};

// creating folder structure
const createFolderStructure = function () {
  shell.mkdir('-p',
          path.join(themeName, 'src', 'js'),
          path.join(themeName, 'src', 'sass'),
          path.join(themeName, 'src', 'img'),
          path.join(themeName, 'src', 'fonts'),
          path.join(themeName, 'src', 'l10n'),
          path.join(themeName, 'src', 'templates', 'com.coremedia.blueprint.common.contentbeans')
  );
};

// creating files
const createFiles = function () {
  const mainJs = "src/js/index.js";
  createFile(`./${themeName}/package.json`, theme.packageJson(themeName, mainJs));
  createFile(`./${themeName}/Gruntfile.js`, theme.gruntfileJs());
  createFile(`./${themeName}/${themeName}-theme.xml`, theme.themedescriptorXml(themeName));
  createFile(`./${themeName}/${mainJs}`, theme.themeJsIndex(themeName));
  createFile(`./${themeName}/src/js/${themeName}.js`, theme.themeJs(themeName));
  createFile(`./${themeName}/src/sass/${themeName}.scss`, theme.themeSass(themeName));
  createFile(`./${themeName}/src/sass/preview.scss`, theme.previewSass(themeName));
  createFile(`./${themeName}/src/l10n/${themeName}_en.properties`, '');
};

// init
shell.echo(`Generating new theme "${themeName}".`);
createFolderStructure();
createFiles();
shell.echo(chalk.green(`Done.`));
