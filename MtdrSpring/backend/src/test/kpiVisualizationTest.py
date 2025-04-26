from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Initialize the Chrome WebDriver
driver = webdriver.Firefox()  # Ensure ChromeDriver is installed and in PATH

try:
    # Step 1: Navigate to the KPI dashboard page
    driver.get("http://localhost:8080/kpi-dashboard")  # Replace with the actual URL of the KPI dashboard page

    # Step 2: Select a user from the dropdown
    user_dropdown = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "userDropdown"))  # Replace with the actual ID of the user dropdown
    )
    user_dropdown.click()

    # Step 3: Select a specific user (e.g., User ID 1)
    user_option = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.XPATH, "//option[@value='1']"))  # Replace with the actual value for the user
    )
    user_option.click()

    # Step 4: Wait for the KPI data to load
    kpi_card = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CLASS_NAME, "kpi-card"))  # Replace with the actual class name of the KPI card
    )

    # Step 5: Verify the KPI data is displayed
    assert "Completed on Time" in kpi_card.text, "KPI data is not displayed correctly!"
    assert "Completed Late" in kpi_card.text, "KPI data is not displayed correctly!"
    assert "Total tasks" in kpi_card.text, "KPI data is not displayed correctly!"

    print("KPI Visualization Test Passed!")

finally:
    # Close the browser
    driver.quit()