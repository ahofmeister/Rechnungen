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
        @NamedQuery(name = Customer.NQ_LIST_ALL, query = "SELECT c FROM Customer c"),
        @NamedQuery(name = Customer.NQ_FIND_BY_COMPANY, query = "SELECT c FROM Customer c where c.company = :company")})
public class Customer extends BaseEntity {

    public static final String NQ_LIST_ALL = "customer.listAll";
    public static final String NQ_FIND_BY_COMPANY = "customer.findByCompany";

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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Bill> bills = new ArrayList<>();

    public String getAddress() {
        return street + " " + streetNumber + ", " + zipCode + " " + city;
    }

    @Override
    public String toString() {
        return company;
    }

}
