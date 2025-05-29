# Run this command to execute -> python test_login.py all

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.edge.service import Service as EdgeService

from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager
from webdriver_manager.microsoft import EdgeChromiumDriverManager

from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.firefox.options import Options as FirefoxOptions
from selenium.webdriver.edge.options import Options as EdgeOptions

import time
import sys

def get_driver(browser="chrome"):
    print(f"[SETUP] Preparing WebDriver for: {browser}")

    if browser == "chrome":
        print("⬇ Downloading ChromeDriver...")
        chrome_path = ChromeDriverManager().install()
        print(f"ChromeDriver installed at: {chrome_path}")
        options = ChromeOptions()
        # options.headless = True
        return webdriver.Chrome(service=ChromeService(chrome_path), options=options)

    elif browser == "firefox":
        print("⬇ Downloading GeckoDriver...")
        firefox_path = GeckoDriverManager().install()
        print(f"GeckoDriver installed at: {firefox_path}")
        options = FirefoxOptions()
        # options.headless = True
        return webdriver.Firefox(service=FirefoxService(firefox_path), options=options)

    elif browser == "edge":
        print("⬇ Downloading EdgeDriver...")
        edge_path = EdgeChromiumDriverManager().install()
        print(f"EdgeDriver installed at: {edge_path}")
        options = EdgeOptions()
        # options.headless = True
        return webdriver.Edge(service=EdgeService(edge_path), options=options)

    else:
        raise ValueError(f"Unsupported browser: {browser}")

def test_login(browser="chrome"):
    print(f"\n[TEST START] Running test on: {browser}")
    try:
        driver = get_driver(browser)
        print("Launching browser...")
        driver.get("http://localhost:8081/login")
        print("Opened login page.")

        print("Typing username and password...")
        driver.find_element(By.NAME, "username").send_keys("admin")
        driver.find_element(By.NAME, "password").send_keys("password")
        driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
        print("Submitted login form.")

        time.sleep(2)

        current_url = driver.current_url
        print(f"Redirected to: {current_url}")

        if current_url != "http://localhost:8081/login":
            print(f"✅ Login successful on {browser}")
        else:
            print(f"⚠ Login failed on {browser} (still on login page)")

        driver.quit()
        print("Browser closed.\n")

    except Exception as e:
        print(f"[ERROR] Exception in {browser}:\n{e}\n")

if __name__ == "__main__":
    browser_to_test = sys.argv[1] if len(sys.argv) > 1 else "chrome"

    supported = ["chrome", "firefox", "edge"]
    if browser_to_test == "all":
        for b in supported:
            test_login(b)
    else:
        test_login(browser_to_test)
