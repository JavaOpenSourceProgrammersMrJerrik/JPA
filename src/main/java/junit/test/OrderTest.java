package junit.test;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;

import com.fsnc.domain.Order;
import com.fsnc.domain.OrderItem;
import com.fsnc.domain.User;
import com.google.common.collect.Sets;

public class OrderTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAddOrder() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			
			Order order = new Order();
			order.setOrderNo("2016081524#123");
			order.setDescription("机票订单");
			
			OrderItem orderItem = new OrderItem();
			orderItem.setItemName("深圳去长沙的机票");
			orderItem.setPrice(234.00D);
			//如果OrderItem.order设置了optional=false,如果不设置order的属性,会抛出not-null property references a null or transient value: com.fsnc.domain.OrderItem.order
			orderItem.setOrder(order);
			
			Set<OrderItem> orderItems = Sets.newHashSet();
			orderItems.add(orderItem);
			
			order.setOrderItems(orderItems);
			em.persist(order);
			
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
	}
	
	@Test
	public void testAddOrderItem() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			
			OrderItem orderItem = new OrderItem();
			orderItem.setItemName("酒店");
			orderItem.setPrice(234.00D);
			
			Order order = new Order();
			order.setOrderNo("2016091524#234");
			order.setDescription("酒店订单");
			
			//如果OrderItem.order设置了optional=false,如果不设置order的属性,会抛出not-null property references a null or transient value: com.fsnc.domain.OrderItem.order
			//orderItem.setOrder(order);
			
			em.persist(orderItem);
			
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
	}

	/**
Hibernate: 
    select
        order0_.order_id as order1_2_0_,
        order0_.description as descript2_2_0_,
        order0_.order_no as order3_2_0_ 
    from
        tbl_order order0_ 
    where
        order0_.order_id=?
Hibernate: 
    select
        orderitems0_.order_item_id as order4_2_1_,
        orderitems0_.item_id as item1_1_,
        orderitems0_.item_id as item1_0_0_,
        orderitems0_.item_name as item2_0_0_,
        orderitems0_.order_item_id as order4_0_0_,
        orderitems0_.price as price0_0_ 
    from
        tbl_order_item orderitems0_ 
    where
        orderitems0_.order_item_id=?
Hibernate: 
    delete 
    from
        tbl_order_item 
    where
        item_id=?
Hibernate: 
    delete 
    from
        tbl_order_item 
    where
        item_id=?
Hibernate: 
    delete 
    from
        tbl_order 
    where
        order_id=?
	 */
	@Test
	public void testDeleteOrder() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			Order order = em.getReference(Order.class, 2L);
			/**
			 * attention: 如果order.orderItems没有设置级联,直接删除order会报错,因为orderItem外键引用到了order
			 */
			em.remove(order);
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
	}
	
	@Test
	public void testUpdateOrder() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			Order order = em.find(Order.class, 3L);
			Set<OrderItem> orderItems = order.getOrderItems();
			for(OrderItem oi : orderItems){
				oi.setItemName("修改后的Item");
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();
		emf.close();
	}

	@Test
	public void testFindOrder() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		EntityManager em = emf.createEntityManager();
		try {
			Order order = em.find(Order.class, 3L);
			System.out.println("order: " + order);
		} catch (Exception e) {
			e.printStackTrace();
		}
		em.close();
		emf.close();
	}
	
	
	/**
Hibernate: 
    select
        orderitem0_.item_id as item1_0_,
        orderitem0_.item_name as item2_0_,
        orderitem0_.order_item_id as order4_0_,
        orderitem0_.price as price0_ 
    from
        tbl_order_item orderitem0_ 
    where
        orderitem0_.order_item_id=?
Hibernate: 
    select
        order0_.order_id as order1_2_0_,
        order0_.description as descript2_2_0_,
        order0_.order_no as order3_2_0_ 
    from
        tbl_order order0_ 
    where
        order0_.order_id=?
order: [com.fsnc.domain.OrderItem@71c665be, com.fsnc.domain.OrderItem@4f1b6ffa, com.fsnc.domain.OrderItem@1254aea9]
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindOrderItemByNaviSQL() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		EntityManager em = emf.createEntityManager();
		try {
			//oi.order.orderId=:id 其实是指定OrderItem的外键 只能从多方导航到一方？
			Query query = em.createQuery("select oi from OrderItem oi where oi.order.orderId=:id");
			query.setParameter("id", 3L);
			System.out.println("order: " + query.getResultList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		em.close();
		emf.close();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindOrderByNativeSQL() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		EntityManager em = emf.createEntityManager();
		try {
			List list = em.createNativeQuery("select * from tbl_order o,tbl_order_item oi where o.order_id=oi.order_item_id and o.order_id=3").getResultList();
			for(int i=0,len=list.size();i<len;i++){
				if(list.get(i) instanceof Order){
					System.out.println((Order)list.get(i));
				}else{
					System.out.println("===" + list.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		em.close();
		emf.close();
	}

	@Test
	public void testOrderList() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		EntityManager em = emf.createEntityManager();
		try {
			@SuppressWarnings("unchecked")
			List<Order> list = em.createQuery("select o from Order o").getResultList();
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		em.close();
		emf.close();
	}
}
