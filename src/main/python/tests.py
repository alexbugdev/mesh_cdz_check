import requests
def status():
    rs = requests.get("https://google.com")
    return rs.status_code