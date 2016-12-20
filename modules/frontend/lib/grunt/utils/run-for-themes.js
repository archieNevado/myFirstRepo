'use strict';

// imports
var shell = require('shelljs');
var fs = require("fs");
var path = require("path");

//variables
var isWin = process.platform === 'win32';
var npmScript = (isWin) ? 'npm.cmd' : 'npm';
var runScript = process.argv.slice(2)[0];
var workingDirectory = process.cwd();
var themesDirectory = path.resolve(workingDirectory, 'themes');

// checks for npm
if (!shell.which('npm')) {
  //no npm found
  // use the one installed by frontend-maven-plugin (explicit)
  var npmMvnScript = path.resolve(workingDirectory, 'node', npmScript);
  // copy the one installed by frontend-maven-plugin (implicit)
  if (!fs.existsSync(npmScript)) {
    shell.cp(path.resolve(workingDirectory, 'node', 'node_modules', 'npm', 'bin',  npmScript), npmMvnScript);
  }
  npmScript = npmMvnScript;

  // still not found => error
  if (!fs.existsSync(npmScript)) {
    shell.echo('Error: This script requires npm. No installed npm version found.');
    shell.exit(1);
  }
}

//check for grunt
if (!shell.which('grunt')) {
  shell.echo('Error: This script requires grunt. Make sure all node_modules are installed properly.');
  shell.exit(1);
}

// run scripts

// find themes and run script
shell.ls('-l', themesDirectory).forEach(function (theme) {
  if (theme.isDirectory()) {
    var themeDirectory = path.resolve(themesDirectory, theme.name);
    var packageJsonFilePath = path.resolve(themeDirectory, "package.json");
    if (fs.existsSync(packageJsonFilePath)) {
      shell.echo("Found theme: " + theme.name);
      shell.cd(themeDirectory);

      // install node_modules
      if (runScript === 'install') {
        if (shell.exec(npmScript + ' ' + runScript + ' --cache-min=Infinity --loglevel=error --no-progress').code !== 0) {
          shell.echo("Error: Installation of dependencies for theme " + theme.name + " failed.");
          shell.exit(1);
        }
      }
      // check for tests first
      else if (runScript === 'test') {
        if (shell.grep('"test":', 'package.json').trim() !== '') {
          // run tests
          if (shell.exec(npmScript + ' test').code !== 0) {
            shell.echo("Error: Tests of theme " + theme.name + " failed.");
            shell.exit(1);
          }
        } else {
          shell.echo('  Skip theme, no tests found.\n');
        }
      }
      // check for production script first
      else if (runScript === 'production') {
        if (shell.grep('"production":', 'package.json').trim() !== '') {
          // build themes
          if (shell.exec(npmScript + ' run ' + runScript).code !== 0) {
            shell.echo("Error: Building theme " + theme.name + " failed.");
            shell.exit(1);
          }
        }
      }
    }
  }
});
