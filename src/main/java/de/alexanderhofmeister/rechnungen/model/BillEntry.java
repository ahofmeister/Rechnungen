package de.alexanderhofmeister.rechnungen.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@NamedQuery(name = BillEntry.NQ_LIST_ALL_BY_BILL,
        query = "SELECT be from BillEntry be where be.bill = :bill")
public class BillEntry extends BaseEntity {

    public static final String NQ_LIST_ALL_BY_BILL = "billentry.listallbybill";
    private static final long serialVersionUID = 1L;

    @Required
    @Label("Position")
    private String position;

    @Label("Zeitraum")
    @Required
    private LocalDate period = LocalDate.now();

    @Required
    @Label("Betrag")
    private BigDecimal amount;

    @ManyToOne
    @Required
    @Label("Rechnung")
    private Bill bill;

    public BillEntry(@NonNull Bill bill) {
        this.bill = bill;
    }

    @Override
    public String toString() {
        return "Eintrag " + (StringUtils.isEmpty(position) ? "[Keine Position angegeben]" : position)
                + " von Rechnung Nr." + bill.getNumber() + " von " + bill.getCustomer();
    }

}
