package org.foodmonks.backend.Pedido;

import org.foodmonks.backend.datatypes.CriterioQuery;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;

public class PedidoSpecification  implements Specification<Pedido> {
    private CriterioQuery criteria;

    public PedidoSpecification(CriterioQuery criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate
            (Root<Pedido> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            if (root.get(criteria.getKey()).getJavaType() == LocalDateTime.class){
                return builder.greaterThanOrEqualTo(
                        root.<LocalDateTime> get(criteria.getKey()), (LocalDateTime) criteria.getValue());
            }else
                return builder.greaterThanOrEqualTo(
                        root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            if (root.get(criteria.getKey()).getJavaType() == LocalDateTime.class){
                return builder.lessThanOrEqualTo(
                        root.<LocalDateTime> get(criteria.getKey()), (LocalDateTime) criteria.getValue());
            }else
                return builder.lessThanOrEqualTo(
                        root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}

