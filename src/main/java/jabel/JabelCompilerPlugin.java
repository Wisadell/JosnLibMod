package jabel;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Source.Feature;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;

/**
 * Makes users able to use Java 9+ syntactic-sugars while still targeting Java 8.
 *
 * @author Eipusino
 */
public class JabelCompilerPlugin implements Plugin {
    static {
        try {
            Field field = Lookup.class.getDeclaredField("IMPL_LOOKUP");
            field.setAccessible(true);
            // Get the trusted private lookup.
            Lookup lookup = (Lookup) field.get(null);
            // Get the minimum level setter, to force certain features to qualify as a Java 8 feature.
            MethodHandle set = lookup.findSetter(Feature.class, "minLevel", Source.class);

            // Downgrade most Java 8-compatible features.
            for (Feature feature : new Feature[]{
		            Feature.EFFECTIVELY_FINAL_VARIABLES_IN_TRY_WITH_RESOURCES,
		            Feature.PRIVATE_SAFE_VARARGS,
		            Feature.DIAMOND_WITH_ANONYMOUS_CLASS_CREATION,
		            Feature.LOCAL_VARIABLE_TYPE_INFERENCE,
		            Feature.VAR_SYNTAX_IMPLICIT_LAMBDAS,
		            Feature.SWITCH_MULTIPLE_CASE_LABELS,
		            Feature.SWITCH_RULE,
		            Feature.SWITCH_EXPRESSION,
		            Feature.TEXT_BLOCKS,
		            Feature.PATTERN_MATCHING_IN_INSTANCEOF,
		            Feature.REIFIABLE_TYPES_INSTANCEOF
            }) set.invokeExact(feature, Source.JDK8);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(JavacTask task, String... args) {}

    @Override
    public String getName() {
        return "jabel";
    }

    // Make it auto start on Java 14+
    public boolean autoStart() {
        return true;
    }
}