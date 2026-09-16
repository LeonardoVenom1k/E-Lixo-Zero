package br.fai.lds.e_lixo_zero.domain;

public class NotificationModel {

    private int id;
    private int userId;
    private String title;
    private String message;
    private String notificationType;
    private boolean read;
    private String sentAt;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(final String notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(final boolean read) {
        this.read = read;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(final String sentAt) {
        this.sentAt = sentAt;
    }
}
