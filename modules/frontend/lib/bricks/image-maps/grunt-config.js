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
        brick_imagemaps_js: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'js/**',
          dest: '../../target/resources/themes/<%= themeConfig.name %>'
        },
        brick_imagemaps_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        }
      },
      watch: {
        brick_imagemaps_js: {
          files: options.brickDirectory + "/js/**",
          tasks: ['copy:brick_imagemaps_js']
        },
        brick_imagemaps_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_imagemaps_templates', 'compress:brick_templates']
        },
        brick_imagemaps_sass: {
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
