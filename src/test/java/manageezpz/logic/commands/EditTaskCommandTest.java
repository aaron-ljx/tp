package manageezpz.logic.commands;

import manageezpz.logic.parser.exceptions.ParseException;
import manageezpz.model.task.Date;
import manageezpz.model.task.Deadline;
import manageezpz.model.task.Description;
import manageezpz.model.task.Event;
import manageezpz.model.task.Time;
import manageezpz.model.task.Todo;

import org.junit.jupiter.api.Test;

import static manageezpz.commons.core.Messages.MESSAGE_INVALID_TIME_RANGE;
import static manageezpz.logic.commands.CommandTestUtil.DEADLINE_TASK;
import static manageezpz.logic.commands.CommandTestUtil.EVENT_TASK;
import static manageezpz.logic.commands.CommandTestUtil.TODO_TASK;
import static manageezpz.logic.commands.CommandTestUtil.VALID_DATE;
import static manageezpz.logic.commands.CommandTestUtil.VALID_TASK_DESCRIPTION;
import static manageezpz.logic.commands.CommandTestUtil.VALID_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EditTaskCommandTest {
    private static final String UPDATED_DESC_STRING = "i have been updated";

    private static final String UPDATED_TIME_STRING = "2200";
    private static final String UPDATED_START_AND_END_TIME_STRING = "2000 2100";
    private static final String UPDATED_START_TIME_STRING = "2000";
    private static final String UPDATED_END_TIME_STRING = "2100";

    private static final String UPDATED_DATE_STRING = "2022-01-01";


    @Test
    public void updateTodo_success() throws ParseException {
        Description updatedDesc = new Description(UPDATED_DESC_STRING);
        Todo expectedTodo = new Todo(updatedDesc);
        Todo actualTodo = (Todo) EditTaskCommand.updateTodo(TODO_TASK, UPDATED_DESC_STRING);
        assertEquals(expectedTodo.getDescription(),
                actualTodo.getDescription());
    }

    @Test
    public void updateDeadlineDate_success() throws ParseException {
        Deadline currentTask = DEADLINE_TASK;
        Date updatedDate = new Date(UPDATED_DATE_STRING);
        Deadline expectedDeadline = new Deadline(currentTask.getDescription(), updatedDate, currentTask.getTime());
        Deadline actualDeadline = (Deadline) EditTaskCommand.updateDeadline(currentTask, VALID_TASK_DESCRIPTION,
                UPDATED_DATE_STRING, VALID_TIME);
        assertEquals(expectedDeadline.getDateTime(), actualDeadline.getDateTime());
    }

    @Test
    public void updateDeadlineDescAndTime_success() throws ParseException{
        Deadline currentTask = DEADLINE_TASK;
        Description updatedDesc = new Description(UPDATED_DESC_STRING);
        Time updatedTime = new Time(UPDATED_TIME_STRING);
        Deadline expectedDeadline = new Deadline(updatedDesc, currentTask.getDate(),
                updatedTime);
        Deadline actualDeadline = (Deadline) EditTaskCommand.updateDeadline(currentTask, UPDATED_DESC_STRING,
                VALID_DATE, UPDATED_TIME_STRING);
        assertEquals(expectedDeadline.getDateTime(), actualDeadline.getDateTime());
        assertEquals(expectedDeadline.getDescription(), actualDeadline.getDescription());
    }

    @Test
    public void updateEventTime_success() throws ParseException {
        Event currentTask = EVENT_TASK;
        Event expectedEvent = new Event(currentTask.getDescription(), currentTask.getDate(),
                new Time(UPDATED_START_TIME_STRING), new Time(UPDATED_END_TIME_STRING));
        Event actualEvent = (Event) EditTaskCommand.updateEvent(currentTask, VALID_TASK_DESCRIPTION, VALID_DATE,
                UPDATED_START_AND_END_TIME_STRING);
        assertEquals(expectedEvent.getDateTime(), actualEvent.getDateTime());
    }

    @Test
    public void updateEventDescAndDate_success() throws ParseException{
        Event currentTask = EVENT_TASK;
        String currentTime = currentTask.getStartTime().getTime() + " " + currentTask.getEndTime().getTime();
        Description updatedDesc = new Description(UPDATED_DESC_STRING);
        Date updatedDate = new Date(UPDATED_DATE_STRING);
        Event expectedEvent = new Event(updatedDesc, updatedDate,
                currentTask.getStartTime(), currentTask.getEndTime());
        Event actualEvent = (Event) EditTaskCommand.updateEvent(currentTask, UPDATED_DESC_STRING, VALID_DATE,
                currentTime);
        assertEquals(expectedEvent.getDateTime(), actualEvent.getDateTime());
        assertEquals(expectedEvent.getDescription(), actualEvent.getDescription());
    }

    @Test
    public void invalidEventTimeRange_failure() {
        String updatedTimeString = "2200 2100";
        assertThrows(ParseException.class, () -> {
            EditTaskCommand.updateEvent(EVENT_TASK, VALID_TASK_DESCRIPTION, VALID_DATE,
                    updatedTimeString);
        }, MESSAGE_INVALID_TIME_RANGE);
    }
}
