package com.david.javassist;

import javassist.*;
import com.david.utils.DynamicEnumUtils;

import java.io.IOException;

/**
 * use javassit to manipulate add/remove/update methods of enum.
 *
 * @author david
 * @since 2022/2/21
 */
public class DynamicEnumGenerator {
    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
        DynamicEnumGenerator generator = new DynamicEnumGenerator();
        generator.makeDynamic("com.david.enumeration.DynamicEnum", "./target/classes/");
    }

    public CtClass makeDynamic(String className, String targetDirectory) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(className);
        return makeDynamic(ctClass, targetDirectory, classPool, className);
    }

    public CtClass makeDynamic(CtClass ctClass, String targetDirectory) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        String className = ctClass.getName();
        return makeDynamic(ctClass, targetDirectory, classPool, className);
    }

    private CtClass makeDynamic(CtClass ctClass, String targetDirectory, ClassPool classPool, String className) throws NotFoundException, CannotCompileException, IOException {
        int hardcodedEnumsCount = 0;
        for (CtField ctField : ctClass.getFields()) {
            if (ctField.getType().equals(ctClass)) {
                hardcodedEnumsCount++;
            }
        }

        CtField ctSizeField = CtField.make(String.format("private final static int $SIZE = %d;", hardcodedEnumsCount), ctClass);
        ctClass.addField(ctSizeField);
        CtField ctConstructorsField = CtField.make("private static java.util.Map $CONSTRUCTORS = new java.util.concurrent.ConcurrentHashMap();", ctClass);
        ctClass.addField(ctConstructorsField);

        int constructorsCount = 0;
        CtClass ctEmbeddedClass = ctClass.makeNestedClass("Embedded", true);
        CtField ctCallableField = CtField.make("public java.util.concurrent.Callable kon;", ctEmbeddedClass);
        ctEmbeddedClass.addField(ctCallableField);
        CtConstructor ctc12 = new CtConstructor(new CtClass[]{classPool.get("java.util.concurrent.Callable")}, ctEmbeddedClass);
        ctc12.setBody("{ this.kon = $1; }");
        ctEmbeddedClass.addConstructor(ctc12);
        ctEmbeddedClass.toClass();
        ctEmbeddedClass.writeFile(targetDirectory);
        ctEmbeddedClass.defrost();

        CtClass ctBasicConstructorClass = ctClass.makeNestedClass("BasicConstructor", true);
        CtConstructor ctConstructor = new CtConstructor(null, ctBasicConstructorClass);
        ctConstructor.setBody("{}");
        ctBasicConstructorClass.addConstructor(ctConstructor);
        CtField ctOrdinalField = new CtField(CtPrimitiveType.intType, "ordinal", ctBasicConstructorClass);
        ctOrdinalField.setModifiers(Modifier.PUBLIC);
        ctBasicConstructorClass.addField(ctOrdinalField);
        ctBasicConstructorClass.toClass();
        ctBasicConstructorClass.writeFile(targetDirectory);
        ctBasicConstructorClass.defrost();
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (method.getName().equals("add")
                    && method.getReturnType().getName().equals("boolean")
                    && Modifier.isStatic(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length > 0
                    && method.getParameterTypes()[0].getName().equals("java.lang.String")
            ) {
                CtClass constructorClass = ctClass.makeNestedClass("Constructor" + constructorsCount, true);
                constructorClass.setSuperclass(ctBasicConstructorClass);
                CtClass ctCallableClass = classPool.get("java.util.concurrent.Callable");
                constructorClass.addInterface(ctCallableClass);
                constructorClass.addField(new CtField(classPool.get("java.lang.String"), "name", constructorClass));
                CtClass[] constructorTypes = new CtClass[method.getParameterTypes().length + 1];

                constructorTypes[0] = classPool.get("java.lang.String");
                constructorTypes[1] = CtPrimitiveType.intType;

                StringBuilder constructorBody = new StringBuilder();
                constructorBody.append("{ this.name=$1; this.ordinal=$2; ");

                for (int i1 = 2; i1 <= method.getParameterTypes().length; i1++) {
                    constructorTypes[i1] = method.getParameterTypes()[i1 - 1];
                    constructorClass.addField(new CtField(constructorTypes[i1], "param" + i1, constructorClass));
                    constructorBody.append("this.param");
                    constructorBody.append(i1);
                    constructorBody.append("=$");
                    constructorBody.append(i1 + 1);
                    constructorBody.append("; ");
                }
                constructorBody.append("} ");

                CtConstructor constructor = new CtConstructor(constructorTypes, constructorClass);
                constructor.setBody(constructorBody.toString());
                constructorClass.addConstructor(constructor);

                CtClass objectClass = classPool.getCtClass("java.lang.Object");

                CtMethod callMethod = new CtMethod(objectClass, "call", null, constructorClass);
                callMethod.setExceptionTypes(new CtClass[]{classPool.getCtClass("java.lang.Exception")});

                StringBuilder callableBody = new StringBuilder();
                callableBody.append("{return new ");
                callableBody.append(className);
                callableBody.append("(name,ordinal");

                for (int i1 = 2; i1 <= method.getParameterTypes().length; i1++) {
                    callableBody.append(",param");
                    callableBody.append(i1);
                }
                callableBody.append(");}");

                callMethod.setBody(callableBody.toString());
                constructorClass.addMethod(callMethod);

                constructorClass.toClass();
                constructorClass.writeFile(targetDirectory);
                constructorClass.defrost();
                StringBuilder methodBody = new StringBuilder("{ for(int i=0; i<$VALUES.length; i++){ if($VALUES[i].name().equals($1)){ return false; }} int currentNumOfEnums = $VALUES.length; %1$s[] temp = new %1$s[currentNumOfEnums + 1]; System.arraycopy($VALUES, 0, temp, 0, currentNumOfEnums); $CONSTRUCTORS.put($1, new ");
                methodBody.append(className);
                methodBody.append(".Embedded(new ");
                methodBody.append(className);
                methodBody.append(".Constructor");
                methodBody.append(constructorsCount);
                methodBody.append("($1, currentNumOfEnums");

                for (int i = 2; i <= method.getParameterTypes().length; i++) {
                    methodBody.append(", $");
                    methodBody.append(i);
                }

                methodBody.append("))); try { temp[currentNumOfEnums] = ((");
                methodBody.append(className);
                methodBody.append(".Constructor");
                methodBody.append(constructorsCount);
                methodBody.append(")((");
                methodBody.append(className);
                methodBody.append(".Embedded)$CONSTRUCTORS.get($1)).kon).call(); } catch (Exception e) { e.printStackTrace(); } $VALUES = temp; return true;}");

                method.setBody(String.format(methodBody.toString(), className));
                insertClearEnumCache(className, method);

//                constructorsCount++;
            }

            if (method.getName().equals("remove")
                    && method.getReturnType().getName().equals("boolean")
                    && Modifier.isStatic(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length > 0
                    && method.getParameterTypes()[0].getName().equals("java.lang.String")
            ) {
                method.setBody(String.format("{ for(int i=0; i<$VALUES.length; i++){ if($VALUES[i].name().equals($1)){ if(i < %1$s.$SIZE){ return false; } int currentNumOfEnums = $VALUES.length; %1$s[] temp = new %1$s[currentNumOfEnums - 1]; System.arraycopy($VALUES, 0, temp, 0, i); System.arraycopy($VALUES, i+1, temp, i, currentNumOfEnums-i-1); $CONSTRUCTORS.remove($VALUES[i].name()); for(int j=i; j<currentNumOfEnums-1; j++){ try { ((" + className + ".BasicConstructor)((" + className + ".Embedded)$CONSTRUCTORS.get(temp[j].name())).kon).ordinal = j; temp[j] = (((" + className + ".Embedded)$CONSTRUCTORS.get(temp[j].name())).kon).call(); } catch (Exception e) { e.printStackTrace(); } } $VALUES = temp; return true; } } return false; }", className));
                insertClearEnumCache(className, method);
            }
        }
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (method.getName().equals("update")
                    && method.getReturnType().getName().equals("boolean")
                    && Modifier.isStatic(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length > 0
                    && method.getParameterTypes()[0].getName().equals("java.lang.String")
            ) {
                StringBuilder methodBody = new StringBuilder("{ int currentNumOfEnums =-1; for(int i=0; i<$VALUES.length; i++){ if($VALUES[i].name().equals($1)){ currentNumOfEnums=i;break; }} if(currentNumOfEnums<0)return false;  $CONSTRUCTORS.put($1, new ");
                methodBody.append(className);
                methodBody.append(".Embedded(new ");
                methodBody.append(className);
                methodBody.append(".Constructor");
                methodBody.append(constructorsCount);
                methodBody.append("($1, currentNumOfEnums");

                for (int i = 2; i <= method.getParameterTypes().length; i++) {
                    methodBody.append(", $");
                    methodBody.append(i);
                }

                methodBody.append("))); try { $VALUES[currentNumOfEnums] = ((");
                methodBody.append(className);
                methodBody.append(".Constructor");
                methodBody.append(constructorsCount);
                methodBody.append(")((");
                methodBody.append(className);
                methodBody.append(".Embedded)$CONSTRUCTORS.get($1)).kon).call(); } catch (Exception e) { e.printStackTrace(); } ");

                String utilClassName = DynamicEnumUtils.class.getName();
                methodBody.append(String.format("%s.setFailsafeFieldValue(%s.lookupField(%s.class, $1), %s.class, $VALUES[currentNumOfEnums]);",
                        utilClassName, utilClassName, className, className));
                methodBody.append("return true;}");
                method.setBody(String.format(methodBody.toString(), className));
                insertClearEnumCache(className, method);
            }
        }
        ctClass.writeFile(targetDirectory);
        ctClass.defrost();
        return ctClass;
    }

    private void insertClearEnumCache(String className, CtMethod method) throws CannotCompileException {
        method.insertAfter(String.format("%s.cleanEnumCache(%s.class);", DynamicEnumUtils.class.getName(), className));
    }
}
