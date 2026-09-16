export interface Notification {
  id: number;
  userId?: number;
  title: string;
  message: string;
  notificationType?: string;
  read?: boolean;
  sentAt: string;
}
