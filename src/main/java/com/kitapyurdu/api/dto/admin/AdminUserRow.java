package com.kitapyurdu.api.dto.admin;

public class AdminUserRow {
    private Integer userId;
    private String username;
    private String roles;
    private boolean enabled;

    public AdminUserRow(Integer userId, String username, String roles, boolean enabled) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
        this.enabled = enabled;
    }

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRoles() { return roles; }
    public boolean isEnabled() { return enabled; }
}
