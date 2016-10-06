'use strict';

/* compress templates to deployable jar and the whole theme to a importable zip file */
module.exports = {
  // create templateset (jar of templates)
  templates: {
    options: {
      archive: '../../target/resources/themes/<%= themeConfig.name %>/templates/<%= themeConfig.name %>-templates.jar',
      mode: 'zip'
    },
    files: [
      {
        expand: true,
        cwd: 'src/templates',
        src: ['**'],
        dest: 'META-INF/resources/WEB-INF/templates/<%= themeConfig.name %>/'
      }
    ]
  },
  // create theme zip
  theme: {
    options: {
      archive: '../../target/themes/<%= themeConfig.name %>-theme.zip',
      mode: 'zip'
    },
    files: [
      {
        expand: true,
        cwd: '../../target/resources/themes/<%= themeConfig.name %>',
        src: ['**'],
        dest: '<%= themeConfig.name %>/'
      },
      {
        expand: true,
        filter: 'isFile',
        src: ['*-theme.xml'],
        dest: 'THEME-METADATA/'
      }
    ]
  }
};
