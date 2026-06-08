Project Overview
This repository contains the backend engine for FirstClub's dynamic, multi-tier subscription membership program. The solution is built with a heavy emphasis on core software engineering principles: strict separation of concerns, high modularity, runtime configuration over rigid compile-time rules, and clean database normalization.
Rather than approaching this purely as a CRUD service, the core problem domain was divided into two distinct logical systems:
The Billing Cycle Layer (MembershipPlan): Controls frequencies (Monthly, Quarterly, Yearly), renewal boundaries, and pricing structures.
The Loyalty Experience Layer (TierType): Governs user privileges, business-driven criteria rules, and target value thresholds.
By decoupling these two systems from the ground up, the platform ensures maximum agility. Business stakeholders can alter billing cadences, execute localized adjustments to pricing tables, or scale up a specialized promotional tier without requiring changes or redeployments to the underlying membership lifecycle rules.
Technical Design Highlights
Configurable Data-Driven Benefits Table (TierBenefit)
Hardcoding logic or writing massive switch blocks inside an enum for benefits (e.g., mapping Gold to a fixed 10% discount) is a massive anti-pattern that limits scale. To prevent this, all member privileges are treated entirely as standard dynamic table rows. Each tier points to configurable entity pairs (BenefitType and its associated string metadata like value limits or delivery tracking flags). Adding a brand-new perk to a specific tier takes seconds and zero code updates.
Strategy-Pattern Ready Architecture (TierCriteria)
Tier progression states (such as verifying total order milestones, monthly transaction sums, or evaluating user tags) utilize a highly isolated milestone entity schema. This structure sets up a perfect foundation to layer on a Strategy Pattern pipeline, meaning automated tier evaluations can execute seamlessly during active checkout operations or background schedulers without introducing state mutation bugs.
Robust Test Pipeline Security
The system is paired with an end-to-end integration workflow running via MockMvc. The transactional layers and integration states are backed by isolated contextual data injections (TestDataSeeder), testing user registration, plan purchases, upgrade/downgrade hooks, cancellation timelines, and historical mutations.
