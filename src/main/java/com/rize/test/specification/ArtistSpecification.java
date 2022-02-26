package com.rize.test.specification;

import com.rize.test.model.Artist;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ArtistSpecification implements Specification<Artist> {

    private SearchCriteria criteria;

    public ArtistSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate
            (Root<Artist> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase(":")) {
            return builder.equal(
                    root.<String>get(criteria.getKey()), criteria.getValue());
        } else if (criteria.getOperation().equalsIgnoreCase("like")) {
            return builder.like(
                    root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        } else if (criteria.getOperation().equalsIgnoreCase("month")) {
            return builder.equal(
                    builder.function("MONTH", Integer.class, root.<String>get(criteria.getKey())), criteria.getValue());
        }
        return null;
    }
}
