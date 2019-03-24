package de.alexanderhofmeister.rechnungen.view;

import de.alexanderhofmeister.rechnungen.model.Bill;
import de.alexanderhofmeister.rechnungen.model.Properties;
import de.alexanderhofmeister.rechnungen.service.BillService;
import de.alexanderhofmeister.rechnungen.util.ButtonUtil;
import de.alexanderhofmeister.rechnungen.util.DateUtil;
import de.alexanderhofmeister.rechnungen.util.ExportUtil;
import de.alexanderhofmeister.rechnungen.util.MoneyUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillOverviewController extends EntityOverviewController<Bill, BillEditController> {


    @Getter
    private BillService service = new BillService();

    @Override
    protected String getEditViewFileName() {
        return "editBill";
    }

    @Override
    protected String getFilterCountNamedQuery() {
        return Bill.NQ_COUNT_FILTER;
    }

    @Override
    protected String getFilterNamedQuery() {
        return Bill.NQ_FILTER;
    }

    @Override
    protected List<TableColumn<Bill, ?>> getEntityColumns() {

        TableColumn<Bill, String> customer = new TableColumn<>("Kunde");
        customer.setPrefWidth(150);

        TableColumn<Bill, String> total = new TableColumn<>("Betrag");
        total.setPrefWidth(100);

        TableColumn<Bill, String> date = new TableColumn<>("Datum");
        total.setPrefWidth(100);


        customer.setCellValueFactory(new PropertyValueFactory<>("customer"));
        date.setCellValueFactory(tableCell -> new SimpleStringProperty(DateUtil.formatToDisplayDate(tableCell.getValue().getDate())));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));

        return Arrays.asList(customer, total, date);
    }


    @Override
    List<Button> getCustomButtons(Bill bill) {
        final Button exportAsPdf = ButtonUtil.createIconButton(event -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("customer", bill.getCustomer());
            attributes.put("bill", bill);
            attributes.put("MoneyUtil", MoneyUtil.class);
            attributes.put("DateUtil", DateUtil.class);
            attributes.put("Properties", Properties.getInstance());
            ExportUtil.createFileFromTemplate(bill.getDate(), new File(ExportUtil.getFileNameBill(bill)), "bill", attributes);
        }, FontAwesomeIconName.
                SAVE, "Speichern");
        final Button printButton = ButtonUtil.createIconButton(event1 -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("customer", bill.getCustomer());
            attributes.put("bill", bill);
            attributes.put("MoneyUtil", MoneyUtil.class);
            attributes.put("DateUtil", DateUtil.class);
            attributes.put("Properties", Properties.getInstance());
            ExportUtil.printFile(ExportUtil.createFileFromTemplate(bill.getDate(), new File(ExportUtil.getFileNameBill(bill)), "bill", attributes));
        }, FontAwesomeIconName.PRINT, "Drucken");


        final Button emailButton = ButtonUtil.createIconButton(event -> {

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("bill", bill);
            try {
                String body = ExportUtil.fillTemplateFromVariables("emailBill", attributes);
                Map<String, Object> attributes1 = new HashMap<>();
                attributes1.put("customer", bill.getCustomer());
                attributes1.put("bill", bill);
                attributes1.put("MoneyUtil", MoneyUtil.class);
                attributes1.put("DateUtil", DateUtil.class);
                attributes1.put("Properties", Properties.getInstance());
                ExportUtil.sendViaEmail(ExportUtil.createFileFromTemplate(bill.getDate(), new File(ExportUtil.getFileNameBill(bill)), "bill", attributes1), bill.toString(), body, bill.getCustomer().getEmail());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, FontAwesomeIconName.SEND, "Als E-Mail senden");


        return Arrays.asList(exportAsPdf, printButton, emailButton);
    }

    @Override
    protected void mapEditEntity(Bill bill, BillEditController controller) {
        bill.setNumber(controller.getNumber());
        bill.setDate(controller.getDate());
        bill.setCustomer(controller.getCustomer());
        bill.setAmount(controller.getAmount());
        bill.setVat(controller.getVat());
        bill.setSubtotal(controller.getSubTotal());
        bill.setPostage(controller.getPostage());
        bill.setTotal(controller.getTotal());
        bill.setEntries(controller.getBillEntries());
    }
}
