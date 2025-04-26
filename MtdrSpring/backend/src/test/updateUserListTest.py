from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Initialize the Firefox WebDriver
driver = webdriver.Firefox()  # Ensure GeckoDriver is installed and in PATH

try:
    # Step 1: Navigate to the KPI dashboard page
    driver.get("http://localhost:8080/kpi-dashboard")

    # Step 2: Wait for the dropdown menu to load
    dropdown_menu = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "filter-dropdown"))
    )
    assert dropdown_menu is not None, "Dropdown menu is not found!"

    # Step 3: Click the dropdown menu to display options
    dropdown_menu.click()

    # Step 4: Verify that the dropdown contains user options
    dropdown_options = WebDriverWait(driver, 10).until(
        EC.presence_of_all_elements_located((By.TAG_NAME, "Oswaldo Rojas"))
    )
    assert len(dropdown_options) > 0, "No options are available in the dropdown menu!"

    # Step 5: Select a specific user (e.g., User ID 1)
    for option in dropdown_options:
        if option.get_attribute("value") == "1": 
            option.click()
            break

    # Step 6: Verify that the KPI data updates for the selected user
    kpi_card = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CLASS_NAME, "kpi-card"))
    )
    assert "Completed on Time" in kpi_card.text, "KPI data is not displayed correctly for the selected user!"

    print("KPI Dropdown Menu Test Passed!")

finally:
    # Close the browser
    driver.quit()