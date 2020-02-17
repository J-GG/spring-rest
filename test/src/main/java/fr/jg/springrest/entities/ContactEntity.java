package fr.jg.springrest.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "contact")
public class ContactEntity {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "contact")
    private List<CompanyEntity> company;

    @OneToOne
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public List<CompanyEntity> getCompany() {
        return this.company;
    }

    public void setCompany(final List<CompanyEntity> company) {
        this.company = company;
    }

    public AddressEntity getAddress() {
        return this.address;
    }

    public void setAddress(final AddressEntity address) {
        this.address = address;
    }
}
