package jpabook.jpashop.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.dto.OrderSearch;
import jpabook.jpashop.dto.OrderSimpleQueryDto;
import jpabook.jpashop.repository.OrderRepositoryCustom;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static jpabook.jpashop.domain.QDelivery.delivery;
import static jpabook.jpashop.domain.QMember.*;
import static jpabook.jpashop.domain.QOrder.*;

public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderRepositoryImpl(EntityManager em) {
        this.query = new JPAQueryFactory(em);
        this.em = em;
    }

    @Override
    public List<Order> findAllByV3Querydsl() {
        return query.selectFrom(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .fetchJoin()
                .fetch();
    }

    @Override
    public List<OrderSimpleQueryDto> findAllByV4Querydsl() {
        return query.select(Projections.constructor(OrderSimpleQueryDto.class, order.id, member.name, order.orderDate, order.status, delivery.address))
                .from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .fetch();
    }

    @Override
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }

    @Override
    public List<Order> findAll(OrderSearch orderSearch) {
        return query.select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression statusEq(OrderStatus statusEq) {
        if (statusEq == null) {
            return null;
        }
        return order.status.eq(statusEq);
    }

    private BooleanExpression nameLike(String memberName) {
        if (!StringUtils.hasText(memberName)) {
            return null;
        }
        return member.name.contains(memberName);
    }
}
