package de.alexanderhofmeister.rechnungen.service;


import de.alexanderhofmeister.rechnungen.model.Customer;


public class CustomerService extends AbstractEntityService<Customer> {

    public Customer findByCompany(String company, String companyAddition) {
        return findSingleWithNamedQuery(Customer.NQ_FIND_BY_COMPANY, QueryParameter.with("company", company).and("companyAddition", companyAddition).parameters());
    }
}
