package com.chavaillaz.jaxb.stream;

import com.chavaillaz.jaxb.stream.schemaorders.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;

import static org.fest.assertions.Assertions.assertThat;

public class StreamingComplexXsdTest {

    @Test
    public void testComplexCase() throws Exception {
        File temp = File.createTempFile("xsdtest", "output.xml");
        try (StreamingMarshaller<Root> marshaller = new StreamingMarshaller<>(Root.class)) {
            marshaller.open(new FileOutputStream(temp));
            writeCustomers(marshaller);
            writeOrders(marshaller);
        }
        assertThat(temp).hasSameContentAs(new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("examples/customersandorders.xml")).toURI()));
    }

    @SneakyThrows
    private void writeCustomers(StreamingMarshaller<Root> marshaller) {
        try (StreamingMarshaller<Root.Customers> customersMarshaller = marshaller.startMarshallingRelatedEntity(Root.Customers.class, "customers")) {
            for (int i = 0; i < 5; i++) {
                writeCustomer(customersMarshaller, i);
            }
        }
    }

    @SneakyThrows
    private void writeCustomer(StreamingMarshaller<Root.Customers> customersMarshaller, int i) {
        try (StreamingMarshaller<CustomerType> singleCustomerMarshaller = customersMarshaller.startMarshallingRelatedEntity(CustomerType.class, "customers")) {
            singleCustomerMarshaller.write(String.class, "companyName", "Company " + i);
            singleCustomerMarshaller.write(String.class, "contactName", "Name " + i);
            singleCustomerMarshaller.write(String.class, "contactTitle", "Mr");
            singleCustomerMarshaller.write(String.class, "phone", "0123456789" + i);
            if (i % 2 == 0) {
                singleCustomerMarshaller.write(String.class, "fax", "0123456789" + i);
            }
            singleCustomerMarshaller.write(String.class, "customerID", String.valueOf(i));
            writeAddress(singleCustomerMarshaller, i);
        }
    }


    @SneakyThrows
    private void writeAddress(StreamingMarshaller<CustomerType> singleCustomerMarshaller, int i) {
        try (StreamingMarshaller<AddressType> addressMarshaller = singleCustomerMarshaller.startMarshallingRelatedEntity(AddressType.class, "fullAddress")) {
            addressMarshaller.write(String.class, "address", i + " China street");
            addressMarshaller.write(String.class, "city", "Shanghai");
            addressMarshaller.write(String.class, "region", "East China");
            addressMarshaller.write(String.class, "postalCode", "20000" + i);
            addressMarshaller.write(String.class, "country", "China");
            addressMarshaller.write(String.class, "customerID", "SAMPLEID" + i);
        }
    }

    @SneakyThrows
    private void writeOrders(StreamingMarshaller<Root> marshaller) {
        try (StreamingMarshaller<Root.Orders> ordersMarshaller = marshaller.startMarshallingRelatedEntity(Root.Orders.class, "orders")) {
            for (int i = 0; i < 10; i++) {
                writeOrder(ordersMarshaller, i);
            }
        }
    }

    @SneakyThrows
    private void writeOrder(StreamingMarshaller<Root.Orders> ordersMarshaller, int i) {
        try (StreamingMarshaller<OrderType> singleOrderMarshaller = ordersMarshaller.startMarshallingRelatedEntity(OrderType.class, "orders")) {
            singleOrderMarshaller.write(String.class, "customerID", String.valueOf(i));
            singleOrderMarshaller.write(String.class, "employeeID", "Employee " + i);
            GregorianCalendar orderDate = buildDate(2024, 7, i);
            singleOrderMarshaller.write(XMLGregorianCalendar.class, "orderDate", DatatypeFactory.newInstance().newXMLGregorianCalendar(orderDate));

            GregorianCalendar requiredDate = buildDate(2025, 1, 15 + i);
            singleOrderMarshaller.write(XMLGregorianCalendar.class, "requiredDate", DatatypeFactory.newInstance().newXMLGregorianCalendar(requiredDate));
            writeShipInfo(singleOrderMarshaller, i);
        }
    }

    @SneakyThrows
    private void writeShipInfo(StreamingMarshaller<OrderType> singleOrderMarshaller, int i) {
        try (StreamingMarshaller<ShipInfoType> shipInfoMarshaller = singleOrderMarshaller.startMarshallingRelatedEntity(ShipInfoType.class, "shipInfo")) {
            shipInfoMarshaller.write(BigDecimal.class, "shipVia", new BigDecimal(i * 100));
            shipInfoMarshaller.write(BigDecimal.class, "freight", new BigDecimal(i * 1000));
            shipInfoMarshaller.write(String.class, "shipName", "Ship " + i);
            shipInfoMarshaller.write(String.class, "shipAddress", i + " Sample street");
            shipInfoMarshaller.write(String.class, "shipCity", "Sheffield");
            shipInfoMarshaller.write(String.class, "shipRegion", "South Yorkshire");
            shipInfoMarshaller.write(String.class, "shipPostalCode", "S" + i + " 1AB");
            shipInfoMarshaller.write(String.class, "shipCountry", "United Kingdom");
            GregorianCalendar shippedDate = buildDate(2024, 7, 25 + i);
            shipInfoMarshaller.write(XMLGregorianCalendar.class, "shippedDate", DatatypeFactory.newInstance().newXMLGregorianCalendar(shippedDate));
        }
    }


    private GregorianCalendar buildDate(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.MONTH, month);
        calendar.set(GregorianCalendar.DATE, day);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, calendar.getActualMinimum(GregorianCalendar.HOUR_OF_DAY));
        calendar.set(GregorianCalendar.MINUTE, calendar.getActualMinimum(GregorianCalendar.MINUTE));
        calendar.set(GregorianCalendar.SECOND, calendar.getActualMinimum(GregorianCalendar.SECOND));
        calendar.set(GregorianCalendar.MILLISECOND, calendar.getActualMinimum(GregorianCalendar.MILLISECOND));
        return calendar;
    }
}
