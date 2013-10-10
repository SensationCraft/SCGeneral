package addon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import addon.storage.AbstractStorage;
import addon.storage.GenericStorage;



@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Storage {

	Class<? extends AbstractStorage> storageType() default GenericStorage.class;
	String instantiationField() default "";
	
}
