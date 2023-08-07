package com.cg.model;

import org.springframework.validation.Errors;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "deposits")
public class Deposits extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //tự động tăng
    private Long id;

    @ManyToOne   //đánh dấu quan hệ nhiều một
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)//tạo khóa ngoại tham chiếu đến Id trong Customer
    private Customer customer;

    @Column(nullable = false)
    private BigDecimal transaction_Amount;

    public Deposits() {
    }

    public Deposits(Long id, Customer customer, BigDecimal transaction_Amount) {
        this.id = id;
        this.customer = customer;
        this.transaction_Amount = transaction_Amount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getTransaction_Amount() {
        return transaction_Amount;
    }

    public void setTransaction_Amount(BigDecimal transaction_Amount) {
        this.transaction_Amount = transaction_Amount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
