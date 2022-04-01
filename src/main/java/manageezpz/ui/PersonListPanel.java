package manageezpz.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import manageezpz.commons.core.LogsCenter;
import manageezpz.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
    }

    /**
     * Handles mouse click event when user clicks an employee from the list.
     */
    @FXML
    public void handleMouseClick(MouseEvent arg) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        // Creating a context menu
        ContextMenu contextMenu = new ContextMenu();

        // Creating the menu Items for the context menu
        MenuItem copyNameItem = new MenuItem("Copy Name");
        MenuItem copyPhoneItem = new MenuItem("Copy Phone Number");
        MenuItem copyEmailItem = new MenuItem("Copy Email");
        contextMenu.getItems().addAll(copyNameItem, copyPhoneItem, copyEmailItem);

        // Adding the context menu to the button and the text field
        personListView.setContextMenu(contextMenu);

        copyNameItem.setOnAction((ActionEvent e) -> {
            content.putString(personListView.getSelectionModel().getSelectedItem().getName().toString());
            clipboard.setContent(content);
        });

        copyPhoneItem.setOnAction((ActionEvent e) -> {
            content.putString(personListView.getSelectionModel().getSelectedItem().getPhone().toString());
            clipboard.setContent(content);
        });

        copyEmailItem.setOnAction((ActionEvent e) -> {
            content.putString(personListView.getSelectionModel().getSelectedItem().getEmail().toString());
            clipboard.setContent(content);
        });
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }
}
