package com.kolmanfreecss.application.service;

import com.kolmanfreecss.domain.model.Score;
import com.kolmanfreecss.domain.model.repository.ScoreRepository;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ScoreService {

    private final ScoreRepository scoreRepository;

    public ScoreService() {
        scoreRepository = new ScoreRepository();
    }

    /**
     * Saves a score for a level and a user
     */
    public void saveScore(long levelId, long scoreValue, long userId) {
        long nextId = scoreRepository.getNextId();
        Score score = new Score(nextId, levelId, userId, scoreValue);
        scoreRepository.addScore(score);
    }

    /**
     * Returns a string in the format: userId=score,userId=score,userId=score
     */
    public String getHighScoreList(long levelId) {
        StringBuilder sb = new StringBuilder();
        Set<Score> scores = scoreRepository.getScoresByLevelId(levelId);
        AtomicInteger i = new AtomicInteger();
        scores.stream().forEach(score -> {
            sb.append(score.getUserId()).append("=").append(score.getValue());
            if (i.get() < scores.size() - 1) {
                sb.append(",");
            }
            i.getAndIncrement();
        });
        return sb.toString();
    }

}
