package de.alexanderhofmeister.rechnungen.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = Customer.NQ_FILTER, query = "SELECT c FROM Customer c WHERE c.company LIKE :filter OR c.companyAddition LIKE :filter"),
        @NamedQuery(name = Customer.NQ_COUNT_FILTER, query = "SELECT count(c) FROM Customer c WHERE c.company LIKE :filter OR c.companyAddition LIKE :filter"),
        @NamedQuery(name = Customer.NQ_FIND_BY_COMPANY, query = "SELECT c FROM Customer c where c.company = :company")})
public class Customer extends BaseEntity {

    public static final String NQ_FILTER = "customer.filterCompanyAndCompanyAddition";
    public static final String NQ_FIND_BY_COMPANY = "customer.findByCompany";
    public static final String NQ_COUNT_FILTER = "customer.countFilterCompanyAndCompanyAddition";

    private static final long serialVersionUID = 1L;


    @Required
    @Label("Firma")
    private String company;

    @Required
    @Label("Firmenzusatz")
    private String companyAddition;

    @Required
    @Label("Straße")
    private String street;

    @Required
    @Label("Hausnummer")
    private String streetNumber;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.MERGE)
    private List<Bill> bills = new ArrayList<>();

    @Required
    @Label("Postleitzahl")
    private String zipCode;

    @Required
    @Label("Ort")
    private String city;

    @Label("E-Mail")
    private String email;

    @Label("Kontaktperson")
    private String contactPerson;

    @Override
    public String getTitle() {
        return "Kunde";
    }

    public String getAddress() {
        return street + " " + streetNumber + ", " + zipCode + " " + city;
    }

    @Override
    public String toString() {
        return company;
    }

}
