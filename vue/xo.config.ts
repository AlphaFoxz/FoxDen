import {type FlatXoConfig} from 'xo';

/**
 * @see {@link 'file://./node_modules/xo/node_modules/eslint-config-xo-typescript/index.js'}
 */
const expose: FlatXoConfig = {
  ignores: [
    'node_modules/**',
    'designs/**',
    '**/*.test.ts',
  ],
  rules: {
    // 'no-restricted-globals': [
    //   'error',
    //   {
    //     name: 'DataView',
    //     message: 'Please use `TuiDataViewWrapper` instead.',
    //   },
    // ],
    '@typescript-eslint/no-restricted-types': [
      'error', {
        types: {
          object: {
            message: 'The `object` type is hard to use. Use `Record<string, unknown>` instead. See: https://github.com/typescript-eslint/typescript-eslint/pull/848',
            fixWith: 'Record<string, unknown>',
          },
          // Null: {
          // 	message: 'Use `undefined` instead. See: https://github.com/sindresorhus/meta/issues/7',
          // 	fixWith: 'undefined',
          // },
          Buffer: {
            message: 'Use Uint8Array instead. See: https://sindresorhus.com/blog/goodbye-nodejs-buffer',
            suggest: [
              'Uint8Array',
            ],
          },
          '[]': 'Don\'t use the empty array type `[]`. It only allows empty arrays. Use `SomeType[]` instead.',
          '[[]]': 'Don\'t use `[[]]`. It only allows an array with a single element which is an empty array. Use `SomeType[][]` instead.',
          '[[[]]]': 'Don\'t use `[[[]]]`. Use `SomeType[][][]` instead.',
          '[[[[]]]]': 'ur drunk 🤡',
          '[[[[[]]]]]': '🦄💥',
        },
      },
    ],
    '@typescript-eslint/naming-convention': [
      'error',
      {
        selector: 'variable',  
        format: ['UPPER_CASE', 'camelCase', 'strictCamelCase', 'PascalCase', 'StrictPascalCase'],
        leadingUnderscore: 'allow',
        trailingUnderscore: 'allow',
      },
    ],
    'unicorn/filename-case': [
      'error',
      {
        cases: {
          kebabCase: true,
          camelCase: true,
          pascalCase: true,
        },
      },
    ],
    '@stylistic/indent': ['error', 2],
    'unicorn/text-encoding-identifier-case': 'off',
    'no-bitwise': 'off',
    '@typescript-eslint/class-literal-property-style': 'off',
    'import-x/extensions': 'off',
    'no-useless-call': 'off',
    '@typescript-eslint/no-unused-vars': 'error',
    '@typescript-eslint/no-unused-private-class-members': 'error',
    'no-redeclare': 'off',
    '@typescript-eslint/no-redeclare': 'off',
    '@typescript-eslint/triple-slash-reference': 'off',
    '@typescript-eslint/consistent-type-definitions': 'off',
  },
};

export default expose;
