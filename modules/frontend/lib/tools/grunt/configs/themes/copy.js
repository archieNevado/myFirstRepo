'use strict';

/* default task for copying all default files to the destination folder */
module.exports = {
  // copy all static files, like css, images, fonts and resourcebundles
  main: {
    expand: true,
    cwd: 'src/',
    src: ['css/**', 'fonts/**', 'img/**', 'images/**', 'js/**', 'vendor/**', 'l10n/**'],
    dest: '../../target/resources/themes/<%= themeConfig.name %>'
  },
  // copy global resourcebundle to target folder
  resourcebundle: {
    expand: true,
    cwd: '../../lib/l10n',
    src: '*.properties',
    dest: '../../target/resources/themes/<%= themeConfig.name %>/l10n'
  },
  // copy templates for local tomcat to target folder
  templates: {
    expand: true,
    cwd: 'src/templates/',
    src: '**',
    dest: '../../target/resources/WEB-INF/templates/<%= themeConfig.name %>'
  },
  // copy theme descriptor to target folder
  themedescriptor: {
    expand: true,
    cwd: './',
    src: '*-theme.xml',
    dest: '../../target/resources/THEME-METADATA'
  }
};
