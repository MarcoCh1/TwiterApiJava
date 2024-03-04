import os
import requests_oauthlib
import uuid


def generate_authorization_url(client_id, redirect_uri):
    print("Hello from Python! Neck ass")
    os.environ['OAUTHLIB_INSECURE_TRANSPORT'] = '1'
    scope = ["identity", "read", "submit"]
    oauth = requests_oauthlib.OAuth2Session(client_id=client_id, redirect_uri=redirect_uri, scope=scope)
    state = str(uuid.uuid4())
    authorization_url, _ = oauth.authorization_url("https://www.reddit.com/api/v1/authorize", state=state)
    return authorization_url, state
