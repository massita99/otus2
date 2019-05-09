package com.massita.model;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "phonedataset")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PhoneDataSet extends DataSet {

    @Column(name = "number")
    @Getter
    @Setter
    private String number;


}
