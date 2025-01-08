package com.example.backend.service.impl;

import com.example.backend.model.Task;
import com.example.backend.model.UserTaskStage;
import com.example.backend.repository.UserTaskStageRepository;
import com.example.backend.service.UserTaskStageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserTaskStageServiceImpl implements UserTaskStageService {
    private final UserTaskStageRepository userTaskStageRepository;

    @Autowired
    public UserTaskStageServiceImpl(UserTaskStageRepository userTaskStageRepository) {
        this.userTaskStageRepository = userTaskStageRepository;
    }

    @Override
    public Integer getStage(Integer userId, Integer taskId) {
        UserTaskStage result = userTaskStageRepository.findByIdUserIdAndIdTaskId(userId, taskId);
        return result == null ? 0 : result.getStage();
    }

    @Override
    public List<Task> getTasks(Integer userId) {
        List<UserTaskStage> result = userTaskStageRepository.findByUserId(userId);
        List<Task> t = new ArrayList<>();
        for (UserTaskStage uts : result) {
            t.add(uts.getTask());
        }
        return t;
    }

    @Override
    @Transactional
    public void updateStage(Integer userId, Integer taskId) {
        UserTaskStage uts = userTaskStageRepository.findByIdUserIdAndIdTaskId(userId, taskId);

        if (uts == null) {
            // 使用新的构造函数创建对象，直接设置复合主键
            uts = new UserTaskStage(userId, taskId);
        }

        Integer stage = determineNextStage(uts);
        uts.setStage(stage);
        uts.setUpdateTime(Instant.now()); // 设置更新时间

        userTaskStageRepository.save(uts);
    }

    private Integer determineNextStage(UserTaskStage uts) {
        Integer stage = uts.getStage();

        if (stage == 0) {
            return 1;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextReviewTime = calculateNextReviewTime(
                LocalDateTime.ofInstant(uts.getUpdateTime(), ZoneId.systemDefault()), uts.getStage()
        );
        LocalDateTime midTime = now.plusSeconds(
                Duration.between(now, nextReviewTime).toSeconds() * 3 / 4
        );

        // 如果当前时间在中间点之后，则更新 stage
        if (!now.isBefore(midTime)) {
            return stage + 1;
        }

        return stage;
    }


    @Override
    @Transactional
    public void updateStage(Integer userId, Integer taskId, Integer stage) {
        UserTaskStage uts = userTaskStageRepository.findByIdUserIdAndIdTaskId(userId, taskId);

        if (uts == null) {
            // 使用新的构造函数创建对象，直接设置复合主键
            uts = new UserTaskStage(userId, taskId);
        }

        uts.setStage(stage);
        // 设置更新时间
        uts.setUpdateTime(Instant.now());

        userTaskStageRepository.save(uts);
    }

    @Override
    public List<Task> getUserReviewTasks(Integer userId) {
        List<UserTaskStage> userTasks = userTaskStageRepository.findByUserId(userId);
        LocalDateTime now = LocalDateTime.now();

        List<Task> pendingReviews = new ArrayList<>();
        for (UserTaskStage uts : userTasks) {
            LocalDateTime nextReviewTime = calculateNextReviewTime(
                    LocalDateTime.ofInstant(uts.getUpdateTime(), ZoneId.systemDefault()), uts.getStage()
            );
            if (now.isAfter(nextReviewTime)) {
                pendingReviews.add(uts.getTask());
            }
        }

        return pendingReviews;
    }

    private LocalDateTime calculateNextReviewTime(LocalDateTime lastUpdateTime, int stage) {
        int[] intervals = {1, 1, 2, 3, 8, 15};
        int interval = stage < intervals.length ? intervals[stage - 1] : intervals[intervals.length - 1];
        return lastUpdateTime.plusDays(interval);
    }

}
