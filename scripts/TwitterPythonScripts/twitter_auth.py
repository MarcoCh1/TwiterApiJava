import os
import sys
import tweepy

# Set your consumer key and consumer secret
CONSUMER_KEY = "2bcFOj1TY9xPBKkCvODGqbhsu"
CONSUMER_SECRET = "Ij14PYzKTBhpeSQ2I1fy315q7dkgwP1w2RLFfv147RMDvcm0qg"


def get_oauth_handler():
    return tweepy.OAuth1UserHandler(CONSUMER_KEY, CONSUMER_SECRET, callback="oob")


def authorize(PIN=None):
    # Try to load tokens from environment variables
    access_token = os.getenv("TWITTER_ACCESS_TOKEN")
    access_token_secret = os.getenv("TWITTER_ACCESS_TOKEN_SECRET")

    if access_token and access_token_secret:
        print("Using existing access tokens.")
    else:
        auth = get_oauth_handler()
        if PIN is None:
            try:
                auth_url = auth.get_authorization_url()
                print("Authorize this app by visiting:", auth_url)
                verifier = input("Input PIN: ")
            except Exception as e:
                print(f"Error during authorization process: {e}")
                sys.exit("Failed to initiate authorization.")
        else:
            verifier = PIN

        try:
            access_token, access_token_secret = auth.get_access_token(verifier)
            # Optionally, set the environment variables for future use
            os.environ["TWITTER_ACCESS_TOKEN"] = access_token
            os.environ["TWITTER_ACCESS_TOKEN_SECRET"] = access_token_secret
            print("Access tokens received.")
        except Exception as e:
            print(f"Failed to exchange PIN for access tokens: {e}")
            sys.exit("Authorization failed.")

    return access_token, access_token_secret


def get_auth_url():
    print("Generating authorization URL...")
    auth = get_oauth_handler()
    try:
        auth_url = auth.get_authorization_url()
        print("Authorize this app by visiting:", auth_url)
    except Exception as e:
        print(f"Error generating authorization URL: {e}")
        sys.exit("Failed to generate authorization URL.")


if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "get_auth_url":
        get_auth_url()
    else:
        PIN = sys.argv[1] if len(sys.argv) > 1 else None
        authorize(PIN=PIN)
