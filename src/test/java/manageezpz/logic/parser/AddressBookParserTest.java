package manageezpz.logic.parser;

import static manageezpz.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND;
import static manageezpz.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static manageezpz.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static manageezpz.testutil.Assert.assertThrows;
import static manageezpz.testutil.TypicalIndexes.INDEX_FIRST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import manageezpz.logic.commands.AddEmployeeCommand;
import manageezpz.logic.commands.ClearCommand;
import manageezpz.logic.commands.DeleteEmployeeCommand;
import manageezpz.logic.commands.EditEmployeeCommand;
import manageezpz.logic.commands.EditEmployeeCommand.EditPersonDescriptor;
import manageezpz.logic.commands.ExitCommand;
import manageezpz.logic.commands.FindTaskCommand;
import manageezpz.logic.commands.HelpCommand;
import manageezpz.logic.commands.ListTaskCommand;
import manageezpz.logic.parser.exceptions.ParseException;
import manageezpz.model.person.Person;
import manageezpz.model.task.TaskMultiplePredicate;
import manageezpz.testutil.EditPersonDescriptorBuilder;
import manageezpz.testutil.PersonBuilder;
import manageezpz.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddEmployeeCommand command = (AddEmployeeCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddEmployeeCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteEmployeeCommand command = (DeleteEmployeeCommand) parser.parseCommand(
                DeleteEmployeeCommand.COMMAND_WORD + " " + INDEX_FIRST.getOneBased());
        assertEquals(new DeleteEmployeeCommand(INDEX_FIRST), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditEmployeeCommand command = (EditEmployeeCommand) parser.parseCommand(
                EditEmployeeCommand.COMMAND_WORD + " " + INDEX_FIRST.getOneBased()
                        + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditEmployeeCommand(INDEX_FIRST, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_findTask() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindTaskCommand command = (FindTaskCommand) parser.parseCommand(
                FindTaskCommand.COMMAND_WORD
                        + " " + PREFIX_DESCRIPTION
                        + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindTaskCommand(
                new TaskMultiplePredicate(null, keywords, null, null, null, null)),
                command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListTaskCommand.COMMAND_WORD) instanceof ListTaskCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT_BIND,
                HelpCommand.MESSAGE_USAGE), () -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
