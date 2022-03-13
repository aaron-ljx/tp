package manageezpz.logic.parser;

import static java.util.Objects.requireNonNull;

import manageezpz.commons.core.index.Index;
import manageezpz.commons.util.StringUtil;
import manageezpz.logic.parser.exceptions.ParseException;
import manageezpz.model.person.Date;
import manageezpz.model.person.Email;
import manageezpz.model.person.Name;
import manageezpz.model.person.Phone;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String date} into a {@code Date}.
     * Supports multiple formatting patterns.
     * @param date
     * @return a {@code Date} object.
     * @throws ParseException
     */

    public static Date parseDate(String date) throws ParseException {
        requireNonNull(date);
        //@@author vishandi-reused
        //Reused from https://github.com/vishandi/ip/blob/master/src/main/java/parser/Parser.java
        //with minor modifications
        String[] patterns = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyy MMM dd", "dd MMM yyyy", "dd-MM-yyyy", "dd/MM/yyyy"};
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDate parsedDate = LocalDate.parse(date, formatter);
                return new Date(parsedDate);
            } catch (Exception e) {
                throw new ParseException(Date.MESSAGE_CONSTRAINTS);
            }
        }
        throw new ParseException(Date.MESSAGE_CONSTRAINTS);
        //@@author vishandi
    }
}