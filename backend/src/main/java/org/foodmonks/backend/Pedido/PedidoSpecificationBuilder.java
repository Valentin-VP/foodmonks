package org.foodmonks.backend.Pedido;

import org.foodmonks.backend.datatypes.CriterioQuery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PedidoSpecificationBuilder {
    private final List<CriterioQuery> params;

    public PedidoSpecificationBuilder() {
        params = new ArrayList<CriterioQuery>();
    }

    public PedidoSpecificationBuilder with(String key, String operation, Object value, boolean isOrPredicate) {
        params.add(new CriterioQuery(key, operation, value, isOrPredicate));
        return this;
    }

    public PedidoSpecificationBuilder with(CriterioQuery spec) {
        params.add(spec);
        return this;
    }

    public Specification<Pedido> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(PedidoSpecification::new)
                .collect(Collectors.toList());

        Specification result = specs.get(0);

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i)
                    .isOrPredicate() ? Specification.where(result).or(specs.get(i))
                    : Specification.where(result)
                    .and(specs.get(i));
        }
        return result;
    }

}
