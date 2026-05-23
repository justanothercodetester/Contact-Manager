package org.lib;

import org.lib.Date;

public class Contact {

    public String firstName;
    public String middleName;
    public String lastName;
    public String company;

    private String phone = "";

    private String email = "";

    private String address = "";

    private boolean birthdayIsInitialized = false;
    private Date birthday;

    public String notes = "";

    public Contact(String firstName, String middleName, String lastName, String company) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean BirthdayIsInitialized() {
        return birthdayIsInitialized;
    }

    public void setBirthday(String birthday) {
        if (birthday.isEmpty()) {
            this.birthday = null;
            birthdayIsInitialized = false;
            return;
        }
        this.birthday = new Date();
        birthdayIsInitialized = true;
        String[] parts = birthday.split("/");
        this.birthday.setMonth(Integer.parseInt(parts[0]));
        this.birthday.setDay(Integer.parseInt(parts[1]));
        this.birthday.setYear(Integer.parseInt(parts[2]));
    }

    public String getBirthday() {
        if (birthdayIsInitialized)
            return this.birthday.getDate();
        return "";
    }

    @Override
    public String toString() {
        if (lastName.isEmpty())
            return firstName;
        if (middleName.isEmpty())
            return firstName + ' ' + lastName;
        return firstName + " " + middleName.charAt(0) + ". " + lastName;
    }
}
