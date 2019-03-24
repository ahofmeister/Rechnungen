package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CustomerEditController extends EntityEditController<Customer> {

    @FXML
    private TextField company;

    @FXML
    private TextField companyAddition;

    @FXML
    private TextField street;

    @FXML
    private TextField streetNumber;

    @FXML
    private TextField zipCode;

    @FXML
    private TextField city;

    @FXML
    private TextField email;

    @FXML
    private TextField contactPerson;

    @Override
    protected void mapEntity(Customer customer) {
        setCompany(customer.getCompany());
        setCompanyAddition(customer.getCompanyAddition());
        setStreet(customer.getStreet());
        setStreetNumber(customer.getStreetNumber());
        setZipCode(customer.getZipCode());
        setCity(customer.getCity());
        setEmail(customer.getEmail());
        setContactPerson(customer.getContactPerson());
    }

    private void setCompany(String company) {
        this.company.setText(company);
    }

    private void setCompanyAddition(String companyAddition) {
        this.companyAddition.setText(companyAddition);
    }

    private void setStreet(String street) {
        this.street.setText(street);
    }

    private void setStreetNumber(String streetNumber) {
        this.streetNumber.setText(streetNumber);
    }

    private void setZipCode(String zipCode) {
        this.zipCode.setText(zipCode);
    }

    private void setCity(String city) {
        this.city.setText(city);
    }

    private void setEmail(String email) {
        this.email.setText(email);
    }

    private void setContactPerson(String contactPerson) {
        this.contactPerson.setText(contactPerson);
    }

    String getCompany() {
        return this.company.getText();
    }

    String getCompanyAddition() {
        return this.companyAddition.getText();
    }

    String getStreet() {
        return this.street.getText();
    }

    String getStreetNumber() {
        return this.streetNumber.getText();
    }

    String getZipCode() {
        return this.zipCode.getText();
    }

    String getCity() {
        return this.city.getText();
    }

    String getEmail() {
        return this.email.getText();
    }

    String getContactPerson() {
        return this.contactPerson.getText();
    }


}