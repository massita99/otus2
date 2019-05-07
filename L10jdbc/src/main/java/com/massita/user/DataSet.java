package com.massita.user;

import lombok.Getter;
import lombok.Setter;

public abstract class DataSet {

    @Setter
    @Getter
    private transient int id;
}
