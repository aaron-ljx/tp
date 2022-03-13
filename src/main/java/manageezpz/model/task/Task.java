package manageezpz.model.task;

import static manageezpz.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;

import manageezpz.model.person.Person;

/**
 * Represents the Tasks a user could create. A <code> Task </code> object would correspond to a task
 * inputted by a user either a Todo, Deadline or Event.
 */
public class Task {
    protected boolean isDone;

    // Identity fields
    private final Description taskDescription;

    // Data fields
    private List<Person> assignees; //List of Strings as of now, V1.3 will incorporate Persons (assign tasks to Persons)

    /**
     * Constructor for the Task class.
     * @param taskDescription information about the task.
     */
    public Task(Description taskDescription) {
        requireAllNonNull(taskDescription);
        this.taskDescription = taskDescription;
        this.isDone = false;
    }

    /**
     * Returns X if the task is done, otherwise blank.
     * @return the string representation of the status of the task.
     */
    public String getStatusIcon() {
        if (this.isDone) {
            return "X";
        } else {
            return " ";
        }
    }

    public void setTaskDone() {
        this.isDone = true;
    }

    public void setTaskNotDone() {
        this.isDone = false;
    }

    public Description getDescription() {
        return this.taskDescription;
    }

    /**
     * Returns the string representation of the task.
     * @return a string representation of the task, consisting of its description and whether its done or not.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + getDescription();
    }

    /**
     * Returns true if both Task have the same name.
     * This defines a weaker notion of equality between two Task.
     */
    public boolean isSameTask(Task otherTask) {
        if (otherTask == this) {
            return true;
        }

        return otherTask != null
                && otherTask.getDescription().equals(getDescription());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Task)) {
            return false;
        }

        Task otherTask = (Task) other;
        return otherTask.getDescription().equals(getDescription())
                && otherTask.getStatusIcon().equals(getStatusIcon());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(taskDescription);
    }

}