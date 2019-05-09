package com.massita.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "addressdataset")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AddressDataSet extends DataSet {

    @Column(name = "street")
    @Getter
    @Setter
    private String street;
}
