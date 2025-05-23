package com.binario.model;

import java.util.List;

public class ChoiceAnswer {
    private List<String> correct;

    public ChoiceAnswer() {}

    public ChoiceAnswer(List<String> correct) {
        this.correct = correct;
    }

    public List<String> getCorrect() {
        return correct;
    }

    public void setCorrect(List<String> correct) {
        this.correct = correct;
    }
}
