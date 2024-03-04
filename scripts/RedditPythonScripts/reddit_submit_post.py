import requests
import sys


def submit_post(access_token, subreddit, title, text):
    headers = {
        'Authorization': f"bearer {access_token}",
        'User-Agent': 'RedditAPI/1.0'
    }
    payload = {
        'title': title,
        'text': text,
        'kind': 'self',
        'sr': subreddit
    }
    url = "https://oauth.reddit.com/api/submit"

    # Log the request details
    print(f"Making request to {url} with headers {headers} and payload {payload}", file=sys.stderr)

    response = requests.post(url, headers=headers, data=payload)

    # Log the response details
    print(f"Response status code: {response.status_code}", file=sys.stderr)
    print(f"Response headers: {response.headers}", file=sys.stderr)
    print(f"Response body: {response.text}", file=sys.stderr)

    if response.status_code == 200:
        # Assuming a successful request returns a 200 status code
        return "Post submitted successfully!"
    else:
        return f"Failed to submit post. Status code: {response.status_code}, Response: {response.text}"


if __name__ == "__main__":
    if len(sys.argv) != 5:
        print("Usage: script.py access_token subreddit title text", file=sys.stderr)
        sys.exit(1)

    access_token = sys.argv[1]
    subreddit = sys.argv[2]
    title = sys.argv[3]
    text = sys.argv[4]

    result = submit_post(access_token, subreddit, title, text)
    print(result)
