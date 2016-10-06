'use strict';

// imports
var shell = require('shelljs');
var fs = require("fs");
var path = require("path");

//variables
var isWin = process.platform === 'win32';
var npmScript = 'npm';
var workingDirectory = process.cwd();
var themesDirectory = path.resolve(workingDirectory, 'themes');

// checks for npm
if (!shell.which('npm')) {
  //no npm found, use the one installed by maven
  if (isWin) {
    npmScript = path.resolve(workingDirectory, 'node', 'npm.cmd');
  } else {
    npmScript = path.resolve(workingDirectory, 'node', 'npm');
  }
  if (!fs.existsSync(npmScript)) {
    shell.echo('Error: This script requires npm. No installed npm version found.');
    exit(1);
  }
}

//check for grunt
if (!shell.which('grunt')) {
  shell.echo('Error: This script requires grunt. Make sure all node_modules are installed properly.');
  exit(1);
}

// script

// find themes and build them
shell.ls('-l', themesDirectory).forEach(function (theme) {
  if (theme.isDirectory()) {
    var themeDirectory = path.resolve(themesDirectory, theme.name);
    var packageJsonFilePath = path.resolve(themeDirectory, "package.json");
    if (fs.existsSync(packageJsonFilePath)) {
      shell.echo("Found theme: " + theme.name);
      shell.cd(themeDirectory);
      // install theme
      if (shell.exec(npmScript + ' install').code !== 0) {
        shell.echo("Error: Installation of theme " + theme.name + " failed. ");
        shell.exit(1);
      }
    }
  }
});
