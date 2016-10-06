/* brick 'generic templates' */
module.exports = function (grunt, options) {
  'use strict';

  // add templates to theme templateset
  var existingTemplates = grunt.config.get('compress.templates.files');
  existingTemplates.push({
    expand: true,
    cwd: options.brickDirectory + '/templates',
    src: '**',
    dest: 'META-INF/resources/WEB-INF/templates/bricks/'
  });

  return {
    tasks: {
      compress: {
        templates: {
          files: existingTemplates
        }
      },
      copy: {
        // copy templates
        brick_genericTemplates: {
          files: [{
            expand: true,
            cwd: options.brickDirectory + '/templates',
            src: '**',
            dest: options.brickTemplatesDest
          },{
            expand: true,
            isFile: true,
            cwd: options.brickDirectory,
            src: ['css/**', 'fonts/**', 'img/**', 'js/**', 'vendor/**', '*.properties'],
            dest: '../../target/resources/themes/<%= themeConfig.name %>'
          }]
        }
        // no task for sass. import them to your theme for customization
      },
      watch: {
        brick_genericTemplates: {
          files: [options.brickDirectory + "**"],
          tasks: ['copy:brick_genericTemplates']
        }
      }
    }
  };
};
