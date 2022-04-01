package manageezpz.logic.parser;

import static java.util.Objects.requireNonNull;
import static manageezpz.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND;
import static manageezpz.logic.commands.EditEmployeeCommand.MESSAGE_USAGE;
import static manageezpz.logic.parser.CliSyntax.PREFIX_EMAIL;
import static manageezpz.logic.parser.CliSyntax.PREFIX_NAME;
import static manageezpz.logic.parser.CliSyntax.PREFIX_PHONE;

import manageezpz.commons.core.index.Index;
import manageezpz.logic.commands.EditEmployeeCommand;
import manageezpz.logic.commands.EditEmployeeCommand.EditPersonDescriptor;
import manageezpz.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditEmployeeCommandParser implements Parser<EditEmployeeCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditEmployeeCommand
     * and returns an EditEmployeeCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditEmployeeCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_BIND, MESSAGE_USAGE), pe);
        }

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        try {
            if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
                editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
            }
            if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
                editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
            }
            if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
                editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
            }
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage() + "\n" + MESSAGE_USAGE, pe);
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditEmployeeCommand.MESSAGE_NOT_EDITED + "\n" + MESSAGE_USAGE);
        }

        return new EditEmployeeCommand(index, editPersonDescriptor);
    }
}
