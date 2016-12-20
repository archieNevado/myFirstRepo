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
        brick_shoppable_video: {
          files: [{
            expand: true,
            cwd: options.brickDirectory + '/templates',
            src: '**',
            dest: options.brickTemplatesDest
          }]
        }
      },
      watch: {
        brick_shoppable_video: {
          files: [options.brickDirectory + "**"],
          tasks: ['copy:brick_shoppable_video']
        }
      }
    }
  };
};
