export function transParameters(parameters: Record<string, unknown>) {
  let result = '';
  for (const propName of Object.keys(parameters)) {
    const value = parameters[propName];
    if (value === null || value === '' || value === undefined) {
      continue;
    }

    const part = encodeURIComponent(propName) + '=';
    if (typeof value === 'object') {
      for (const key of Object.keys(value)) {
        const itemValue = (value as Record<string, unknown>)[key] as string;
        if (itemValue === null || itemValue === '' || itemValue === undefined) {
          continue;
        }

        const parameters = propName + '[' + key + ']';
        const subPart = encodeURIComponent(parameters) + '=';
        result += subPart + encodeURIComponent(itemValue) + '&';
      }
    } else {
      result += part + encodeURIComponent(value as string | number | boolean) + '&';
    }
  }

  return result;
}
