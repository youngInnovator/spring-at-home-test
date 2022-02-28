package com.rize.test.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "artists")
public class Artist implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    @Size(max = 50, message = "firstName must be less than or equal to 50")
    @NotEmpty(message = "firstName cannot be empty. Please provide value for firstName")
    private String firstName;

    @Column(name = "middle_name")
    @Size(max = 50, message = "middleName must be less than or equal to 50")
    private String middleName;

    @Column(name = "last_name")
    @Size(max = 50, message = "lastName must be less than or equal to 50")
    @NotEmpty(message = "lastName cannot be empty. Please provide value for lastName")
    private String lastName;

    @NotNull(message = "category cannot be empty. Please provide value for category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @JsonFormat(pattern="yyyy-MM-dd", lenient = OptBoolean.FALSE)
    @NotNull(message = "birthday cannot be empty. Please provide value for birthday")
    private Date birthday;

    @Size(max = 50, message = "email must be less than or equal to 50")
    @NotEmpty(message = "email cannot be empty. Please provide value for email")
    @Email(message = "email must be a valid email address. Please provide correct value.")
    private String email;

    @Size(max = 50, message = "notes must be less than or equal to 200")
    private String notes;

    @JsonIgnore
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "timestamp with time zone")
    @CreationTimestamp
    private Instant createdAt;

    @JsonIgnore
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp with time zone")
    @UpdateTimestamp
    private Instant updatedAt;

    public void setCategory(String category){
        this.category = Category.valueOf(category.toUpperCase());
    }
}
