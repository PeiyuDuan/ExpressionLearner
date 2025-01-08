package com.example.backend.repository;

import com.example.backend.model.UserTaskStage;
import com.example.backend.model.UserTaskStageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTaskStageRepository  extends JpaRepository<UserTaskStage, UserTaskStageId> {
    List<UserTaskStage> findByUserId(Integer userId);

    UserTaskStage findByIdUserIdAndIdTaskId(Integer userId, Integer taskId);
}
