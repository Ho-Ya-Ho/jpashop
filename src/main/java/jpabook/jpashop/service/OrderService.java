package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final MemberService memberService;
    private final ItemService itemService;
    private final OrderRepository orderRepository;

    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        Member member = memberService.findById(memberId);
        Item item = itemService.findById(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        Order order = Order.createOrder(member, delivery, orderItem);

        orderRepository.save(order);

        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        order.ifPresent(Order::cancel);
    }

//    public List<Order> findOrders(OrderSearch orderSearch) {
//        return orderRepository.findAllByCriteria(orderSearch);
//    }
}
