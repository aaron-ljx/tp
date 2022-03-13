package manageezpz.model.util;

import manageezpz.model.AddressBook;
import manageezpz.model.ReadOnlyAddressBook;
import manageezpz.model.person.Email;
import manageezpz.model.person.Name;
import manageezpz.model.person.Person;
import manageezpz.model.person.Phone;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com")),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com")),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com")),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

}