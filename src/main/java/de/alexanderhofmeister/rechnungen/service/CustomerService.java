package de.alexanderhofmeister.rechnungen.service;


import de.alexanderhofmeister.rechnungen.model.Customer;


public class CustomerService extends AbstractEntityService<Customer> {

    public Customer findByCompany(String company) {
        return findSingleWithNamedQuery(Customer.NQ_FIND_BY_COMPANY, QueryParameter.with("company", company).parameters());
    }
}
