package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Customer;
import de.alexanderhofmeister.rechnungen.service.CustomerService;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class CustomerOverviewController extends EntityOverviewController<Customer, CustomerEditController> {

    @Getter
    private CustomerService service = new CustomerService();


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
        customer.setCompany(controller.getCompany());
        customer.setCompanyAddition(controller.getCompanyAddition());
        customer.setStreet(controller.getStreet());
        customer.setStreetNumber(controller.getStreetNumber());
        customer.setZipCode(controller.getZipCode());
        customer.setCity(controller.getCity());
        customer.setEmail(controller.getEmail());
        customer.setContactPerson(controller.getContactPerson());
    }

    @Override
    List<Button> getCustomButtons(Customer entity) {
        return null;
    }

}