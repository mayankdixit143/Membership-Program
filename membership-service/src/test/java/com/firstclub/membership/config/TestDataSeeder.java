package com.firstclub.membership.config;

import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.enums.*;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("test")
public class TestDataSeeder implements CommandLineRunner {

    private final MembershipPlanRepository planRepository;
    private final TierBenefitRepository tierBenefitRepository;
    private final TierCriteriaRepository tierCriteriaRepository;

    @Override
    public void run(String... args) {
        seedPlans();
        seedTierBenefits();
        seedTierCriteria();
        log.info("✅ Test data seeding complete");
    }

    private void seedPlans() {
        planRepository.deleteAll();
        planRepository.saveAll(List.of(
                MembershipPlan.builder()
                        .planType(PlanType.MONTHLY)
                        .price(new BigDecimal("199.00"))
                        .description("Monthly membership — renews every month")
                        .active(true)
                        .build(),
                MembershipPlan.builder()
                        .planType(PlanType.QUARTERLY)
                        .price(new BigDecimal("499.00"))
                        .description("Quarterly membership — save 16% vs monthly")
                        .promoLabel("Popular")
                        .active(true)
                        .build(),
                MembershipPlan.builder()
                        .planType(PlanType.YEARLY)
                        .price(new BigDecimal("1499.00"))
                        .description("Annual membership — best value, save 37%")
                        .promoLabel("Best Value")
                        .active(true)
                        .build()
        ));
        log.info("Test: Seeded 3 membership plans");
    }

    private void seedTierBenefits() {
        tierBenefitRepository.deleteAll();
        tierBenefitRepository.saveAll(List.of(
                TierBenefit.builder().tierType(TierType.SILVER).benefitType(BenefitType.FREE_DELIVERY)
                        .description("Free delivery on all eligible orders").build(),
                TierBenefit.builder().tierType(TierType.SILVER).benefitType(BenefitType.EXCLUSIVE_DEALS)
                        .description("Access to member-only deals").build(),
                TierBenefit.builder().tierType(TierType.GOLD).benefitType(BenefitType.FREE_DELIVERY)
                        .description("Free delivery on all orders").build(),
                TierBenefit.builder().tierType(TierType.GOLD).benefitType(BenefitType.DISCOUNT_PERCENTAGE)
                        .benefitValue("5").description("5% extra discount on selected categories").build(),
                TierBenefit.builder().tierType(TierType.GOLD).benefitType(BenefitType.EARLY_SALE_ACCESS)
                        .description("Early access to sales (24h before public)").build(),
                TierBenefit.builder().tierType(TierType.GOLD).benefitType(BenefitType.EXCLUSIVE_DEALS)
                        .description("Access to Gold-exclusive deals").build(),
                TierBenefit.builder().tierType(TierType.PLATINUM).benefitType(BenefitType.FREE_DELIVERY)
                        .description("Free priority delivery on all orders").build(),
                TierBenefit.builder().tierType(TierType.PLATINUM).benefitType(BenefitType.DISCOUNT_PERCENTAGE)
                        .benefitValue("10").description("10% extra discount on all categories").build(),
                TierBenefit.builder().tierType(TierType.PLATINUM).benefitType(BenefitType.EARLY_SALE_ACCESS)
                        .description("Early access to sales (48h before public)").build(),
                TierBenefit.builder().tierType(TierType.PLATINUM).benefitType(BenefitType.PRIORITY_SUPPORT)
                        .description("24/7 priority customer support").build(),
                TierBenefit.builder().tierType(TierType.PLATINUM).benefitType(BenefitType.EXCLUSIVE_COUPONS)
                        .benefitValue("200").description("₹200 exclusive monthly coupon").build(),
                TierBenefit.builder().tierType(TierType.PLATINUM).benefitType(BenefitType.FASTER_DELIVERY)
                        .description("Same-day delivery on eligible orders").build()
        ));
        log.info("Test: Seeded tier benefits");
    }

    private void seedTierCriteria() {
        tierCriteriaRepository.deleteAll();
        tierCriteriaRepository.saveAll(List.of(
                TierCriteria.builder().tierType(TierType.GOLD)
                        .criteriaType(TierCriteriaType.MIN_ORDER_COUNT).thresholdValue("3").build(),
                TierCriteria.builder().tierType(TierType.PLATINUM)
                        .criteriaType(TierCriteriaType.MIN_ORDER_COUNT).thresholdValue("10").build(),
                TierCriteria.builder().tierType(TierType.PLATINUM)
                        .criteriaType(TierCriteriaType.MIN_ORDER_VALUE).thresholdValue("5000").build()
        ));
        log.info("Test: Seeded tier criteria");
    }
}