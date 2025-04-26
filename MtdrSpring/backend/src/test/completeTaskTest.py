from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Initialize the Chrome WebDriver
driver = webdriver.Chrome()  # Ensure ChromeDriver is installed and in PATH

try:
    # Step 1: Navigate to the task list page
    driver.get("http://localhost:8081/todolist")  # Replace with the actual URL of the task list page

    # Step 2: Locate the task to mark as completed
    task_checkbox = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CLASS_NAME, "taskCheckbox"))  # Replace with the actual class name of the checkbox
    )

    # Step 3: Mark the task as completed
    task_checkbox.click()

    # Step 4: Wait for the task to move to the "Completed Tasks" section
    completed_tasks_section = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "completedTasks"))  # Replace with the actual ID of the completed tasks section
    )

    # Step 5: Verify the task is in the "Completed Tasks" section
    assert "Test Task Description" in completed_tasks_section.text, "Task was not marked as completed successfully!"

    print("Task Mark as Completed Test Passed!")

finally:
    # Close the browser
    driver.quit()