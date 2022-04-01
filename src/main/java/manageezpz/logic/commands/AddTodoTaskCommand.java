package manageezpz.logic.commands;

import static java.util.Objects.requireNonNull;
import static manageezpz.commons.core.Messages.MESSAGE_DUPLICATE_TASK;
import static manageezpz.logic.parser.CliSyntax.PREFIX_DESCRIPTION;

import manageezpz.logic.commands.exceptions.CommandException;
import manageezpz.model.Model;
import manageezpz.model.task.Todo;

public class AddTodoTaskCommand extends Command {

    public static final String COMMAND_WORD = "addTodo";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a Todo Task to ManageEZPZ.\n"
            + "Parameters: "
            + PREFIX_DESCRIPTION + "DESCRIPTION\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_DESCRIPTION + "Play Genshin Impact";

    public static final String MESSAGE_SUCCESS = "New Todo Task added: %1$s";

    private final Todo toAdd;

    /**
     * Creates an AddEmployeeCommand to add the specified {@code Person}
     */
    public AddTodoTaskCommand(Todo task) {
        requireNonNull(task);
        toAdd = task;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasTask(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_TASK,
                    toAdd.getDescription()) + MESSAGE_USAGE);
        }

        model.addTodo(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }
}
