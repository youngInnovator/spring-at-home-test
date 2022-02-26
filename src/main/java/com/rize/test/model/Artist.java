package com.rize.test.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "artists")
public class Artist implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Date birthday;

    private String email;

    private String notes;

    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "timestamp with time zone")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp with time zone")
    @UpdateTimestamp
    private Instant updatedAt;

    public void setCategory(String category){
        this.category = Category.valueOf(category.toUpperCase());
    }
}
