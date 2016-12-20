/* brick 'elastic_social' */
module.exports = function (grunt, options) {
  'use strict';

  // add templates to theme templateset
  var existingTemplates = grunt.config.get('compress.brick_templates.files');
  existingTemplates.push({
    expand: true,
    cwd: options.brickDirectory + '/templates',
    src: '**',
    dest: 'META-INF/resources/WEB-INF/templates/bricks/'
  });

  return {
    tasks: {
      compress: {
        brick_templates: {
          files: existingTemplates
        }
      },
      copy: {
        brick_elastic_social: {
          files: [
            // copy templates
            {
              expand: true,
              isFile: true,
              cwd: '../../../extensions/es/es-theme/src/',
              src: ['fonts/**', 'img/**', 'images/**', 'js/**', 'vendor/**', 'l10n/*.properties'],
              dest: '../../target/resources/themes/<%= themeConfig.name %>'
            },
            {
              expand: true,
              isFile: true,
              cwd: options.brickDirectory,
              src: ['fonts/**', 'img/**', 'images/**', 'js/**', 'vendor/**', 'l10n/*.properties'],
              dest: '../../target/resources/themes/<%= themeConfig.name %>'
            }
          ]
        }
      },
      watch: {
        brick_elastic_social: {
          files: [options.brickDirectory + "**"],
          tasks: ['copy:brick_elastic_social']
        }
      }
    }
  };
};
