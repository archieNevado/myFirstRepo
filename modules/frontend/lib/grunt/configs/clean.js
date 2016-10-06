'use strict';

/* default task for cleaning the destination folder */
module.exports = {
  options: {
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
  ]
};
