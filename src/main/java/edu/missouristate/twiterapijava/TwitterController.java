package edu.missouristate.twiterapijava;

import edu.missouristate.twiterapijava.service.SocialMediaAccountService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Controller
public class TwitterController {

    // At the beginning of your controller class
    private static final Logger log = LoggerFactory.getLogger(TwitterController.class);
    @Autowired
    private SocialMediaAccountService socialMediaAccountService;
    @Value("${python.path}")
    private String pythonPath; // This will hold the path to Python executable

    @PostMapping("/tweet")
    public ModelAndView postTweet(@RequestParam String tweetText, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            String accessToken = (String) session.getAttribute("access_token");
            String accessTokenSecret = (String) session.getAttribute("access_token_secret");

            log.debug("Attempting to post a tweet with text: {}", tweetText);
            log.debug("Using Access Token: {}", accessToken);
            log.debug("Using Access Token Secret: {}", accessTokenSecret);

            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "scripts/TwitterPythonScripts/twitter_post_manager.py", accessToken, accessTokenSecret, tweetText);
            log.debug("Executing command: {}", String.join(" ", processBuilder.command()));

            Process process = processBuilder.start();
            String scriptOutput = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining("\n"));
            int exitCode = process.waitFor();

            socialMediaAccountService.saveSocialMediaAccount(2, "Twitter", accessToken);

            if (exitCode == 0) {
                log.debug("Tweet posted successfully. Script output: {}", scriptOutput);
                modelAndView.setViewName("result");
                modelAndView.addObject("message", "Tweet posted successfully!");
                modelAndView.addObject("scriptOutput", scriptOutput);
            } else {
                log.error("Failed to post tweet, exit code: {}", exitCode);
                throw new Exception("Failed to post tweet, exit code " + exitCode);
            }
        } catch (Exception e) {
            log.error("Exception occurred while posting tweet: {}", e.getMessage(), e);
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Failed to post tweet: " + e.getMessage());
        }

        return modelAndView;
    }


    @PostMapping("/submit-pin")
    public String submitPin(@RequestParam("pin") String pin, HttpSession session) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "scripts/TwitterPythonScripts/exchange_pin_for_tokens.py", pin);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            String tokens = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
            // Assume tokens are returned as "access_token,access_token_secret"
            String[] parts = tokens.split(",");
            session.setAttribute("access_token", parts[0]);
            session.setAttribute("access_token_secret", parts[1]);

            return "redirect:/post-tweet"; //<-- Redirect to the tweet posting form
        } catch (IOException e) {
            e.printStackTrace();
            return "error"; // Return an error view if something goes wrong
        }
    }

    @GetMapping("/post-tweet")
    public String showTweetForm(HttpSession session) {
        if (session.getAttribute("access_token") != null) {
            // User is authenticated, show the tweet form
            return "post-tweet";
        } else {
            // User is not authenticated, redirect to start authentication
            return "redirect:/"; // <--- Might need to change later
        }
    }

    @GetMapping("/oauth-callback")
    public String oauthCallback(@RequestParam("oauth_token") String oauthToken, @RequestParam("oauth_verifier") String oauthVerifier, HttpSession session) {
        try {
            // Initial log messages to confirm method entry and parameter reception
            System.out.println("Entering oauthCallback method.");
            System.out.println("OAuth Token: " + oauthToken);
            System.out.println("OAuth Verifier: " + oauthVerifier);

            // Retrieving stored request token and secret from session
            String requestToken = (String) session.getAttribute("request_token");
            String requestTokenSecret = (String) session.getAttribute("request_token_secret");
            System.out.println("Request Token from session: " + requestToken);
            System.out.println("Request Token Secret from session: " + requestTokenSecret);

            // Early exit check if request token or secret is missing
            if (requestToken == null || requestTokenSecret == null) {
                System.err.println("Request token or secret is null. Redirecting to error.");
                return "error";
            }

            // Preparing and starting the process to exchange verifier for tokens
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "scripts/TwitterPythonScripts/exchange_verifier_for_tokens.py", oauthVerifier, requestToken, requestTokenSecret);
            processBuilder.redirectErrorStream(true);
            System.out.println("Starting process to exchange verifier for tokens.");
            Process process = processBuilder.start();

            // Reading script response
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            System.out.println("Response from script: " + line);

            // Additional checks for script response analysis
            if (line == null || line.isEmpty()) {
                System.err.println("Script response is null or empty. Redirecting to error.");
                return "error";
            }

            // Parsing token response from script
            String[] tokens = line.split(",");
            if (tokens.length < 2) {
                System.err.println("Invalid tokens format: " + line);
                throw new RuntimeException("Invalid tokens format: " + line);
            }

            // Storing access token and secret in session and confirming action, added
            // console logs because I was stuck
            session.setAttribute("access_token", tokens[0]);
            session.setAttribute("access_token_secret", tokens[1]);
            System.out.println("Token 0 Access Token-> " + tokens[0]);
            System.out.println("Token 1 Access Token Secret-> " + tokens[1]);
            System.out.println("Access token and secret set in session. Redirecting to post-tweet.");

            return "redirect:/post-tweet";
        } catch (Exception e) {
            // Logging exception details
            e.printStackTrace();
            System.err.println("Exception in oauthCallback: " + e.getMessage());
            return "error";
        }
    }


    @GetMapping("/start-auth")
    public ModelAndView startAuth(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("auth-start");
        try {
            // Ensure the process builder is correctly pointing to your script
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "scripts/TwitterPythonScripts/generate_auth_url.py");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String authUrl = reader.readLine(); // First line is the authorization URL
            String tokensLine = reader.readLine(); // Second line contains the request token and secret
            String[] tokens = tokensLine.split(",");

            // Check if tokens array has the expected length
            if (tokens.length >= 2) {
                String requestToken = tokens[0];
                String requestTokenSecret = tokens[1];

                // Store these values in the session
                session.setAttribute("request_token", requestToken);
                session.setAttribute("request_token_secret", requestTokenSecret);

                modelAndView.addObject("authUrl", authUrl);
            } else {
                // Handle error if the tokens array does not have the expected length
                throw new IOException("Invalid token response from script");
            }
        } catch (IOException e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Error generating authorization URL.");
        }
        return modelAndView;
    }

}
