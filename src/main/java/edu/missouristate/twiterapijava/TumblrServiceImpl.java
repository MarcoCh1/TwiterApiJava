package edu.missouristate.twiterapijava;

import com.github.scribejava.apis.TumblrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TumblrServiceImpl implements TumblrService {
    private OAuth10aService oauthService;
    private OAuth1RequestToken requestToken;
    private OAuth1AccessToken accessToken;
    private String blogIdentifier;

    @Value("${tumblr.consumerKey}")
    private String consumerKey;

    @Value("${tumblr.consumerSecret}")
    private String consumerSecret;

    @Value("${tumblr.callbackUrl}")
    private String callbackUrl;

    @Override
    public String getAuthorizationUrl() {
        oauthService = new ServiceBuilder(consumerKey)
                .apiSecret(consumerSecret)
                .callback(callbackUrl)
                .build(TumblrApi.instance());
        try {
            requestToken = oauthService.getRequestToken();
            return oauthService.getAuthorizationUrl(requestToken);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getUserInfo(String oauthVerifier) {
        try {
            accessToken = oauthService.getAccessToken(requestToken, oauthVerifier);
            OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.tumblr.com/v2/user/info");
            oauthService.signRequest(accessToken, request);
            Response response = oauthService.execute(request);
            System.out.println(response);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            System.out.println(jsonResponse);

            String blogUrl = jsonResponse.getJSONObject("response")
                    .getJSONObject("user")
                    .getJSONArray("blogs")
                    .getJSONObject(0)
                    .getString("url");

            System.out.println(blogUrl);
            blogIdentifier = blogUrl.substring(blogUrl.lastIndexOf('/') + 1);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving user info";
        }
    }

    @Override
    public void postToBlog(String postContent) throws Exception {

        String postUrl = "https://api.tumblr.com/v2/blog/" + blogIdentifier + "/post";
        System.out.println(postUrl);

        OAuthRequest request = new OAuthRequest(Verb.POST, postUrl);
        request.addBodyParameter("type", "text");
        request.addBodyParameter("body", postContent);
        oauthService.signRequest(accessToken, request);

        Response response = oauthService.execute(request);
        if (response.getCode() != 201) {
            throw new RuntimeException("Failed to post to Tumblr: " + response.getBody());
        }
    }
}
