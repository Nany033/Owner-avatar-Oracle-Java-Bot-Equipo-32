from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Initialize the Chrome WebDriver
driver = webdriver.Firefox()  # Ensure ChromeDriver is installed and in PATH

try:
    # Step 1: Navigate to the team KPI dashboard page
    driver.get("http://localhost:8080/team-kpi-dashboard")  # Replace with the actual URL of the team KPI dashboard page

    # Step 2: Wait for the KPI cards to load
    kpi_cards = WebDriverWait(driver, 10).until(
        EC.presence_of_all_elements_located((By.CLASS_NAME, "kpi-card"))  # Replace with the actual class name of the KPI cards
    )

    # Step 3: Verify that KPI data is displayed for each team member
    for card in kpi_cards:
        assert "Completed on Time" in card.text, "KPI data is missing 'Completed on Time'!"
        assert "Completed Late" in card.text, "KPI data is missing 'Completed Late'!"
        assert "Total tasks" in card.text, "KPI data is missing 'Total tasks'!"

    print("Team KPI Visualization Test Passed!")

finally:
    # Close the browser
    driver.quit()