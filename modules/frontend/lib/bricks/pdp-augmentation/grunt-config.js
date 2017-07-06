/* brick 'responsive images' */
module.exports = function (grunt, options) {
  'use strict';

  // add templates to theme templateset
  var existingTemplates = options.brickCompressFiles;
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
        brick_pdp_augmentation_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        },
        brick_pdp_augmentation_vendor: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'vendor/**',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
      },
      watch: {
        brick_pdp_augmentation_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_pdp_augmentation_templates', 'compress:brick_templates']
        },
        brick_generic_templates_sass: {
          options: {
            spawn: true
          },
          files: options.brickDirectory + '/sass/**/*.scss',
          tasks: ['sass']
        }
      }
    }
  };
};
