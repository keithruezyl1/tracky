import { z } from 'zod';

// Environment bindings
interface Env {
  USDA_CACHE: KVNamespace;
  OPENAI_API_KEY: string;
  USDA_API_KEY: string;
  SERP_API_KEY: string;
  ENVIRONMENT: string;
}

// ─────────────────────────────────────────────────────────────────────────────
// Zod Schemas for Request/Response Validation
// ─────────────────────────────────────────────────────────────────────────────

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

// ─────────────────────────────────────────────────────────────────────────────
// Response Types
// ─────────────────────────────────────────────────────────────────────────────

interface ParsedFoodItem {
  name: string;
  quantity: number;
  unit: string;
  confidence: number;
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
// OpenAI API Integration (cost-efficient with Tracky system prompt)
// ─────────────────────────────────────────────────────────────────────────────

// Dual-model setup: gpt-4o-mini for text (faster/smarter), gpt-4o-mini for images (vision-capable)
const OPENAI_TEXT_MODEL = 'gpt-4o-mini';
const OPENAI_VISION_MODEL = 'gpt-4o-mini';
const OPENAI_API_URL = 'https://api.openai.com/v1/chat/completions';

// Tracky AI System Prompt (Accuracy & Safety First)
// Tracky AI System Prompt (Optimized for Speed & Safety)
const TRACKY_SYSTEM_PROMPT = `You are Tracky AI, a nutrition/workout assistant. Convert input to JSON.
1) ACCURACY: NO hallucinations. Flag unknown values as estimates/assumptions. NO medical advice.
2) UNITS: Scale portions strictly (e.g., 3oz != 100g). Cross-check calorie triggers (~500+ kcal must be huge).
3) PRIORITY: USDA > Common Knowledge > Estimates.
4) STYLE: Concise, premium, no filler/emojis.
5) FORMAT: Valid JSON only. No markdown.`;

async function parseWithOpenAI(
  apiKey: string,
  prompt: string,
  imageBase64?: string
): Promise<string> {
  const messages: any[] = [
    { role: 'system', content: TRACKY_SYSTEM_PROMPT }
  ];

  // Build user message content
  if (imageBase64) {
    // Vision request with image - use gpt-4o-mini
    messages.push({
      role: 'user',
      content: [
        { type: 'text', text: prompt },
        {
          type: 'image_url',
          image_url: {
            url: `data:image/jpeg;base64,${imageBase64}`,
            detail: 'low' // Cost-efficient
          }
        }
      ]
    });
  } else {
    // Text-only request - use gpt-3.5-turbo
    messages.push({
      role: 'user',
      content: prompt
    });
  }

  const model = imageBase64 ? OPENAI_VISION_MODEL : OPENAI_TEXT_MODEL;

  const response = await fetch(OPENAI_API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey}`
    },
    body: JSON.stringify({
      model,
      messages,
      temperature: 0.2,
      max_tokens: 800, // Cost-efficient limit
    }),
  });

  if (!response.ok) {
    const errorText = await response.text();
    console.error(`OpenAI API error: ${response.status} - ${errorText}`);
    throw new Error(`OpenAI API error: ${response.status} - ${errorText}`);
  }

  const data = await response.json() as any;
  return data.choices?.[0]?.message?.content || '';
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

Return ONLY valid JSON in this format:
{
  "exercises": [
    {
      "activity": "walking",
      "durationMinutes": 27.6,
      "distanceKm": 2.35,
      "caloriesBurned": 115,
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
// Request Handlers
// ─────────────────────────────────────────────────────────────────────────────

async function handleLogFood(
  request: Request,
  env: Env
): Promise<Response> {
  const body = await request.json();
  const parsed = LogFoodRequestSchema.safeParse(body);

  if (!parsed.success) {
    return jsonResponse({ error: 'Invalid request', details: parsed.error.issues }, 400);
  }

  const { text, imageBase64 } = parsed.data;

  if (!text && !imageBase64) {
    return jsonResponse({ error: 'Either text or image is required' }, 400);
  }

  // Build prompt for Gemini - parse only, no nutrition values
  const prompt = `Parse the following ${imageBase64 ? 'food image' : 'food description'} into structured items.

${text ? `User input: "${text}"` : 'Analyze the food items visible in the image.'}

Return ONLY valid JSON in this exact format (no markdown, no explanation):
{
  "items": [
    {
      "name": "food name (e.g., 'grilled chicken breast')",
      "quantity": 1,
      "unit": "piece/g/oz/cup/tbsp/etc",
      "confidence": 0.9,
      "suggestedQueries": ["chicken breast", "grilled chicken"]
    }
  ],
  "narrative": "Brief description of the meal"
}

IMPORTANT:
- Focus on identifying foods and reasonable portions
- Use common serving units
- Include multiple suggestedQueries for database matching
- If portion size is unclear, assume standard serving and note as Assumption
- SPLIT separate items (e.g. 'chicken and rice') into separate objects in the items array`;

  try {
    const aiResponse = await parseWithOpenAI(env.OPENAI_API_KEY, prompt, imageBase64 || undefined);

    // Parse the JSON response
    const jsonMatch = aiResponse.match(/\{[\s\S]*\}/);
    if (!jsonMatch) {
      return jsonResponse({ error: 'Failed to parse AI response' }, 500);
    }

    const result = JSON.parse(jsonMatch[0]);

    return jsonResponse({
      status: 'draft',
      items: result.items || [],
      narrative: result.narrative || '',
      requiresConfirmation: true,
    });
  } catch (error) {
    return jsonResponse({ error: 'Failed to process food log', details: String(error) }, 500);
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
   - Intensity level (if mentioned: light/moderate/vigorous)
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

  // Build prompt for auto-detection
  const prompt = `You are Tracky AI, a nutrition and exercise logging assistant.

${imageBase64 ? 'Analyze this image.' : `User input: "${text}"`}

Determine if this input describes:
- "food": eating, meals, snacks, drinks, ingredients
- "exercise": workouts, physical activities, sports, steps, walking, running
- "mixed": contains both food AND exercise
- "none": neither food nor exercise (greetings, questions, irrelevant)

Return ONLY valid JSON in this exact format (no markdown):
{
  "entry_type": "food" | "exercise" | "mixed" | "none",
  "food_items": [
    { "name": "item name", "quantity": 1, "unit": "serving", "confidence": 0.9, "suggestedQueries": ["query1"] }
  ],
  "exercises": [
    {
      "activity": "activity name",
      "durationMinutes": 30,
      "intensity": "moderate",
      "confidence": 0.9
    }
  ],
  "narrative": "Brief description"
}

Rules:
- If entry_type is "food" or "mixed", include food_items array
- If entry_type is "exercise" or "mixed", include exercises array
- If entry_type is "none", food_items and exercises should be empty
- Do NOT invent calorie/macro values
- Use reasonable portion assumptions`;

  try {
    const aiResponse = await parseWithOpenAI(env.OPENAI_API_KEY, prompt, imageBase64 || undefined);

    const jsonMatch = aiResponse.match(/\{[\s\S]*\}/);
    if (!jsonMatch) {
      return jsonResponse({ error: 'Failed to parse AI response' }, 500);
    }

    const result = JSON.parse(jsonMatch[0]);

    return jsonResponse({
      status: 'draft',
      entry_type: result.entry_type || 'none',
      food_items: result.food_items || [],
      exercises: result.exercises || [], // Changed from single exercise object to list
      narrative: result.narrative || '',
      requiresConfirmation: true,
    });
  } catch (error) {
    return jsonResponse({ error: 'Failed to process input', details: String(error) }, 500);
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
    console.log(`[Resolution] Dictionary fail for "${activity}". Trying AI estimation.`);
    try {
      const aiEst = await estimateMetWithAI(activity, env.OPENAI_API_KEY);

      if (aiEst && aiEst.isConsistent && aiEst.metValue > 0) {
        console.log(`[Resolution] AI Success: ${aiEst.metValue} MET for "${activity}" (Confidence: ${aiEst.confidence})`);
        metValue = aiEst.metValue;
        caloriesBurned = calculateExerciseCalories(metValue, userWeightKg, durationMinutes);
        source = 'ai_estimate';
        confidence = aiEst.confidence;
      } else {
        console.log(`[Resolution] AI rejected: ${aiEst?.reason || 'Unknown reason'}`);
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
    formula: `${metValue.toFixed(1)} MET × ${userWeightKg}kg × ${(durationMinutes / 60).toFixed(2)}h = ${caloriesBurned} kcal`,
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

Write 2-3 sentences that:
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
  const url = `${SERP_API_BASE_URL}?engine=google&q=${encodeURIComponent(query)} +calories +nutrition&api_key=${apiKey}&num=5`;

  const response = await fetch(url);
  if (!response.ok) {
    const errorText = await response.text();
    console.error(`SerpAPI error: ${response.status} - ${errorText}`);
    throw new Error(`SerpAPI error: ${response.status}`);
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

  const url = `${SERP_API_BASE_URL}?engine=google&q=${encodeURIComponent(query)}&api_key=${apiKey}&num=5`;

  const response = await fetch(url);
  if (!response.ok) {
    console.error(`SerpAPI error: ${response.status}`);
    return null;
  }

  const data = await response.json() as any;
  const searchResults = data.organic_results || [];

  if (searchResults.length === 0) return null;

  // Extract snippets
  const snippets = searchResults.slice(0, 3).map((r: any) =>
    `Title: ${r.title}\nSnippet: ${r.snippet}`
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
  const prompt = `Extract exercise calorie data for "${activity}" (${durationMinutes} minutes, ${userWeightKg}kg bodyweight) SOLELY from these search results.

Search Results:
${snippets}

Rules:
1. ONLY use data explicitly in the snippets. Do NOT guess.
2. Prefer sources like: Mayo Clinic, Harvard Health, ACE Fitness, Compendium of Physical Activities
3. If snippets show calories for different durations/weights, calculate proportionally
4. Return MET value if mentioned, otherwise derive from: MET ≈ (kcal/hr) / weight_kg
5. Sanity checks:
   - Walking: 150-300 kcal/hour for 70kg person
   - Running: 600-1000 kcal/hour for 70kg person
   - Strength training: 200-400 kcal/hour for 70kg person

Return ONLY valid JSON (no markdown):
{
  "caloriesBurned": 250,
  "metValue": 7.5,
  "confidence": 0.85
}

Confidence scoring:
- 0.9-1.0: Exact match from credible source
- 0.7-0.8: Good approximation from snippet
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
        `Title: ${r.title}\nSnippet: ${r.snippet}`
      ).join('\n\n');

      // 3. Use OpenAI to extract nutrition from snippets with strict guardrails
      const prompt = `Extract nutrition data for "${candidate.name}" SOLELY based on the provided search results.
      
      Search Results:
      ${snippets}
      
      Rules:
      1. ONLY use data explicitly found in the snippets. Do NOT guess.
      2. If multiple sources conflict, average them if close, or pick the most credible source (USDA, Healthline, WebMD).
      3. If the snippets do not contain calorie/macro info, return null.
      4. "servingSize" and "servingUnit" must be standardized (e.g. 100g, 1 cup, 1 piece).
      
      CRITICAL SANITY CHECKS:
      - Verify caloric density. Meat is rarely > 300kcal/100g. Vegetables are rarely > 100kcal/100g.
      - If user input was small (e.g. 3oz / 85g), ensure the result isn't for a full pound or 10 servings.
      - "Lechon Paksiw 3.0 oz" should be ~180-220kcal, NOT 900kcal.
      
      Return ONLY valid JSON in this exact format (no markdown):
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
      - 0.9-1.0: Precise match from snippet AND passes sanity check.
      - 0.7-0.8: Good approximation or average from snippet.
      - < 0.5: Guesswork or fails sanity check (return null instead).`;

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
      console.error(`Internet resolution failed for ${candidate.name}:`, error);
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

function jsonResponse(data: any, status = 200): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    },
  });
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Router
// ─────────────────────────────────────────────────────────────────────────────

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    // Handle CORS preflight
    if (request.method === 'OPTIONS') {
      return new Response(null, {
        headers: {
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
          'Access-Control-Allow-Headers': 'Content-Type',
        },
      });
    }

    const url = new URL(request.url);
    const path = url.pathname;

    try {
      // Health check with API key verification
      if (path === '/health' && request.method === 'GET') {
        return jsonResponse({
          status: 'ok',
          timestamp: new Date().toISOString(),
          openaiApiKey: env.OPENAI_API_KEY ? 'configured' : 'MISSING',
          usdaApiKey: env.USDA_API_KEY ? 'configured' : 'MISSING',
        });
      }

      // Food logging
      if (path === '/log/food' && request.method === 'POST') {
        return handleLogFood(request, env);
      }

      // Food resolution
      if (path === '/resolve/food' && request.method === 'POST') {
        return handleResolveFood(request, env);
      }

      // Internet resolution
      if (path === '/resolve/internet' && request.method === 'POST') {
        return handleResolveInternet(request, env);
      }

      // Exercise logging
      if (path === '/log/exercise' && request.method === 'POST') {
        return handleLogExercise(request, env);
      }

      // Auto-detect logging (food, exercise, mixed, or none)
      if (path === '/log/auto' && request.method === 'POST') {
        return handleLogAuto(request, env);
      }

      // Exercise resolution
      if (path === '/resolve/exercise' && request.method === 'POST') {
        return handleResolveExercise(request, env);
      }

      // Generate narrative
      if (path === '/narrative' && request.method === 'POST') {
        return handleGenerateNarrative(request, env);
      }

      return jsonResponse({ error: 'Not found' }, 404);
    } catch (error) {
      console.error('Worker error:', error);
      return jsonResponse({ error: 'Internal server error' }, 500);
    }
  },
};
