package me.vukas.trans;

import javassist.CtClass;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import me.vukas.javassist.DynamicEnumGenerator;

/**
 * @author david
 * @since 2022/2/19
 */
public class DynamicEnumTransformer implements IClassTransformer {
    DynamicEnumGenerator generator = new DynamicEnumGenerator();

    @Override
    public void applyTransformations(CtClass ctClass) throws JavassistBuildException {
        try {
            generator.makeDynamic(ctClass, "./target/classes/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldTransform(CtClass candidateClass) throws JavassistBuildException {
        boolean equals = candidateClass.getName().equals("me.vukas.enumeration.DynamicEnum");
        if (equals) {
            candidateClass.defrost();
        }
        System.out.println(candidateClass.getName() + ":" + equals);
        return equals;
    }
}
