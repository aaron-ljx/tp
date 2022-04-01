package manageezpz.logic.parser;

import static manageezpz.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static manageezpz.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static manageezpz.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static manageezpz.logic.parser.CliSyntax.PREFIX_EMAIL;
import static manageezpz.logic.parser.CliSyntax.PREFIX_NAME;
import static manageezpz.logic.parser.CliSyntax.PREFIX_PHONE;
import static manageezpz.logic.parser.CommandParserTestUtil.assertParseFailure;
import static manageezpz.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import manageezpz.commons.core.Messages;
import manageezpz.logic.commands.FindEmployeeCommand;
import manageezpz.model.person.PersonMultiplePredicate;

class FindEmployeeCommandParserTest {
    private static final String EMPTY_STRING = "";
    private static final String NO_OPTION_MESSAGE = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindEmployeeCommand.NO_OPTIONS + FindEmployeeCommand.MESSAGE_USAGE);
    private static final String INVALID_NAME_MESSAGE = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindEmployeeCommand.INVALID_NAME + FindEmployeeCommand.MESSAGE_USAGE);
    private static final String INVALID_PHONE_MESSAGE = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindEmployeeCommand.INVALID_PHONE + FindEmployeeCommand.MESSAGE_USAGE);
    private static final String INVALID_EMAIL_MESSAGE = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindEmployeeCommand.INVALID_EMAIL + FindEmployeeCommand.MESSAGE_USAGE);

    private FindEmployeeCommandParser parser;

    @BeforeEach
    void setParser() {
        parser = new FindEmployeeCommandParser();
    }

    @Test
    void findEmployeeCommandParser_noOptions_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING);
        assertParseFailure(parser, userInput, NO_OPTION_MESSAGE);
    }

    @Test
    void findEmployeeCommandParser_invalidOption_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, "nonExistentCommand/");
        assertParseFailure(parser, userInput, NO_OPTION_MESSAGE);
    }

    @Test
    void findEmployeeCommandParser_noName_throwsParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_NAME.getPrefix());
        assertParseFailure(parser, userInput, INVALID_NAME_MESSAGE);
    }

    @Test
    void findEmployeeCommandParser_invalidNamesUsed_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_NAME.getPrefix(),
                VALID_NAME_AMY, "James&");
        assertParseFailure(parser, userInput, INVALID_NAME_MESSAGE);
    }

    @Test
    void findEmployeeCommandParser_validNamesUsed_findCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_NAME.getPrefix(), VALID_NAME_AMY);
        List<String> names = List.of((VALID_NAME_AMY).split(" "));
        PersonMultiplePredicate predicate = new PersonMultiplePredicate(names, null, null);
        FindEmployeeCommand command = new FindEmployeeCommand(predicate);
        assertParseSuccess(parser, userInput, command);
    }

    @Test
    void findEmployeeCommandParser_emptyPhone_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_PHONE.getPrefix());
        assertParseFailure(parser, userInput, INVALID_PHONE_MESSAGE);
    }

    @Test
    void findEmployeeCommandParser_invalidPhone_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_PHONE.getPrefix(), "1800-200-200");
        assertParseFailure(parser, userInput, INVALID_PHONE_MESSAGE);
    }

    @Test
    void findEmployeeCommandParse_validPhone_findCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_PHONE.getPrefix(), VALID_PHONE_AMY);
        PersonMultiplePredicate predicate = new PersonMultiplePredicate(null, VALID_PHONE_AMY, null);
        FindEmployeeCommand command = new FindEmployeeCommand(predicate);
        assertParseSuccess(parser, userInput, command);
    }

    @Test
    void findEmployeeCommandParser_emptyEmail_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_EMAIL.getPrefix());
        assertParseFailure(parser, userInput, INVALID_EMAIL_MESSAGE);
    }

    @Test
    void findEmployeeCommandParser_invalidEmail_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_EMAIL.getPrefix(), "Booby!Yahoo");
        assertParseFailure(parser, userInput, INVALID_EMAIL_MESSAGE);
    }

    @Test
    void findEmployeeCommandParse_validEmail_findCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_EMAIL.getPrefix(),
                VALID_EMAIL_AMY);
        PersonMultiplePredicate predicate = new PersonMultiplePredicate(null, null, VALID_EMAIL_AMY);
        FindEmployeeCommand command = new FindEmployeeCommand(predicate);
        assertParseSuccess(parser, userInput, command);
    }

    @Test
    void findEmployeeCommandParse_multipleOptions_findCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_NAME.getPrefix(),
                VALID_NAME_AMY, PREFIX_EMAIL.getPrefix(), VALID_EMAIL_AMY, PREFIX_PHONE.getPrefix(), VALID_PHONE_AMY);
        List<String> names = List.of((VALID_NAME_AMY).split(" "));
        PersonMultiplePredicate predicate = new PersonMultiplePredicate(names, VALID_PHONE_AMY, VALID_EMAIL_AMY);
        FindEmployeeCommand command = new FindEmployeeCommand(predicate);
        assertParseSuccess(parser, userInput, command);
    }
}
