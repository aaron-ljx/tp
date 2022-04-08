package manageezpz.logic.commands;

import static manageezpz.commons.core.Messages.MESSAGE_DUPLICATE_TASK;
import static manageezpz.logic.commands.CommandTestUtil.VALID_DATE;
import static manageezpz.logic.commands.CommandTestUtil.VALID_END_TIME;
import static manageezpz.logic.commands.CommandTestUtil.VALID_START_TIME;
import static manageezpz.logic.commands.CommandTestUtil.VALID_TASK_DESCRIPTION;
import static manageezpz.logic.commands.CommandTestUtil.VALID_TIME;
import static manageezpz.testutil.TypicalTasks.getTypicalAddressBookTasks;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import manageezpz.logic.commands.exceptions.CommandException;
import manageezpz.model.Model;
import manageezpz.model.ModelManager;
import manageezpz.model.UserPrefs;
import manageezpz.model.task.Date;
import manageezpz.model.task.Deadline;
import manageezpz.model.task.Description;
import manageezpz.model.task.Event;
import manageezpz.model.task.Time;
import manageezpz.model.task.Todo;

public class AddTaskCommandTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBookTasks(), new UserPrefs());
    }

    @Test
    public void addTodo_success() throws CommandException {
        Todo newTodoTask = new Todo(new Description("new description"));
        AddTodoTaskCommand addTodoTaskCommand = new AddTodoTaskCommand(newTodoTask);
        addTodoTaskCommand.execute(model);
    }

    @Test
    public void addDeadline_success() throws CommandException {
        Deadline newDeadlineTask = new Deadline(new Description("new description"), new Date(VALID_DATE),
                new Time(VALID_TIME));
        AddDeadlineTaskCommand addDeadlineTaskCommand = new AddDeadlineTaskCommand(newDeadlineTask);
        addDeadlineTaskCommand.execute(model);
    }

    @Test
    public void addEvent_success() throws CommandException {
        Event newEventTask = new Event(new Description("new description"), new Date(VALID_DATE),
                new Time(VALID_START_TIME), new Time(VALID_END_TIME));
        AddEventTaskCommand addEventTaskCommand = new AddEventTaskCommand(newEventTask);
        addEventTaskCommand.execute(model);
    }

    @Test
    public void addDeadline_sameDescriptionAsTaskAlreadyInList_failure() {
        Deadline newDeadLineTask = new Deadline(new Description(VALID_TASK_DESCRIPTION), new Date(VALID_DATE),
                new Time(VALID_TIME));
        AddDeadlineTaskCommand addDeadlineTaskCommand = new AddDeadlineTaskCommand(newDeadLineTask);
        assertThrows(CommandException.class, () -> {
            addDeadlineTaskCommand.execute(model);
        }, MESSAGE_DUPLICATE_TASK);
    }
}
