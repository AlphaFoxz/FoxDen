export enum LanguageEnum {
  zh_CN = 'zh_CN',
  en_US = 'en_US',
}
export const Language = {
  zhCN: 'zh_CN',
  enUS: 'en_US',
} as const;

export type Language = Enum<typeof Language>;
