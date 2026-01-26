declare global {
  function isNever(...args: never[]): void;
  type Enum<T extends Record<string, any>> = T[keyof T];
}

export {};
