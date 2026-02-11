export interface CacheVO {
  commandStats: Array<{name: string; value: string}>;

  dbSize: number;

  info: Record<string, string>;
}
