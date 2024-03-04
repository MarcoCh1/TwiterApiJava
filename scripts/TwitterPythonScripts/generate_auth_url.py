import sys
import tweepy

# DON'T CHANGE
CONSUMER_KEY = "2bcFOj1TY9xPBKkCvODGqbhsu"
CONSUMER_SECRET = "Ij14PYzKTBhpeSQ2I1fy315q7dkgwP1w2RLFfv147RMDvcm0qg"


def generate_auth_url():
    try:
        auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET, 'oob')  # 'oob' for PIN-based auth
        auth_url = auth.get_authorization_url()
        # Output the authorization URL to stdout
        print(auth_url)
        # Also output the request token and secret for later use
        print(auth.request_token['oauth_token'], auth.request_token['oauth_token_secret'], sep=',')
    except tweepy.TweepError as e:
        print(f"Failed to get authorization URL: {e}", file=sys.stderr)


if __name__ == "__main__":
    generate_auth_url()
