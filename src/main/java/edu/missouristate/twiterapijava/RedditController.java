package edu.missouristate.twiterapijava;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;


@Controller
public class RedditController {

    private static final Logger log = LoggerFactory.getLogger(RedditController.class);
    String CLIENT_ID = "6aK_iXozHqB7AlJY3aF6ZA";
    String CLIENT_SECRET = "6bEXPVk7tYpAAFj4fbH9Vj-XSKzGag";
    String REDIRECT_URI = "http://localhost:8080/reddit/callback";

    @GetMapping("/reddit/auth")
    public ModelAndView redditAuth(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("reddit/redditAuth");
        String state = UUID.randomUUID().toString();
        session.setAttribute("REDDIT_STATE", state);

        String encodedRedirectUri = URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);

        String authUrl = "https://www.reddit.com/api/v1/authorize?" +
                "client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&state=" + state +
                "&redirect_uri=" + encodedRedirectUri +
                "&duration=permanent" +
                "&scope=identity submit read";


        modelAndView.addObject("authUrl", authUrl);
        return modelAndView;
    }


    @GetMapping("/reddit/callback")
    public ModelAndView redditCallback(@RequestParam("state") String state, @RequestParam("code") String code, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        String sessionState = (String) session.getAttribute("REDDIT_STATE");

        if (sessionState == null || !sessionState.equals(state)) {
            log.debug("State mismatch. sessionState: {}, state: {}", sessionState, state);
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Invalid state parameter");
            return modelAndView;
        }

        try {
            String pythonScriptPath = "scripts/RedditPythonScripts/exchange_auth_code_for_access_token.py";
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, code);
            log.debug("Executing Python script: {}", String.join(" ", processBuilder.command()));

            Process process = processBuilder.start();

            String scriptOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining(System.lineSeparator()));
            String errorOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                    .lines().collect(Collectors.joining(System.lineSeparator()));

            int exitCode = process.waitFor();
            log.info("Script Exit Code: " + exitCode);
            log.info("Script Output: " + scriptOutput);
            log.info("Error Output: " + errorOutput);

            // Extract and validate the access token
            String accessToken = extractAccessToken(scriptOutput);
            log.error("Outputted string<?> : " + accessToken);
            if (accessToken.isEmpty()) {
                throw new IllegalArgumentException("Extracted access token is empty.");
            }
            session.setAttribute("REDDIT_ACCESS_TOKEN", accessToken);

            modelAndView.setViewName("redirect:/reddit/submitRedditPost");
        } catch (Exception e) {
            log.error("Failed to exchange code for token", e);
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Failed to exchange code for token: " + e.getMessage());
        }

        return modelAndView;
    }

    private String extractAccessToken(String scriptOutput) throws IllegalArgumentException {
        return Arrays.stream(scriptOutput.split(System.lineSeparator()))
                .filter(line -> line.startsWith("ACCESS_TOKEN:"))
                .findFirst()
                .map(line -> line.substring("ACCESS_TOKEN:".length()))
                .orElseThrow(() -> new IllegalArgumentException("Access token not found in script output"));
    }


    @PostMapping("/reddit/submitPost")
    public ModelAndView submitPost(@RequestParam("subreddit") String subreddit,
                                   @RequestParam("title") String title,
                                   @RequestParam("text") String text,
                                   HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("reddit/postResult");

        // Directly use session-stored accessToken
        String accessToken = (String) session.getAttribute("REDDIT_ACCESS_TOKEN");
        log.debug("Using Access Token from session: {}", accessToken);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            log.error("Access Token is missing or empty.");
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Access Token is missing or invalid.");
            return modelAndView;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "scripts/RedditPythonScripts/reddit_submit_post.py", accessToken, subreddit, title, text);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            String result = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            log.debug("Result from post submission: {}", result);
            modelAndView.addObject("result", result);
        } catch (Exception e) {
            log.error("Failed to submit post", e);
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Failed to submit post: " + e.getMessage());
        }

        return modelAndView;
    }


    @GetMapping("/reddit/submitRedditPost")
    public ModelAndView showSubmitRedditPost(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("reddit/submitRedditPost");
        String accessToken = (String) session.getAttribute("REDDIT_ACCESS_TOKEN");

        log.debug("Retrieved Access Token from session: {}", accessToken);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Access token is missing or invalid.");
            return modelAndView;
        }

        return modelAndView;
    }


}

