package com.firstclub.membership.config;

import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.TierCriteria;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.*;
import com.firstclub.membership.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Seeds reference data on startup.
 * In production, this data lives in Flyway/Liquibase migrations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final MembershipPlanRepository planRepository;
    private final TierBenefitRepository tierBenefitRepository;
    private final TierCriteriaRepository tierCriteriaRepository;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        seedPlans();
        seedTierBenefits();
        seedTierCriteria();
        seedSampleUsers();
        log.info("✅ Data seeding complete");
    }

    private void seedPlans() {
        if (planRepository.count() > 0) return;
        planRepository.saveAll(List.of(
            MembershipPlan.builder()
                .planType(PlanType.MONTHLY)
                .price(new BigDecimal("199.00"))
                .description("Monthly membership — renews every month")
                .build(),
            MembershipPlan.builder()
                .planType(PlanType.QUARTERLY)
                .price(new BigDecimal("499.00"))
                .description("Quarterly membership — save 16% vs monthly")
                .promoLabel("Popular")
                .build(),
            MembershipPlan.builder()
                .planType(PlanType.YEARLY)
                .price(new BigDecimal("1499.00"))
                .description("Annual membership — best value, save 37%")
                .promoLabel("Best Value")
                .build()
        ));
        log.info("Seeded membership plans");
    }

    private void seedTierBenefits() {
        if (tierBenefitRepository.count() > 0) return;
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
        log.info("Seeded tier benefits");
    }

    private void seedTierCriteria() {
        if (tierCriteriaRepository.count() > 0) return;
        tierCriteriaRepository.saveAll(List.of(
            TierCriteria.builder().tierType(TierType.GOLD)
                .criteriaType(TierCriteriaType.MIN_ORDER_COUNT).thresholdValue("3")
                .build(),
            TierCriteria.builder().tierType(TierType.PLATINUM)
                .criteriaType(TierCriteriaType.MIN_ORDER_COUNT).thresholdValue("10")
                .build(),
            TierCriteria.builder().tierType(TierType.PLATINUM)
                .criteriaType(TierCriteriaType.MIN_ORDER_VALUE).thresholdValue("5000")
                .build()
        ));
        log.info("Seeded tier criteria");
    }

    private void seedSampleUsers() {
        if (userRepository.count() > 0) return;
        userRepository.saveAll(List.of(
            User.builder().name("Alice Kumar").email("alice@example.com").cohortTags(Set.of("vip")).build(),
            User.builder().name("Bob Singh").email("bob@example.com").cohortTags(Set.of()).build(),
            User.builder().name("Charlie Dev").email("charlie@example.com").cohortTags(Set.of("beta_tester", "vip")).build()
        ));
        log.info("Seeded sample users");
    }
}
