package me.vukas.enumeration;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class DynamicEnumTest {
    @Test
    public void DemoEnumUsage() {
        System.out.println("==DEMO ENUM TEST ==");
        Assert.assertEquals("[ONE, TWO, THREE]", Arrays.toString(DynamicEnum.values()));
        //add
        DynamicEnum.add("FOUR", 4, "444");
        Assert.assertEquals("444", Enum.valueOf(DynamicEnum.class, "FOUR").getAlternativeName());
        Assert.assertEquals("444", DynamicEnum.valueOf("FOUR").getAlternativeName());
        Assert.assertEquals("[ONE, TWO, THREE, FOUR]", Arrays.toString(DynamicEnum.values()));
        boolean addDup = DynamicEnum.add("FOUR", 14324, "xx");
        Assert.assertFalse(addDup);
        //remove
        DynamicEnum.remove("FOUR");
        Assert.assertEquals("[ONE, TWO, THREE]", Arrays.toString(DynamicEnum.values()));
        boolean removeNosExist = DynamicEnum.remove("FOUR");
        Assert.assertFalse(removeNosExist);
        boolean removeOrigin = DynamicEnum.remove("ONE");
        Assert.assertFalse(removeOrigin);
        //update
        boolean updateNotExist = DynamicEnum.update("FIVE", 1313, "adaff");
        Assert.assertFalse(updateNotExist);
        DynamicEnum.update("ONE", 1, "updated one");
        Assert.assertEquals("updated one", DynamicEnum.valueOf("ONE").getAlternativeName());
        Assert.assertEquals("updated one", Enum.valueOf(DynamicEnum.class, "ONE").getAlternativeName());
        Assert.assertEquals("updated one", DynamicEnum.values()[0].getAlternativeName());
        System.out.println("==DEMO ENUM TEST END ==");
    }

}
