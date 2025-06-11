package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.DeadlineService;
import com.springboot.MyTodoList.service.ToDoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class ToDoItemController {
    @Autowired
    private ToDoItemService toDoItemService;
    @Autowired
    private DeadlineService deadlineService;

    // @CrossOrigin
    @GetMapping(value = "/api/todolist")
    public List<ToDoItem> getAllToDoItems() {
        return toDoItemService.findAll();
    }

    // @CrossOrigin
    @GetMapping(value = "/api/todolist/{id}")
    public ResponseEntity<ToDoItem> getToDoItemById(@PathVariable int id) {
        try {
            ResponseEntity<ToDoItem> responseEntity = toDoItemService.getItemById(id);
            return new ResponseEntity<ToDoItem>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @PostMapping(value = "/api/todolist")
    public ResponseEntity addToDoItem(@RequestBody ToDoItem todoItem) throws Exception {
        ToDoItem td = toDoItemService.addToDoItem(todoItem);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + td.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        // URI location = URI.create(""+td.getID())

        return ResponseEntity.ok()
                .headers(responseHeaders).build();
    }

    // @CrossOrigin
    @PutMapping(value = "/api/todolist/{id}")
    public ResponseEntity updateToDoItem(@RequestBody ToDoItem toDoItem, @PathVariable int id) {
        try {
            ToDoItem toDoItem1 = toDoItemService.updateToDoItem(id, toDoItem);
            System.out.println(toDoItem1.toString());
            return new ResponseEntity<>(toDoItem1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @DeleteMapping(value = "/api/todolist/{id}")
    public ResponseEntity<Boolean> deleteToDoItem(@PathVariable("id") int id) {
        Boolean flag = false;
        try {
            flag = toDoItemService.deleteToDoItem(id);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/api/todolist/upcoming/{days}")
    public ResponseEntity<List<ToDoItem>> getUpcomingDeadlines(@PathVariable int days) {
        List<ToDoItem> upcomingItems = deadlineService.getUpcomingDeadlines(days);
        return new ResponseEntity<>(upcomingItems, HttpStatus.OK);
    }

    @GetMapping(value = "/api/todolist/overdue")
    public ResponseEntity<List<ToDoItem>> getOverdueItems() {
        List<ToDoItem> overdueItems = deadlineService.getOverdueItems();
        return new ResponseEntity<>(overdueItems, HttpStatus.OK);
    }

    @PostMapping(value = "/api/todolist/{id}/deadline")
    public ResponseEntity<ToDoItem> setDeadline(@PathVariable int id, @RequestBody String deadlineStr) {
        ToDoItem updatedItem = deadlineService.setDeadlineFromString(id, deadlineStr);

        if (updatedItem == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/todolist/{id}/deadline")
    public ResponseEntity<ToDoItem> removeDeadline(@PathVariable int id) {
        ToDoItem updatedItem = deadlineService.removeDeadline(id);

        if (updatedItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(updatedItem, HttpStatus.OK);
    }

    @GetMapping("/api/todolist/user/{user_id}")
    public ResponseEntity<List<ToDoItem>> getItemsByUserId(@PathVariable int user_id) {
        List<ToDoItem> userItems = toDoItemService.findByUserId(user_id);

        if (userItems.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Or OK with empty list
        }

        return new ResponseEntity<>(userItems, HttpStatus.OK);
    }

}