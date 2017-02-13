/* brick 'generic templates' */
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
        brick_generic_templates_js: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'js/*.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_generic_templates_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        }
      },
      watch: {
        brick_generic_templates_js: {
          files: options.brickDirectory + "/js/**",
          tasks: ['copy:brick_generic_templates_js']
        },
        brick_generic_templates_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_generic_templates_templates', 'compress:brick_templates']
        },
        brick_generic_templates_sass: {
          options: {
            spawn: true
          },
          files: options.brickDirectory + '/sass/**/*.scss',
          tasks: ['sass', 'postcss']
        }
      }
    }
  };
};
