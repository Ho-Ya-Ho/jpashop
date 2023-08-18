package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.dto.OrderSearch;
import jpabook.jpashop.dto.OrderSimpleQueryDto;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findAll(OrderSearch orderSearch);

    List<Order> findAllByV3Querydsl();

    List<OrderSimpleQueryDto> findAllByV4Querydsl();

    List<Order> findAllWithMemberDelivery(int offset, int limit);

    List<Order> findAllWithItem();
}
