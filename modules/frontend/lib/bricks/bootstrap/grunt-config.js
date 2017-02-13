/* brick 'call to action' */
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
        brick_bootstrap_assets: {
          files: [
            // copy jquery
            {
              expand: true,
              cwd: '../../node_modules/jquery/dist/',
              src: 'jquery.min.js',
              dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
            },
            // copy bootstrap fonts
            {
              expand: true,
              flatten: true,
              isFile: true,
              cwd: '../../node_modules/bootstrap-sass/assets/fonts/bootstrap/',
              src: '*',
              dest: '../../target/resources/themes/<%= themeConfig.name %>/fonts/bootstrap/'
            },
            // copy bootstrap javascript
            {
              expand: true,
              flatten: true,
              isFile: true,
              src: [
                '../../node_modules/bootstrap-sass/assets/javascripts/bootstrap.min.js',
                '../../node_modules/bootstrap-carousel-swipe/carousel-swipe.js'
              ],
              dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/bootstrap/'
            }
          ]
        },
        brick_bootstrap_js: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'js/*.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_bootstrap_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        }
      },
      watch: {
        brick_bootstrap_js: {
          files: options.brickDirectory + "/js/**",
          tasks: ['copy:brick_bootstrap_js']
        },
        brick_bootstrap_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_bootstrap_templates', 'compress:brick_templates']
        },
        brick_bootstrap_sass: {
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
