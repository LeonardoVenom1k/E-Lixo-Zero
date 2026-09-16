export interface CollectionPoint {
  id: number;
  name: string;
  address: string;
  openingHours: string;
  phone: string;
  acceptedWastes: string[];
  latitude: number;
  longitude: number;
  distanceKm?: number | null;
}
