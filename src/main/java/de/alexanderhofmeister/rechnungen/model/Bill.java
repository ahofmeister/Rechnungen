package de.alexanderhofmeister.rechnungen.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@NoArgsConstructor
@NamedQuery(name = Bill.NQ_LIST_ALL_BY_CUSTOMER,
        query = "SELECT b FROM Bill b where b.customer = :customer")
public class Bill extends BaseEntity {
    public static final String NQ_LIST_ALL_BY_CUSTOMER = "bill.listallbycustomer";
    private static final long serialVersionUID = 1L;
    @Required
    @Label("Rechnungsnummer")
    private Integer number;

    @Required
    @Label("Datum")
    private LocalDate date;

    @Required
    @Label("Betrag")
    private BigDecimal amount;

    @Required
    @Label("Mehrwertsteuer")
    private BigDecimal vat;

    @Required
    @Label("Zwischensumme")
    private BigDecimal subtotal;

    @Required
    @Label("Porto")
    private BigDecimal postage;

    @Required
    @Label("Summe")
    private BigDecimal total;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BillEntry> entries = new ArrayList<>();

    @Override
    public String toString() {
        return "Rechnung Nr. " + number + " von " + this.customer;
    }
}
