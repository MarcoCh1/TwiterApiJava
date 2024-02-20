import sys
import tweepy

import twitter_auth


def get_client(consumer_key, consumer_secret, access_token, access_token_secret):
    client = tweepy.Client(
        consumer_key=consumer_key,
        consumer_secret=consumer_secret,
        access_token=access_token,
        access_token_secret=access_token_secret
    )
    return client


def post_tweet(client, tweet_text):
    response = client.create_tweet(text=tweet_text)
    return response


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
    if len(sys.argv) != 2:
        print("Usage: python twitter_post_manager.py '<tweet_text>'")
        sys.exit(1)
    tweet_text = sys.argv[1]
    main(tweet_text)
