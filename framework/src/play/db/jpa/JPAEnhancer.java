package play.db.jpa;

import play.Logger;
import play.db.Configuration;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.*;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;

import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
/**
 * Enhance JPABase entities classes
 */
public class JPAEnhancer extends Enhancer {

    public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {
        CtClass ctClass = makeClass(applicationClass);

        if (!ctClass.subtypeOf(classPool.get("play.db.jpa.JPABase"))) {
            return;
        }

        // Enhance only JPA entities
        if (!hasAnnotation(ctClass, "javax.persistence.Entity")) {
            return;
        }

        AnnotationsAttribute annotation = getAnnotations(ctClass);
        String dbName = JPA.DEFAULT;
        String dbNameRead = JPA.DEFAULT;
        if (annotation != null) {
            Annotation an = annotation.getAnnotation("javax.persistence.PersistenceUnit");
            if (an != null) {
                dbName = ((StringMemberValue)an.getMemberValue("name")).getValue();
            }
            Annotation an2 = annotation.getAnnotation("play.db.jpa.ReadSlave");
            if (an2 != null) {
                String tempDB = ((StringMemberValue)an2.getMemberValue("name")).getValue();

                dbNameRead = JPA.checkDBExists(tempDB) ? tempDB : JPA.DEFAULT;

                if(dbNameRead.equals(JPA.DEFAULT)){
                    Logger.debug("Database : %s not found in configuration", tempDB);
                }
            }
        }
      
        String entityName = ctClass.getName();

        // count
        CtMethod count = CtMethod.make("public static long count() { return play.db.jpa.JPQL.instance.count(\"" + dbNameRead + "\", \"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(count);

        // count2
        CtMethod count2 = CtMethod.make("public static long count(String query, Object[] params) { return play.db.jpa.JPQL.instance.count(\"" + dbNameRead + "\", \"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(count2);

        // findAll
        CtMethod findAll = CtMethod.make("public static java.util.List findAll() { return play.db.jpa.JPQL.instance.findAll(\"" + dbNameRead + "\", \"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(findAll);

        // findById
        CtMethod findById = CtMethod.make("public static play.db.jpa.JPABase findById(Object id) { return play.db.jpa.JPQL.instance.findById(\"" + dbNameRead + "\",\"" + entityName + "\", id); }", ctClass);
        ctClass.addMethod(findById);

        // find
        CtMethod find = CtMethod.make("public static play.db.jpa.GenericModel.JPAQuery find(String query, Object[] params) { return play.db.jpa.JPQL.instance.find(\"" + dbNameRead + "\", \"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(find);

        // find
        CtMethod find2 = CtMethod.make("public static play.db.jpa.GenericModel.JPAQuery find() { return play.db.jpa.JPQL.instance.find(\"" + dbNameRead + "\", \"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(find2);

        // TunnelBear: findAllTB
        CtMethod findAllTB = CtMethod.make("public static java.util.List findAllTB(String query, Object[] params) { return play.db.jpa.JPQL.instance.findAllTB(\"" + dbNameRead + "\",\"" + dbName + "\", \"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(findAllTB);

        // TunnelBear: firstTB
        CtMethod firstTB = CtMethod.make("public static play.db.jpa.JPABase firstTB(String query, Object[] params) { return play.db.jpa.JPQL.instance.firstTB(\"" + dbNameRead + "\",\"" + dbName + "\", \"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(firstTB);

        // all
        CtMethod all = CtMethod.make("public static play.db.jpa.GenericModel.JPAQuery all() { return play.db.jpa.JPQL.instance.all(\"" + dbNameRead + "\", \"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(all);

        // delete
        CtMethod delete = CtMethod.make("public static int delete(String query, Object[] params) { return play.db.jpa.JPQL.instance.delete(\"" + dbName + "\", \"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(delete);

        // deleteAll
        CtMethod deleteAll = CtMethod.make("public static int deleteAll() { return play.db.jpa.JPQL.instance.deleteAll(\"" + dbName + "\", \"" + entityName + "\"); }", ctClass);
        ctClass.addMethod(deleteAll);

        // findOneBy
        CtMethod findOneBy = CtMethod.make("public static play.db.jpa.JPABase findOneBy(String query, Object[] params) { return play.db.jpa.JPQL.instance.findOneBy(\"" + dbNameRead + "\", \"" + entityName + "\", query, params); }", ctClass);
        ctClass.addMethod(findOneBy);

        // create
        CtMethod create = CtMethod.make("public static play.db.jpa.JPABase create(String name, play.mvc.Scope.Params params) { return play.db.jpa.JPQL.instance.create(\"" + dbName + "\", \"" + entityName + "\", name, params); }", ctClass);
        ctClass.addMethod(create);

        // Done.
        applicationClass.enhancedByteCode = ctClass.toBytecode();
        ctClass.defrost();
    }

}
