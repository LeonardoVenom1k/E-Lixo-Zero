export interface CollectionPoint {
  id: number;
  name: string;
  address: string;
  street?: string;
  number?: string;
  neighborhood?: string;
  city?: string;
  state?: string;
  openingHours: string;
  phone: string;
  acceptedWastes: string[];
  latitude: number;
  longitude: number;
  active?: boolean;
  distanceKm?: number | null;
}
