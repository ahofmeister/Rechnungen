package de.alexanderhofmeister.rechnungen.model;


import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NamedQuery(name = BillEntry.NQ_LIST_ALL_BY_BILL,
        query = "SELECT be from BillEntry be where be.bill = :bill")
public class BillEntry extends BaseEntity {

    public BillEntry() {
    }

    static final String NQ_LIST_ALL_BY_BILL = "billentry.listallbybill";

    @Required
    @Label("Position")
    public String position;

    @Label("Zeitraum")
    @Required
    public LocalDate period = LocalDate.now();

    @Required
    @Label("Betrag")
    public BigDecimal amount;

    @ManyToOne
    @Required
    @Label("Rechnung")
    private Bill bill;

    public String getPosition() {
        return position;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public BillEntry(Bill bill) {
        this.bill = bill;
    }

    @Override
    public String toString() {
        return "Eintrag " + (StringUtils.isEmpty(position) ? "[Keine Position angegeben]" : position)
                + " von Rechnung Nr." + bill.number + " von " + bill.customer;
    }

}
