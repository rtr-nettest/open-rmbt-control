package at.rtr.rmbt.test;

import org.mockito.MockitoAnnotations;
import org.springframework.core.Conventions;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Initializes Mockito {@code @Mock}/{@code @Spy}/{@code @Captor}/{@code @InjectMocks} fields on the
 * test instance for Spring-managed tests.
 *
 * <p>Spring Boot 3 provided this automatically through its {@code MockitoTestExecutionListener}. That
 * listener was removed in Spring Boot 4 (together with {@code @MockBean}/{@code @SpyBean}); the
 * replacement {@code MockitoResetTestExecutionListener} only handles {@code @MockitoBean}, not plain
 * Mockito field annotations. This project's JUnit 4 tests ({@code @RunWith(SpringRunner.class)} with
 * {@code @Mock} fields and no explicit {@code MockitoAnnotations.openMocks(...)}) therefore saw their
 * mocks stay {@code null}. Registered globally via {@code META-INF/spring.factories} so every
 * Spring-managed test keeps the old behaviour; it is a no-op for test classes without Mockito fields.
 */
public class MockitoFieldInitTestExecutionListener extends AbstractTestExecutionListener {

    private static final String CLOSEABLE_ATTRIBUTE =
            Conventions.getQualifiedAttributeName(MockitoFieldInitTestExecutionListener.class, "closeable");

    @Override
    public void prepareTestInstance(final TestContext testContext) {
        testContext.setAttribute(CLOSEABLE_ATTRIBUTE,
                MockitoAnnotations.openMocks(testContext.getTestInstance()));
    }

    @Override
    public void afterTestMethod(final TestContext testContext) throws Exception {
        final Object closeable = testContext.removeAttribute(CLOSEABLE_ATTRIBUTE);
        if (closeable instanceof AutoCloseable autoCloseable) {
            autoCloseable.close();
        }
    }

    @Override
    public int getOrder() {
        // Run just before DependencyInjectionTestExecutionListener (2000), matching the order the
        // removed Spring Boot MockitoTestExecutionListener used, so mocks exist before injection.
        return 1950;
    }
}
