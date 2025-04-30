package redirex.shipping.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users") // Sugestão de boas práticas: nomear a tabela explicitamente
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fullname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String complement;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String occupation;

    @Column
    private String role;

    // Constructors
    public User() {}

    public User(Long id, String fullname, String email, String password, String cpf, String phone,
                String address, String complement, String city, String state, String zipcode,
                String country, String occupation, String role) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.cpf = cpf;
        this.phone = phone;
        this.address = address;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.country = country;
        this.occupation = occupation;
        this.role = role;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Implementação manual do Builder Pattern
    public static class Builder {
        private Long id;
        private String fullname;
        private String email;
        private String password;
        private String cpf;
        private String phone;
        private String address;
        private String complement;
        private String city;
        private String state;
        private String zipcode;
        private String country;
        private String occupation;
        private String role;

        public Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fullname(String fullname) {
            this.fullname = fullname;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder cpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder complement(String complement) {
            this.complement = complement;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder zipcode(String zipcode) {
            this.zipcode = zipcode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder occupation(String occupation) {
            this.occupation = occupation;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(id, fullname, email, password, cpf, phone, address, complement, city, state, zipcode, country, occupation, role);
        }
    }
}
