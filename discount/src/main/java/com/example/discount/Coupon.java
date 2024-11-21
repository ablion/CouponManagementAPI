package com.example.discount;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String type; // cart-wise, product-wise, bxgy

	@Lob
	private String details; // Store coupon specifics as JSON string

	private Date expirationDate;

	private Date createdAt = new Date();
}

