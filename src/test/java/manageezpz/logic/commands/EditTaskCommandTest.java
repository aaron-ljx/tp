package manageezpz.logic.commands;

import static manageezpz.commons.core.Messages.MESSAGE_INVALID_TIME_FORMAT;
import static manageezpz.commons.core.Messages.MESSAGE_INVALID_TIME_RANGE;
import static manageezpz.logic.commands.CommandTestUtil.DEADLINE_TASK;
import static manageezpz.logic.commands.CommandTestUtil.EVENT_TASK;
import static manageezpz.logic.commands.CommandTestUtil.TODO_TASK;
import static manageezpz.logic.commands.CommandTestUtil.VALID_DATE;
import static manageezpz.logic.commands.CommandTestUtil.VALID_TASK_DESCRIPTION;
import static manageezpz.logic.commands.EditTaskCommand.MESSAGE_EDIT_TASK_NO_EMPTY_VALUES;
import static manageezpz.logic.commands.EditTaskCommand.MESSAGE_EDIT_TODO_TASK_NO_DATE_AND_TIME_VALUES;
import static manageezpz.testutil.TypicalTasks.getTypicalAddressBookTasks;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import manageezpz.commons.core.index.Index;
import manageezpz.logic.commands.exceptions.CommandException;
import manageezpz.logic.parser.exceptions.ParseException;
import manageezpz.model.Model;
import manageezpz.model.ModelManager;
import manageezpz.model.UserPrefs;

public class EditTaskCommandTest {
    // A Todo task has index 0
    // A Deadline task has index 1
    // An Event task has index 10
    // Based on the list returned by getTypicalAddressBookTasks()

    private static final String UPDATED_DESC_STRING = "i have been updated";

    private static final String UPDATED_TIME_STRING = "2200";
    private static final String UPDATED_START_AND_END_TIME_STRING = "2000 2100";

    private static final String UPDATED_DATE_STRING = "2022-02-02";

    private static final HashMap<String, Boolean> defaultPrefixStatusHash = new HashMap<>();

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBookTasks(), new UserPrefs());
    }

    static {
        defaultPrefixStatusHash.put("description", true);
        defaultPrefixStatusHash.put("date", true);
        defaultPrefixStatusHash.put("datetime", true);
    }

    private void initDefaultPrefixStatusHash() {
        defaultPrefixStatusHash.put("description", true);
        defaultPrefixStatusHash.put("date", true);
        defaultPrefixStatusHash.put("datetime", true);
    }

    @Test
    public void updateTodo_updateDescription_success() throws CommandException {
        initDefaultPrefixStatusHash();
        EditTaskCommand editTaskCommand = new EditTaskCommand(Index.fromZeroBased(0), UPDATED_DESC_STRING,
                "", "", defaultPrefixStatusHash);
        defaultPrefixStatusHash.replace("date", false);
        defaultPrefixStatusHash.replace("datetime", false);
        editTaskCommand.execute(model);
    }

    @Test
    public void updateDeadline_updateDate_success() throws CommandException {
        initDefaultPrefixStatusHash();
        EditTaskCommand editTaskCommand = new EditTaskCommand(Index.fromZeroBased(1), "",
                UPDATED_DATE_STRING, "", defaultPrefixStatusHash);
        defaultPrefixStatusHash.replace("description", false);
        defaultPrefixStatusHash.replace("datetime", false);
        editTaskCommand.execute(model);
    }

    @Test
    public void updateDeadline_updateDescriptionAndTime_success() throws CommandException {
        initDefaultPrefixStatusHash();
        EditTaskCommand editTaskCommand = new EditTaskCommand(Index.fromZeroBased(1), UPDATED_DESC_STRING,
                "", UPDATED_TIME_STRING, defaultPrefixStatusHash);
        defaultPrefixStatusHash.replace("date", false);
        editTaskCommand.execute(model);
    }

    @Test
    public void updateEvent_updateTime_success() throws CommandException {
        initDefaultPrefixStatusHash();
        EditTaskCommand editTaskCommand = new EditTaskCommand(Index.fromZeroBased(10),
                "", "", UPDATED_START_AND_END_TIME_STRING, defaultPrefixStatusHash);
        defaultPrefixStatusHash.replace("description", false);
        defaultPrefixStatusHash.replace("date", false);
        editTaskCommand.execute(model);
    }

    @Test
    public void updateEvent_updateDescriptionAndDate_success() throws CommandException {
        initDefaultPrefixStatusHash();
        EditTaskCommand editTaskCommand = new EditTaskCommand(Index.fromZeroBased(10),
                UPDATED_DESC_STRING, UPDATED_DATE_STRING, "", defaultPrefixStatusHash);
        defaultPrefixStatusHash.replace("datetime", false);
        editTaskCommand.execute(model);
    }

    @Test
    public void updateEvent_invalidTimeRange_failure() {
        initDefaultPrefixStatusHash();
        String updatedTimeString = "2200 2100";
        EditTaskCommand editTaskCommand = new EditTaskCommand(Index.fromZeroBased(10),
                UPDATED_DESC_STRING, UPDATED_DATE_STRING, updatedTimeString, defaultPrefixStatusHash);
        assertThrows(ParseException.class, () -> {
            editTaskCommand.updateEvent(EVENT_TASK, VALID_TASK_DESCRIPTION, VALID_DATE,
                    updatedTimeString);
        }, MESSAGE_INVALID_TIME_RANGE);
    }

    @Test
    public void updateEvent_invalidTimeFormat_failure() {
        initDefaultPrefixStatusHash();
        EditTaskCommand editTaskCommand = new EditTaskCommand(Index.fromZeroBased(10),
                UPDATED_DESC_STRING, UPDATED_DATE_STRING, UPDATED_TIME_STRING, defaultPrefixStatusHash);
        assertThrows(ParseException.class, () -> {
            editTaskCommand.updateEvent(EVENT_TASK, VALID_TASK_DESCRIPTION, VALID_DATE,
                    UPDATED_TIME_STRING);
        }, MESSAGE_INVALID_TIME_FORMAT);
    }

    @Test
    public void updateDeadline_updateDescriptionButDatePrefixHasNoValue_failure() {
        initDefaultPrefixStatusHash();
        EditTaskCommand editTaskCommand = new EditTaskCommand(Index.fromZeroBased(1), UPDATED_DESC_STRING,
                "", UPDATED_TIME_STRING, defaultPrefixStatusHash);
        assertThrows(ParseException.class, () -> {
            editTaskCommand.updateDeadline(DEADLINE_TASK, UPDATED_DESC_STRING, "",
                    UPDATED_TIME_STRING);
        }, MESSAGE_EDIT_TASK_NO_EMPTY_VALUES);
    }

    @Test
    public void updateTodo_updateDescriptionButDatePrefixExists_failure() {
        initDefaultPrefixStatusHash();
        EditTaskCommand editTaskCommand = new EditTaskCommand(Index.fromZeroBased(0), UPDATED_DESC_STRING,
                "", "", defaultPrefixStatusHash);
        assertThrows(ParseException.class, () -> {
            editTaskCommand.updateTodo(TODO_TASK, UPDATED_DESC_STRING);
        }, MESSAGE_EDIT_TODO_TASK_NO_DATE_AND_TIME_VALUES);
    }
}
