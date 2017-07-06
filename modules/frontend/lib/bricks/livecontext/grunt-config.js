/* brick 'livecontext' 
 * containing templates which are required by the livecontext scenario
 */
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
        brick_livecontext_fonts: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'fonts/**',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_livecontext_img: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'img/**',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_livecontext_js: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'js/*.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_livecontext_l10n: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'l10n/**',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_livecontext_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        }
      },
      watch: {
        brick_livecontext_fonts: {
          files: options.brickDirectory + "/fonts/**",
          tasks: ['copy:brick_livecontext_fonts']
        },
        brick_livecontext_img: {
          files: options.brickDirectory + "/img/**",
          tasks: ['copy:brick_livecontext_img']
        },
        brick_livecontext_js: {
          files: options.brickDirectory + "/js/**",
          tasks: ['copy:brick_livecontext_js']
        },
        brick_livecontext_l10n: {
          files: options.brickDirectory + "/l10n/**",
          tasks: ['copy:brick_livecontext_l10n']
        },
        brick_livecontext_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_livecontext_templates', 'compress:brick_templates']
        },
        brick_livecontext_sass: {
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
