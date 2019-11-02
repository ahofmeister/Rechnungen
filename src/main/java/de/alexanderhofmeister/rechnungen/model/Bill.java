package de.alexanderhofmeister.rechnungen.model;

import de.alexanderhofmeister.rechnungen.util.DateUtil;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = Bill.NQ_FILTER, query = "SELECT b FROM Bill b JOIN b.customer c WHERE c.company LIKE :filter OR c.companyAddition LIKE :filter ORDER BY b.date DESC, c.company, c.companyAddition"),
        @NamedQuery(name = Bill.NQ_COUNT_FILTER, query = "SELECT count(b) FROM Bill b JOIN b.customer c WHERE c.company LIKE :filter OR c.companyAddition LIKE :filter"),
        @NamedQuery(name = Bill.NQ_LIST_ALL_BY_CUSTOMER, query = "SELECT b FROM Bill b where b.customer = :customer")})
public class Bill extends BaseEntity {
    public Bill() {
    }

    public static final String NQ_FILTER = "bill.filter";

    static final String NQ_LIST_ALL_BY_CUSTOMER = "bill.listallbycustomer";
    public static final String NQ_COUNT_FILTER = "bill.countFilter";

    @Required
    @Label("Rechnungsnummer")
    public Integer number;

    @Required
    @Label("Datum")
    public LocalDate date;

    @Required
    @Label("Betrag")
    public BigDecimal amount;

    @Required
    @Label("Mehrwertsteuer")
    public BigDecimal vat;

    @Required
    @Label("Zwischensumme")
    public BigDecimal subtotal;

    @Required
    @Label("Porto")
    public BigDecimal postage;

    @Required
    @Label("Summe")
    public BigDecimal total;

    @ManyToOne
    @Required
    @Label("Kunde")
    public Customer customer;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<BillEntry> entries = new ArrayList<>();

    @Override
    public String toString() {
        return "Nr. " + number + " vom " + DateUtil.formatToDisplayDate(this.date);
    }

    public BigDecimal getAmount() {
        return this.entries.stream().map(entry -> entry.amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Integer getNumber() {
        return number;
    }

    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public String getTitle() {
        return "Rechnung";
    }
}
