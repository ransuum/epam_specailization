package org.epam.utils.querybuilder;

import jakarta.persistence.TypedQuery;

@FunctionalInterface
public interface TypedQueryBuilder<T> {
    TypedQuery<T> createQuery(StringBuilder jpqlBuilder);
}
