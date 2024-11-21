package com.example.discount;

import com.example.coupons.repository.CouponRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CouponService {

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private ObjectMapper objectMapper;

	public List<Coupon> getAllCoupons() {
		return couponRepository.findAll();
	}

	public Optional<Coupon> getCouponById(Long id) {
		return couponRepository.findById(id);
	}

	public Coupon createCoupon(Coupon coupon) {
		return couponRepository.save(coupon);
	}

	public Coupon updateCoupon(Long id, Coupon updatedCoupon) {
		return couponRepository.findById(id).map(coupon -> {
			coupon.setType(updatedCoupon.getType());
			coupon.setDetails(updatedCoupon.getDetails());
			coupon.setExpirationDate(updatedCoupon.getExpirationDate());
			return couponRepository.save(coupon);
		}).orElseThrow(() -> new RuntimeException("Coupon not found"));
	}

	public void deleteCoupon(Long id) {
		couponRepository.deleteById(id);
	}

	public Map<String, Object> applyCoupon(Long couponId, Cart cart) {
		Map<String, Object> response = new HashMap<>();
		Coupon coupon = couponRepository.findById(couponId)
				.orElseThrow(() -> new RuntimeException("Coupon not found"));

		switch (coupon.getType()) {
		case "cart-wise":
			response = applyCartWiseCoupon(coupon, cart);
			break;
		case "product-wise":
			response = applyProductWiseCoupon(coupon, cart);
			break;
		case "bxgy":
			response = applyBxGyCoupon(coupon, cart);
			break;
		default:
			throw new RuntimeException("Invalid coupon type");
		}

		return response;
	}

	private Map<String, Object> applyCartWiseCoupon(Coupon coupon, Cart cart) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Object> details = objectMapper.readValue(coupon.getDetails(), Map.class);
			double threshold = (double) details.get("threshold");
			double discount = (double) details.get("discount");

			if (cart.getTotalAmount() > threshold) {
				double discountAmount = cart.getTotalAmount() * (discount / 100);
				response.put("discountAmount", discountAmount);
				response.put("finalAmount", cart.getTotalAmount() - discountAmount);
			} else {
				response.put("message", "Cart total does not meet the coupon threshold.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Error applying cart-wise coupon", e);
		}
		return response;
	}

	private Map<String, Object> applyProductWiseCoupon(Coupon coupon, Cart cart) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Object> details = objectMapper.readValue(coupon.getDetails(), Map.class);
			Long productId = ((Number) details.get("product_id")).longValue();
			double discount = (double) details.get("discount");

			for (CartItem item : cart.getItems()) {
				if (item.getProductId().equals(productId)) {
					double discountAmount = item.getPrice() * (discount / 100) * item.getQuantity();
					response.put("discountAmount", discountAmount);
					response.put("finalAmount", cart.getTotalAmount() - discountAmount);
					return response;
				}
			}
			response.put("message", "Product not eligible for this coupon.");
		} catch (Exception e) {
			throw new RuntimeException("Error applying product-wise coupon", e);
		}
		return response;
	}

	private Map<String, Object> applyBxGyCoupon(Coupon coupon, Cart cart) {
		Map<String, Object> response = new HashMap<>();
		try {
			Map<String, Object> details = objectMapper.readValue(coupon.getDetails(), Map.class);
			List<Map<String, Object>> buyProducts = (List<Map<String, Object>>) details.get("buy_products");
			List<Map<String, Object>> getProducts = (List<Map<String, Object>>) details.get("get_products");
			int repetitionLimit = (int) details.get("repetition_limit");

			int buyCount = 0;
			for (Map<String, Object> buyProduct : buyProducts) {
				Long productId = ((Number) buyProduct.get("product_id")).longValue();
				int requiredQty = (int) buyProduct.get("quantity");

				for (CartItem item : cart.getItems()) {
					if (item.getProductId().equals(productId)) {
						buyCount += item.getQuantity() / requiredQty;
					}
				}
			}

			if (buyCount > 0) {
				int applicableRepetitions = Math.min(buyCount, repetitionLimit);
				double discountAmount = 0;

				for (Map<String, Object> getProduct : getProducts) {
					Long productId = ((Number) getProduct.get("product_id")).longValue();
					double productPrice = cart.getItems().stream()
							.filter(item -> item.getProductId().equals(productId))
							.findFirst()
							.map(CartItem::getPrice)
							.orElse(0.0);

					discountAmount += productPrice * applicableRepetitions;
				}

				response.put("discountAmount", discountAmount);
				response.put("finalAmount", cart.getTotalAmount() - discountAmount);
			} else {
				response.put("message", "Cart does not meet Buy X, Get Y requirements.");
			}
		} catch (Exception e) {
			throw new RuntimeException("Error applying BxGy coupon", e);
		}
		return response;
	}
}

