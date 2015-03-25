package play.db.jpa;

@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE,java.lang.annotation.ElementType.METHOD,java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
public abstract @interface ReadSlave{
  
  // Method descriptor #5 ()Ljava/lang/String;
  public abstract java.lang.String name() default "default";
}