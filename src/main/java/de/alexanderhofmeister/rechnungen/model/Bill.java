package de.alexanderhofmeister.rechnungen.model;

import de.alexanderhofmeister.rechnungen.util.DateUtil;
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
@NamedQueries({
        @NamedQuery(name = Bill.NQ_FILTER, query = "SELECT b FROM Bill b JOIN b.customer c WHERE c.company LIKE :filter OR c.companyAddition LIKE :filter"),
        @NamedQuery(name = Bill.NQ_COUNT_FILTER, query = "SELECT count(b) FROM Bill b JOIN b.customer c WHERE c.company LIKE :filter OR c.companyAddition LIKE :filter"),
        @NamedQuery(name = Bill.NQ_LIST_ALL_BY_CUSTOMER, query = "SELECT b FROM Bill b where b.customer = :customer")})
public class Bill extends BaseEntity {

    public static final String NQ_FILTER = "bill.filter";

    public static final String NQ_LIST_ALL_BY_CUSTOMER = "bill.listallbycustomer";
    public static final String NQ_COUNT_FILTER = "bill.countFilter";
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
    @Required
    @Label("Kunde")
    private Customer customer;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BillEntry> entries = new ArrayList<>();

    @Override
    public String toString() {
        return "Nr. " + number + " vom " + DateUtil.formatToDisplayDate(this.date);
    }

    public BigDecimal getAmount() {
        return this.entries.stream().map(BillEntry::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String getTitle() {
        return "Rechnung";
    }

}
