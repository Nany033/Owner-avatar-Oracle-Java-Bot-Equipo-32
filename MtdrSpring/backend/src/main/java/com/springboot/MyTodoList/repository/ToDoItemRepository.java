package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
@Transactional
@EnableTransactionManagement
public interface ToDoItemRepository extends JpaRepository<ToDoItem, Integer> {


    @Query("SELECT t FROM ToDoItem t WHERE t.done = false AND t.deadline IS NOT NULL AND t.deadline < :now")
    List<ToDoItem> findOverdueItems(@Param("now") OffsetDateTime now);

    
}
