/* brick 'download_portal' */
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
        brick_download_portal_img: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'img/**',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_download_portal_js: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'js/*.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_download_portal_l10n: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'l10n/**',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_download_portal_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        }
      },
      watch: {
        brick_download_portal_img: {
          files: options.brickDirectory + "/img/**",
          tasks: ['copy:brick_download_portal_img']
        },
        brick_download_portal_js: {
          files: options.brickDirectory + "/js/**",
          tasks: ['copy:brick_download_portal_js']
        },
        brick_download_portal_l10n: {
          files: [options.brickDirectory + "/l10n/**"],
          tasks: ['copy:brick_download_portal_l10n']
        },
        brick_download_portal_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_download_portal_templates', 'compress:brick_templates']
        },
        brick_download_portal_sass: {
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
