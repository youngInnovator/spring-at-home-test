package com.rize.test.specification;

import com.rize.test.model.Artist;
import com.rize.test.model.Category;
import org.springframework.data.jpa.domain.Specification;

import javax.validation.ConstraintViolationException;

import java.util.Arrays;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.hasText;

public class ArtistSpecificationBuilder {
    private String category;
    private String search;
    private Integer birthdayMonth;


    public ArtistSpecificationBuilder withCategory(String category){
        this.category = category;
        return this;
    }

    public ArtistSpecificationBuilder withSearch(String search){
        this.search = search;
        return this;
    }

    public ArtistSpecificationBuilder withBirthdayMonth(Integer birthdayMonth){
        this.birthdayMonth = birthdayMonth;
        return this;
    }

    public Specification<Artist> build(){
        Specification<Artist> result = null;
        if(hasText(category)){
            result = addCriteria(null, new ArtistSpecification(
                    new SearchCriteria("category", ":", getCategory(category))));
        }

        if(birthdayMonth != null){
            result = addCriteria(result, new ArtistSpecification(
                    new SearchCriteria("birthday", "month", birthdayMonth)));
        }

        if(hasText(search)){
            Specification<Artist> firstOrLastNameSpecification = new ArtistSpecification(
                    new SearchCriteria("firstName", "like", search));
            firstOrLastNameSpecification = Specification.where(firstOrLastNameSpecification).or(
                    new ArtistSpecification(new SearchCriteria("lastName", "like", search)));
            result = addCriteria(result, firstOrLastNameSpecification);
        }

        return result;
    }

    private Specification<Artist> addCriteria(Specification<Artist> result, Specification<Artist> artistSpecification){
        if(result == null){
            return artistSpecification;
        } else {
            result = Specification.where(result).and(artistSpecification);
        }

        return result;
    }

    private Category getCategory(String category){
        try{
            return Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException ex){
            throw new ConstraintViolationException(format("Category value %s is incorrect. It must be in %s", category, Arrays.toString(Category.values())), null);
        }
    }

}
