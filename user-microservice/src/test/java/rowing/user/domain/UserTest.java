package rowing.user.domain;

import org.junit.jupiter.api.Test;
import rowing.commons.AvailabilityIntervals;
import rowing.user.domain.user.User;
import rowing.commons.entities.UserDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Test
    public void userConstructorIdTest() {
        User user = new User("bogdan");
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo("bogdan");
    }

    @Test
    public void userConstructorMoreTest() {
        User user = new User("bogdi", "bogdan", "bancuta", "bogdiandu@gmail.com");
        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo("bogdi");
        assertThat(user.getFirstName()).isEqualTo("bogdan");
        assertThat(user.getLastName()).isEqualTo("bancuta");
        assertThat(user.getEmail()).isEqualTo("bogdiandu@gmail.com");
    }

    @Test
    public void setAvailabilityNullTest() {
        User user = new User("bogdan");
        user.setAvailability(null);
        assertThat(user.getAvailability()).isEmpty();
    }

    @Test
    public void setAvailabilityEmptyTest() {
        User user = new User("bogdan");
        user.setAvailability(new ArrayList<>());
        assertThat(user.getAvailability()).isEqualTo(new ArrayList<>());
    }

    @Test
    public void setAvailabilityCorrectlyTest() {
        User user = new User("bogdan");
        AvailabilityIntervals a = new AvailabilityIntervals("Monday", "10:00", "11:00");
        List<AvailabilityIntervals> av = Arrays.asList(a);
        user.setAvailability(av);
        assertThat(user.getAvailability()).isEqualTo(av);
    }

    @Test
    public void setEmailCorrectlyTest() {
        User user = new User("bogdan");
        user.setEmail("ana@x.r");
        assertThat(user.getEmail()).isEqualTo("ana@x.r");
    }

    @Test
    public void setEmailNullTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail(null);
        });
    }

    @Test
    public void setEmailTooSmallTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail("a@x.r");
        });
    }

    @Test
    public void setEmailNoSpecialCharacterTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail("bogdan.com");
        });
    }

    @Test
    public void setEmailNoDotTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setEmail("bogdan@gmail-com");
        });
    }

    @Test
    public void setFirstNameCorrectlyTest() {
        User user = new User("bogdan");
        user.setFirstName("bo");
        assertThat(user.getFirstName()).isEqualTo("bo");
    }

    @Test
    public void setFirstNameNullTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setFirstName(null);
        });
    }

    @Test
    public void setFirstNameEmptyTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setFirstName("");
        });
    }

    @Test
    public void setFirstNameTooSmallTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setFirstName("b");
        });
    }

    @Test
    public void setFirstNameInvalidCharactersTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setFirstName("X AE A-12");
        });
    }

    @Test
    public void setLastNameCorrectlyTest() {
        User user = new User("bogdan");
        user.setLastName("bo");
        assertThat(user.getLastName()).isEqualTo("bo");
    }

    @Test
    public void setLastNameNullTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setLastName(null);
        });
    }

    @Test
    public void setLastNameEmptyTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setLastName("");
        });
    }

    @Test
    public void setLastNameTooSmallTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setLastName("b");
        });
    }

    @Test
    public void setLastNameInvalidCharactersTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setLastName("X AE A-12");
        });
    }

    @Test
    public void setRowingOrganisationCorrectlyTest() {
        User user = new User("bogdan");
        user.setRowingOrganization("ABC");
        assertThat(user.getRowingOrganization()).isEqualTo("ABC");
    }

    @Test
    public void setRowingOrganisationNullTest() {
        User user = new User("bogdan");
        user.setRowingOrganization(null);
        assertThat(user.getRowingOrganization()).isNull();
    }

    @Test
    public void setRowingOrganisationEmptyTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setRowingOrganization("");
        });
    }

    @Test
    public void setRowingOrganisationTooSmallTest() {
        User user = new User("bogdan");
        assertThrows(IllegalArgumentException.class, () -> {
            user.setRowingOrganization("AB");
        });
    }

    @Test
    public void equalsSameTest() {
        User user = new User("bogdan");
        assertThat(user.equals(user)).isTrue();
    }

    @Test
    public void equalsNullTest() {
        User user = new User("bogdan");
        assertThat(user.equals(null)).isFalse();
    }

    @Test
    public void equalsDifferentClassTest() {
        User user = new User("bogdan");
        assertThat(user.equals(new AvailabilityIntervals("Monday", "10:00", "11:00"))).isFalse();
    }

    @Test
    public void equalsSameIdTest() {
        User user1 = new User("bogdan");
        User user2 = new User("bogdan");
        assertThat(user1.equals(user2)).isTrue();
    }

    @Test
    public void equalsDifferentIdTest() {
        User user1 = new User("bogdan");
        User user2 = new User("alex");
        assertThat(user1.equals(user2)).isFalse();
    }

    @Test
    public void hashCodeTest() {
        User user = new User("bogdan");
        int h = -1383631946;
        assertThat(user.hashCode()).isEqualTo(h);
    }

    @Test
    public void toDtoTest() {
        User user = new User("bogdan");
        UserDTO userDTO = user.toDTO();
        UserDTO shouldBeEqual = new UserDTO();
        shouldBeEqual.setUserId("bogdan");
        shouldBeEqual.setAvailability(new ArrayList<>());
        assertThat(userDTO).isEqualTo(shouldBeEqual);
    }
}
