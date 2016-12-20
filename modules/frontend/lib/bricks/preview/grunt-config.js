/* brick 'preview' */
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
        brick_preview: {
          files: [
            // copy templates
            {
              expand: true,
              cwd: options.brickDirectory + '/templates',
              src: '**',
              dest: options.brickTemplatesDest
            },
            // copy resource bundles
            {
              expand: true,
              isFile: true,
              cwd: options.brickDirectory,
              src: 'l10n/*.properties',
              dest: '../../target/resources/themes/<%= themeConfig.name %>'
            }
          ]
        }
      },
      watch: {
        brick_preview: {
          files: [options.brickDirectory + "**"],
          tasks: ['copy:brick_preview']
        }
      }
    }
  };
};
