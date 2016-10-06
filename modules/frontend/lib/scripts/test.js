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

// script

// find themes and build them
shell.ls('-l', themesDirectory).forEach(function (theme) {
  if (theme.isDirectory()) {
    var themeDirectory = path.resolve(themesDirectory, theme.name);
    var packageJsonFilePath = path.resolve(themeDirectory, "package.json");
    if (fs.existsSync(packageJsonFilePath)) {
      shell.echo("Found theme: " + theme.name);
      shell.cd(themeDirectory);
      //check for tests first
      if (shell.grep('"test":', 'package.json').trim() != '') {
        // run tests
        if (shell.exec(npmScript + ' test').code !== 0) {
          shell.echo("Error: Tests of theme " + theme.name + " failed. ");
          shell.exit(1);
        }
      } else {
        shell.echo('  Skip theme, no tests found.\n');
      }
    }
  }
});
