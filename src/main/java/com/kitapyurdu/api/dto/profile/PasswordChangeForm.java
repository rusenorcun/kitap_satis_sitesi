package com.kitapyurdu.api.dto.profile;

public class PasswordChangeForm {
    private String currentPassword;
    private String newPassword;
    private String newPassword2;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getNewPassword2() { return newPassword2; }
    public void setNewPassword2(String newPassword2) { this.newPassword2 = newPassword2; }
}
