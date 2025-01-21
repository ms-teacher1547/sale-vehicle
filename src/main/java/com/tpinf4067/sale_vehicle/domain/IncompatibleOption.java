package com.tpinf4067.sale_vehicle.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "incompatible_options")
public class IncompatibleOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Option option1;

    @ManyToOne
    private Option option2;

    public IncompatibleOption() {}

    public IncompatibleOption(Option option1, Option option2) {
        this.option1 = option1;
        this.option2 = option2;
    }

    public Long getId() {
        return id;
    }

    public Option getOption1() {
        return option1;
    }

    public Option getOption2() {
        return option2;
    }
}
