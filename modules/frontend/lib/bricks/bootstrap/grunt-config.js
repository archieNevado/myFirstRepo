/* brick 'call to action' */
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
        brick_bootstrap: {
          files: [
            // copy jquery
            {
              expand: true,
              cwd: '../../node_modules/jquery/dist/',
              src: [
                'jquery.min.js',
              ],
              dest: '../../target/resources/themes/<%= themeConfig.name %>/vendor/'
            },
            // copy fonts (icons)
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
            },
            // copy extra carousel javascript
            {
              expand: true,
              cwd: options.brickDirectory,
              src: 'js/*.js',
              dest: '../../target/resources/themes/<%= themeConfig.name %>/'
            },
            // templates
            {
              expand: true,
              cwd: options.brickDirectory + '/templates',
              src: '**',
              dest: options.brickTemplatesDest
            }
            // no task for sass. import them to your theme for customization
          ]
        }
      },
      watch: {
        brick_bootstrap: {
          files: [options.brickDirectory + "**"],
          tasks: ['copy:brick_bootstrap']
        }
      }
    }
  };
};
