export type ContextMenuItem = {
  id: string;
  action: (id: string) => void;
  label: string;
  color?: string;
};
