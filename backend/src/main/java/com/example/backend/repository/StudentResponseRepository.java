package com.example.backend.repository;

import com.example.backend.model.StudentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentResponseRepository extends JpaRepository<StudentResponse, Integer> {
    List<StudentResponse> findByStudentIdAndTaskId(Integer userId, Integer taskId);
}
