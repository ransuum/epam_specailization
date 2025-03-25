package org.epam.utils.querybuilder;

import jakarta.persistence.TypedQuery;

public interface TypedQueryBuilder<T> {
    TypedQuery<T> createQuery(StringBuilder jpqlBuilder);
}
