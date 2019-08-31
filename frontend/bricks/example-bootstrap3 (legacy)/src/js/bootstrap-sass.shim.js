import $ from "jquery";

import "bootstrap-sass/assets/javascripts/bootstrap/transition";
import "bootstrap-sass/assets/javascripts/bootstrap/alert";
import "bootstrap-sass/assets/javascripts/bootstrap/button";
import "bootstrap-sass/assets/javascripts/bootstrap/carousel";
import "bootstrap-sass/assets/javascripts/bootstrap/collapse";
import "bootstrap-sass/assets/javascripts/bootstrap/dropdown";
import "bootstrap-sass/assets/javascripts/bootstrap/modal";
import "bootstrap-sass/assets/javascripts/bootstrap/tab";
import "bootstrap-sass/assets/javascripts/bootstrap/affix";
import "bootstrap-sass/assets/javascripts/bootstrap/scrollspy";
import "bootstrap-sass/assets/javascripts/bootstrap/tooltip";
import "bootstrap-sass/assets/javascripts/bootstrap/popover";

// exposes the bootstrap JavaScript API as ES6 module

export function affix($self, ...options) {
  return $($self).affix(...options);
}

export function alert($self, ...options) {
  return $($self).alert(...options);
}

export function button($self, ...options) {
  return $($self).button(...options);
}

export function carousel($self, ...options) {
  return $($self).carousel(...options);
}

export function collapse($self, ...options) {
  return $($self).collapse(...options);
}

export function dropdown($self, ...options) {
  return $($self).dropdown(...options);
}

export function modal($self, ...options) {
  return $($self).modal(...options);
}

export function popover($self, ...options) {
  return $($self).popover(...options);
}

export function scrollspy($self, ...options) {
  return $($self).scrollspy(...options);
}

export function tab($self, ...options) {
  return $($self).tab(...options);
}

export function tooltip($self, ...options) {
  return $($self).tooltip(...options);
}
