package org.nimbus.demo;

public class TestOutput {

    private final String stringValue;
    private final int intValue;
    private final int rangeValue;
    private final TestInput.Const constValue;

    public TestOutput(String stringValue, int intValue, int rangeValue, TestInput.Const constValue) {
        this.stringValue = stringValue;
        this.intValue = intValue;
        this.rangeValue = rangeValue;
        this.constValue = constValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public int getRangeValue() {
        return rangeValue;
    }

    public TestInput.Const getConstValue() {
        return constValue;
    }
}
