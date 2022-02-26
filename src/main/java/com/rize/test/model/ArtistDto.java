package com.rize.test.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rize.test.validator.EnumValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class ArtistDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 50, message = "firstName must be less than or equal to 50")
    @NotEmpty(message = "firstName cannot be empty. Please provide value for firstName")
    private String firstName;

    @Size(max = 50, message = "middleName must be less than or equal to 50")
    private String middleName;

    @Size(max = 50, message = "lastName must be less than or equal to 50")
    @NotEmpty(message = "lastName cannot be empty. Please provide value for lastName")
    private String lastName;

    @NotNull(message = "category cannot be empty. Please provide value for category")
    @EnumValidator(enumClass = Category.class, message = "Category value must be one of [ACTOR, PAINTER, SCULPTOR]")
    private String category;

    @JsonFormat(pattern="yyyy-MM-dd")
    @NotNull(message = "birthday cannot be empty. Please provide value for birthday")
    private Date birthday;

    @Size(max = 50, message = "email must be less than or equal to 50")
    @NotEmpty(message = "email cannot be empty. Please provide value for email")
    @Email(message = "email must be a valid email address. Please provide correct value.")
    private String email;

    @Size(max = 50, message = "notes must be less than or equal to 200")
    private String notes;

    public void setCategory(Category category) {
        this.category = category.name();
    }

    public void setCategory(String category){
        this.category = category;
    }
}
