package com.kolmanfreecss.domain.model.repository;

import com.kolmanfreecss.domain.model.Score;

import java.util.LinkedHashSet;
import java.util.Set;

public class ScoreRepository {

    private static final Set<Score> scores = new LinkedHashSet<Score>() {
        @Override
        public boolean add(Score score) {
            for (Score s : this) {
                if (s.getUserId() == score.getUserId() && s.getLevelId() == score.getLevelId()) {
                    if (s.getValue() < score.getValue()) {
                        remove(s);
                        break;
                    }
                    return false;
                }
            }
            return super.add(score);
        }
    };

    public void addScore(Score score) {
        scores.add(score);
    }

    /**
     * Obtains the next id for a new score. (Max id + 1)
     */
    public long getNextId() {
        long nextId = 0;
        for (Score score : scores) {
            if (score.getId() > nextId) {
                nextId = score.getId();
            }
        }
        return nextId + 1;
    }

    /**
     * Obtains the scores of a level ordered by value (descending) with max 15 scores.
     */
    public Set<Score> getScoresByLevelId(long levelId) {
        Set<Score> scoresByLevelId = new LinkedHashSet<>();
        int i = 0;
        for (Score score : scores) {
            if (score.getLevelId() == levelId) {
                scoresByLevelId.add(score);
                i++;
                if (i == 15) {
                    break;
                }
            }
        }
        return scoresByLevelId;
    }

}
