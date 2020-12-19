from api_handler import get_method

url = "https://be-dot-fii-covidtracker.ey.r.appspot.com/article/getByRegion"


def get(query):
    global url
    params = {
        "region": query
    }
    final_time, result = get_method(url, params)
    words = result.text
    return words, final_time


if __name__ == "__main__":
    print(get("Iasi"))
