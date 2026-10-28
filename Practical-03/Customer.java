package minibank.model;
import java.io.Serializable;
import minibank.model.annotation.Id;
import minibank.model.annotation.MaxLength;

public class Customer implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private static long customerCounter = 100;
    @Id
    private final String customerId;
    @MaxLength(80)
    private String name;
    private String email;
    private String mobile;
    private Address address;

    private static String generateCustomerId() {
        return "CUST" + (++customerCounter);
    }

    public Customer(String name, String email, String mobile) {
        this(name, email, mobile, new Address("", "", ""));
    }

    public Customer(String name, String email, String mobile, Address address) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public Address getAddress() { return address; }

    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();
            if (address != null) {
                copy.address = new Address(address.line, address.city, address.pincode);
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public static class Address implements Serializable {
        private static final long serialVersionUID = 1L;
        private String line;
        private String city;
        private String pincode;

        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }
        public String getLine() { return line; }
        public String getCity() { return city; }
        public String getPincode() { return pincode; }
        @Override public String toString() { return line + ", " + city + " " + pincode; }
    }

    @Override
    public String toString() {
        return customerId + " | " + name + " | " + email + " | " + mobile;
    }
}
