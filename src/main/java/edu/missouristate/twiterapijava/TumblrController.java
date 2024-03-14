package edu.missouristate.twiterapijava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TumblrController {


    private final TumblrService tumblrService;

    @Autowired
    public TumblrController(TumblrService tumblrService) {
        this.tumblrService = tumblrService;
    }

    @GetMapping("/tumblr/start-oauth")
    public String startOAuthProcess(RedirectAttributes redirectAttributes) {
        String authorizationUrl = tumblrService.getAuthorizationUrl();
        return "redirect:" + authorizationUrl;
    }

    @GetMapping("/tumblr/oauth-callback")
    public String oauthCallback(@RequestParam("oauth_verifier") String oauthVerifier, Model model) throws Exception {
        String userInfo = tumblrService.getUserInfo(oauthVerifier);
        model.addAttribute("userInfo", userInfo);
        return "tumblr/create-post";
    }

    @PostMapping("/tumblr/post-to-tumblr")
    public String postToTumblr(@RequestParam("postContent") String postContent, Model model) throws Exception {
        tumblrService.postToBlog(postContent);
        model.addAttribute("message", "Successfully posted to Tumblr!");
        return "tumblr/post-success";
    }

    @GetMapping("/tumblr/create-post")
    public String showPostCreationPage() {
        return "/tumblr/create-post";
    }

}
