package auctionsniper;

import org.jmock.Mockery;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.TestExtensionContext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by holi on 4/18/17.
 */
public class JMockExtension implements AfterTestExecutionCallback {
    @Override
    public void afterTestExecution(TestExtensionContext context) throws Exception {
        if (!context.getTestException().isPresent()) {
            allOf(Mockery.class, context).forEach(Mockery::assertIsSatisfied);
        }
    }

    private <T> Stream<T> allOf(Class<T> type, TestExtensionContext context) {
        Object test = context.getTestInstance();
        Class<?> testClass = test.getClass();
        return Arrays.stream(testClass.getDeclaredFields())
                .filter(isA(type).and(isNotStatic()))
                .map(valueIn(test, type));
    }

    private <R> Function<Field, R> valueIn(Object owner, Class<R> type) {
        return it -> {
            try {
                it.setAccessible(true);
                return type.cast(it.get(owner));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private <T extends Field> Predicate<T> isA(Class<?> type) {
        return it -> type.isAssignableFrom(it.getType());
    }

    private Predicate<? super Field> isNotStatic() {
        return it -> !Modifier.isStatic(it.getModifiers());
    }


}
