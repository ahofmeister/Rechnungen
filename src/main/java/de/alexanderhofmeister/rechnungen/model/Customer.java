package de.alexanderhofmeister.rechnungen.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = Customer.NQ_FILTER, query = "SELECT c FROM Customer c WHERE c.company LIKE :filter OR c.companyAddition LIKE :filter ORDER BY c.company, c.companyAddition"),
        @NamedQuery(name = Customer.NQ_COUNT_FILTER, query = "SELECT count(c) FROM Customer c WHERE c.company LIKE :filter OR c.companyAddition LIKE :filter"),
        @NamedQuery(name = Customer.NQ_FIND_BY_COMPANY, query = "SELECT c FROM Customer c where c.company = :company and c.companyAddition = :companyAddition")})
public class Customer extends BaseEntity {

    public Customer() {
    }

    public static final String NQ_FILTER = "customer.filterCompanyAndCompanyAddition";
    public static final String NQ_FIND_BY_COMPANY = "customer.findByCompany";
    public static final String NQ_COUNT_FILTER = "customer.countFilterCompanyAndCompanyAddition";

    @Required
    @Label("Firma")
    public String company;

    public String getCompany() {
        return company;
    }

    @Required
    @Label("Firmenzusatz")
    public String companyAddition;

    public String getCompanyAddition() {
        return companyAddition;
    }

    @Required
    @Label("Straße")
    public String street;

    public String getStreet() {
        return street;
    }

    @Required
    @Label("Hausnummer")
    public String streetNumber;

    public String getStreetNumber() {
        return streetNumber;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer", cascade = CascadeType.MERGE)
    public List<Bill> bills = new ArrayList<>();

    @Required
    @Label("Postleitzahl")
    public String zipCode;

    public String getZipCode() {
        return zipCode;
    }

    @Required
    @Label("Ort")
    public String city;

    public String getCity() {
        return city;
    }

    @Label("E-Mail")
    public String email;

    @Label("Kontaktperson")
    public String contactPerson;

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
