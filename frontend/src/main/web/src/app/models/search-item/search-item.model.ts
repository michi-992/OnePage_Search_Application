import { User } from "../user/user.model";

export interface SearchItem {
  id?: number;
  searchTerm: string;
  searchedAt?: string;
  user?: User | null;
}
