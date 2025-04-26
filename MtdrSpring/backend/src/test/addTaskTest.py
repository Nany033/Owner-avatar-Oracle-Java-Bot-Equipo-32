from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

# Initialize the Chrome WebDriver
driver = webdriver.Chrome()  # Ensure ChromeDriver is installed and in PATH

try:
    # Step 1: Navigate to the task creation page
    driver.get("http://localhost:8081/todolist")  #Replace when deployed

    # Step 2: Locate the input fields for task description and deadline
    task_description_input = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "newiteminput"))  # Replace with the actual ID of the description input
    )
    task_deadline_input = driver.find_element(By.ID, "deadlineinput")  # Replace with the actual ID of the deadline input

    # Step 3: Enter task details
    task_description_input.send_keys("Test Task Description")
    task_deadline_input.send_keys("2025-04-30")  # Example deadline

    # Step 4: Click the "Add" button
    add_button = driver.find_element(By.CLASS_NAME, "AddButton")  # Replace with the actual class name of the button
    add_button.click()

    # Step 5: Wait for the task to appear in the task list
    task_list = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "itemlistNotDone"))  # Replace with the actual ID of the task list
    )

    # Step 6: Verify the task is added
    assert "Test Task Description" in task_list.text, "Task was not added successfully!"

    print("Task Creation Test Passed!")

finally:
    # Close the browser
    driver.quit()