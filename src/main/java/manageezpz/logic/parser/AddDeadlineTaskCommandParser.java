package manageezpz.logic.parser;

import static manageezpz.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static manageezpz.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND;
import static manageezpz.logic.commands.AddDeadlineTaskCommand.MESSAGE_USAGE;
import static manageezpz.logic.parser.CliSyntax.PREFIX_BY_DATETIME;
import static manageezpz.logic.parser.CliSyntax.PREFIX_DESCRIPTION;

import java.util.stream.Stream;

import manageezpz.logic.commands.AddDeadlineTaskCommand;
import manageezpz.logic.parser.exceptions.ParseException;
import manageezpz.model.task.Date;
import manageezpz.model.task.Deadline;
import manageezpz.model.task.Description;
import manageezpz.model.task.Time;

public class AddDeadlineTaskCommandParser implements Parser<AddDeadlineTaskCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddDeadlineTaskCommand
     * and returns an AddDeadlineTaskCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddDeadlineTaskCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimapDeadline =
                ArgumentTokenizer.tokenize(args, PREFIX_DESCRIPTION, PREFIX_BY_DATETIME);

        if (!arePrefixesPresent(argMultimapDeadline, PREFIX_DESCRIPTION, PREFIX_BY_DATETIME)
                || !argMultimapDeadline.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_BIND,
                    AddDeadlineTaskCommand.MESSAGE_USAGE));
        }

        try {
            Description desc = ParserUtil.parseDescription(argMultimapDeadline.getValue(PREFIX_DESCRIPTION).get());

            String byDateTime = argMultimapDeadline.getValue(PREFIX_BY_DATETIME).get();
            String[] parseByDateTime = byDateTime.split(" ");

            if (parseByDateTime.length != 2) {
                throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
            }

            Date date = ParserUtil.parseDate(parseByDateTime[0]);
            Time time = ParserUtil.parseTime(parseByDateTime[1]);

            Deadline deadline = new Deadline(desc, date, time);
            return new AddDeadlineTaskCommand(deadline);
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage() + "\n\n" + MESSAGE_USAGE);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
