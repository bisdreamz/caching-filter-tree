package org.nimbus.demo;

import java.util.*;

public class TestInput {

    public static enum Const {
        ONE,
        TWO,
        THREE,
        FOUR
    }

    private final String stringValue;
    private final int intValue;
    private final int rangeValueMin;
    private final int rangeValueMax;
    private final List<Const> listVal;

    private TestInput(String stringValue, int intValue, int rangeValueMin, int rangeValueMax, List<Const> listVal) {
        this.stringValue = stringValue;
        this.intValue = intValue;
        this.rangeValueMin = rangeValueMin;
        this.rangeValueMax = rangeValueMax;
        this.listVal = listVal;
    }

    public static TestInput sampleOne() {
        return new TestInput("myString", 10, 5, 10,  List.of(Const.ONE, Const.FOUR));
    }

    public static TestInput sampleTwo() {
        return new TestInput("otherString", 20, 100, 200, List.of(Const.THREE));
    }

    public static TestInput sampleRandomPool() {
        List<String> strs = List.of("one", "two", "three", "four", "five");
        List<Integer> ints = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Const> consts = List.of(Const.ONE, Const.TWO, Const.THREE, Const.FOUR);

        Random random = new Random();

        String randomStr = strs.get(random.nextInt(strs.size()));
        int randomInt1 = ints.get(random.nextInt(ints.size()));
        int randomInt2 = ints.get(random.nextInt(ints.size()));

        List<Const> randomConsts = new ArrayList<>();
        int numConsts = random.nextInt(2) + 1; // Gets either 1 or 2

        // Shuffle and take first 1 or 2 elements
        List<Const> shuffledConsts = new ArrayList<>(consts);
        Collections.shuffle(shuffledConsts);
        randomConsts = shuffledConsts.subList(0, numConsts);

        return new TestInput(randomStr, randomInt1, randomInt2, randomInt2 * 4, randomConsts);
    }

    public String getStringValue() {
        return stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public int getRangeValueMin() {
        return rangeValueMin;
    }

    public int getRangeValueMax() {
        return rangeValueMax;
    }

    public List<Const> getListVal() {
        return listVal;
    }
}
