package rowing.commons.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rowing.commons.Certificates;
import rowing.commons.CoxCertificate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CoxCertificateTest {

    @BeforeAll
    public static void setup() {
        Certificates.initialize();
    }

    @Test
    public void testCoxCertificateOk() {
        CoxCertificate c = new CoxCertificate("C8", null);
        assertThat(c.getName()).isEqualTo("C8");
        assertThat(c.getSupersedes()).isNull();
    }

    @Test
    public void testCoxCertificateOk2() {
        List<String> supersede = new ArrayList<>(Arrays.asList("C4", "4+", "8+"));
        CoxCertificate c = new CoxCertificate("C12", supersede);
        assertThat(c.getName()).isEqualTo("C12");
        assertThat(c.getSupersedes()).isEqualTo(supersede);
    }

    @Test
    public void testNameNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            CoxCertificate c = new CoxCertificate(null, null);
        });
    }

    @Test
    public void testNameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            CoxCertificate c = new CoxCertificate("", null);
        });
    }

    @Test
    public void testAlreadyExistingName() {
        assertThrows(IllegalArgumentException.class, () -> {
            CoxCertificate c = new CoxCertificate("C4", null);
        });
    }

    @Test
    public void testInvalidSupersede() {
        List<String> supersede = new ArrayList<>(Arrays.asList("C1"));
        assertThrows(IllegalArgumentException.class, () -> {
            CoxCertificate c = new CoxCertificate("C12", supersede);
        });
    }

    @Test
    public void testToString() {
        CoxCertificate c = new CoxCertificate("C12", Arrays.asList("C4", "4+"));
        assertThat(c.toString()).isEqualTo("C12;[C4, 4+]");
    }
}
