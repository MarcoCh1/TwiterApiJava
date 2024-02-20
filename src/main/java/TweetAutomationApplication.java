import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * TweetAutomationController is designed to automate the process of Twitter authentication and tweet posting.
 * Running this program will:
 * 1. Generate an authorization URL using the Twitter API and prompt the user to authorize the application.
 * 2. Request the user to enter the PIN obtained from the authorization process.
 * 3. Use the entered PIN to complete the authentication process and obtain access tokens.
 * 4. Prompt the user to enter a tweet message.
 * 5. Post the entered tweet message to the user's Twitter timeline using the authenticated session.
 * <p>
 * This program integrates with Python scripts for handling the authentication flow and tweet posting,
 * streamlining the process of tweeting directly from the command line or a Java application interface.
 */

public class TweetAutomationApplication {
    public static void main(String[] args) {
        try {
            // Step 1: Run the Python script to get the authorization URL
            ProcessBuilder processBuilderForURL = new ProcessBuilder(
                    "python",
                    "C:\\Users\\mchic\\IdeaProjects\\TwiterApiJava\\scripts\\twitter_auth.py",
                    "get_auth_url"
            );
            processBuilderForURL.redirectErrorStream(true); // Combine standard error and standard output streams
            Process processForURL = processBuilderForURL.start();

            BufferedReader readerForURL = new BufferedReader(new InputStreamReader(processForURL.getInputStream()));
            String authUrl = readerForURL.readLine(); // Assuming the URL is the first line printed
            System.out.println("Please visit the following URL to authorize: " + authUrl);

            String errorOutput = readerForURL.lines().collect(Collectors.joining("\n"));
            if (!errorOutput.isEmpty()) {
                System.out.println("Error/Output while getting URL: " + errorOutput);
            }

            int exitCodeForURL = processForURL.waitFor();
            if (exitCodeForURL != 0) {
                System.out.println("Failed to get authentication URL. Exiting.");
                return;
            }

            // Step 2: Prompt user for PIN
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter the PIN from Twitter and press Enter:");
            String pin = consoleReader.readLine();

            // Step 3: Prompt user for the tweet
            System.out.println("Enter your tweet:");
            String tweetText = consoleReader.readLine();

            // Step 4: Execute the Python script with PIN and tweet text
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python",
                    "C:\\Users\\mchic\\IdeaProjects\\TwiterApiJava\\scripts\\twitter_post_manager.py",
                    pin,
                    tweetText
            );
            processBuilder.redirectErrorStream(true); // Combine standard error and standard output streams
            Process process = processBuilder.start();

            // Output script execution results
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with error code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
