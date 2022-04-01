package manageezpz.logic.parser;

import static manageezpz.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT_BIND;
import static manageezpz.logic.parser.CliSyntax.PREFIX_ASSIGNEES;
import static manageezpz.logic.parser.CliSyntax.PREFIX_DATE;
import static manageezpz.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static manageezpz.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static manageezpz.logic.parser.CliSyntax.PREFIX_EVENT;
import static manageezpz.logic.parser.CliSyntax.PREFIX_IS_MARKED;
import static manageezpz.logic.parser.CliSyntax.PREFIX_PRIORITY;
import static manageezpz.logic.parser.CliSyntax.PREFIX_TODO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import manageezpz.logic.commands.FindTaskCommand;
import manageezpz.logic.parser.exceptions.ParseException;
import manageezpz.model.person.Name;
import manageezpz.model.task.Date;
import manageezpz.model.task.Description;
import manageezpz.model.task.Priority;
import manageezpz.model.task.TaskMultiplePredicate;

/**
 * Subclass of FindCommandParser which check if the options are valid for finding tasks.
 */
public class FindTaskCommandParser implements Parser<FindTaskCommand> {
    private static final Prefix[] TASK_TYPES = {PREFIX_TODO, PREFIX_DEADLINE, PREFIX_EVENT};
    private static final Prefix[] VALID_OPTIONS = {PREFIX_TODO, PREFIX_DEADLINE, PREFIX_EVENT, PREFIX_DATE,
        PREFIX_DESCRIPTION, PREFIX_PRIORITY, PREFIX_ASSIGNEES, PREFIX_IS_MARKED};

    private String errorMessage = "";
    private boolean hasError = false;

    /**
     * {@inheritDoc}
     */
    public FindTaskCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultiMap = ArgumentTokenizer.tokenize(args, VALID_OPTIONS);

        checkIfHaveAtLeastOneOption(argMultiMap);
        Prefix taskType = getPrefix(argMultiMap);
        List<String> descriptions = getDescriptions(argMultiMap);
        Date date = getTaskDate(argMultiMap);
        Priority priority = getTaskPriority(argMultiMap);
        String assignee = getAssignee(argMultiMap);
        Boolean isMarked = getIsMarked(argMultiMap);

        checkIfTodoAndDateTogether(argMultiMap, taskType);

        if (hasError) {
            String finalMessage = errorMessage + FindTaskCommand.MESSAGE_USAGE;
            String displayedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT_BIND, finalMessage);
            throw new ParseException(displayedMessage);
        } else {
            return new FindTaskCommand(new TaskMultiplePredicate(
                    taskType, descriptions, date, priority, assignee, isMarked));
        }
    }

    private void checkIfHaveAtLeastOneOption(ArgumentMultimap argMultiMap) {
        if (!isAtLeastOnePrefixPresent(argMultiMap, VALID_OPTIONS) || !argMultiMap.getPreamble().isEmpty()) {
            addErrorMessage(FindTaskCommand.NO_OPTIONS);
        }
    }

    private void checkIfTodoAndDateTogether(ArgumentMultimap argMultiMapProperties, Prefix taskTypes) {
        boolean isContainsTodo = taskTypes instanceof Prefix && taskTypes.equals(PREFIX_TODO);
        if (isContainsTodo && argMultiMapProperties.isPrefixExist(PREFIX_DATE)) {
            addErrorMessage(FindTaskCommand.TODO_AND_DATE_OPTION_TOGETHER);
        }
    }

    private Prefix getPrefix(ArgumentMultimap argMultiMap) {
        List<Prefix> currentPrefixes = Arrays.stream(TASK_TYPES)
                .filter(prefix -> argMultiMap.isPrefixExist(prefix)).collect(Collectors.toList());
        if (currentPrefixes.size() > 1) {
            // If the user enters more than 1 task type
            addErrorMessage(FindTaskCommand.MORE_THAN_ONE_TASK_TYPE);
            return null;
        } else if (currentPrefixes.isEmpty()) {
            return null;
        } else {
            return currentPrefixes.get(0);
        }
    }

    private List<String> getDescriptions(ArgumentMultimap argMultiMap) {
        List<String> descriptions = null;
        if (argMultiMap.isPrefixExist(PREFIX_DESCRIPTION)) {
            String descriptionString = argMultiMap.getValue(PREFIX_DESCRIPTION).get();
            descriptions = List.of(descriptionString.split("\\s+"));
            checkIfValidDescription(descriptions);
        }
        return descriptions;
    }

    private void checkIfValidDescription(List<String> description) {
        boolean isValid = description.stream().allMatch(name -> Description.isValidDescription(name));
        if (!isValid) {
            addErrorMessage(FindTaskCommand.INVALID_DESCRIPTION);
        }
    }

    private Date getTaskDate(ArgumentMultimap argMultiMap) {
        Date date = null;
        if (argMultiMap.isPrefixExist(PREFIX_DATE)) {
            String dateString = argMultiMap.getValue(PREFIX_DATE).get().trim();
            boolean isDateValid = checkIfDateIsValid(dateString);
            if (isDateValid) {
                date = new Date(dateString);
            }
        }
        return date;
    }

    private boolean checkIfDateIsValid(String dateString) {
        boolean isValidDate = Date.isValidDate(dateString);
        if (!isValidDate) {
            addErrorMessage(FindTaskCommand.INVALID_DATE);
            return false;
        }
        return true;
    }

    private Priority getTaskPriority(ArgumentMultimap argMultiMap) {
        Priority priority = null;
        if (argMultiMap.isPrefixExist(PREFIX_PRIORITY)) {
            String priorityString = argMultiMap.getValue(PREFIX_PRIORITY).get().trim().toUpperCase();
            priority = checkPriority(priorityString);
        }
        return priority;
    }

    private Priority checkPriority(String priorityString) {
        switch (priorityString) {
        case "HIGH":
            return Priority.HIGH;
        case "MEDIUM":
            return Priority.MEDIUM;
        case "LOW":
            return Priority.LOW;
        case "NONE":
            return Priority.NONE;
        default:
            addErrorMessage(FindTaskCommand.INVALID_PRIORITY);
            return null;
        }
    }

    private String getAssignee(ArgumentMultimap argMultiMap) {
        String assignee = null;
        if (argMultiMap.isPrefixExist(PREFIX_ASSIGNEES)) {
            assignee = argMultiMap.getValue(PREFIX_ASSIGNEES).get().trim();
            checkedIfNameValid(assignee);
        }
        return assignee;
    }

    private void checkedIfNameValid(String assignee) {
        boolean isNameValid = Name.isValidName(assignee);
        if (!isNameValid) {
            addErrorMessage(FindTaskCommand.INVALID_ASSIGNEE);
        }
    }

    private Boolean getIsMarked(ArgumentMultimap argMultiMap) {
        Boolean isMarked = null;
        if (argMultiMap.isPrefixExist(PREFIX_IS_MARKED)) {
            String booleanString = argMultiMap.getValue(PREFIX_IS_MARKED).get().trim().toLowerCase();
            boolean isEitherTrueOrFalse = checkIfEitherTrueOrFalse(booleanString);
            if (isEitherTrueOrFalse) {
                isMarked = Boolean.valueOf(booleanString);
            }
        }
        return isMarked;
    }

    private boolean checkIfEitherTrueOrFalse(String booleanString) {
        if (booleanString.equals("true") || booleanString.equals("false")) {
            return true;
        } else if (!booleanString.equals("true") && !booleanString.equals("false")) {
            addErrorMessage(FindTaskCommand.INVALID_BOOLEAN);
            return false;
        }
        assert false : "Error in checkIfEitherTrueOrFalse";
        return false;
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
