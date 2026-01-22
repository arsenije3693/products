package edu.brajovic.products.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("USERS")
public class UserEntity {
    @Id
    @Column("ID")
    private int id;

    @Column("USERNAME")
    private String username;

    @Column("PASSWORD")
    private String password;

    @Column("ROLE")
    private double role;

    @Column("ENABLED")
    private int enabled;
}
