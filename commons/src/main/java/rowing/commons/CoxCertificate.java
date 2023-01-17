package rowing.commons;

import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
public class CoxCertificate {

    private String name;
    private List<String> supersedes;

    /**
     * Create cox certificate.
     *
     * @param name - string representing the name of the certificate. Must be unique.
     * @param isSupersededBy - a list of names of certificates it supersedes.
     * @throws IllegalArgumentException - throws exception if name not valid or list doesn't contain valid certificates.
     */
    public CoxCertificate(String name, List<String> isSupersededBy) throws IllegalArgumentException {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Name of the certificate cannot be empty");
        }
        if (Certificates.existByName(name) == true) {
            throw new IllegalArgumentException("A certificate with this name already exists");
        }
        this.name = name;
        if (isSupersededBy == null) {
            this.supersedes = supersedes;
        } else {
            for (String certificate : isSupersededBy) {
                if (Certificates.existByName(certificate) == false) {
                    throw new IllegalArgumentException("Certificate is not recognized");
                }
            }
            this.supersedes = isSupersededBy;
        }
    }

    public String getName() {
        return this.name;
    }

    public List<String> getSupersedes() {
        return this.supersedes;
    }

    @Override
    public String toString() {
        return this.name + ";" + this.supersedes;
    }
}
