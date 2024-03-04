import os
import requests_oauthlib
import sys

os.environ['OAUTHLIB_INSECURE_TRANSPORT'] = '1'


def exchange_code_for_token(client_id, client_secret, redirect_uri, code):
    try:
        oauth = requests_oauthlib.OAuth2Session(client_id=client_id, redirect_uri=redirect_uri)
        token_response = oauth.fetch_token(
            "https://www.reddit.com/api/v1/access_token",
            client_secret=client_secret,
            code=code
        )
        access_token = token_response.get('access_token', 'No access token found in response')
        print("THIS IS THE CODE: " + code)
        print("ACCESS_TOKEN:", access_token)
    except Exception as e:
        print(f"Error exchanging code for token: {e}", file=sys.stderr)


if __name__ == "__main__":
    client_id = sys.argv[1]
    client_secret = sys.argv[2]
    redirect_uri = sys.argv[3]
    code = sys.argv[4]
    exchange_code_for_token(client_id, client_secret, redirect_uri, code)
