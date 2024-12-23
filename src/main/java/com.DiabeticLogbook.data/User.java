package com.DiabeticLogbook.data;

import java.util.Objects;

public class User {
    protected String name;
    protected String phone;
    protected String email;

    public User(String name, String phone, String email) {
        // Validate inputs before setting them
        validateName(name);
        validateEmail(email);
        validatePhone(phone);  // Added phone validation

        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Private validation methods to ensure code reusability
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        // You could add more sophisticated email validation if needed
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        // You could add phone number format validation if needed
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    // Getter and setter for phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        validatePhone(phone);
        this.phone = phone;
    }

    // Getter and setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    // Method to get all user details at once
    public UserDetails getUserDetails() {
        return new UserDetails(name, phone, email);
    }

    // Method to update all user details at once
    public void updateUserDetails(UserDetails details) {
        validateName(details.getName());
        validatePhone(details.getPhone());
        validateEmail(details.getEmail());

        this.name = details.getName();
        this.phone = details.getPhone();
        this.email = details.getEmail();
    }

    // toString method for debugging and logging
    @Override
    public String toString() {
        return String.format("User[name=%s, phone=%s, email=%s]",
                name, phone, email);
    }

    // Inner class to hold user details together
    public static class UserDetails {
        private final String name;
        private final String phone;
        private final String email;

        public UserDetails(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        // Getters for all fields
        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        // toString method for UserDetails
        @Override
        public String toString() {
            return String.format("UserDetails[name=%s, phone=%s, email=%s]",
                    name, phone, email);
        }

        // Equals method to compare UserDetails objects
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserDetails that = (UserDetails) o;
            return Objects.equals(name, that.name) &&
                    Objects.equals(phone, that.phone) &&
                    Objects.equals(email, that.email);
        }

        // HashCode method to complement equals
        @Override
        public int hashCode() {
            return Objects.hash(name, phone, email);
        }
    }
}
