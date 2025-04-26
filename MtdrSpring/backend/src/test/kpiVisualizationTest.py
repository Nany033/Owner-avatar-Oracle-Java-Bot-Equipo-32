from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

# Initialize the Chrome WebDriver
driver = webdriver.Chrome()  # Ensure ChromeDriver is installed and in PATH

try:
    # Step 1: Navigate to the dashboard or page containing the PieChart
    driver.get("http://localhost:8081/dashboard")  # Replace with the actual URL of the dashboard page

    # Step 2: Wait for the PieChart to load
    pie_chart = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CLASS_NAME, "recharts-pie"))  # Replace with the actual class name of the PieChart
    )

    # Step 3: Hover over a segment of the PieChart
    segment = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CLASS_NAME, "recharts-sector"))  # Replace with the actual class name of a PieChart segment
    )
    webdriver.ActionChains(driver).move_to_element(segment).perform()

    # Step 4: Verify the tooltip appears with the correct data
    tooltip = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CLASS_NAME, "recharts-tooltip-wrapper"))  # Replace with the actual class name of the tooltip
    )
    assert "PV" in tooltip.text, "Tooltip does not contain expected data!"
    assert "Rate" in tooltip.text, "Tooltip does not contain expected percentage!"

    print("Data Visualization Test Passed!")

finally:
    # Close the browser
    driver.quit()