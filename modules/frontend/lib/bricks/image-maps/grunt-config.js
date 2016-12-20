/* brick 'responsive images' */
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
        brick_imagemaps: {
          files: [{
            expand: true,
            isFile: true,
            cwd: options.brickDirectory,
            src: ['css/**', 'fonts/**', 'img/**', 'images/**', 'js/**', 'vendor/**', 'l10n/**'],
            dest: '../../target/resources/themes/<%= themeConfig.name %>'
          }, {
            expand: true,
            cwd: options.brickDirectory + '/templates',
            src: '**',
            dest: options.brickTemplatesDest
          }]
        }
      },
      watch: {
        brick_imagemaps: {
          files: [options.brickDirectory + "**"],
          tasks: ['copy:brick_imagemaps']
        }
      }
    }
  };
};
