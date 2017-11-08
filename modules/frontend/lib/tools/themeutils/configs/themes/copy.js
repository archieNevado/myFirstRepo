'use strict';

/* default task for copying all default files to the destination folder */
module.exports = {
  // copy all static files, like css, images, fonts and resourcebundles
  main: {
    expand: true,
    cwd: 'src/',
    src: ['css/**', 'fonts/**', 'img/**', 'images/**', 'vendor/**', 'l10n/**'],
    dest: '<%= themeConfig.targetPath %>/resources/themes/<%= themeConfig.name %>'
  },
  // copy templates for local tomcat to target folder
  templates: {
    expand: true,
    cwd: 'src/templates/',
    src: '**',
    dest: '<%= themeConfig.targetPath %>/resources/WEB-INF/templates/<%= themeConfig.name %>'
  },
  // copy theme descriptor to target folder
  themedescriptor: {
    expand: true,
    cwd: './',
    src: '<%= themeConfig.name %>-theme.xml',
    dest: '<%= themeConfig.targetPath %>/resources/THEME-METADATA'
  }
};
