package com.binario.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestCase {
    private String input;

    @JsonProperty("expected_output")
    private String expectedOutput;

    public TestCase() {}

    public TestCase(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }
}
