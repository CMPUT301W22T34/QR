package com.example.qrhunter;

public class CodeScore {
    String code;
    int score;

//    public CodeScore() {}

    public CodeScore(String code, int score) {
        this.code = code;
        this.score = score;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}