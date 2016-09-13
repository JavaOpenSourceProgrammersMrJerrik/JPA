package com.fsnc.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_order_item")
public class OrderItem implements Serializable {
	@Id
	@Column(name = "item_id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long orderItemId;

	@Column(name = "item_name")
	private String itemName;

	@Column(name = "price")
	private Double price;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "order_item_id")
	private Order order;

	public Long getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}
