package manageezpz.logic.parser;

import static manageezpz.logic.parser.CliSyntax.PREFIX_EMAIL;
import static manageezpz.logic.parser.CliSyntax.PREFIX_NAME;
import static manageezpz.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import manageezpz.commons.core.Messages;
import manageezpz.logic.commands.FindEmployeeCommand;
import manageezpz.logic.parser.exceptions.ParseException;
import manageezpz.model.person.Email;
import manageezpz.model.person.Name;
import manageezpz.model.person.PersonMultiplePredicate;
import manageezpz.model.person.Phone;

/**
 * Checks if the options are valid for finding employees.
 */
public class FindEmployeeCommandParser implements Parser<FindEmployeeCommand> {
    private static final Prefix[] PERSON_PROPERTIES = {PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL};

    private String errorMessage = "";
    private boolean hasError = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public FindEmployeeCommand parse(String userInput) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(userInput, PERSON_PROPERTIES);

        checkIfHaveAtLeastOneOption(argMultimap);
        List<String> names = getPersonName(argMultimap);
        String phone = getPersonPhone(argMultimap);
        String email = getPersonEmail(argMultimap);

        if (hasError) {
            String finalMessage = errorMessage + FindEmployeeCommand.MESSAGE_USAGE;
            String displayedMessage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND, finalMessage);
            throw new ParseException(displayedMessage);
        } else {
            PersonMultiplePredicate predicate = new PersonMultiplePredicate(names, phone, email);
            return new FindEmployeeCommand(predicate);
        }
    }

    private void checkIfHaveAtLeastOneOption(ArgumentMultimap argMultiMap) {
        if (!isAtLeastOnePrefixPresent(argMultiMap, PERSON_PROPERTIES) || !argMultiMap.getPreamble().isEmpty()) {
            addErrorMessage(FindEmployeeCommand.NO_OPTIONS);
        }
    }

    private List<String> getPersonName(ArgumentMultimap argMultimap) {
        List<String> names = null;
        if (argMultimap.isPrefixExist(PREFIX_NAME)) {
            String nameArgumentString = argMultimap.getValue(PREFIX_NAME).get().trim();
            String[] nameArguments = nameArgumentString.split("\\s+");
            names = checkIfAllNamesValid(nameArguments);
        }
        return names;
    }

    private List<String> checkIfAllNamesValid(String[] nameArguments) {
        List<String> names = null;
        boolean isValid = Arrays.stream(nameArguments).allMatch(name -> Name.isValidName(name));
        if (!isValid) {
            addErrorMessage(FindEmployeeCommand.INVALID_NAME);
        } else {
            names = Arrays.asList(nameArguments);
        }
        return names;
    }

    private String getPersonPhone(ArgumentMultimap argMultimap) {
        String phone = null;
        if (argMultimap.isPrefixExist(PREFIX_PHONE)) {
            phone = argMultimap.getValue(PREFIX_PHONE).get();
            checkIfPhoneValid(phone);
        }
        return phone;
    }

    private void checkIfPhoneValid(String phone) {
        boolean isValidPhone = Phone.isValidPhone(phone);
        if (!isValidPhone) {
            addErrorMessage(FindEmployeeCommand.INVALID_PHONE);
        }
    }

    private String getPersonEmail(ArgumentMultimap argMultimap) {
        String email = null;
        if (argMultimap.isPrefixExist(PREFIX_EMAIL)) {
            email = argMultimap.getValue(PREFIX_EMAIL).get();
            checkIfEmailIsValid(email);
        }
        return email;
    }

    private void checkIfEmailIsValid(String email) {
        boolean isValidEmail = Email.isValidEmail(email);
        if (!isValidEmail) {
            addErrorMessage(FindEmployeeCommand.INVALID_EMAIL);
        }
    }

    /**
     * Collates all the errors and shows the UI after processing all properties.
     * @param errorMessage Error message from each checking to be added to the overall error message.
     */
    private void addErrorMessage(String errorMessage) {
        hasError = true;
        this.errorMessage = this.errorMessage + errorMessage;
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean isAtLeastOnePrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
