package me.vukas.enumeration;

import org.junit.Test;

import java.util.Arrays;

public class DynamicEnumTest {
    @Test
    public void DemoEnumUsage1() {
        System.out.println("==DEMO ENUM TEST1 ==");
        System.out.println(Arrays.toString(DynamicEnum.values()));
        DynamicEnum.add("FOUR", 4, "444");
        System.out.println(Enum.valueOf(DynamicEnum.class, "FOUR").getAlternativeName());
        System.out.println(Arrays.toString(DynamicEnum.values()));
        DynamicEnum.remove("FOUR");
        System.out.println(Enum.valueOf(DynamicEnum.class, "FOUR").getAlternativeName());
        System.out.println(Arrays.toString(DynamicEnum.values()));
        DynamicEnum.update("ONE", -1, "-111");
        System.out.println(Enum.valueOf(DynamicEnum.class, "ONE").getAlternativeName());
        System.out.println(DynamicEnum.ONE.getId());
        System.out.println(DynamicEnum.values()[0].getAlternativeName());
    }

    @Test
    public void DemoEnumUsage() {
        System.out.println("==DEMO ENUM TEST ==");
        for (DynamicEnum dynamicEnum : DynamicEnum.values()) {
            System.out.println("Name: " + dynamicEnum.name() + "; Id:" + dynamicEnum.getId() + "; AlternativeName: " + dynamicEnum.getAlternativeName() + "; Ordinal: " + dynamicEnum.ordinal());
        }

        System.out.println("Adding 4,5,6 in runtime...");

//        DynamicEnum.add("FOUR", 4);
        DynamicEnum.add("FIVE", 5, "five");
        DynamicEnum.add("SIX", 6, "six");

        System.out.println("Values after update:");
        for (DynamicEnum dynamicEnum : DynamicEnum.values()) {
            System.out.println("Name: " + dynamicEnum.name() + "; Id:" + dynamicEnum.getId() + "; AlternativeName: " + dynamicEnum.getAlternativeName() + "; Ordinal: " + dynamicEnum.ordinal());
        }

        System.out.println("Deleting 5...");

        DynamicEnum.remove("FIVE");

        System.out.println("Values after removal of enum 5 (Six got new ordinal):");
        for (DynamicEnum dynamicEnum : DynamicEnum.values()) {
            System.out.println("Name: " + dynamicEnum.name() + "; Id:" + dynamicEnum.getId() + "; AlternativeName: " + dynamicEnum.getAlternativeName() + "; Ordinal: " + dynamicEnum.ordinal());
        }

        System.out.println("Readding 5 and adding 10...");

//        DynamicEnum.add("FIVE", 5);
        DynamicEnum.add("TEN", 10, "ten");

        System.out.println("Values after update:");
        for (DynamicEnum dynamicEnum : DynamicEnum.values()) {
            System.out.println("Name: " + dynamicEnum.name() + "; Id:" + dynamicEnum.getId() + "; AlternativeName: " + dynamicEnum.getAlternativeName() + "; Ordinal: " + dynamicEnum.ordinal());
        }

        System.out.println("Trying to remove 1 (cannot because it is hardcoded) and remove 6...");

        DynamicEnum.remove("ONE");
        DynamicEnum.remove("SIX");

        System.out.println("Values after update:");
        for (DynamicEnum dynamicEnum : DynamicEnum.values()) {
            System.out.println("Name: " + dynamicEnum.name() + "; Id:" + dynamicEnum.getId() + "; AlternativeName: " + dynamicEnum.getAlternativeName() + "; Ordinal: " + dynamicEnum.ordinal());
        }
    }

}
