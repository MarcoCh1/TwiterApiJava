import sys
import tweepy

# DON'T CHANGE
CONSUMER_KEY = "2bcFOj1TY9xPBKkCvODGqbhsu"
CONSUMER_SECRET = "Ij14PYzKTBhpeSQ2I1fy315q7dkgwP1w2RLFfv147RMDvcm0qg"

if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: python exchange_verifier_for_token.py <oauth_verifier> <request_token> <request_token_secret>")
        sys.exit(1)

    oauth_verifier = sys.argv[1]
    request_token = sys.argv[2]
    request_token_secret = sys.argv[3]

    auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
    auth.request_token = {'oauth_token': request_token, 'oauth_token_secret': request_token_secret}

    try:
        auth.get_access_token(oauth_verifier)
        print(f"{auth.access_token},{auth.access_token_secret}")
    except tweepy.TweepyException as e:
        print(f"Error exchanging verifier for tokens: {e}", file=sys.stderr)
        sys.exit(1)
