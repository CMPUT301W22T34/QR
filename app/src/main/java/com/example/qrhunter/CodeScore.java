package com.example.qrhunter;
/**
 * @description Handling for QR code scores
 *
 *
 */
public class CodeScore implements Comparable<CodeScore>{
    String code;
    int score;

//    public CodeScore() {}

    /**
     * @description to show the code and score
     *
     *
     */
    public CodeScore(String code, int score) {
        this.code = code;
        this.score = score;
    }
    /**
     * @description set Code
     *
     *
     */
    public void setCode(String code) {
        this.code = code;
    }
    /**
     * @description get score
     *
     *
     */
    public String getCode() {
        return code;
    }
    /**
     * @description set QR code score
     *
     *
     */
    public void setScore(int score) {
        this.score = score;
    }
    /**
     * @description get QR code score
     *
     *
     */
    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(CodeScore o) {
        return this.score - o.getScore();

    }

    @Override
    public String toString() {
        return
                "{code='" + code + '\'' +
                        ", score=" + score +
                        '}';
    }

}
