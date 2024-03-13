package edu.missouristate.twiterapijava;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username; // Or any other identifier
    private Map<String, String> connectedAccounts;

    public User(String username) {
        this.username = username;
        this.connectedAccounts = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, String> getConnectedAccounts() {
        return connectedAccounts;
    }

    // Connect a new social media account
    public void connectAccount(String platform, String accessToken) {
        connectedAccounts.put(platform, accessToken);
    }

    // Check if a specific platform is connected
    public boolean isPlatformConnected(String platform) {
        return connectedAccounts.containsKey(platform);
    }

    // Get the access token for a specific platform
    public String getAccessTokenForPlatform(String platform) {
        return connectedAccounts.get(platform);
    }
}
