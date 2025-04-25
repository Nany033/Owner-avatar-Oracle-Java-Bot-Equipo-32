package com.springboot.MyTodoList.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.Sprints;

@Repository
@Transactional
@EnableTransactionManagement
public interface  SprintsRepository extends JpaRepository<Sprints, Integer> {
    // Custom query methods can be defined here if needed

}
