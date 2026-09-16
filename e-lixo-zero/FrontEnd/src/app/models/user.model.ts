export interface User {
  id: number | string; // Accepts both number and string for compatibility
  fullName: string;
  email: string;
  password?: string; // Optional password
  street?: string;
  number?: string;
  neighborhood?: string;
  city?: string;
  state?: string;
  phone?: string;
  userType?: string;
  active?: boolean;
  cpf?: string; // CPF opcional
}

// For compatibility with existing code
export interface UserCompat {
  id: string;
  name: string;
  email: string;
  password?: string; // Optional password for security
  street?: string;
  number?: string;
  neighborhood?: string;
  city?: string;
  userType?: string;
}