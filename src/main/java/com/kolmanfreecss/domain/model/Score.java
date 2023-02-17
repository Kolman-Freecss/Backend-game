package com.kolmanfreecss.domain.model;

public class Score implements Comparable<Score> {

    long id;
    long levelId;
    long userId;
    long value;

    public Score() {
    }

    public Score(long id, long levelId, long userId, long score) {
        this.id = id;
        this.levelId = levelId;
        this.userId = userId;
        this.value = score;
    }

    public long getId() {
        return id;
    }

    public long getLevelId() {
        return levelId;
    }

    public long getUserId() {
        return userId;
    }

    public long getValue() {
        return value;
    }

    @Override
    public int compareTo(Score o) {
        return Long.compare(value, o.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return id == score.id && levelId == score.levelId && userId == score.userId && value == score.value;
    }

}
