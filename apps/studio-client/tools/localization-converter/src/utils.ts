export interface ILocalization {
  displayName?: string;
  description?: string;
  svgIcon?: string;
}

export interface ITypeLocalization extends ILocalization {
  properties?: Record<string, IPropertyLocalization | string>;
}

export interface IPropertyLocalization extends ITypeLocalization {
  emptyText?: string
}

export interface IContentTypeLocalization extends ITypeLocalization {
  displayName: string,
  svgIcon?: string;
}

export function dashCaseToCamelCase(input) {
  return input.toLowerCase().replace(/-(.)/g, function(match, group1) {
    return group1.toUpperCase();
  });
}

export function firstLetterUpperCase(input) {
  if (input) {
    return input[0].toUpperCase() + input.substr(1);
  }
  return input;
}
