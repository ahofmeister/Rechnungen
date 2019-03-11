package de.alexanderhofmeister.rechnungen.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@NamedQuery(name = Customer.NQ_LIST_ALL, query = "SELECT c FROM Customer c")
public class Customer extends BaseEntity {

    public static final String NQ_LIST_ALL = "customer.listall";

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

    public String getAddress() {
        return street + " " + streetNumber + ", " + zipCode + " " + city;
    }

}
