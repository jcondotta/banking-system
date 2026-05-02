package com.jcondotta.application.query;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageResultTest {

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;

    private static final List<String> RESULT_LIST = List.of("recipient");

    private static final int PAGE = 0;
    private static final int SIZE = 20;
    private static final long TOTAL_ELEMENTS = 1;
    private static final int TOTAL_PAGES = 1;

    @Test
    void shouldCreatePageResult_whenContentIsProvided() {
        var pageResult = new PageResult<>(RESULT_LIST, PAGE, SIZE, TOTAL_ELEMENTS, TOTAL_PAGES);

        assertThat(pageResult.content()).containsExactlyElementsOf(RESULT_LIST);
        assertThat(pageResult.page()).isEqualTo(PAGE);
        assertThat(pageResult.size()).isEqualTo(SIZE);
        assertThat(pageResult.totalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(pageResult.totalPages()).isEqualTo(TOTAL_PAGES);
    }

    @Test
    void shouldCreatePageResult_whenContentIsEmpty() {
        var pageResult = new PageResult<>(List.of(), ZERO, SIZE, ZERO, ZERO);

        assertThat(pageResult.content()).isEmpty();
        assertThat(pageResult.hasNext()).isFalse();
        assertThat(pageResult.hasPrevious()).isFalse();
    }

    @Test
    void shouldCreateDefensiveCopy_whenContentIsModifiedAfterCreation() {
        var content = new ArrayList<>(RESULT_LIST);
        var pageResult = new PageResult<>(content, PAGE, SIZE, TOTAL_ELEMENTS, TOTAL_PAGES);

        content.clear();

        assertThat(pageResult.content()).containsExactlyElementsOf(RESULT_LIST);
    }

    @Test
    void shouldReturnImmutableContent_whenTryingToModifyResult() {
        var pageResult = new PageResult<>(RESULT_LIST, PAGE, SIZE, TOTAL_ELEMENTS, TOTAL_PAGES);

        assertThatThrownBy(() -> pageResult.content().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldThrowNullPointerException_whenContentIsNull() {
        assertThatThrownBy(() -> new PageResult<>(null, PAGE, SIZE, ZERO, ZERO))
            .isInstanceOf(NullPointerException.class)
            .hasMessage(PageResult.CONTENT_REQUIRED);
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @MethodSource("invalidConstructorArguments")
    void shouldThrowIllegalArgumentException_whenArgumentIsInvalid(ThrowingCallable callable, String scenario) {
        assertThatThrownBy(callable).isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> invalidConstructorArguments() {
        return Stream.of(
            Arguments.of((ThrowingCallable) () -> new PageResult<>(RESULT_LIST, -1, SIZE, TOTAL_ELEMENTS, TOTAL_PAGES), "page is negative"),
            Arguments.of((ThrowingCallable) () -> new PageResult<>(RESULT_LIST, PAGE, 0, TOTAL_ELEMENTS, TOTAL_PAGES), "size is zero"),
            Arguments.of((ThrowingCallable) () -> new PageResult<>(RESULT_LIST, PAGE, SIZE, TOTAL_ELEMENTS, -1), "totalPages is negative"),
            Arguments.of((ThrowingCallable) () -> new PageResult<>(RESULT_LIST, PAGE, SIZE, 0L, TOTAL_PAGES), "totalElements is zero but totalPages is positive")
        );
    }

    @ParameterizedTest(name = "page={0}, totalPages={1}")
    @MethodSource("hasNextFalseArguments")
    void shouldReturnFalseForHasNext_whenPageIsAtOrBeyondLastPage(int page, int totalPages) {
        var pageResult = new PageResult<>(RESULT_LIST, page, SIZE, TOTAL_ELEMENTS, totalPages);

        assertThat(pageResult.hasNext()).isFalse();
    }

    static Stream<Arguments> hasNextFalseArguments() {
        return Stream.of(
            Arguments.of(ZERO, ONE),  // single page
            Arguments.of(ONE, TWO),   // on last page
            Arguments.of(5, TWO)      // past last page
        );
    }

    @Test
    void shouldReturnTrueForHasNext_whenCurrentPageIsBeforeLastPage() {
        var pageResult = new PageResult<>(RESULT_LIST, ZERO, SIZE, TOTAL_ELEMENTS, TWO);

        assertThat(pageResult.hasNext()).isTrue();
    }

    @ParameterizedTest(name = "page={0}, totalPages={1}")
    @MethodSource("hasPreviousFalseArguments")
    void shouldReturnFalseForHasPrevious_whenPageIsZero(int page, int totalPages) {
        var pageResult = new PageResult<>(RESULT_LIST, page, SIZE, TOTAL_ELEMENTS, totalPages);

        assertThat(pageResult.hasPrevious()).isFalse();
    }

    static Stream<Arguments> hasPreviousFalseArguments() {
        return Stream.of(
            Arguments.of(ZERO, ONE),  // single page
            Arguments.of(ZERO, TWO)   // first of multiple pages
        );
    }

    @Test
    void shouldReturnTrueForHasPrevious_whenCurrentPageIsGreaterThanZero() {
        var pageResult = new PageResult<>(RESULT_LIST, ONE, SIZE, TOTAL_ELEMENTS, TWO);

        assertThat(pageResult.hasPrevious()).isTrue();
    }
}
