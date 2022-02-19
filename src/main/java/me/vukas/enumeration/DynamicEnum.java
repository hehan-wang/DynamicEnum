package me.vukas.enumeration;

public enum DynamicEnum {
    ONE(1, "one"),
    TWO(2, "two"),
    THREE(3, "three");

    private int id;
    private String alternativeName;

    DynamicEnum(int id) {
        this(id, null);
    }

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

    public static boolean add(String value, int id) {
        throw new IllegalStateException();
    }

    //must have this single method per enum. allows removal of dynamic enum in runtime
    //body will be replaced in the compile time
    public static boolean remove(String value) {
        throw new IllegalStateException();
    }
}
