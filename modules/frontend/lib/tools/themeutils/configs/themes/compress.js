'use strict';

/* compress templates to deployable jar and the whole theme to a importable zip file */
module.exports = {
  // create templateset (jar of templates)
  templates: {
    options: {
      archive: '<%= themeConfig.targetPath %>/resources/themes/<%= themeConfig.name %>/templates/<%= themeConfig.name %>-templates.jar',
      mode: 'zip'
    },
    files: [
      {
        expand: true,
        cwd: '<%= themeConfig.targetPath %>/resources/WEB-INF/templates/<%= themeConfig.name %>',
        src: ['**'],
        dest: 'META-INF/resources/WEB-INF/templates/<%= themeConfig.name %>/'
      }
    ]
  },
  brick_templates: {
    options: {
      archive: '<%= themeConfig.targetPath %>/resources/themes/<%= themeConfig.name %>/templates/bricks-templates.jar',
      mode: 'zip'
    },
    files: [
      {
        expand: true,
        cwd: '<%= themeConfig.targetPath %>/resources/WEB-INF/templates/bricks',
        src: ['**'],
        dest: 'META-INF/resources/WEB-INF/templates/bricks/'
      }
    ]
  },
  // create theme zip
  theme: {
    options: {
      archive: '<%= themeConfig.targetPath %>/themes/<%= themeConfig.name %>-theme.zip',
      mode: 'zip'
    },
    files: [
      {
        expand: true,
        cwd: '<%= themeConfig.targetPath %>/resources/themes/<%= themeConfig.name %>',
        src: ['**'],
        dest: '<%= themeConfig.name %>/'
      },
      {
        expand: true,
        filter: 'isFile',
        cwd: '<%= themeConfig.targetPath %>/resources/THEME-METADATA',
        src: ['<%= themeConfig.name %>-theme.xml'],
        dest: 'THEME-METADATA/'
      }
    ]
  }
};
