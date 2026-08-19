package com.example.ECommerce.Platform.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderItemId;

    private Integer quantity;

    private Double price;
    private Float discount;
    private Double sellPrice;
    private Double discountedPrice;
    private Double finalPrice;//after discount per product
    private Double totalPrice;//qty* after Discounted price
    private Double totalSellPrice;//qty*sellPrice
    private Double totalDisPrice;


    @ManyToOne
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_orderitem_orders")
    )
    private Orders orders;

    @ManyToOne
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_orderitem_product")
    )
    private Product product;

    @Builder.Default
    @OneToMany(mappedBy = "orderItem")
    private List<ReturnEntity> returns = new ArrayList<>();

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Review review;

    @Builder.Default
    private Integer returnQty = 0;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime refundAt;

    private LocalDateTime refundInitiatedAt;

    private Integer returnWindowInDays;

    private String warrantyPeriod;
}
