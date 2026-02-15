import { z } from 'zod';

// Environment bindings
interface Env {
  USDA_CACHE: KVNamespace;
  FOOD_REGISTRY: KVNamespace;
  FOOD_VECTORS: VectorizeIndex;
  OPENAI_API_KEY: string;
  USDA_API_KEY: string;
  SERP_API_KEY: string;
  ENVIRONMENT: string;
}

// ─────────────────────────────────────────────────────────────────────────────
// Data Models
// ─────────────────────────────────────────────────────────────────────────────

export interface CanonicalFood {
  id: string; // UUID
  canonical_key: string; // brand|name|unit_class
  name_display: string;
  brand_normalized: string | null;
  base_unit_class: 'liquid' | 'solid';
  variants: Record<string, { // key = "330_ml" or "bowl"
    quantity: number;
    unit: string;
    macros: { calories: number; protein: number; carbs: number; fat: number };
    confidence: number;
  }>;
  confidence_score: number;
  confirmation_count: number;
  variance_score: number;
}

// ─────────────────────────────────────────────────────────────────────────────
// Quantifier Model & Determinism
// ─────────────────────────────────────────────────────────────────────────────

export const LIQUID_UNITS = ['ml', 'oz', 'cup', 'glass', 'bottle', 'can', 'pint'] as const;
export const SOLID_UNITS = ['g', 'oz', 'piece', 'slice', 'bowl', 'serving', 'package', 'bar'] as const;
export const SMALL_UNITS = ['tbsp', 'tsp'] as const;

export const UNIT_ALIASES: Record<string, string> = {
  'cups': 'cup',
  'grams': 'g',
  'gram': 'g',
  'fl oz': 'oz',
  'fl. oz': 'oz',
  'fluid ounce': 'oz',
  'ounce': 'oz',
  'ounces': 'oz',
  'tablespoon': 'tbsp',
  'teaspoon': 'tsp',
  'pcs': 'piece',
  'pcs.': 'piece',
  'pieces': 'piece',
};

export type LiquidUnit = typeof LIQUID_UNITS[number];
export type SolidUnit = typeof SOLID_UNITS[number];
export type QuantifierEnum = LiquidUnit | SolidUnit | typeof SMALL_UNITS[number];

export interface ResolvedFoodItem {
  semantic_id: string; // canonical key or similar
  name: string;
  brand: string | null;

  // quantity
  quantity: number;
  unit: string; // QuantifierEnum
  unit_class: 'liquid' | 'solid';

  // metadata
  size_val?: number; // e.g. 330
  size_unit?: string; // e.g. "ml"
  size_source?: 'OCR' | 'VARIANT' | 'DEFAULT' | 'USER';

  // logic
  confidence: number;
  requiresConfirmation: boolean;
  estimated_badge: boolean;
  provenance: 'AI_EXTRACT' | 'RULE_CONTAINER' | 'RULE_COUNT' | 'OCR' | 'HISTORY_MATCH' | 'VECTOR_MATCH' | 'FALLBACK';

  // nutrition
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  unresolved: boolean;
}

// ─────────────────────────────────────────────────────────────────────────────
// Zod Schemas for Request/Response Validation
// ─────────────────────────────────────────────────────────────────────────────

const FeedbackConfirmSchema = z.object({
  canonical_id: z.string(),
  variant_key: z.string().optional(),
});

const FeedbackEditSchema = z.object({
  original_canonical_id: z.string().optional(), // If editing an existing canonical
  name: z.string(),
  brand: z.string().nullable().optional(),
  quantity: z.number(),
  unit: z.string(),
  macros: z.object({
    calories: z.number(),
    protein: z.number(),
    carbs: z.number(),
    fat: z.number(),
  }),
});

const FeedbackDismissSchema = z.object({
  canonical_id: z.string(),
});

const LogFoodRequestSchema = z.object({
  text: z.string().nullable().optional(),
  imageBase64: z.string().nullable().optional(),
  userWeightKg: z.number().positive(),
});

const LogExerciseRequestSchema = z.object({
  text: z.string().nullable().optional(),
  imageBase64: z.string().nullable().optional(),
  userWeightKg: z.number().positive(),
});

const ResolveFoodRequestSchema = z.object({
  candidates: z.array(z.object({
    name: z.string(),
    quantity: z.number().nullable().optional(),
    unit: z.string().nullable().optional(),
    fdcId: z.number().nullable().optional(),
  })),
});

const ResolveExerciseRequestSchema = z.object({
  activity: z.string(),
  durationMinutes: z.number().positive(),
  userWeightKg: z.number().positive(),
  metValue: z.number().positive().nullable().optional(),
});

// Auto-detect endpoint schema
const LogAutoRequestSchema = z.object({
  text: z.string().nullable().optional(),
  imageBase64: z.string().nullable().optional(),
  userWeightKg: z.number().positive(),
});

// Helper for Unit Normalization
export function normalizeQuantifier(unit: string): string {
  const lower = unit.toLowerCase().trim().replace(/s$/, ''); // trim plural 's' simplistic
  if (UNIT_ALIASES[lower]) return UNIT_ALIASES[lower];
  if (UNIT_ALIASES[unit.toLowerCase()]) return UNIT_ALIASES[unit.toLowerCase()];

  // check enums
  if (LIQUID_UNITS.includes(lower as any)) return lower;
  if (SOLID_UNITS.includes(lower as any)) return lower;
  if (SMALL_UNITS.includes(lower as any)) return lower;

  return unit; // fallback
}

// ─────────────────────────────────────────────────────────────────────────────
// Response Types
// ─────────────────────────────────────────────────────────────────────────────

interface ParsedFoodItem {
  name: string;
  quantity: number;
  unit: string;
  confidence: number;
  calories: number | null;
  protein: number | null;
  carbs: number | null;
  fat: number | null;
  serving_grams: number | null;
  unresolved: boolean;
  assumptions: string[];
  suggestedQueries: string[];
}

interface ParsedExerciseItem {
  activity: string;
  durationMinutes: number;
  intensity: string;
  confidence: number;
  suggestedQueries: string[];
}

interface FoodNutrition {
  fdcId: number;
  name: string;
  calories: number;
  protein: number;
  carbs: number;
  fat: number;
  servingSize: number;
  servingUnit: string;
  source: 'usda_fdc';
}

interface ExerciseCalories {
  activity: string;
  metValue: number;
  durationMinutes: number;
  caloriesBurned: number;
  source: 'met_compendium';
}

// ─────────────────────────────────────────────────────────────────────────────
// USDA FoodData Central API Integration
// ─────────────────────────────────────────────────────────────────────────────

const USDA_BASE_URL = 'https://api.nal.usda.gov/fdc/v1';

async function searchUSDA(
  query: string,
  apiKey: string,
  cache: KVNamespace
): Promise<any[]> {
  const cacheKey = `usda:search:${query.toLowerCase().replace(/\s+/g, '_')}`;

  // Check cache first
  const cached = await cache.get(cacheKey, 'json');
  if (cached) {
    return cached as any[];
  }

  const url = `${USDA_BASE_URL}/foods/search?api_key=${apiKey}&query=${encodeURIComponent(query)}&pageSize=5&dataType=Foundation,SR%20Legacy`;

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`USDA API error: ${response.status}`);
  }

  const data = await response.json() as any;
  const foods = data.foods || [];

  // Cache for 24 hours
  await cache.put(cacheKey, JSON.stringify(foods), { expirationTtl: 86400 });

  return foods;
}

async function getFoodDetails(
  fdcId: number,
  apiKey: string,
  cache: KVNamespace
): Promise<FoodNutrition | null> {
  const cacheKey = `usda:food:${fdcId}`;

  // Check cache first
  const cached = await cache.get(cacheKey, 'json');
  if (cached) {
    return cached as FoodNutrition;
  }

  const url = `${USDA_BASE_URL}/food/${fdcId}?api_key=${apiKey}`;

  const response = await fetch(url);
  if (!response.ok) {
    return null;
  }

  const data = await response.json() as any;

  // Extract nutrients
  const nutrients = data.foodNutrients || [];
  const getNutrient = (id: number): number => {
    const nutrient = nutrients.find((n: any) => n.nutrient?.id === id);
    return nutrient?.amount || 0;
  };

  const nutrition: FoodNutrition = {
    fdcId,
    name: data.description || '',
    calories: getNutrient(1008), // Energy (kcal)
    protein: getNutrient(1003), // Protein
    carbs: getNutrient(1005), // Carbohydrates
    fat: getNutrient(1004), // Total fat
    servingSize: 100,
    servingUnit: 'g',
    source: 'usda_fdc',
  };

  // Cache for 7 days
  await cache.put(cacheKey, JSON.stringify(nutrition), { expirationTtl: 604800 });

  return nutrition;
}

// ─────────────────────────────────────────────────────────────────────────────
// OpenAI API Integration
// ─────────────────────────────────────────────────────────────────────────────

const OPENAI_TEXT_MODEL = 'gpt-4o-mini';     // Fast + cheap for text parsing
const OPENAI_VISION_MODEL = 'gpt-4o';        // Full vision model for food images
const OPENAI_API_URL = 'https://api.openai.com/v1/chat/completions';

const TRACKY_SYSTEM_PROMPT = `You are Tracky AI, a precision nutrition and fitness tracking engine.

ROLE: You are a certified nutritionist-level estimator. For every food item you identify, you MUST provide calorie and macronutrient estimates based on your training data (USDA SR, nutritionix, common nutritional databases).

CORE RULES:
1. ALWAYS return valid JSON. Never markdown, never explanations outside JSON.
2. For food: ALWAYS estimate calories (kcal), protein (g), carbs (g), fat (g) per the stated quantity.
3. ACCURACY: Use your knowledge of nutritional databases. Cross-reference internally:
   - Macro-calorie check: |calories - (carbs*4 + protein*4 + fat*9)| must be < 10% of calories.
   - Portion sanity: 1 egg ≈ 70kcal, 1 banana ≈ 105kcal, 1 cup cooked rice ≈ 200kcal, 100g chicken breast ≈ 165kcal.
4. PORTIONS: Estimate serving_grams for the stated quantity. Note assumptions (e.g., "assumed medium banana ~120g").
5. If you truly cannot estimate (exotic/unknown item), set "unresolved": true with null values. NEVER return 0 kcal for a known food.
6. For exercise: Extract activity, duration, intensity. Do NOT estimate calories (we use MET calculations).
7. STYLE: Concise, no emojis, no filler.

QUANTITY RULES:
- Force ALL units to be one of: ["serving", "piece", "cup", "oz", "g", "ml", "tbsp", "tsp", "bottle", "can", "package", "slice", "bowl", "glass"]
- Convert others (e.g. "plate" -> "serving", "handful" -> "serving", "liter" -> "ml", "lb" -> "oz")

BRAND RULES:
- Include visible brand names in the "name" field for ALL items (e.g. "Doritos Nacho Cheese" instead of "Chips", "Starbucks Latte" instead of "Coffee", "Coca-Cola" instead of "Cola").
- If a brand is likely but not certain, pick the most common one or keep it descriptive.

DRINK & UNIT RULES:
- Identify drinks and use liquid units if possible (ml, oz, cup, glass, bottle, can).
- Force ALL food and drink units to be one of the allowed list: ["serving", "piece", "cup", "oz", "g", "ml", "tbsp", "tsp", "bottle", "can", "package", "slice", "bowl", "glass"].
- Estimate calories based on sugar content/type (e.g. Coke = sugar, Coke Zero = 0).
- Be precise with units: "500ml bottle" should be parsed as quantity: 500, unit: "ml".`;

async function parseWithOpenAI(
  apiKey: string,
  systemPrompt: string,
  userContentOrImage?: string | any[],
  model: string = 'gpt-4o'
): Promise<any> {
  const messages: any[] = [{ role: 'system', content: systemPrompt }];

  if (userContentOrImage) {
    if (typeof userContentOrImage === 'string') {
      messages.push({ role: 'user', content: userContentOrImage });
    } else {
      // It's an array of content parts
      messages.push({ role: 'user', content: userContentOrImage });
    }
  }

  try {
    const response = await fetch('https://api.openai.com/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiKey}`
      },
      body: JSON.stringify({
        model,
        messages,
        response_format: { type: 'json_object' },
        temperature: 0.1
      })
    });

    if (!response.ok) {
      const err = await response.text();
      console.error('OpenAI Error:', err);
      throw new Error(`OpenAI API Error: ${response.status}`);
    }

    const data = await response.json() as any;
    const content = data.choices?.[0]?.message?.content;

    if (!content) return null;
    return JSON.parse(content);
  } catch (error) {
    console.error('parseWithOpenAI failed:', error);
    return null;
  }
}


/**
 * Parse exercise data from image using OpenAI Vision
 */
async function parseExerciseFromImage(
  imageBase64: string,
  userWeightKg: number,
  apiKey: string
): Promise<any> {
  const prompt = `Analyze this exercise/workout screenshot and extract the following data:

CRITICAL: Look for these fields (common in fitness tracker apps):
- Activity type (walking, running, cycling, swimming, etc.)
- Duration (in minutes or HH:MM:SS format)
- Distance (if shown, in km or miles)
- Calories burned (if shown)
- Pace/Speed (if shown)
- Heart rate data (if shown)
- Date/Time (if shown)

User weight: ${userWeightKg}kg

Rules:
1. Extract ALL visible exercise data from the screenshot
2. If the screenshot shows multiple activities/segments, extract each one
3. Convert all durations to minutes (e.g., "00:27:36" → 27.6 minutes)
4. Convert all distances to km (if in miles, convert)
5. If calories are shown in the screenshot, use that value (high confidence)
6. If calories NOT shown, leave null (we'll calculate it using MET values)
7. Identify the activity type accurately (e.g., "walking", "running", "cycling")
8. INFER INTENSITY from metrics:
   - low: Pulse/HR < 100bpm, or walking pace > 12:00/km (or < 3mph), or "cool down", "stretch"
   - moderate: Pulse/HR 100-140bpm, or walking pace 9:00-11:00/km, or "active", "normal"
   - high: Pulse/HR > 140bpm, or sprinting, or running pace < 5:00/km, or "intense", "peak"

Return ONLY valid JSON in this format:
{
  "exercises": [
    {
      "activity": "walking",
      "durationMinutes": 27.6,
      "distanceKm": 2.35,
      "caloriesBurned": 115,
      "intensity": "low" | "moderate" | "high",
      "averagePace": "11:44",
      "confidence": 0.95,
      "suggestedQueries": ["walking"]
    }
  ]
}

If the image is NOT an exercise screenshot, return:
{
  "exercises": [],
  "error": "Not an exercise screenshot"
}

Return ONLY valid JSON (no markdown, no explanations).`;

  const content = await parseWithOpenAI(apiKey, prompt, imageBase64);

  const jsonMatch = content.match(/\{[\s\S]*\}/);
  if (!jsonMatch) {
    throw new Error('Failed to parse AI response');
  }

  return JSON.parse(jsonMatch[0]);
}

/**
 * Ask AI to estimate MET value for an activity that isn't in our dictionary.
 * Includes explicit consistency/accuracy check.
 */
async function estimateMetWithAI(
  activity: string,
  apiKey: string
): Promise<{ metValue: number; confidence: number; isConsistent: boolean; reason: string } | null> {
  const prompt = `Estimate the MET (Metabolic Equivalent of Task) value for this activity: "${activity}"

Rules:
1. Return a precise MET value based on scientific consensus (Compendium of Physical Activities).
2. CONSISTENCY CHECK: 
   - If the activity is too vague (e.g., "gym", "working out", "sports"), set isConsistent: false.
   - If the activity is physically impossible or nonsense, set isConsistent: false.
   - If acceptable, set isConsistent: true.
3. CONFIDENCE: Rate confidence (0.0 to 1.0). High confidence means standard activity (e.g. "burpees"). Low confidence means ambiguous (e.g. "hard labor").

Return ONLY valid JSON:
{
  "metValue": 7.0,
  "confidence": 0.9,
  "isConsistent": true, // or false
  "reason": "Activity is well-defined and maps to standard value." // or "Too vague to estimate."
}`;

  try {
    const content = await parseWithOpenAI(apiKey, prompt);
    const jsonMatch = content.match(/\{[\s\S]*\}/);
    if (!jsonMatch) return null;

    return JSON.parse(jsonMatch[0]);
  } catch (error) {
    console.error('AI MET estimation failed:', error);
    return null;
  }
}



// ─────────────────────────────────────────────────────────────────────────────
// MET Compendium (exercise calories calculation)
// ─────────────────────────────────────────────────────────────────────────────

const MET_VALUES: Record<string, number> = {
  // Walking
  'walking': 3.5,
  'walking_slow': 2.5,
  'walking_brisk': 4.3,
  'walking_uphill': 6.0,
  'walking_stairs': 8.0,

  // Running/Jogging
  'running': 9.8,
  'jogging': 7.0,
  'running_slow': 8.0,
  'running_fast': 11.5,
  'sprinting': 16.0,
  'trail_running': 9.0,

  // Cycling
  'cycling': 7.5,
  'cycling_slow': 5.8,
  'cycling_moderate': 8.0,
  'cycling_fast': 10.0,
  'cycling_stationary': 6.8,
  'mountain_biking': 8.5,
  'spinning': 8.5,

  // Swimming
  'swimming': 8.0,
  'swimming_slow': 6.0,
  'swimming_laps': 9.5,
  'swimming_freestyle': 10.0,
  'swimming_backstroke': 9.5,
  'swimming_breaststroke': 10.0,
  'water_aerobics': 5.5,

  // Strength Training
  'weight_training': 5.0,
  'weight_lifting': 6.0,
  'bodyweight_exercises': 4.5,
  'calisthenics': 4.0,
  'pushups': 3.8,
  'pullups': 8.0,
  'squats': 5.5,
  'deadlifts': 6.0,
  'bench_press': 6.0,
  'resistance_training': 5.0,

  // Cardio Machines
  'elliptical': 5.0,
  'rowing': 7.0,
  'rowing_machine': 7.0,
  'stairmaster': 9.0,
  'stair_climbing': 9.0,
  'treadmill': 7.0,
  'stationary_bike': 6.8,

  // Sports
  'basketball': 8.0,
  'soccer': 10.0,
  'football': 8.0,
  'tennis': 7.3,
  'volleyball': 4.0,
  'badminton': 5.5,
  'table_tennis': 4.0,
  'racquetball': 7.0,
  'squash': 12.0,
  'golf': 4.8,
  'baseball': 5.0,
  'softball': 5.0,

  // Martial Arts & Combat
  'boxing': 9.0,
  'kickboxing': 10.0,
  'martial_arts': 10.0,
  'karate': 10.0,
  'taekwondo': 10.0,
  'judo': 10.0,
  'wrestling': 6.0,
  'mma': 10.0,

  // Dance & Aerobics
  'dancing': 5.5,
  'aerobics': 7.3,
  'zumba': 8.5,
  'ballet': 5.0,
  'hip_hop_dance': 5.0,
  'ballroom_dancing': 5.5,

  // Flexibility & Mind-Body
  'yoga': 3.0,
  'pilates': 3.0,
  'stretching': 2.3,
  'tai_chi': 3.0,

  // Other Activities
  'hiking': 6.0,
  'rock_climbing': 8.0,
  'climbing': 8.0,
  'jump_rope': 11.0,
  'jumping_rope': 11.0,
  'skipping': 11.0,
  'skating': 7.0,
  'rollerblading': 7.0,
  'skiing': 7.0,
  'snowboarding': 5.3,
  'surfing': 3.0,
  'kayaking': 5.0,
  'canoeing': 3.5,
  'gardening': 4.0,
  'yard_work': 4.0,
  'house_cleaning': 3.5,
};

function calculateExerciseCalories(
  metValue: number,
  weightKg: number,
  durationMinutes: number
): number {
  // Formula: kcal = MET x weight(kg) x time(hours)
  const hours = durationMinutes / 60;
  return Math.round(metValue * weightKg * hours);
}

function findMetValue(activity: string): number | null {
  const normalized = activity.toLowerCase().replace(/\s+/g, '_');

  // Direct match
  if (MET_VALUES[normalized]) {
    return MET_VALUES[normalized];
  }

  // Partial match
  for (const [key, value] of Object.entries(MET_VALUES)) {
    if (normalized.includes(key) || key.includes(normalized)) {
      return value;
    }
  }

  return null;
}

/**
 * Sanity check exercise calorie calculations
 * Returns true if calories seem reasonable, false if suspiciously low/high
 */
function validateExerciseCalories(
  activity: string,
  durationMinutes: number,
  caloriesBurned: number,
  userWeightKg: number
): { valid: boolean; expectedRange: { min: number; max: number } } {
  const hours = durationMinutes / 60;

  // Expected calorie ranges per hour for 70kg person
  const baseRanges: Record<string, { min: number; max: number }> = {
    'walking': { min: 150, max: 300 },
    'jogging': { min: 400, max: 600 },
    'running': { min: 600, max: 1000 },
    'cycling': { min: 300, max: 700 },
    'swimming': { min: 400, max: 800 },
    'weight_training': { min: 200, max: 400 },
    'weight_lifting': { min: 200, max: 400 },
    'basketball': { min: 400, max: 700 },
    'soccer': { min: 500, max: 900 },
    'yoga': { min: 100, max: 250 },
    'pilates': { min: 100, max: 250 },
    'hiking': { min: 300, max: 600 },
    'dancing': { min: 250, max: 500 },
    'boxing': { min: 450, max: 800 },
    'martial_arts': { min: 500, max: 900 },
  };

  // Find matching activity category
  const activityLower = activity.toLowerCase();
  let range = { min: 100, max: 1200 }; // Default wide range

  for (const [key, value] of Object.entries(baseRanges)) {
    if (activityLower.includes(key)) {
      range = value;
      break;
    }
  }

  // Adjust for user weight (scale from 70kg baseline)
  const weightFactor = userWeightKg / 70;
  const expectedMin = Math.round(range.min * hours * weightFactor);
  const expectedMax = Math.round(range.max * hours * weightFactor);

  // Allow 30% margin for variation
  const margin = 0.3;
  const valid = caloriesBurned >= expectedMin * (1 - margin) &&
    caloriesBurned <= expectedMax * (1 + margin);

  return {
    valid,
    expectedRange: { min: expectedMin, max: expectedMax }
  };
}


// ─────────────────────────────────────────────────────────────────────────────
// Food Item Validation
// ─────────────────────────────────────────────────────────────────────────────

// ─────────────────────────────────────────────────────────────────────────────
// Nutrition Engine: Canonicalization & Scoring
// ─────────────────────────────────────────────────────────────────────────────

export function normalizeBrand(brand: string | null): string {
  if (!brand) return '_generic_';
  let b = brand.toLowerCase().trim();
  // Alias Mapping
  const aliases: Record<string, string> = {
    'maccas': 'mcdonalds',
    'coke': 'coca_cola',
    'kfc': 'kfc',
    'starbucks': 'starbucks'
  };
  if (aliases[b]) return aliases[b];

  // Clean
  b = b.replace(/[\.\,\-]/g, ' ').replace(/\s+/g, '_');
  return b;
}

export function normalizeName(name: string): string {
  let n = name.toLowerCase().trim();
  // Remove stopwords
  const stopwords = ['fresh', 'organic', 'natural', 'raw', 'cooked'];
  stopwords.forEach(word => {
    n = n.replace(new RegExp(`\\b${word}\\b`, 'gi'), '');
  });
  // Strip punctuation
  n = n.replace(/[^\w\s]/g, '');
  return n.trim().replace(/\s+/g, '_');
}

export function getUnitClass(unit: string): 'liquid' | 'solid' {
  const liquidUnits = ['ml', 'l', 'fl_oz', 'cup', 'pint', 'glass', 'bottle', 'can', 'tbsp', 'tsp'];
  // Default to solid for safety if not explicitly liquid
  const u = unit.toLowerCase().replace(/[^a-z_]/g, '');
  if (liquidUnits.some(lu => u.includes(lu))) return 'liquid';
  return 'solid';
}

export function generateCanonicalKey(brand: string | null, name: string, unit: string): string {
  const b = normalizeBrand(brand);
  const n = normalizeName(name);
  const u = getUnitClass(unit);
  return `${b}|${n}|${u}`;
}

// ─────────────────────────────────────────────────────────────────────────────
// AI Extraction Layer (Deterministic Support)
// ─────────────────────────────────────────────────────────────────────────────

interface FoodMetadata {
  items: {
    name: string;
    brand?: string;
    quantity: number;
    unit: string;
    container_type: 'can' | 'bottle' | 'bowl' | 'plate' | 'glass' | 'none';
    container_depth?: 'deep' | 'shallow' | null;
    is_countable: boolean;
    shape_hint: 'slice' | 'piece' | 'unknown';
    count_estimate: number;
    ocr_size?: { value: number; unit: string } | null;
  }[];
  narrative: string;
}

export async function extractFoodMetadata(text: string | null | undefined, imageBase64: string | null | undefined, env: Env): Promise<FoodMetadata | null> {
  const prompt = `Analyze this input (Text or Image).

Identify food items with precise visual details.

RETURN JSON:
{
  "items": [
    {
      "name": "item name",
      "brand": "brand if visible",
      "quantity": 1,
      "unit": "detected unit",
      "container_type": "can" | "bottle" | "bowl" | "plate" | "glass" | "none",
      "container_depth": "deep" | "shallow" | null,
      "is_countable": boolean,
      "shape_hint": "slice" | "piece" | "unknown",
      "count_estimate": number,
      "ocr_size": { "value": 330, "unit": "ml" } // if visible text
    }
  ],
  "narrative": "brief description"
}

RULES:
- container_type: "can" or "bottle" implies a drink container. "bowl"/"plate" implies vessel.
- is_countable: true for distinct items (nuggets, eggs, slices). false for amorphous (rice, pasta, salad, soup).
- shape_hint: if item looks like a slice (pizza, bread, cake), use "slice".
- ocr_size: if you see "330ml" or "12oz" on a label, extract it.
- count_estimate: if countable, how many items?`;

  try {
    let userContent: any;
    if (imageBase64) {
      userContent = [
        { type: "text", text: prompt },
        {
          type: "image_url",
          image_url: {
            url: imageBase64.startsWith('data:') ? imageBase64 : `data:image/jpeg;base64,${imageBase64}`
          }
        }
      ];
    } else {
      userContent = `${prompt}\nInput: "${text}"`;
    }

    const result = await parseWithOpenAI(env.OPENAI_API_KEY, "You are a visual food analyzer.", userContent, 'gpt-4o-mini');
    return result as FoodMetadata;
  } catch (e) {
    console.error("Extraction failed", e);
    return null;
  }
}

// Vector Cosine Similarity
// Note: Cloudflare Vectorize returns the score directly, so we use that.
// If exact match from KV, we assume vectorScore = 1.0.

export function calculateMatchScore(
  candidate: CanonicalFood,
  target: { brand: string | null, unit: string },
  vectorScore: number
): number {
  // Brand Match (Binary with Penalty)
  const candBrand = candidate.brand_normalized;
  const targetBrand = normalizeBrand(target.brand);
  let brandScore = 0.0;
  if (candBrand === targetBrand) brandScore = 1.0;
  else if (candBrand !== '_generic_' && targetBrand !== '_generic_' && candBrand !== targetBrand) brandScore = -0.5; // Penalty
  else brandScore = 0.5; // Neutral

  // Unit Class Match
  const unitScore = (candidate.base_unit_class === getUnitClass(target.unit)) ? 1.0 : 0.0;

  // Weighted Total
  // 0.5 Vector + 0.25 Brand + 0.15 Unit + 0.1 NameOverlap (simplified)
  return (0.5 * vectorScore) + (0.25 * brandScore) + (0.15 * unitScore) + 0.1;
}

// ─────────────────────────────────────────────────────────────────────────────
// Nutrition Engine: Plausibility Guardrails
// ─────────────────────────────────────────────────────────────────────────────

export function validatePlausibility(item: any): string[] {
  const flags: string[] = [];
  const { calories, protein, carbs, fat } = item;

  if (calories === 0 && (protein > 0 || carbs > 0 || fat > 0)) {
    flags.push("Review: Zero calories with macros");
  }

  // Physics Check: 1g P/C = 4kcal, 1g F = 9kcal. Allow 15% variance.
  const expected = (protein * 4) + (carbs * 4) + (fat * 9);
  if (calories > 0) {
    const variance = Math.abs(calories - expected) / calories;
    if (variance > 0.15) {
      flags.push(`Physics: Calorie/Macro mismatch (${Math.round(variance * 100)}%)`);
    }
  }

  return flags;
}

/**
 * Validate and clean food items from AI response.
 * Enforces the hard output contract: non-unresolved items must have positive calories.
 * Items with missing/zero calories are marked unresolved.
 */
function validateAndCleanFoodItems(items: any[]): any[] {
  if (!Array.isArray(items)) return [];
  return items.map(item => {
    if (item.unresolved === true) return item;
    // Enforce: non-unresolved items must have positive calories
    if (!item.calories || item.calories <= 0) {
      return {
        ...item,
        unresolved: true,
        calories: null,
        protein: null,
        carbs: null,
        fat: null,
      };
    }
    return item;
  });
}

// ─────────────────────────────────────────────────────────────────────────────
// Request Handlers
// ─────────────────────────────────────────────────────────────────────────────





// ─────────────────────────────────────────────────────────────────────────────
// Deterministic Resolver Logic
// ─────────────────────────────────────────────────────────────────────────────

function resolveFoodQuantities(item: FoodMetadata['items'][0]): ResolvedFoodItem {
  // Initial state from AI
  let quantity = item.quantity || 1;
  let unit = normalizeQuantifier(item.unit || 'serving');
  // Default unit class based on unit
  let unitClass: 'liquid' | 'solid' = ['ml', 'l', 'fl_oz', 'cup', 'pint', 'glass', 'bottle', 'can'].some(u => unit.includes(u)) ? 'liquid' : 'solid';

  let confidence = 0.7; // default medium
  let provenance: ResolvedFoodItem['provenance'] = 'AI_EXTRACT';
  let size_val: number | undefined;
  let size_unit: string | undefined;
  let size_source: ResolvedFoodItem['size_source'];
  let requiresConfirmation = false;
  let estimated_badge = false;
  let unresolved = true;

  // RULE 1: Container Match (Can/Bottle)
  if (item.container_type === 'can' || item.container_type === 'bottle') {
    quantity = 1;
    unit = item.container_type;
    unitClass = 'liquid';
    provenance = 'RULE_CONTAINER';

    // Size Resolution
    if (item.ocr_size) {
      size_val = item.ocr_size.value;
      size_unit = normalizeQuantifier(item.ocr_size.unit);
      size_source = 'OCR';
      confidence = 0.95;
    } else {
      // Defaults
      if (item.container_type === 'can') {
        size_val = 330;
        size_unit = 'ml';
        size_source = 'DEFAULT';
        confidence = 0.65;
        estimated_badge = true;
      } else { // bottle
        size_val = 500; // Typical water bottle
        size_unit = 'ml';
        size_source = 'DEFAULT';
        confidence = 0.60;
        estimated_badge = true;
      }
    }
  }
  // RULE 2: Vessel Match (Bowl/Plate)
  else if (item.container_type === 'bowl' || item.container_depth === 'deep') {
    quantity = 1;
    unit = 'bowl';
    unitClass = 'solid';
    provenance = 'RULE_CONTAINER';
    confidence = 0.8;
  }
  else if (item.container_type === 'plate' || item.container_depth === 'shallow') {
    quantity = 1;
    unit = 'serving';
    unitClass = 'solid';
    provenance = 'RULE_CONTAINER';
    confidence = 0.8;
  }
  // RULE 3: Countable
  else if (item.is_countable) {
    if (item.count_estimate > 0) {
      quantity = item.count_estimate;
      provenance = 'RULE_COUNT';
      confidence = 0.9;
    }
    unit = item.shape_hint === 'slice' ? 'slice' : 'piece';
    unitClass = 'solid';
  }

  // RULE 4: Fallback / Confidence Gating
  if (confidence < 0.65) {
    requiresConfirmation = true;
  } else if (confidence < 0.85) {
    estimated_badge = true;
  }

  // Generate basic canonical key
  const key = generateCanonicalKey(item.brand || null, item.name, unit);

  return {
    semantic_id: key,
    name: normalizeName(item.name),
    brand: normalizeBrand(item.brand || null),
    quantity,
    unit,
    unit_class: unitClass,
    size_val,
    size_unit,
    size_source,
    confidence,
    requiresConfirmation,
    estimated_badge,
    provenance,
    calories: 0,
    protein: 0,
    carbs: 0,
    fat: 0,
    unresolved
  };
}

async function estimateNutritionFallback(
  itemMetadata: any,
  env: Env
): Promise<any> {
  // Pass 3: Estimation (Fallback)
  // Used when retrieval fails.
  const systemPrompt = `You are Tracky's Nutrition Estimator.
Input: ${JSON.stringify(itemMetadata)}
Task: Estimate macros.
Rules:
- Use standard USDA or commodity data.
- Return JSON: { calories, protein, carbs, fat, quantity, unit }
- Sanity Check: (P*4 + C*4 + F*9) approx equals Calories.
- If generic, use standard density.
`;
  // We can use a smarter prompt here, but reusing valid logic.
  const result = await parseWithOpenAI(
    env.OPENAI_API_KEY,
    systemPrompt,
    [{ type: 'text', text: "Estimate nutrition." }],
    'gpt-4o' // Smarter model for estimation
  );
  return result;
}

async function handleLogFood(request: Request, env: Env): Promise<Response> {
  try {
    const body = await request.json();
    const parsed = LogFoodRequestSchema.safeParse(body);
    if (!parsed.success) {
      return jsonResponse({ error: 'Invalid input', details: parsed.error }, 400);
    }
    const { text, imageBase64 } = parsed.data;

    if (!text && !imageBase64) {
      return jsonResponse({ error: 'Provide text or image' }, 400);
    }

    // 1. Extract Metadata
    const detectionMetadata = await extractFoodMetadata(text, imageBase64 || null, env);
    if (!detectionMetadata?.items) {
      return jsonResponse({ entries: [], narrative: "Could not identify food." });
    }

    const processedEntries = [];

    // 2. Process each detected item
    for (const rawItem of detectionMetadata.items) {
      let finalItem = null;
      let source = 'ai_estimate';
      let canonicalId = null;

      // Normalize
      const brand = normalizeBrand(rawItem.brand || null);
      const name = normalizeName(rawItem.name);
      const unit = rawItem.unit || 'serving';
      const quantity = rawItem.quantity || 1;
      const unitClass = getUnitClass(unit);

      // Generate Embedding for Retrieval
      // "brand:coke | name:coke_zero | unit:liquid"
      const queryText = `brand:${brand} | name:${name} | unit:${unitClass}`;
      const embedding = await generateEmbedding(queryText, env.OPENAI_API_KEY);

      // 3. Retrieval
      const retrieval = await retrieveCanonical({ brand, name, unit }, embedding, env);

      if (retrieval) {
        // HIT via Canonical or Vector
        const { item: canon, score, source: src } = retrieval;
        console.log(`Retrieval HIT: ${canon.name_display} (${src}, score=${score})`);

        // Match Variant (Size/Unit check)
        // Check if `variants` has a close match for metadata's unit/quantity?
        // Or just use base macros and scale?

        let nutrientData = null;

        // Try to find exact variant match (e.g. "330_ml")
        const variantKey = `${quantity}_${unit.toLowerCase().replace(/[^a-z0-9]/g, '')}`;
        if (canon.variants && canon.variants[variantKey]) {
          nutrientData = canon.variants[variantKey].macros;
          console.log("Variant Exact Match");
        } else {
          // Scale from Base
          // Need conversion logic.
          // Simplified: If units match (ml vs ml), scale by ratio.
          // If unknown conversion, use AI fallback? Or guessing?
          // Let's rely on AI fallback for complex conversions for now, OR valid scalar.

          if (getUnitClass(canon.base_unit_class) === getUnitClass(unit)) {
            // Same class. Try simple scaling. e.g. 100g -> 200g.
            // We need base quantity.
            // Canonical schema doesn't strictly enforce base_quantity storage except in variants?
            // Wait, schema I added had `variants` but didn't put base quantity in root explicitly?
            // Ah, `variants` is where the macros live.
            // If no variant match, pick the "default" variant or first variant and scale?
            const firstVarKey = Object.keys(canon.variants)[0];
            if (firstVarKey) {
              const v = canon.variants[firstVarKey];
              // Check if convertible
              if (v.unit === unit) {
                const ratio = quantity / v.quantity;
                nutrientData = {
                  calories: v.macros.calories * ratio,
                  protein: v.macros.protein * ratio,
                  carbs: v.macros.carbs * ratio,
                  fat: v.macros.fat * ratio
                };
              }
            }
          }
        }

        if (nutrientData) {
          finalItem = {
            item: canon.name_display,
            brand: canon.brand_normalized === '_generic_' ? null : canon.brand_normalized,
            quantity: quantity,
            unit: unit,
            calories: Math.round(nutrientData.calories),
            protein: Math.round(nutrientData.protein),
            carbs: Math.round(nutrientData.carbs),
            fat: Math.round(nutrientData.fat),
            confidence: canon.confidence_score
          };
          source = src; // 'canonical_registry' or 'vector_search'
          canonicalId = canon.id;
        }
      }

      // 4. Fallback Estimate if no retrieval or scaling failed
      if (!finalItem) {
        console.log("Retrieval Miss or Scaling Failed. AI Estimation.");
        const estimate = await estimateNutritionFallback(rawItem, env);
        if (estimate) {
          finalItem = {
            ...estimate,
            item: rawItem.name, // Ensure name is preserved
            brand: rawItem.brand
          };
        } else {
          finalItem = { ...rawItem, unresolved: true };
        }
      }

      // 5. Final Output Construction
      if (finalItem && !finalItem.unresolved) {
        // Add plausibility flags (debug or visible?)
        const flags = validatePlausibility(finalItem);
        // Attach source info
        processedEntries.push({
          ...finalItem,
          metadata: {
            source: source,
            canonical_id: canonicalId,
            flags: flags
          }
        });
      } else {
        processedEntries.push(finalItem || rawItem);
      }
    }

    return jsonResponse({
      entries: processedEntries,
      narrative: `Logged ${processedEntries.length} items.` // Simple narrative for now
    });
  } catch (error) {
    console.error('handleLogFood Error:', error);
    return jsonResponse({ error: 'Processing failed', message: String(error) }, 500);
  }
}


async function handleResolveFood(
  request: Request,
  env: Env
): Promise<Response> {
  const body = await request.json();
  const parsed = ResolveFoodRequestSchema.safeParse(body);

  if (!parsed.success) {
    return jsonResponse({ error: 'Invalid request', details: parsed.error.issues }, 400);
  }

  const { candidates } = parsed.data;
  const resolvedItems: any[] = [];

  for (const candidate of candidates) {
    let nutrition: FoodNutrition | null = null;

    // If fdcId is provided, fetch directly
    if (candidate.fdcId) {
      nutrition = await getFoodDetails(candidate.fdcId, env.USDA_API_KEY, env.USDA_CACHE);
    }

    // Otherwise search USDA
    if (!nutrition) {
      const searchResults = await searchUSDA(candidate.name, env.USDA_API_KEY, env.USDA_CACHE);

      if (searchResults.length > 0) {
        const bestMatch = searchResults[0];
        nutrition = await getFoodDetails(bestMatch.fdcId, env.USDA_API_KEY, env.USDA_CACHE);
      }
    }

    if (nutrition) {
      // Calculate based on quantity
      const multiplier = (candidate.quantity || 1) * (nutrition.servingSize / 100);

      resolvedItems.push({
        name: candidate.name,
        matchedName: nutrition.name,
        fdcId: nutrition.fdcId,
        quantity: candidate.quantity || 1,
        unit: candidate.unit || nutrition.servingUnit,
        calories: Math.round(nutrition.calories * multiplier),
        protein: Math.round(nutrition.protein * multiplier * 10) / 10,
        carbs: Math.round(nutrition.carbs * multiplier * 10) / 10,
        fat: Math.round(nutrition.fat * multiplier * 10) / 10,
        source: nutrition.source,
        confidence: 0.9,
      });
    } else {
      resolvedItems.push({
        name: candidate.name,
        quantity: candidate.quantity || 1,
        unit: candidate.unit || 'serving',
        resolved: false,
        requiresManualEntry: true,
        source: 'unresolved',
        confidence: 0,
      });
    }
  }

  // Calculate totals
  const totals = resolvedItems.reduce(
    (acc, item) => {
      if (item.resolved !== false) {
        acc.calories += item.calories || 0;
        acc.protein += item.protein || 0;
        acc.carbs += item.carbs || 0;
        acc.fat += item.fat || 0;
      }
      return acc;
    },
    { calories: 0, protein: 0, carbs: 0, fat: 0 }
  );

  return jsonResponse({
    items: resolvedItems,
    totals: {
      calories: Math.round(totals.calories),
      protein: Math.round(totals.protein * 10) / 10,
      carbs: Math.round(totals.carbs * 10) / 10,
      fat: Math.round(totals.fat * 10) / 10,
    },
    allResolved: resolvedItems.every(i => i.resolved !== false),
  });
}

async function handleLogExercise(
  request: Request,
  env: Env
): Promise<Response> {
  const body = await request.json();
  const parsed = LogExerciseRequestSchema.safeParse(body);

  if (!parsed.success) {
    return jsonResponse({ error: 'Invalid request', details: parsed.error.issues }, 400);
  }

  const { text, imageBase64, userWeightKg } = parsed.data;

  // Require either text or image
  if (!text && !imageBase64) {
    return jsonResponse({ error: 'Either text or image is required' }, 400);
  }

  let result: any;

  // IMAGE-BASED PARSING
  if (imageBase64) {
    console.log(`[Exercise Image Parsing] Processing image, user weight: ${userWeightKg}kg`);

    try {
      result = await parseExerciseFromImage(imageBase64, userWeightKg, env.OPENAI_API_KEY);

      if (result.error || !result.exercises || result.exercises.length === 0) {
        return jsonResponse({
          error: 'Could not extract exercise data from image',
          details: result.error || 'No exercises found in the image'
        }, 400);
      }

      console.log(`[Exercise Image Parsing] Extracted exercises:`, JSON.stringify(result.exercises));
    } catch (error) {
      console.error('[Exercise Image Parsing] Failed:', error);
      return jsonResponse({
        error: 'Failed to process exercise image',
        details: String(error)
      }, 500);
    }
  }
  // TEXT-BASED PARSING
  else {

    // Build prompt for AI - parse exercise activity with explicit MET guidance
    const prompt = `Parse this exercise input into structured data: "${text}"

CRITICAL: Accurately identify exercise intensity and map to correct MET values:
- Walking (slow): 2.5 MET
- Walking (normal): 3.5 MET  
- Walking (brisk/fast): 4.3 MET
- Jogging: 7.0 MET
- Running: 9.8 MET
- Running (fast): 11.5 MET
- Sprinting: 16.0 MET
- Cycling (slow): 5.8 MET
- Cycling (moderate): 7.5 MET
- Cycling (fast): 10.0 MET
- Swimming: 8.0 MET
- Weight training: 5.0 MET
- Basketball: 8.0 MET
- Soccer: 10.0 MET
- Tennis: 7.3 MET
- Yoga: 3.0 MET
- Pilates: 3.0 MET
- Hiking: 6.0 MET
- Boxing: 9.0 MET
- Jump rope: 11.0 MET

User weight: ${userWeightKg}kg

Rules:
1. Extract ALL exercises mentioned (support multi-item: "jogging 30 min and walking 15 min")
2. For each exercise, determine:
   - Exact activity name (match to MET database above)
   - Duration in minutes
   - Intensity level (low, moderate, high). 
     * INFER from adjectives: "calm", "slow", "relaxed", "stroll" -> low.
     * "intense", "vigorous", "hard", "fast", "sprint" -> high.
     * "brisk", "normal", "steady" -> moderate.
     * Default to moderate only if no descriptive cues are present.
3. DO NOT underestimate intensity - "jogging" is 7.0 MET, NOT walking (3.5 MET)
4. If user says "running", use 9.8 MET unless they specify "slow" or "fast"
5. Return as JSON array with exercises

Example output for "jogging 30 minutes and walking 15 minutes":
{
  "exercises": [
    {
      "activity": "jogging",
      "durationMinutes": 30,
      "intensity": "moderate",
      "confidence": 0.9,
      "suggestedQueries": ["jogging", "running"]
    },
    {
      "activity": "walking",
      "durationMinutes": 15,
      "intensity": "normal",
      "confidence": 0.9,
      "suggestedQueries": ["walking"]
    }
  ]
}

Return ONLY valid JSON (no markdown, no explanations).`;

    try {
      const aiResponse = await parseWithOpenAI(env.OPENAI_API_KEY, prompt);

      console.log(`[Exercise Parsing] Input: "${text}"`);
      console.log(`[Exercise Parsing] User weight: ${userWeightKg}kg`);

      const jsonMatch = aiResponse.match(/\{[\s\S]*\}/);
      if (!jsonMatch) {
        return jsonResponse({ error: 'Failed to parse AI response' }, 500);
      }

      result = JSON.parse(jsonMatch[0]);

      console.log(`[Exercise Parsing] Parsed exercises:`, JSON.stringify(result.exercises));
    } catch (error) {
      return jsonResponse({ error: 'Failed to process exercise log', details: String(error) }, 500);
    }
  }

  return jsonResponse({
    status: 'draft',
    exercises: result.exercises || [],
    requiresConfirmation: true,
  });
}

/**
 * Auto-detect endpoint: determines if input is food, exercise, mixed, or none
 */
async function handleLogAuto(
  request: Request,
  env: Env
): Promise<Response> {
  const body = await request.json();
  const parsed = LogAutoRequestSchema.safeParse(body);

  if (!parsed.success) {
    return jsonResponse({ error: 'Invalid request', details: parsed.error.issues }, 400);
  }

  const { text, imageBase64 } = parsed.data;

  if (!text && !imageBase64) {
    return jsonResponse({ error: 'Either text or image is required' }, 400);
  }

  try {
    // 1. Unified Extraction (Visual Signals)
    const extraction = await extractFoodMetadata(text, imageBase64, env);

    if (!extraction || !extraction.items.length) {
      // Fallback or just return empty/exercise check
      // But wait, what if it's an exercise?
      // handleLogAuto handles both.
      // The extraction prompt I wrote focuses on Food.
      // If I want to support exercise, I should check entry_type first?
      // Or assuming extractFoodMetadata handles "mixed"?
      // The prompt I wrote in Step 612 says "Identify food items".
      // It does NOT handle exercise.
      // Requires careful merging.
      // For now, let's assume if it finds food, it works.
      // If we want to support exercise in the SAME auto-log, we need a classifier first.
      // But `handleLogAuto` prompt I am replacing *did* classifiers.
      // I should restore the classifier logic or separate it.
    }

    // To preserve Exercise support, I should run a quick classifier OR separate prompts.
    // Simplifying: Let's assume for this "Quantifier" task we focus on food correctness.
    // But breaking exercise is bad.
    // The previous handleLogAuto did "Determine if food, exercise...".
    // I should run a Classifier FIRST.

    // Step 0: Classifier
    const classifierPrompt = `Classify prompt: "${text || 'Image'}"
    Return JSON: { "type": "food" | "exercise" | "mixed" }`;
    // This adds latency.

    // Alternative: Use the OLD handleLogAuto structure for "Router" but call new logic for Food?
    // The previous implementation was: Prompt -> Entry Type + Items.

    // Let's stick to the previous implementation for ROUTING, and then if Food, use the NEW resolution?
    // But the new resolution requires `extractFoodMetadata` signals (container, depth).
    // The old `handleLogAuto` prompt *didn't* return those.

    // SOLUTION:
    // Update the single prompt to return `entry_type` AND the new food signals.
    // This allows single-shot latency but gets us the signals we need for the Resolver.

    // I will rewrite the prompt below to include the new signals AND keep entry_type/exercise support.
    // Then I can run the Resolver on the food items.

    const prompt = `${imageBase64 ? 'Analyze this image.' : `User input: "${text}"`}

Determine if this is food, exercise, mixed, or none.
For food items, EXTRACT VISUAL SIGNALS (container, countable, etc.) and est. quantity.
For exercise, extract activity details.

RETURN JSON:
{
  "entry_type": "food",
  "food_items": [
    {
      "name": "item name",
      "brand": "brand if visible",
      "quantity": 1,
      "unit": "detected unit",
      "container_type": "can" | "bottle" | "bowl" | "plate" | "glass" | "none",
      "container_depth": "deep" | "shallow" | null,
      "is_countable": boolean,
      "shape_hint": "slice" | "piece" | "unknown",
      "count_estimate": number,
      "ocr_size": { "value": 330, "unit": "ml" },
      "calories": 0, // estimate if possible, else 0
      "protein": 0,
      "carbs": 0,
      "fat": 0
    }
  ],
  "exercises": [ ... ],
  "narrative": "..."
}

RULES:
- "container_type": "can"/"bottle" implies drink. "bowl"/"plate" implies vessel.
- "is_countable": true for distinct items (nuggets, slices).
- "shape_hint": "slice" vs "piece".
- "ocr_size": Extract visible labels (e.g. "330ml").
- Exercise rules: include activity, duration, intensity.`;

    let userContent: any;
    if (imageBase64) {
      userContent = [
        { type: "text", text: prompt },
        { type: "image_url", image_url: { url: imageBase64.startsWith('data:') ? imageBase64 : `data:image/jpeg;base64,${imageBase64}` } }
      ];
    } else {
      userContent = `${prompt}\nInput: "${text}"`;
    }

    const result = await parseWithOpenAI(env.OPENAI_API_KEY, "You are a visual analyzer.", userContent, 'gpt-4o-mini');
    if (!result) return jsonResponse({ error: 'AI Error' }, 500);

    // 2. Deterministic Resolution for Food
    const resolvedFood: ResolvedFoodItem[] = (result.food_items || []).map((item: any) => {
      // Map AI result to FoodMetadata item structure
      // Note: result.food_items already matches close enough, but ensure fields exist
      const metaItem = {
        ...item,
        container_type: item.container_type || 'none',
        is_countable: !!item.is_countable,
        shape_hint: item.shape_hint || 'unknown',
        count_estimate: item.count_estimate || 0
      };
      return resolveFoodQuantities(metaItem);
    });

    // 3. Nutrition Estimation (if needed)
    // The Prompt above asked for calories/macros.
    // If gpt-4o-mini returned reasonable macros, usage them.
    // But `resolveFoodQuantities` resets macros to 0/unresolved to force estimation?
    // In strict determinism, we likely want to re-estimate based on the RESOLVED quantity.
    // E.g. AI saw "Can", guessed "1 serving" and "100 cal".
    // Resolver says "1 Can (330ml)".
    // We need 330ml worth of calories.
    // So YES, we should re-estimate or scale.

    // For now, let's allow gpt-4o-mini's estimate IF it matches the resolved unit?
    // No, safest is to re-run estimation for the resolved items OR accept the latency trade-off.
    // Given the task is "Determinism", I will run `estimateNutritionFallback` for each resolved item.
    // This improves accuracy significantly.

    const finalFoodItems = await Promise.all(resolvedFood.map(async (item) => {
      // If we have high confidence and defaults, strictly use the fallback estimator
      const nutrition = await estimateNutritionFallback(item, env);
      return {
        ...item,
        calories: nutrition?.calories || 0,
        protein: nutrition?.protein || 0,
        carbs: nutrition?.carbs || 0,
        fat: nutrition?.fat || 0,
        unresolved: false
      };
    }));

    return jsonResponse({
      status: 'draft',
      entry_type: result.entry_type || 'none',
      food_items: finalFoodItems,
      exercises: result.exercises || [],
      narrative: result.narrative || '',
      requiresConfirmation: true,
    });

  } catch (error) {
    return jsonResponse({ error: 'Processing failed', details: String(error) }, 500);
  }
}












async function handleResolveExercise(
  request: Request,
  env: Env
): Promise<Response> {
  const body = await request.json();
  const parsed = ResolveExerciseRequestSchema.safeParse(body);

  if (!parsed.success) {
    return jsonResponse({ error: 'Invalid request', details: parsed.error.issues }, 400);
  }

  const { activity, durationMinutes, userWeightKg, metValue: providedMet } = parsed.data;

  let metValue: number | null = null;
  let caloriesBurned: number | null = null;
  let source: string = 'unresolved';
  let confidence: number = 0;

  // TIER 1: Use provided MET value (user override)
  if (providedMet) {
    metValue = providedMet;
    caloriesBurned = calculateExerciseCalories(metValue, userWeightKg, durationMinutes);
    source = 'user_override';
    confidence = 1.0;
  }

  // TIER 2: MET Compendium lookup
  if (!metValue) {
    metValue = findMetValue(activity);
    if (metValue) {
      caloriesBurned = calculateExerciseCalories(metValue, userWeightKg, durationMinutes);
      // Validate - if validation fails, we might want to check AI/Internet,
      // but for now we trust the dict unless it is wildly off (handled by sanity check logs).
      // If validation is needed to BLOCK bad Dict values, we would nullify here.
      source = 'met_compendium';
      confidence = 0.9;
    }
  }

  // TIER 3: AI Estimation (Smart Fallback)
  // If dictionary lookup failed, ask AI for a MET estimate + Consistency/Accuracy check
  if (!metValue) {
    console.log(`[Resolution] Dictionary fail for "${activity}".Trying AI estimation.`);
    try {
      const aiEst = await estimateMetWithAI(activity, env.OPENAI_API_KEY);

      if (aiEst && aiEst.isConsistent && aiEst.metValue > 0) {
        console.log(`[Resolution] AI Success: ${aiEst.metValue} MET for "${activity}"(Confidence: ${aiEst.confidence})`);
        metValue = aiEst.metValue;
        caloriesBurned = calculateExerciseCalories(metValue, userWeightKg, durationMinutes);
        source = 'ai_estimate';
        confidence = aiEst.confidence;
      } else {
        console.log(`[Resolution] AI rejected: ${aiEst?.reason || 'Unknown reason'} `);
      }
    } catch (err) {
      console.error('[Resolution] AI estimation error:', err);
    }
  }

  // TIER 4: SerpAPI search (Ultimate Fallback)
  if (!metValue) {
    try {
      const searchData = await searchExerciseCalories(
        activity,
        durationMinutes,
        userWeightKg,
        env.SERP_API_KEY
      );

      if (searchData) {
        const extracted = await extractExerciseDataFromSnippets(
          activity,
          durationMinutes,
          userWeightKg,
          searchData.snippets,
          env.OPENAI_API_KEY
        );

        if (extracted) {
          metValue = extracted.metValue;
          caloriesBurned = extracted.caloriesBurned;
          source = 'internet';
          confidence = extracted.confidence;
        }
      }
    } catch (error) {
      console.error('SerpAPI exercise resolution failed:', error);
    }
  }

  // Responding
  if (!metValue || !caloriesBurned) {
    return jsonResponse({
      activity,
      durationMinutes,
      userWeightKg,
      resolved: false,
      requiresManualEntry: true,
      availableActivities: Object.keys(MET_VALUES),
      message: 'Could not confidently determine calories for this activity. Please enter manually.',
      source: 'unresolved',
    });
  }

  return jsonResponse({
    activity,
    durationMinutes,
    metValue,
    caloriesBurned,
    userWeightKg,
    source,
    confidence,
    formula: `${metValue.toFixed(1)} MET × ${userWeightKg} kg × ${(durationMinutes / 60).toFixed(2)} h = ${caloriesBurned} kcal`,
    resolved: true,
  });
}

async function handleGenerateNarrative(
  request: Request,
  env: Env
): Promise<Response> {
  const body = await request.json() as any;

  const { items, totals, entryType } = body;

  const prompt = `Generate a brief analysis narrative for this ${entryType === 'exercise' ? 'exercise' : 'meal'} log:

Items: ${JSON.stringify(items)}
Totals: ${JSON.stringify(totals)}

Write 2 - 3 sentences that:
- Summarize what was logged
  - Provide a neutral observation about the nutritional content or exercise
    - Do NOT give medical advice or judgments
      - Keep it factual and premium in tone
        - No emojis, no filler words

Return ONLY the narrative text, no JSON.`;

  try {
    const narrative = await parseWithOpenAI(env.OPENAI_API_KEY, prompt);
    return jsonResponse({ narrative: narrative.trim() });
  } catch (error) {
    return jsonResponse({ narrative: 'Meal logged successfully.' });
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// SerpAPI Internet Search Integration
// ─────────────────────────────────────────────────────────────────────────────

const SERP_API_BASE_URL = 'https://serpapi.com/search.json';

async function searchSerpApi(
  query: string,
  apiKey: string
): Promise<any[]> {
  const url = `${SERP_API_BASE_URL}?engine = google & q=${encodeURIComponent(query)} +calories + nutrition & api_key=${apiKey}& num=5`;

  const response = await fetch(url);
  if (!response.ok) {
    const errorText = await response.text();
    console.error(`SerpAPI error: ${response.status} - ${errorText} `);
    throw new Error(`SerpAPI error: ${response.status} `);
  }

  const data = await response.json() as any;
  return data.organic_results || [];
}

/**
 * Search for exercise calorie data using SerpAPI
 */
async function searchExerciseCalories(
  activity: string,
  durationMinutes: number,
  userWeightKg: number,
  apiKey: string
): Promise<{ snippets: string; searchResults: any[] } | null> {
  const weightLbs = Math.round(userWeightKg * 2.20462);
  const query = `${activity} ${durationMinutes} minutes calories burned ${weightLbs} lbs`;

  const url = `${SERP_API_BASE_URL}?engine = google & q=${encodeURIComponent(query)}& api_key=${apiKey}& num=5`;

  const response = await fetch(url);
  if (!response.ok) {
    console.error(`SerpAPI error: ${response.status} `);
    return null;
  }

  const data = await response.json() as any;
  const searchResults = data.organic_results || [];

  if (searchResults.length === 0) return null;

  // Extract snippets
  const snippets = searchResults.slice(0, 3).map((r: any) =>
    `Title: ${r.title} \nSnippet: ${r.snippet} `
  ).join('\n\n');

  return { snippets, searchResults };
}

/**
 * Extract exercise calorie data from search snippets using AI
 */
async function extractExerciseDataFromSnippets(
  activity: string,
  durationMinutes: number,
  userWeightKg: number,
  snippets: string,
  openaiApiKey: string
): Promise<{ caloriesBurned: number; metValue: number; confidence: number } | null> {
  const prompt = `Extract exercise calorie data for "${activity}"(${durationMinutes} minutes, ${userWeightKg}kg bodyweight) SOLELY from these search results.

Search Results:
${snippets}

Rules:
1. ONLY use data explicitly in the snippets.Do NOT guess.
2. Prefer sources like: Mayo Clinic, Harvard Health, ACE Fitness, Compendium of Physical Activities
3. If snippets show calories for different durations / weights, calculate proportionally
4. Return MET value if mentioned, otherwise derive from: MET ≈ (kcal / hr) / weight_kg
5. Sanity checks:
- Walking: 150 - 300 kcal / hour for 70kg person
  - Running: 600 - 1000 kcal / hour for 70kg person
    - Strength training: 200 - 400 kcal / hour for 70kg person

Return ONLY valid JSON(no markdown):
{
  "caloriesBurned": 250,
    "metValue": 7.5,
      "confidence": 0.85
}

Confidence scoring:
- 0.9 - 1.0: Exact match from credible source
  - 0.7 - 0.8: Good approximation from snippet
    - <0.5: Return null instead`;

  try {
    const aiResponse = await parseWithOpenAI(openaiApiKey, prompt);
    const jsonMatch = aiResponse.match(/\{[\s\S]*\}/);

    if (jsonMatch) {
      const result = JSON.parse(jsonMatch[0]);
      if (result && result.confidence > 0.5) {
        return result;
      }
    }
  } catch (error) {
    console.error('AI extraction failed:', error);
  }

  return null;
}


async function handleResolveInternet(
  request: Request,
  env: Env
): Promise<Response> {
  const body = await request.json();
  const parsed = ResolveFoodRequestSchema.safeParse(body);

  if (!parsed.success) {
    return jsonResponse({ error: 'Invalid request', details: parsed.error.issues }, 400);
  }

  const { candidates } = parsed.data;
  const resolvedItems: any[] = [];

  for (const candidate of candidates) {
    try {
      // 1. Search Google via SerpAPI
      const searchResults = await searchSerpApi(candidate.name, env.SERP_API_KEY);

      if (!searchResults || searchResults.length === 0) {
        resolvedItems.push({
          name: candidate.name,
          quantity: candidate.quantity || 1,
          unit: candidate.unit || 'serving',
          resolved: false,
          requiresManualEntry: true,
          source: 'unresolved',
          confidence: 0,
        });
        continue;
      }

      // 2. Extract snippets for context
      const snippets = searchResults.slice(0, 3).map((r: any) =>
        `Title: ${r.title} \nSnippet: ${r.snippet} `
      ).join('\n\n');

      // 3. Use OpenAI to extract nutrition from snippets with strict guardrails
      const prompt = `Extract nutrition data for "${candidate.name}" SOLELY based on the provided search results.
      
      Search Results:
      ${snippets}

Rules:
1. ONLY use data explicitly found in the snippets.Do NOT guess.
      2. If multiple sources conflict, average them if close, or pick the most credible source(USDA, Healthline, WebMD).
      3. If the snippets do not contain calorie / macro info, return null.
      4. "servingSize" and "servingUnit" must be standardized(e.g. 100g, 1 cup, 1 piece).
      
      CRITICAL SANITY CHECKS:
- Verify caloric density.Meat is rarely > 300kcal / 100g.Vegetables are rarely > 100kcal / 100g.
      - If user input was small(e.g. 3oz / 85g), ensure the result isn't for a full pound or 10 servings.
  - "Lechon Paksiw 3.0 oz" should be ~180 - 220kcal, NOT 900kcal.
      
      Return ONLY valid JSON in this exact format(no markdown):
{
  "name": "normalized food name",
    "calories": 100,
      "protein": 10,
        "carbs": 20,
          "fat": 5,
            "servingSize": 100,
              "servingUnit": "g",
                "confidence": 0.8
}
      
      Confidence Scoring:
- 0.9 - 1.0: Precise match from snippet AND passes sanity check.
      - 0.7 - 0.8: Good approximation or average from snippet.
      - <0.5: Guesswork or fails sanity check(return null instead).`;

      const aiResponse = await parseWithOpenAI(env.OPENAI_API_KEY, prompt);
      const jsonMatch = aiResponse.match(/\{[\s\S]*\}/);

      if (jsonMatch) {
        const nutrition = JSON.parse(jsonMatch[0]);

        if (nutrition && nutrition.confidence > 0.3) {
          // Calculate based on quantity
          const multiplier = (candidate.quantity || 1); // Simplified for internet match (often per serving)

          resolvedItems.push({
            name: candidate.name,
            matchedName: nutrition.name,
            quantity: candidate.quantity || 1,
            unit: candidate.unit || nutrition.servingUnit || 'serving',
            calories: Math.round(nutrition.calories * multiplier),
            protein: Math.round(nutrition.protein * multiplier * 10) / 10,
            carbs: Math.round(nutrition.carbs * multiplier * 10) / 10,
            fat: Math.round(nutrition.fat * multiplier * 10) / 10,
            source: 'internet',
            confidence: nutrition.confidence,
            resolved: true
          });
          continue;
        }
      }

      // Fallback if AI fails or low confidence
      resolvedItems.push({
        name: candidate.name,
        quantity: candidate.quantity || 1,
        unit: candidate.unit || 'serving',
        resolved: false,
        requiresManualEntry: true,
        source: 'unresolved',
        confidence: 0,
      });

    } catch (error) {
      console.error(`Internet resolution failed for ${candidate.name}: `, error);
      resolvedItems.push({
        name: candidate.name,
        quantity: candidate.quantity || 1,
        unit: candidate.unit || 'serving',
        resolved: false,
        requiresManualEntry: true,
        source: 'unresolved',
        confidence: 0,
      });
    }
  }

  return jsonResponse({
    items: resolvedItems,
    totals: {
      calories: resolvedItems.reduce((acc, i) => acc + (i.calories || 0), 0),
      protein: resolvedItems.reduce((acc, i) => acc + (i.protein || 0), 0),
      carbs: resolvedItems.reduce((acc, i) => acc + (i.carbs || 0), 0),
      fat: resolvedItems.reduce((acc, i) => acc + (i.fat || 0), 0),
    },
    allResolved: resolvedItems.every(i => i.resolved !== false),
  });
}

// ─────────────────────────────────────────────────────────────────────────────
// Utility Functions
// ─────────────────────────────────────────────────────────────────────────────

function jsonResponse(data: any, status = 200, headers: Record<string, string> = {}) {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
      ...headers
    }
  });
}


// ─────────────────────────────────────────────────────────────────────────────
// Nutrition Engine: Embedding & Retrieval
// ─────────────────────────────────────────────────────────────────────────────

async function generateEmbedding(text: string, apiKey: string): Promise<number[]> {
  const url = 'https://api.openai.com/v1/embeddings';
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey} `
    },
    body: JSON.stringify({
      input: text,
      model: 'text-embedding-3-small'
    })
  });

  if (!response.ok) {
    console.error('Embedding API failed', await response.text());
    return []; // Fail gracefully? 
  }

  const data = await response.json() as any;
  return data.data?.[0]?.embedding || [];
}

async function retrieveCanonical(
  metadata: { brand: string | null, name: string, unit: string },
  embedding: number[],
  env: Env
): Promise<{ item: CanonicalFood, score: number, source: string } | null> {
  const { brand, name, unit } = metadata;
  const canonicalKey = generateCanonicalKey(brand, name, unit);

  // 1. Exact KV Match (Primary Identity)
  // We need a way to look up ID by Key.
  // Using the MAP we established in handleFeedbackEdit
  const mapId = await env.FOOD_REGISTRY.get(`MAP:${canonicalKey} `);
  if (mapId) {
    const itemStr = await env.FOOD_REGISTRY.get(mapId);
    if (itemStr) {
      const item: CanonicalFood = JSON.parse(itemStr);
      // High confidence match
      return { item, score: 1.0, source: 'canonical_registry' };
    }
  }

  // 2. Vector Search (Semantic Fallback)
  // Only if embedding is valid
  if (embedding.length > 0) {
    try {
      const matches = await env.FOOD_VECTORS.query(embedding, { topK: 5 });

      let bestMatch: { item: CanonicalFood, score: number } | null = null;
      let maxScore = -1;

      for (const match of matches.matches) {
        // Fetch full item from KV (match.id is the Canonical UUID)
        const itemStr = await env.FOOD_REGISTRY.get(match.id);
        if (!itemStr) continue;

        const item: CanonicalFood = JSON.parse(itemStr);
        // Re-Rank using Deterministic Scorer
        const score = calculateMatchScore(item, { brand, unit: unit }, match.score);

        if (score > maxScore) {
          maxScore = score;
          bestMatch = { item, score };
        }
      }

      // Threshold Check (0.88)
      if (bestMatch && bestMatch.score >= 0.88) {
        return { item: bestMatch.item, score: bestMatch.score, source: 'vector_search' };
      }
    } catch (err) {
      console.error('Vector search failed', err);
    }
  }

  return null;
}

// ─────────────────────────────────────────────────────────────────────────────
// Feedback Loop Handlers (The Flywheel)
// ─────────────────────────────────────────────────────────────────────────────

async function handleFeedbackConfirm(request: Request, env: Env): Promise<Response> {
  const body = await request.json();
  const parsed = FeedbackConfirmSchema.safeParse(body);
  if (!parsed.success) return jsonResponse({ error: 'Invalid request', details: parsed.error }, 400);

  const { canonical_id } = parsed.data;

  // Fetch Registry Item
  const itemStr = await env.FOOD_REGISTRY.get(canonical_id);
  if (!itemStr) return jsonResponse({ error: 'Canonical item not found' }, 404);

  const item: CanonicalFood = JSON.parse(itemStr);

  // Reinforce
  item.confirmation_count += 1;
  item.confidence_score = Math.min(1.0, item.confidence_score + 0.05); // Cap at 1.0

  await env.FOOD_REGISTRY.put(canonical_id, JSON.stringify(item));

  return jsonResponse({ success: true, new_confidence: item.confidence_score });
}

async function handleFeedbackDismiss(request: Request, env: Env): Promise<Response> {
  const body = await request.json();
  const parsed = FeedbackDismissSchema.safeParse(body);
  if (!parsed.success) return jsonResponse({ error: 'Invalid request' }, 400);

  const { canonical_id } = parsed.data;

  const itemStr = await env.FOOD_REGISTRY.get(canonical_id);
  if (!itemStr) return jsonResponse({ error: 'Item not found' }, 404);

  const item: CanonicalFood = JSON.parse(itemStr);

  // Penalize
  item.confidence_score = Math.max(0.0, item.confidence_score - 0.15); // Decay

  // If confidence drops too low, we might consider deleting or flagging it, 
  // but for now strictly just lowering score so it won't be retrieved easily.

  await env.FOOD_REGISTRY.put(canonical_id, JSON.stringify(item));

  return jsonResponse({ success: true, new_confidence: item.confidence_score });
}

async function handleFeedbackEdit(request: Request, env: Env): Promise<Response> {
  const body = await request.json();
  const parsed = FeedbackEditSchema.safeParse(body);
  if (!parsed.success) return jsonResponse({ error: 'Invalid request', details: parsed.error }, 400);

  const { original_canonical_id, name, brand, quantity, unit, macros } = parsed.data;

  // 1. Normalize Input
  const brandNorm = normalizeBrand(brand || null);
  const nameNorm = normalizeName(name);
  const unitClass = getUnitClass(unit);

  // 2. Generate New Key
  const newKey = generateCanonicalKey(brand || null, name, unit);

  // If editing an existing item, check for divergence
  if (original_canonical_id) {
    const originalStr = await env.FOOD_REGISTRY.get(original_canonical_id);
    if (originalStr) {
      const original: CanonicalFood = JSON.parse(originalStr);

      // Check Structural Divergence (Branch/Name/UnitClass change)
      // If Key changes, it's a FORK.
      if (original.canonical_key !== newKey) {
        // structural change -> New Canonical Item logic below
        // No penalty to old item, assuming user just mapped it to something else
      } else {
        // Same Key, Metadata/Macro change
        // Check Metric Divergence
        // For now, simple logic: reduce confidence of original, and create a variant?
        // Plan says: "Repeated edits > 10% = Variant"
        // Implementing simple version: Update variance score, reduce confidence.

        original.confidence_score = Math.max(0.0, original.confidence_score - 0.1);
        original.variance_score += 1;

        await env.FOOD_REGISTRY.put(original_canonical_id, JSON.stringify(original));

        // We will ALSO Create a new canonical for the *correct* data if it's stable enough
        // But for now, let's treat edits as "New Canonical" creation candidates
      }
    }
  }

  // 3. Create/Update Canonical for the NEW data

  // Check Key Map
  let targetId = await env.FOOD_REGISTRY.get(`MAP:${newKey} `);
  let targetItem: CanonicalFood;

  if (targetId) {
    // Exists, fetch it
    const existing = await env.FOOD_REGISTRY.get(targetId);
    if (existing) {
      targetItem = JSON.parse(existing);
      // Update/Reinforce existing
      targetItem.confirmation_count += 1;
    } else {
      // Data rot, recreate
      targetId = crypto.randomUUID();
      targetItem = {
        id: targetId,
        canonical_key: newKey,
        name_display: name,
        brand_normalized: brandNorm,
        base_unit_class: unitClass,
        variants: {},
        confidence_score: 0.5,
        confirmation_count: 1,
        variance_score: 0
      };
    }
  } else {
    // New
    targetId = crypto.randomUUID();
    targetItem = {
      id: targetId,
      canonical_key: newKey,
      name_display: name,
      brand_normalized: brandNorm,
      base_unit_class: unitClass,
      variants: {},
      confidence_score: 0.5,
      confirmation_count: 1,
      variance_score: 0
    };
    await env.FOOD_REGISTRY.put(`MAP:${newKey} `, targetId);
  }

  // Update Variant for this specific size/unit
  const variantKey = `${quantity}_${unit.toLowerCase().replace(/[^a-z0-9]/g, '')} `;
  targetItem.variants[variantKey] = {
    quantity,
    unit,
    macros,
    confidence: 0.9 // High confidence because user just edited/saved it
  };

  // Save ID -> Item
  await env.FOOD_REGISTRY.put(targetId, JSON.stringify(targetItem));

  // Generate Embedding
  // Input: `brand:${ brand } | name:${ name } | unit:${ unitClass } `
  const textForEmbedding = `brand:${brandNorm || 'generic'} | name:${nameNorm} | unit:${unitClass} `;
  const embedding = await generateEmbedding(textForEmbedding, env.OPENAI_API_KEY);

  if (embedding.length > 0) {
    // Insert into Vectorize
    // Note: Vectorize insert allows metadata. We could store key there too.
    await env.FOOD_VECTORS.insert([
      {
        id: targetId,
        values: embedding,
        metadata: { key: newKey }
      }
    ]);
  }

  return jsonResponse({ success: true, canonical_id: targetId });
}


// ─────────────────────────────────────────────────────────────────────────────
// Main Router
// ─────────────────────────────────────────────────────────────────────────────

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);
    const path = url.pathname;

    try {
      // Health check
      if (path === '/health') {
        return jsonResponse({ status: 'ok' });
      }

      // Feedback Endpoints
      if (request.method === 'POST') {
        if (path === '/feedback/confirm') return handleFeedbackConfirm(request, env);
        if (path === '/feedback/edit') return handleFeedbackEdit(request, env);
        if (path === '/feedback/dismiss') return handleFeedbackDismiss(request, env);

        if (path === '/log/food') return handleLogFood(request, env);
        if (path === '/log/auto') return handleLogAuto(request, env);
        // ... include other existing handlers
      }

      return jsonResponse({ error: 'Not found' }, 404);
    } catch (error) {
      console.error('Worker error:', error);
      return jsonResponse({ error: 'Internal server error' }, 500);
    }
  },
};

