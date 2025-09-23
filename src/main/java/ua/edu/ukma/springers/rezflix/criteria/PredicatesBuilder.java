package ua.edu.ukma.springers.rezflix.criteria;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.*;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class PredicatesBuilder<ENTITY> {

    @Getter
    private final List<Predicate> predicates;
    private final Root<ENTITY> root;
    private final CriteriaBuilder cb;

    public PredicatesBuilder(Root<ENTITY> root, CriteriaBuilder cb) {
        this.predicates = new ArrayList<>();
        this.root = root;
        this.cb = cb;
    }

    public PredicatesBuilder<ENTITY> or(Predicate... predicates) {
        Objects.checkIndex(0, predicates.length);
        this.predicates.add(predicates.length == 1 ? predicates[0] : cb.or(predicates));
        return this;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final PredicatesBuilder<ENTITY> like(@Nullable String value, SingularAttribute<ENTITY, String>... attributes) {
        return like(value, Arrays.stream(attributes).map(root::get).toArray(Expression[]::new));
    }

    @SafeVarargs
    public final PredicatesBuilder<ENTITY> like(@Nullable String value, Expression<String>... expressions) {
        Objects.checkIndex(0, expressions.length);

        if (StringUtils.isBlank(value))
            return this;

        String likeQuery = '%' + value.toLowerCase(Locale.ROOT) + '%';
        Predicate[] likePredicates = Arrays.stream(expressions)
                .map(expression -> cb.like(cb.lower(expression), likeQuery))
                .toArray(Predicate[]::new);

        predicates.add(cb.or(likePredicates));
        return this;
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <E, A extends Attribute<ENTITY, ?> & Bindable<E>> PredicatesBuilder<ENTITY> likeJoin(@Nullable String value, A join,
                                                                                                      SingularAttribute<E, String>... attributes)
    {
        if (StringUtils.isBlank(value))
            return this;
        Join<ENTITY, E> joined = createJoin(join);
        return like(value, Arrays.stream(attributes).map(joined::get).toArray(Expression[]::new));
    }

    public <K> PredicatesBuilder<ENTITY> eq(K value, SingularAttribute<ENTITY, K> attribute) {
        return eq(value, root.get(attribute));
    }

    public <K> PredicatesBuilder<ENTITY> eq(K value, Expression<K> expression) {
        if (value != null && !"".equals(value)) {
            predicates.add(cb.equal(expression, value));
        }
        return this;
    }

    public <K, E, A extends Attribute<ENTITY, ?> & Bindable<E>> PredicatesBuilder<ENTITY> eq(K value, A join, SingularAttribute<E, K> attribute) {
        return eq(value, join, j -> j.get(attribute));
    }

    public <K, E, A extends Attribute<ENTITY, ?> & Bindable<E>> PredicatesBuilder<ENTITY> eq(K value, A join, Function<Join<ENTITY, E>,
            Expression<K>> expression)
    {
        if (value != null && !"".equals(value)) {
            predicates.add(cb.equal(expression.apply(createJoin(join)), value));
        }
        return this;
    }

    public <K extends Comparable<? super K>> PredicatesBuilder<ENTITY> between(K min, K max, SingularAttribute<ENTITY, K> attribute) {
        return between(min, max, root.get(attribute));
    }

    public <K extends Comparable<? super K>> PredicatesBuilder<ENTITY> between(K min, K max, Expression<K> expression) {
        if (min != null && max != null) {
            predicates.add(cb.between(expression, min, max));
        } else if (min != null) {
            predicates.add(cb.greaterThanOrEqualTo(expression, min));
        } else if (max != null) {
            predicates.add(cb.lessThanOrEqualTo(expression, max));
        }
        return this;
    }

    public <K> PredicatesBuilder<ENTITY> in(Collection<K> items, SingularAttribute<ENTITY, K> attribute) {
        return in(items, root.get(attribute));
    }

    public <K> PredicatesBuilder<ENTITY> in(Collection<K> items, Expression<K> expression) {
        if (CollectionUtils.isNotEmpty(items)) {
            predicates.add(expression.in(items));
        }
        return this;
    }

    public <K, E, A extends Attribute<ENTITY, ?> & Bindable<E>> PredicatesBuilder<ENTITY> in(Collection<K> items, A join,
                                                                                             SingularAttribute<E, K> attribute)
    {
        return in(items, join, j -> j.get(attribute));
    }

    public <K, E, A extends Attribute<ENTITY, ?> & Bindable<E>> PredicatesBuilder<ENTITY> in(Collection<K> items, A join, Function<Join<ENTITY, E>,
            Expression<K>> expression)
    {
        if (CollectionUtils.isNotEmpty(items)) {
            predicates.add(expression.apply(createJoin(join)).in(items));
        }
        return this;
    }

    public <K extends Enum<K>, T extends Enum<T>> PredicatesBuilder<ENTITY> in(Collection<T> items, Function<T, K> converter,
                                                                               SingularAttribute<ENTITY, K> attribute)
    {
        return in(items, converter, root.get(attribute));
    }

    public <K extends Enum<K>, T extends Enum<T>> PredicatesBuilder<ENTITY> in(Collection<T> items, Function<T, K> converter,
                                                                               Expression<K> expression)
    {
        if (CollectionUtils.isNotEmpty(items)) {
            Collection<K> mapped = items.stream().map(converter).toList();
            predicates.add(expression.in(mapped));
        }
        return this;
    }

    public <K extends Enum<K>, T extends Enum<T>, E, A extends Attribute<ENTITY, ?> & Bindable<E>> PredicatesBuilder<ENTITY> in(
            Collection<T> items,
            Function<T, K> converter,
            A join,
            SingularAttribute<E, K> attribute
    )
    {
        return in(items, converter, join, j -> j.get(attribute));
    }

    public <K extends Enum<K>, T extends Enum<T>, E, A extends Attribute<ENTITY, ?> & Bindable<E>> PredicatesBuilder<ENTITY> in(
            Collection<T> items,
            Function<T, K> converter,
            A join,
            Function<Join<ENTITY, E>, Expression<K>> expression)
    {
        if (CollectionUtils.isNotEmpty(items)) {
            Collection<K> mapped = items.stream().map(converter).toList();
            predicates.add(expression.apply(createJoin(join)).in(mapped));
        }
        return this;
    }

    public <K> PredicatesBuilder<ENTITY> isNull(SingularAttribute<ENTITY, K> attribute) {
        predicates.add(cb.isNull(root.get(attribute)));
        return this;
    }

    public <K> PredicatesBuilder<ENTITY> isNotNull(SingularAttribute<ENTITY, K> attribute) {
        predicates.add(cb.isNotNull(root.get(attribute)));
        return this;
    }

    public <K> PredicatesBuilder<ENTITY> forcedIn(Collection<K> items, SingularAttribute<ENTITY, K> attribute) {
        if (items != null) {
            if (items.isEmpty())
                predicates.add(cb.disjunction());
            else
                in(items, attribute);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private <E, A extends Attribute<ENTITY, ?> & Bindable<E>> Join<ENTITY, E> createJoin(A attribute) {
        if (attribute instanceof SingularAttribute<?,?>) {
            return root.join((SingularAttribute<ENTITY, E>) attribute);
        }
        if (attribute instanceof PluralAttribute<?,?,?> pa) {
            return switch (pa.getCollectionType()) {
                case COLLECTION -> root.join((CollectionAttribute<ENTITY, E>) pa);
                case SET -> root.join((SetAttribute<ENTITY, E>) pa);
                case LIST -> root.join((ListAttribute<ENTITY, E>) pa);
                case MAP -> root.join((MapAttribute<ENTITY, ?, E>) pa);
            };
        }
        throw new IllegalArgumentException("Can not create join");
    }
}
