package com.mallang.common;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import com.mallang.common.config.JpaConfig;
import com.mallang.common.config.QueryDslConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DisplayNameGeneration(ReplaceUnderscores.class)
@DataJpaTest(
        includeFilters = {
                @Filter(type = ASSIGNABLE_TYPE, classes = {
                        JpaConfig.class,
                        QueryDslConfig.class
                })
        }
)
public @interface RepositoryTest {
}
