package manageezpz.logic.parser;

import static manageezpz.commons.core.Messages.MESSAGE_EMPTY_NAME;
import static manageezpz.commons.core.Messages.MESSAGE_EMPTY_TASK_NUMBER;
import static manageezpz.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND;
import static manageezpz.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.stream.Stream;

import manageezpz.commons.core.index.Index;
import manageezpz.logic.commands.UntagTaskCommand;
import manageezpz.logic.parser.exceptions.ParseException;

public class UntagTaskCommandParser implements Parser<UntagTaskCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the UntagTaskCommand
     * and returns an UntagTaskCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public UntagTaskCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimapTag =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimapTag.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_BIND,
                    UntagTaskCommand.MESSAGE_USAGE), pe);
        }

        if (!arePrefixesPresent(argMultimapTag, PREFIX_NAME)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT_BIND,
                    UntagTaskCommand.MESSAGE_USAGE));
        }

        if (argMultimapTag.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_EMPTY_TASK_NUMBER,
                    UntagTaskCommand.MESSAGE_USAGE));
        }

        String name = argMultimapTag.getValue(PREFIX_NAME).get();

        if (name.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_EMPTY_NAME,
                    UntagTaskCommand.MESSAGE_USAGE));
        }

        return new UntagTaskCommand(index, name);
    }


    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
