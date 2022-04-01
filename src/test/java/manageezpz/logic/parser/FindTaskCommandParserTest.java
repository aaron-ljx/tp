package manageezpz.logic.parser;

import static manageezpz.logic.commands.CommandTestUtil.INVALID_BOOLEAN;
import static manageezpz.logic.commands.CommandTestUtil.INVALID_DATE;
import static manageezpz.logic.commands.CommandTestUtil.INVALID_PRIORITY;
import static manageezpz.logic.commands.CommandTestUtil.LIST_DESCRIPTIONS;
import static manageezpz.logic.commands.CommandTestUtil.VALID_BOOLEAN;
import static manageezpz.logic.commands.CommandTestUtil.VALID_DATE;
import static manageezpz.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static manageezpz.logic.commands.CommandTestUtil.VALID_PRIORITY;
import static manageezpz.logic.commands.CommandTestUtil.VALID_TASK_DESCRIPTION;
import static manageezpz.logic.parser.CliSyntax.PREFIX_ASSIGNEES;
import static manageezpz.logic.parser.CliSyntax.PREFIX_DATE;
import static manageezpz.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static manageezpz.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static manageezpz.logic.parser.CliSyntax.PREFIX_IS_MARKED;
import static manageezpz.logic.parser.CliSyntax.PREFIX_PRIORITY;
import static manageezpz.logic.parser.CliSyntax.PREFIX_TODO;
import static manageezpz.logic.parser.CommandParserTestUtil.assertParseFailure;
import static manageezpz.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import manageezpz.commons.core.Messages;
import manageezpz.logic.commands.FindTaskCommand;
import manageezpz.model.task.Date;
import manageezpz.model.task.Priority;
import manageezpz.model.task.TaskMultiplePredicate;

class FindTaskCommandParserTest {
    private static final String EMPTY_STRING = "";
    private static final String NO_OPTIONS_MESSAGE =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindTaskCommand.NO_OPTIONS + FindTaskCommand.MESSAGE_USAGE);
    private static final String INVALID_DESCRIPTION_ERROR_MESSAGE =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindTaskCommand.INVALID_DESCRIPTION + FindTaskCommand.MESSAGE_USAGE);
    private static final String INVALID_DATE_ERROR_MESSAGE =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindTaskCommand.INVALID_DATE + FindTaskCommand.MESSAGE_USAGE);
    private static final String INVALID_PRIORITY_ERROR_MESSAGE =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindTaskCommand.INVALID_PRIORITY + FindTaskCommand.MESSAGE_USAGE);
    private static final String INVALID_ASSIGNEE_COMMAND_MESSAGE =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindTaskCommand.INVALID_ASSIGNEE + FindTaskCommand.MESSAGE_USAGE);
    private static final String INVALID_BOOLEAN_ERROR_MESSAGE =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindTaskCommand.INVALID_BOOLEAN + FindTaskCommand.MESSAGE_USAGE);
    private static final String TODO_DATE_ERROR_MESSAGE =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindTaskCommand.TODO_AND_DATE_OPTION_TOGETHER + FindTaskCommand.MESSAGE_USAGE);
    private static final String MORE_THAN_ONE_TASK_TYPE_ERROR_MESSAGE =
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND,
            FindTaskCommand.MORE_THAN_ONE_TASK_TYPE + FindTaskCommand.MESSAGE_USAGE);

    private FindTaskCommandParser parser;

    @BeforeEach
    void setParser() {
        parser = new FindTaskCommandParser();
    }

    @Test
    void findTaskCommandParser_noArguments_throwParseException() {
        String userInput = " ";
        assertParseFailure(parser, userInput, NO_OPTIONS_MESSAGE);
    }

    @Test
    void findTaskCommandParser_invalidArguments_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, "someNonExistentCommand/");
        assertParseFailure(parser, userInput, NO_OPTIONS_MESSAGE);
    }

    @Test
    void findTaskCommandParser_specificTaskType_findTaskCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_TODO.toString());
        TaskMultiplePredicate expectedPredicate = new TaskMultiplePredicate(PREFIX_TODO, null, null,
                null, null, null);
        FindTaskCommand expectedCommand = new FindTaskCommand(expectedPredicate);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    void findTaskCommandParser_multipleTaskType_throwsParseError() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_TODO.toString(), PREFIX_DEADLINE.toString());
        assertParseFailure(parser, userInput, MORE_THAN_ONE_TASK_TYPE_ERROR_MESSAGE);
    }

    @Test
    void findTaskCommandParser_emptyDescription_throwsParseError() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_DESCRIPTION.toString());
        assertParseFailure(parser, userInput, INVALID_DESCRIPTION_ERROR_MESSAGE);
    }

    @Test
    void findTaskCommandParser_withDescription_findTaskCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_DESCRIPTION.toString(), VALID_TASK_DESCRIPTION);
        TaskMultiplePredicate expectedPredicate = new TaskMultiplePredicate(null, LIST_DESCRIPTIONS, null,
                null, null, null);
        FindTaskCommand expectedCommand = new FindTaskCommand(expectedPredicate);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    void findTaskCommandParser_noDate_throwsParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_DATE.toString());
        assertParseFailure(parser, userInput, INVALID_DATE_ERROR_MESSAGE);
    }

    @Test
    void findTaskCommandParser_invalidDate_throwsParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_DATE.toString(), INVALID_DATE);
        assertParseFailure(parser, userInput, INVALID_DATE_ERROR_MESSAGE);
    }

    @Test
    void findTaskCommandParser_todoWithDateOption_throwParseError() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_TODO.toString(), PREFIX_DATE.toString(),
                VALID_DATE);
        assertParseFailure(parser, userInput, TODO_DATE_ERROR_MESSAGE);
    }

    @Test
    void findTaskCommandParser_validDate_findTaskCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_DATE.toString(), VALID_DATE);
        TaskMultiplePredicate expectedPredicate = new TaskMultiplePredicate(null, null,
                new Date(VALID_DATE), null, null, null);
        FindTaskCommand expectedCommand = new FindTaskCommand(expectedPredicate);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    void findTaskCommandParser_emptyPriority_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_PRIORITY.toString());
        assertParseFailure(parser, userInput, INVALID_PRIORITY_ERROR_MESSAGE);
    }

    @Test
    void findTaskCommandParser_invalidPriority_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_PRIORITY.toString(), INVALID_PRIORITY);
        assertParseFailure(parser, userInput, INVALID_PRIORITY_ERROR_MESSAGE);
    }

    @Test
    void findTaskCommandParser_validPriority_findTaskCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_PRIORITY.toString(), VALID_PRIORITY);
        TaskMultiplePredicate expectedPredicate = new TaskMultiplePredicate(null, null,
                null, Priority.valueOf(VALID_PRIORITY), null, null);
        FindTaskCommand expectedCommand = new FindTaskCommand(expectedPredicate);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    void findTaskCommandParser_emptyAssignee_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_ASSIGNEES.toString());
        assertParseFailure(parser, userInput, INVALID_ASSIGNEE_COMMAND_MESSAGE);
    }

    @Test
    void findTaskCommandParser_invalidAssignee_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_ASSIGNEES.toString(), "James&");
        assertParseFailure(parser, userInput, INVALID_ASSIGNEE_COMMAND_MESSAGE);
    }

    @Test
    void findTaskCommandParser_haveAssignee_findTaskCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_ASSIGNEES.toString(), VALID_NAME_AMY);
        TaskMultiplePredicate expectedPredicate = new TaskMultiplePredicate(null, null, null,
                null, VALID_NAME_AMY, null);
        FindTaskCommand expectedCommand = new FindTaskCommand(expectedPredicate);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    void findTaskCommandParser_emptyBoolean_throwParseError() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_IS_MARKED.toString());
        assertParseFailure(parser, userInput, INVALID_BOOLEAN_ERROR_MESSAGE);
    }

    @Test
    void findTaskCommandParser_invalidBoolean_throwParseException() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_IS_MARKED.toString(), INVALID_BOOLEAN);
        assertParseFailure(parser, userInput, INVALID_BOOLEAN_ERROR_MESSAGE);
    }

    @Test
    void findTaskCommandParser_validBoolean_findTaskCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_IS_MARKED.toString(), VALID_BOOLEAN);
        TaskMultiplePredicate expectedPredicate = new TaskMultiplePredicate(null, null,
                null, null, null, Boolean.valueOf(VALID_BOOLEAN));
        FindTaskCommand expectedCommand = new FindTaskCommand(expectedPredicate);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    void findTaskCommandParser_multipleArguments_findTaskCommand() {
        String userInput = String.join(" ", EMPTY_STRING, PREFIX_DEADLINE.toString(),
                PREFIX_DESCRIPTION.toString(), VALID_TASK_DESCRIPTION, PREFIX_DATE.toString(), VALID_DATE,
                PREFIX_PRIORITY.toString(), VALID_PRIORITY, PREFIX_ASSIGNEES.toString(), VALID_NAME_AMY,
                PREFIX_IS_MARKED.toString(), VALID_BOOLEAN);
        TaskMultiplePredicate expectedPredicate = new TaskMultiplePredicate(PREFIX_DEADLINE, LIST_DESCRIPTIONS,
                new Date(VALID_DATE), Priority.valueOf(VALID_PRIORITY), VALID_NAME_AMY,
                Boolean.valueOf(VALID_BOOLEAN));
        FindTaskCommand expectedCommand = new FindTaskCommand(expectedPredicate);
        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
