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
        brick_shoppable_video_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        }
      },
      watch: {
        brick_shoppable_video_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_shoppable_video_templates', 'compress:brick_templates']
        },
        brick_shoppable_video_sass: {
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
