package ir.ceno.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static ir.ceno.util.StringProcessor.WORD_MAX_LENGTH;
import static ir.ceno.util.StringProcessor.ZERO_WIDTH_SPACE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@Tag("unit")
class StringProcessorTest {

    private StringProcessor stringProcessor;

    @BeforeEach
    void setUp() {
        stringProcessor = new StringProcessor();
    }

    @Test
    void makeUriOf_nullTitle() {
        Executable executable = () -> stringProcessor.makeUriOf(407, null);

        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void makeUriOf_emptyTitle() {
        Executable executable = () -> stringProcessor.makeUriOf(407, "");

        assertThrows(IllegalArgumentException.class, executable);
    }

    @ParameterizedTest
    @ValueSource(ints = {-8, 0, 1, 407, 1_082_600_157})
    void makeUriOf_variedId(int postId) {
        String postTitle = "How Many Tulips Can You Buy With One Bitcoin?";

        String uri = stringProcessor.makeUriOf(postId, postTitle);

        assertThat(uri).isNotEmpty().startsWith(postId + "/").endsWith(")")
                .containsIgnoringCase("How-Many-Tulips-Can-You-Buy-With-One-Bitcoin")
                .doesNotContain(" ");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "How Many Tulips Can You Buy With One Bitcoin?",
            "Holi ( /ˈhoʊliː/; Sanskrit: होली Holī)",
            "معرفی نامزدهای بهترین بازیکن سال اروپا"}
    )
    void makeUriOf_variedTitle(String postTitle) {
        int postId = 407;

        String uri = stringProcessor.makeUriOf(postId, postTitle);

        assertThat(uri).isNotEmpty().startsWith(postId + "/").endsWith(")").doesNotContain(" ");
    }

    @Test
    void breakLongWords_null() {
        Executable executable = () -> stringProcessor.breakLongWords(null);

        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void breakLongWords_empty() {
        String input = "";

        String result = stringProcessor.breakLongWords(input);

        assertEquals(input, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "3", "گ", "۳", ",", "~", "*", " "})
    void breakLongWords_oneCharacter(String input) {
        String result = stringProcessor.breakLongWords(input);

        assertEquals(input, result);
    }

    @Test
    void breakLongWords_oneLessThanEdgeCase() {
        String input = randomAlphanumeric(WORD_MAX_LENGTH - 1);

        String result = stringProcessor.breakLongWords(input);

        assertEquals(input, result);
    }

    @Test
    void breakLongWords_edgeCase() {
        String input = randomAlphanumeric(WORD_MAX_LENGTH);

        String result = stringProcessor.breakLongWords(input);

        assertEquals(input, result);
    }

    @Test
    void breakLongWords_edgeCaseWithInnerSpace() {
        String input = randomAlphanumeric(WORD_MAX_LENGTH / 2) +
                " " + randomAlphanumeric(WORD_MAX_LENGTH / 2);

        String result = stringProcessor.breakLongWords(input);

        assumingThat(input.length() == WORD_MAX_LENGTH + 1, () -> assertEquals(input, result));
    }

    @Test
    void breakLongWords_oneMoreThanEdgeCase() {
        String input = randomAlphanumeric(WORD_MAX_LENGTH + 1);
        String expected = input.substring(0, WORD_MAX_LENGTH) + ZERO_WIDTH_SPACE +
                input.substring(WORD_MAX_LENGTH);

        String result = stringProcessor.breakLongWords(input);

        assertEquals(expected, result);
    }

    @Test
    void breakLongWords_1LessThanEdgeCaseDoubled() {
        String input = randomAlphanumeric(WORD_MAX_LENGTH * 2 - 1);

        String result = stringProcessor.breakLongWords(input);

        assertEquals(input.length() + 1, result.length());
    }

    @Test
    void breakLongWords_edgeCaseDoubled() {
        String input = randomAlphanumeric(WORD_MAX_LENGTH * 2);

        String result = stringProcessor.breakLongWords(input);

        assertEquals(input.length() + 1, result.length());
    }

    @Test
    void breakLongWords_oneMoreThanEdgeCaseDoubled() {
        String input = randomAlphanumeric(WORD_MAX_LENGTH * 2 + 1);

        String result = stringProcessor.breakLongWords(input);

        assertEquals(input.length() + 2, result.length());
    }
}
