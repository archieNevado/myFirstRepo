/* brick 'preview' */
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
        brick_preview_l10n: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'l10n/**',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_preview_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        }
      },
      watch: {
        brick_preview_l10n: {
          files: options.brickDirectory + "/l10n/**",
          tasks: ['copy:brick_preview_l10n']
        },
        brick_generic_templates_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_preview_templates', 'compress:brick_templates']
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
