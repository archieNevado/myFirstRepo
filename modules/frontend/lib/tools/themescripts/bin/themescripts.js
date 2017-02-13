#!/usr/bin/env node

'use strict';

// imports
const shell = require('shelljs');
const fs = require('fs');
const path = require('path');

//variables
const isWin = process.platform === 'win32';
let npmScript = (isWin) ? 'npm.cmd' : 'npm';
const RUN_SCRIPT = process.argv.slice(2)[0];
const WORKING_DIRECTORY = process.cwd();
const THEMES_DIRECTORY = path.resolve(WORKING_DIRECTORY, 'themes');
const NPM_LOCAL_SCRIPT_EXPLICIT = path.resolve(WORKING_DIRECTORY, 'node', npmScript);
const NPM_LOCAL_SCRIPT_IMPLICIT = path.resolve(WORKING_DIRECTORY, 'node', 'node_modules', 'npm', 'bin',  npmScript);

// checks for npm
// prefer npm installed by frontend-maven-plugin, fallback to global npm
if (fs.existsSync(NPM_LOCAL_SCRIPT_EXPLICIT)) {
  npmScript = NPM_LOCAL_SCRIPT_EXPLICIT;
} else if (fs.existsSync(NPM_LOCAL_SCRIPT_IMPLICIT)) {
  shell.cp('-f', NPM_LOCAL_SCRIPT_IMPLICIT, NPM_LOCAL_SCRIPT_EXPLICIT);
  npmScript = NPM_LOCAL_SCRIPT_EXPLICIT;
} else if (!shell.which('npm')) {
  shell.echo('Error: This script requires npm. No installed npm version found.');
  shell.exit(1);
}

//check for grunt
if (!shell.which('grunt')) {
  shell.echo('Error: This script requires grunt. Make sure all node_modules are installed properly.');
  shell.exit(1);
}

// run scripts

// find themes and run script
shell.ls('-l', THEMES_DIRECTORY).forEach(theme => {
  if (theme.isDirectory()) {
    const THEME_DIRECTORY = path.resolve(THEMES_DIRECTORY, theme.name);
    const PACKAGE_JSON_FILE = path.resolve(THEME_DIRECTORY, 'package.json');
    if (fs.existsSync(PACKAGE_JSON_FILE)) {
      shell.echo(`Found theme: ${theme.name}`);
      shell.cd(THEME_DIRECTORY);

      // install node_modules
      if (RUN_SCRIPT === 'install') {
        if (shell.exec(`${npmScript} ${RUN_SCRIPT} --cache-min=Infinity --loglevel=error --no-progress`).code !== 0) {
          shell.echo(`Error: Installation of dependencies for theme ${theme.name} failed.`);
          shell.exit(1);
        }
      }
      // check for tests first
      else if (RUN_SCRIPT === 'test') {
        if (shell.grep('"test":', 'package.json').trim() !== '') {
          // run tests
          if (shell.exec(`${npmScript} test`).code !== 0) {
            shell.echo(`Error: Tests of theme ${theme.name} failed.`);
            shell.exit(1);
          }
        } else {
          shell.echo('  Skip theme, no tests found.\n');
        }
      }
      // check for production script first
      else if (RUN_SCRIPT === 'production') {
        if (shell.grep('"production":', 'package.json').trim() !== '') {
          // build themes
          if (shell.exec(`${npmScript} run ${RUN_SCRIPT}`).code !== 0) {
            shell.echo(`Error: Building theme ${theme.name} failed.`);
            shell.exit(1);
          }
        }
      }
    }
  }
});
