package com.example.discount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

	@Autowired
	private CouponService couponService;

	@GetMapping
	public List<Coupon> getAllCoupons() {
		return couponService.getAllCoupons();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
		return couponService.getCouponById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Coupon createCoupon(@RequestBody Coupon coupon) {
		return couponService.createCoupon(coupon);
	}

	@PostMapping("/apply-coupon/{id}")
	public Map<String, Object> applyCoupon(@PathVariable Long id, @RequestBody Cart cart) {
		return couponService.applyCoupon(id, cart);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon updatedCoupon) {
		try {
			return ResponseEntity.ok(couponService.updateCoupon(id, updatedCoupon));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
		couponService.deleteCoupon(id);
		return ResponseEntity.noContent().build();
	}
}
