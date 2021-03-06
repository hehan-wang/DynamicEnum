package com.david.trans;

import com.david.javassist.DynamicEnumGenerator;
import javassist.CtClass;
import javassist.build.IClassTransformer;
import javassist.build.JavassistBuildException;
import com.david.anno.DEnum;

/**
 * compile class with {@link DEnum} in maven compile stage.
 *
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
        boolean check = false;
        check = candidateClass.hasAnnotation(DEnum.class);
        System.out.println(candidateClass.getName() + ":" + check);
        return check;
    }

}
