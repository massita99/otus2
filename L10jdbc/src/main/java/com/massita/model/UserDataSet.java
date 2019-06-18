package com.massita.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Entity

@Table(name = "userdataset")
public class UserDataSet extends DataSet {

    public UserDataSet(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    private AddressDataSet address;

    @Setter
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Set<PhoneDataSet> phones = new HashSet<>();


    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Override
    public UserDataSet makeClone() {
        UserDataSet clonned = new UserDataSet(name, age);
        HashSet<PhoneDataSet> phoneDataSets = new HashSet<>();
        phoneDataSets.addAll(phones);
        clonned.setPhones(phoneDataSets);
        clonned.setAddress(address);
        return clonned;
    }
}
