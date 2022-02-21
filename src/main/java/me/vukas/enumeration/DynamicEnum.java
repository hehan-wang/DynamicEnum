package me.vukas.enumeration;

import me.vukas.anno.DEnum;

/**
 * demo enum.
 *
 * @author david
 * @since 2022/2/21
 */
@DEnum
public enum DynamicEnum {
    ONE(1, "one"),
    TWO(2, "two"),
    THREE(3, "three");

    private int id;
    private String alternativeName;

    //must have all args constructor
    DynamicEnum(int id, String alternativeName) {
        this.id = id;
        this.alternativeName = alternativeName;
    }

    public int getId() {
        return id;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    //must have methods per constructor that will be used in runtime.
    //first parameter is name of the enum that wants to be added
    //body will be replaced in the compile time
    public static boolean add(String value, int id, String alternativeName) {
        throw new IllegalStateException();
    }

    //must have methods per constructor that will be used in runtime.
    //first parameter is name of the enum that wants to be added
    //body will be replaced in the compile time
    public static boolean update(String value, int id, String alternativeName) {
        throw new IllegalStateException();
    }

    //must have this single method per enum. allows removal of dynamic enum in runtime
    //body will be replaced in the compile time
    public static boolean remove(String value) {
        throw new IllegalStateException();
    }
}
