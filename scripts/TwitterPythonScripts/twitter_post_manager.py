import sys
import tweepy

import twitter_auth

CONSUMER_KEY = "2bcFOj1TY9xPBKkCvODGqbhsu"
CONSUMER_SECRET = "Ij14PYzKTBhpeSQ2I1fy315q7dkgwP1w2RLFfv147RMDvcm0qg"


def get_client(consumer_key, consumer_secret, access_token, access_token_secret):
    client = tweepy.Client(
        consumer_key=consumer_key,
        consumer_secret=consumer_secret,
        access_token=access_token,
        access_token_secret=access_token_secret
    )
    return client


def post_tweet(client, tweet_text):
    try:
        response = client.create_tweet(text=tweet_text)
        print("Tweet posted successfully:", response.data)
    except Exception as e:
        print("Error posting tweet:", str(e))
        sys.exit(1)  # Ensure to exit with a non-zero code to indicate failure


def get_user_info(client):
    user_info = client.get_me()
    if user_info.data:
        return user_info.data.username
    else:
        return None


def main(tweet_text):
    keys = twitter_auth.authorize()
    client = get_client(*keys)

    username = get_user_info(client)
    if username:
        print(f"Posting tweet as @{username}:")
    else:
        print("Posting tweet:")

    response = post_tweet(client, tweet_text)
    print("Tweet posted:", response)


if __name__ == "__main__":
    try:
        # Adjust the check to expect 4 arguments in total (script name + 3 parameters)
        if len(sys.argv) != 4:
            print("Usage: python twitter_post_manager.py '<access_token>' '<access_token_secret>' '<tweet_text>'")
            sys.exit(1)
        access_token = sys.argv[1]
        access_token_secret = sys.argv[2]
        tweet_text = sys.argv[3]
        # Directly use the provided access token and secret without calling authorize()
        client = get_client(CONSUMER_KEY, CONSUMER_SECRET, access_token, access_token_secret)
        post_tweet(client, tweet_text)
    except Exception as e:
        print(f"An error occurred: {e}")
        sys.exit(1)  # Exit with a non-zero code to indicate failure
