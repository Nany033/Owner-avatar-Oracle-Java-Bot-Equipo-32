package com.springboot.MyTodoList.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.ToDoItem;

@Repository
@Transactional
@EnableTransactionManagement
public interface ToDoItemRepository extends JpaRepository<ToDoItem, Integer> {

    @Query("SELECT t FROM ToDoItem t WHERE t.done = false AND t.deadline IS NOT NULL AND t.deadline < :now")
    List<ToDoItem> findOverdueItems(@Param("now") OffsetDateTime now);

    @Query("SELECT t.sprint_id FROM ToDoItem t WHERE t.ID = :todoItemId")
    Optional<Integer> findSprintIdById(@Param("todoItemId") int todoItemId);

    @Query("SELECT t.done FROM ToDoItem t WHERE t.ID = :todoItemId")
    Optional<Boolean> isDone(@Param("todoItemId") int todoItemId);

    @Query("SELECT t FROM ToDoItem t WHERE t.done = false ORDER BY t.creation_ts DESC")
    List<ToDoItem> findAllActiveItems();

    @Query("SELECT t FROM ToDoItem t WHERE t.done = true ORDER BY t.creation_ts DESC")
    List<ToDoItem> findAllCompletedItems();

    @Query("SELECT t FROM ToDoItem t WHERE t.done = false AND t.deadline BETWEEN :startDate AND :endDate ORDER BY t.deadline ASC")
    List<ToDoItem> findUpcomingDeadlines(@Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT t FROM ToDoItem t WHERE t.user_id = :user_id")
    List<ToDoItem> findByUserId(@Param("user_id") Integer user_id);
}