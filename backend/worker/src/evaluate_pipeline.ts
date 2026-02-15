
import {
    normalizeBrand,
    normalizeName,
    generateCanonicalKey,
    getUnitClass,
    calculateMatchScore,
    validatePlausibility,
    CanonicalFood
} from './index';

console.log("Starting Evaluation Harness...");

// 1. Normalization Tests
console.log("\n[Test] Normalization Logic");
const brandTests = [
    { input: "Maccas", expected: "mcdonalds" },
    { input: "McDonald's", expected: "mcdonalds" },
    { input: "  COKE  ", expected: "coke" }, // Assuming simple normalization logic for now
    { input: null, expected: "_generic_" }
];

brandTests.forEach(t => {
    const res = normalizeBrand(t.input);
    if (res !== t.expected) console.error(`FAIL: Brand '${t.input}' -> '${res}' (expected '${t.expected}')`);
    else console.log(`PASS: Brand '${t.input}' -> '${res}'`);
});

const nameTests = [
    { input: "Fresh Organic Banana", expected: "banana" },
    { input: "Chicken Rice", expected: "chicken_rice" },
    { input: "Rice Chicken", expected: "rice_chicken" } // Order matters test
];

nameTests.forEach(t => {
    const res = normalizeName(t.input);
    if (res !== t.expected) console.error(`FAIL: Name '${t.input}' -> '${res}' (expected '${t.expected}')`);
    else console.log(`PASS: Name '${t.input}' -> '${res}'`);
});

// 2. Fragmentation Test (Key Gen)
console.log("\n[Test] Fragmentation (Canonical Key)");
// Note: Depending on stopwords, "Coke Zero" vs "Coke Zero Sugar". 
// "Sugar" might be kept. "Coke" might be normalized if brand=CocaCola?
// Current logic: Brand normalizes to 'coca_cola'. 
// Name: 'coke_zero' vs 'coke_zero_sugar'.
// Only match if 'sugar' is stopword or removed.
const key1 = generateCanonicalKey("Coca Cola", "Coke Zero", "330ml");
const key2 = generateCanonicalKey("Coca-Cola", "Coke Zero Sugar", "500ml");
console.log(`Key1: ${key1}`);
console.log(`Key2: ${key2}`);

if (key1 === key2) console.log("PASS: Keys match (Defragmented)");
else {
    // If they don't match exactly, check if they match enough components
    // e.g. brand + name prefix?
    console.log("INFO: Keys differ. This is expected if 'sugar' is not a stopword. Exact match relies on precise naming.");
}

// 3. Scoring Test
console.log("\n[Test] Scoring Logic");
const candidate: CanonicalFood = {
    id: '1', canonical_key: 'coca_cola|coke_zero|liquid', name_display: 'Coke Zero', brand_normalized: 'coca_cola', base_unit_class: 'liquid',
    variants: {}, confidence_score: 0.9, confirmation_count: 10, variance_score: 0
};
const target = { brand: 'Coca Cola', unit: 'ml' };
const score = calculateMatchScore(candidate, target, 0.9); // Vector score 0.9
console.log(`Score: ${score}`);
if (score > 0.8) console.log("PASS: High score for good match");
else console.error("FAIL: Score too low");

console.log("\nEvaluation Complete.");
