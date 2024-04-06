package arena;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
public @interface ArenaMaker {

	public String[] arenas() default { "NULL" };

	public String arena() default "NULL";

}
