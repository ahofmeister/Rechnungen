package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Customer;
import de.alexanderhofmeister.rechnungen.service.CustomerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Arrays;
import java.util.List;

public class CustomerOverviewController extends EntityOverviewController<Customer, CustomerEditController> {

    private CustomerService service = new CustomerService();

    @Override
    public CustomerService getService() {
        return this.service;
    }

    @Override
    protected String getFilterNamedQuery() {
        return Customer.NQ_FILTER;
    }

    @Override
    protected String getFilterCountNamedQuery() {
        return Customer.NQ_COUNT_FILTER;
    }

    @Override
    protected String getEditViewFileName() {
        return "editCustomer";
    }


    @Override
    protected List<TableColumn<Customer, ?>> getEntityColumns() {
        TableColumn<Customer, String> customer = new TableColumn<>("Kunde");
        customer.setPrefWidth(150);
        customer.setCellValueFactory(new PropertyValueFactory<>("company"));

        TableColumn<Customer, String> address = new TableColumn<>("Anschrift");
        address.setPrefWidth(300);
        address.setCellValueFactory(tableCell -> new SimpleStringProperty(tableCell.getValue().getAddress()));

        return Arrays.asList(customer, address);
    }


    @Override
    protected void mapEditEntity(Customer customer, CustomerEditController controller) {
        customer.company = controller.getCompany();
        customer.companyAddition = controller.getCompanyAddition();
        customer.street = controller.getStreet();
        customer.streetNumber = controller.getStreetNumber();
        customer.zipCode = controller.getZipCode();
        customer.city = controller.getCity();
        customer.email = controller.getEmail();
        customer.contactPerson = controller.getContactPerson();
    }

    @Override
    List<Button> getCustomButtons(Customer entity) {
        return null;
    }

}