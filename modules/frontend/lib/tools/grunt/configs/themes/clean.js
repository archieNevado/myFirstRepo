'use strict';

/* default task for cleaning the destination folder */
module.exports = {
  options: {
    cwd: '../../target/resources/themes/<%= themeConfig.name %>',
    force: true
  },
  content: [
    '../../target/content/Themes/<%= themeConfig.name %>'
  ],
  themes: [
    '../../target/resources/themes/<%= themeConfig.name %>',
    '../../target/themes/<%= themeConfig.name %>-theme.zip'
  ],
  templates: [
    '../../target/resources/WEB-INF/templates/<%= themeConfig.name %>',
    '../../target/resources/WEB-INF/templates/bricks'
  ],
  filelist: {
    src: ["../../target/resources/themes/corporate/js/oen.js"]
  }
};
