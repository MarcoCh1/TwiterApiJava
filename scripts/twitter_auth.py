import os
import sys
import tweepy

# DON'T CHANGE CONSUMER_KEY AND CONSUMER_SECRET
CONSUMER_KEY = "2bcFOj1TY9xPBKkCvODGqbhsu"
CONSUMER_SECRET = "Ij14PYzKTBhpeSQ2I1fy315q7dkgwP1w2RLFfv147RMDvcm0qg"

# path for the request_token.txt file
request_token_file = os.path.join(os.path.dirname(__file__), 'request_token.txt')


def save_tokens(access_token, access_token_secret):
    with open("tokens.txt", "w") as file:
        file.write(access_token + '\n' + access_token_secret)


def load_tokens():
    if os.path.exists("tokens.txt"):
        with open("tokens.txt", "r") as file:
            tokens = file.readlines()
            return tokens[0].strip(), tokens[1].strip()
    return None, None


def authorize(PIN=None):
    ACCESS_TOKEN, ACCESS_TOKEN_SECRET = load_tokens()

    if not ACCESS_TOKEN:
        auth = tweepy.OAuth1UserHandler(CONSUMER_KEY, CONSUMER_SECRET, callback="oob")
        if PIN is None:
            try:
                auth_url = auth.get_authorization_url()
                print(auth_url)
                with open(request_token_file, "w") as f:
                    print(f"Writing request token to: {request_token_file}")
                    f.write(auth.request_token["oauth_token"] + '\n' + auth.request_token["oauth_token_secret"])
            except Exception as e:
                print(f"Failed to write request token to file: {e}")
                sys.exit("Failed to initiate authorization process.")
            verifier = input("Input PIN: ")
        else:
            if not os.path.exists(request_token_file) or os.stat(request_token_file).st_size == 0:
                print(f"Request token file not found or empty: {request_token_file}")
                sys.exit("Authorization process cannot continue without a request token.")
            with open(request_token_file, "r") as f:
                oauth_token, oauth_token_secret = f.read().splitlines()
            auth.request_token = {'oauth_token': oauth_token, 'oauth_token_secret': oauth_token_secret}
            verifier = PIN
        try:
            ACCESS_TOKEN, ACCESS_TOKEN_SECRET = auth.get_access_token(verifier)
            save_tokens(ACCESS_TOKEN, ACCESS_TOKEN_SECRET)
        except Exception as e:
            print(f"Failed to exchange PIN for access tokens: {e}")
            sys.exit("Authorization failed.")

    return CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET


def get_auth_url():
    print("Generating authorization URL...")
    try:
        auth = tweepy.OAuth1UserHandler(CONSUMER_KEY, CONSUMER_SECRET, callback="oob")
        auth_url = auth.get_authorization_url()
        print(auth_url)
        with open(request_token_file, "w") as f:
            print(f"Writing request token to: {request_token_file}")
            f.write(auth.request_token["oauth_token"] + '\n' + auth.request_token["oauth_token_secret"])
    except Exception as e:
        print(f"Error generating authorization URL: {e}")
        sys.exit("Failed to generate authorization URL.")


if __name__ == "__main__":
    if len(sys.argv) > 1:
        if sys.argv[1] == "get_auth_url":
            get_auth_url()
        else:
            print("Authorizing with PIN provided...")
            authorize(PIN=sys.argv[1])
    else:
        print("No PIN provided, starting authorization...")
        authorize()
