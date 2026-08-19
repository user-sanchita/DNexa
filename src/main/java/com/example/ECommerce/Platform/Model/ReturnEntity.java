package com.example.ECommerce.Platform.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReturnEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String returnId;

    @Builder.Default
    private Integer returnQty=0;
    private Double refundAmount;

    @Enumerated(EnumType.STRING)
    private ReturnReason reason;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    private LocalDateTime requestDate;

    private String description;

    private String adminComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    private String refundType;
    private String bankAccountNo;
    private String bankName;
    private String ifscCode;
    private String accountHolderName;

}
