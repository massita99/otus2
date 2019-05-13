package com.massita.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private Set<PhoneDataSet> phones;


    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;
}
