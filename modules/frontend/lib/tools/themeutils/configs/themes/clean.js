'use strict';

/* default task for cleaning the destination folder */
module.exports = {
  options: {
    cwd: '<%= themeConfig.targetPath %>/resources/themes/<%= themeConfig.name %>',
    force: true
  },
  content: [
    '<%= themeConfig.targetPath %>/content/Themes/<%= themeConfig.name %>'
  ],
  themes: [
    '<%= themeConfig.targetPath %>/resources/themes/<%= themeConfig.name %>',
    '<%= themeConfig.targetPath %>/themes/<%= themeConfig.name %>-theme.zip'
  ],
  templates: [
    '<%= themeConfig.targetPath %>/resources/WEB-INF/templates/<%= themeConfig.name %>',
    '<%= themeConfig.targetPath %>/resources/WEB-INF/templates/bricks'
  ],
  filelist: {
    src: []
  }
};
