package edu.missouristate.twiterapijava;

public interface TumblrService {
    String getAuthorizationUrl();

    String getUserInfo(String oauthVerifier) throws Exception;

    void postToBlog(String postContent) throws Exception;
}
