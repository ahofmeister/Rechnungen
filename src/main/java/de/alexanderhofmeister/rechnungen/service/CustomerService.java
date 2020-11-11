package de.alexanderhofmeister.rechnungen.service;


import de.alexanderhofmeister.rechnungen.model.BusinessException;
import de.alexanderhofmeister.rechnungen.model.Customer;


public class CustomerService extends AbstractEntityService<Customer> {

  public Customer findByCompany(String company, String companyAddition) {
    return findSingleWithNamedQuery(Customer.NQ_FIND_BY_COMPANY,
        QueryParameter.with("company", company).and("companyAddition", companyAddition)
            .parameters());
  }

  @Override
  void validate(Customer entity) throws BusinessException {
    if (entity.isNew()) {
      Customer duplicate = findByCompany(entity.company, entity.companyAddition);
      if (duplicate != null) {
        throw new BusinessException(
            "Kunde: " + entity.company + " - " + entity.companyAddition + " existiert bereits!");
      }
    }
  }
}
