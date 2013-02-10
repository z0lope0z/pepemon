package com.lopefied.pepemon.notifications;

public interface NotificationsManager {
    public void launchMessage(String message);

    public Boolean isCurrentlyDisplaying();

    public void cancelMessage();
}
