import sys
import tweepy

# DON'T CHANGE
CONSUMER_KEY = "2bcFOj1TY9xPBKkCvODGqbhsu"
CONSUMER_SECRET = "Ij14PYzKTBhpeSQ2I1fy315q7dkgwP1w2RLFfv147RMDvcm0qg"

if len(sys.argv) != 4:
    print("Usage: python exchange_pin_for_tokens.py <PIN> <request_token> <request_token_secret>", file=sys.stderr)
    sys.exit(1)

pin = sys.argv[1]
request_token = sys.argv[2]
request_token_secret = sys.argv[3]

auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth.request_token = {'oauth_token': request_token, 'oauth_token_secret': request_token_secret}

try:
    auth.get_access_token(pin)
    print(f"{auth.access_token},{auth.access_token_secret}")
except tweepy.TweepError as e:
    print(f"Failed to get access token: {e}", file=sys.stderr)
    sys.exit(1)
