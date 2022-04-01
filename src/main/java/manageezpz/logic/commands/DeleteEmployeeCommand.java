package manageezpz.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import manageezpz.commons.core.Messages;
import manageezpz.commons.core.index.Index;
import manageezpz.logic.commands.exceptions.CommandException;
import manageezpz.model.Model;
import manageezpz.model.person.Person;
import manageezpz.model.task.Task;

/**
 * Deletes a person identified using its displayed index from the address book.
 */
public class DeleteEmployeeCommand extends Command {

    public static final String COMMAND_WORD = "deleteEmployee";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the Employee identified by the index number used in the displayed employee list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Employee: %1$s";

    private final Index targetIndex;

    /**
     * Constructor to initialize an instance of DeleteEmployeeCommand class
     * with the given targetIndex.
     *
     * @param targetIndex Index of the Employee to be deleted
     */
    public DeleteEmployeeCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownPersonList = model.getFilteredPersonList();
        List<Task> fullTaskList = model.getAddressBook().getTaskList();

        if (targetIndex.getZeroBased() >= lastShownPersonList.size()) {
            throw new CommandException(String.format(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, MESSAGE_USAGE));
        }

        Person personToDelete = lastShownPersonList.get(targetIndex.getZeroBased());

        List<Task> affectedTaskList = fullTaskList.stream()
                .filter(task -> task.getAssignees().contains(personToDelete))
                .collect(Collectors.toList());

        for (Task task : affectedTaskList) {
            model.untagEmployeeFromTask(task, personToDelete);
        }

        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, personToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteEmployeeCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteEmployeeCommand) other).targetIndex)); // state check
    }
}
