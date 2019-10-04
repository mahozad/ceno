package ir.ceno.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

import static java.lang.Character.isWhitespace;
import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

/**
 * Utility class for manipulations and operations on strings.
 */
@Component
public class StringProcessor {

    static final int WORD_MAX_LENGTH = 40;
    static final char ZERO_WIDTH_SPACE = '\u200B';
    /**
     * Compiled pattern to avoid using {@link String} methods like <i>replaceAll</i>
     * that compile the regex in every invocation of them.
     */
    private static final Pattern URI_ILLEGAL_CHARS = compile("[^\\wا-ی۰-۹$\\-.+!*'()/]");

    /**
     * Makes url for the given {@link CharSequence}.
     * <p>
     * Concatenates current date and hash of current time to the base (to ensure same base in the
     * same date doesn't produce the same url) and then replaces illegal characters with "-".
     *
     * @param postTitle character sequence to make url from
     * @return url made from the base
     */
    public String makeUriOf(long postId, CharSequence postTitle) {
        if (postTitle == null || postTitle.length() == 0) {
            throw new IllegalArgumentException();
        }

        String date = LocalDate.now().toString();
        int timeHashCode = LocalTime.now().hashCode();

        String uri = format("%d/%s-%s-(%d)", postId, postTitle, date, timeHashCode).toLowerCase();

        // replace illegal characters in the uri with '-' and then replace multiple '-'s with one
        return URI_ILLEGAL_CHARS.matcher(uri).replaceAll("-");
    }

    /**
     * Breaks long words by adding a non-visible space character in them.
     * <p>
     * Whenever a sequence of characters <b>longer</b> than
     * {@value WORD_MAX_LENGTH} and without any whitespace in between is detected, a
     * {@link StringProcessor#ZERO_WIDTH_SPACE zero-width space} is added after the
     * {@value WORD_MAX_LENGTH}th character.
     * <p>
     * Note that a sequence with a length of {@value WORD_MAX_LENGTH} is NOT considered long.
     * Again the space is added if the sequence is detected to be <b>longer</b>
     * than {@value WORD_MAX_LENGTH}.
     *
     * @param input the character sequence to break its long words
     * @return the input with the long words broken
     */
    public String breakLongWords(CharSequence input) {
        StringBuilder result = new StringBuilder();
        int wordLength = 0;
        for (char nextChar : input.toString().toCharArray()) {
            wordLength++; // should be the first statement in the loop
            if (isWhitespace(nextChar)) {
                wordLength = 0;
            } else if (wordLength > WORD_MAX_LENGTH) {
                result.append(ZERO_WIDTH_SPACE);
                wordLength = 1; // the character would be for the next word, so 1
            }
            result.append(nextChar); // should be the last statement in the loop
        }
        return result.toString();
    }
}
