from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Initialize the Firefox WebDriver
driver = webdriver.Firefox()  # Ensure GeckoDriver is installed and in PATH

try:
    # Step 1: Navigate to the main page
    driver.get("http://localhost:8080/")

    # Step 2: Wait for the team task table to load
    team_task_table = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CLASS_NAME, "todo-list"))
    )
    
    assert team_task_table is not None, "Team task table is not found!"
    
    # Step 3: Verify that the table contains tasks
    rows = team_task_table.find_elements(By.TAG_NAME, "tr")
    assert len(rows) > 1, "No tasks are displayed in the team task table!"

    # Step 4: Verify that each row contains table headers.
    for row in rows[1:]:  # Skip the header row
        columns = row.find_elements(By.TAG_NAME, "th")
        assert len(columns) >= 6, "A task row does not contain all expected columns!"
        assert columns[0].text != "", "Task description is missing!"
        assert columns[1].text != "", "Assigned member is missing!"
        assert columns[2].text in ["Pending", "Done"], "Task status is invalid!"

    print("Team Task Display Test Passed!")

finally:
    # Close the browser
    driver.quit()