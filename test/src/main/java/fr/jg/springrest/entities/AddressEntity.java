package fr.jg.springrest.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "address")
public class AddressEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String city;

    private String postalCode;

    @OneToOne(mappedBy = "address")
    private ContactEntity contact;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public ContactEntity getContact() {
        return this.contact;
    }

    public void setContact(final ContactEntity contact) {
        this.contact = contact;
    }
}
