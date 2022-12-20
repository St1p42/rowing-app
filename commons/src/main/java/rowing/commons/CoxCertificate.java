package rowing.commons;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class CoxCertificate {

    private String name;
    private List<String> isSupersededBy;

    /**
     * Create cox certificate.
     *
     * @param name - string representing the name of the certificate. Must be unique.
     * @param isSupersededBy - a list of names of certificates it supersedes.
     * @throws IllegalArgumentException - throws exception if name not valid or list doesn't contain valid certificates.
     */
    public CoxCertificate(String name, List<String> isSupersededBy) throws IllegalArgumentException {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException();
        }
        if (Certificates.existByName(name) == true) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        if (isSupersededBy == null) {
            this.isSupersededBy = isSupersededBy;
        } else {
            for (String certificate : isSupersededBy) {
                if (Certificates.existByName(certificate) == false) {
                    throw new IllegalArgumentException();
                }
            }
            this.isSupersededBy = isSupersededBy;
        }
    }

    public String getName() {
        return this.name;
    }

    public List<String> getIsSupersededBy() {
        return this.isSupersededBy;
    }

    @Override
    public String toString() {
        return this.name + ";" + this.isSupersededBy;
    }
}
