package com.example.data.education

data class HealthArticle(
  val id: String,
  val title: String,
  val category: String, // "Sleep", "Hydration", "Physical Activity", "Routines", "Nutrition", "Stress Management", "Everyday Wellness"
  val summary: String,
  val fullContent: String,
  val keyTakeaways: List<String>,
  val sourceOrg: String,
  val sourceReference: String,
  val lastReviewedDate: String, // e.g. "October 2025"
  val readTimeMinutes: Int = 3
)

object EducationLibrary {
  val articles: List<HealthArticle> = listOf(
    HealthArticle(
      id = "hydration-basics",
      title = "The Physiology of Everyday Hydration",
      category = "Hydration",
      summary = "Why water requirements vary by individual physiology, climate, and daily movement rather than a rigid 8-glass rule.",
      fullContent = """
        Water accounts for approximately 60% of an adult's body weight and is critical for cellular metabolism, temperature regulation, joint lubrication, and cognitive clarity.

        The common myth of '8 glasses for everyone' ignores biological variation. Authoritative health bodies like the WHO and the Indian Council of Medical Research (ICMR) recommend basing fluid intake on body mass, physical exertion, ambient humidity, and dietary moisture (such as fruits, soups, and lentils).

        Signs of optimal hydration include pale straw-colored urine, stable energy levels, and minimal dry mouth. Mild dehydration (as little as a 1.5% fluid loss) can impair working memory, increase perceived fatigue, and trigger tension headaches.

        Best practice: Distribute water intake steadily across waking hours, beginning with a glass upon waking to replenish nocturnal respiratory water loss.
      """.trimIndent(),
      keyTakeaways = listOf(
        "Individual fluid targets depend on weight, activity, and climate.",
        "Pale straw-colored urine is the gold-standard indicator of adequate hydration.",
        "Drink steadily throughout the day rather than chugging large volumes at once."
      ),
      sourceOrg = "World Health Organization (WHO) & ICMR Guidelines",
      sourceReference = "WHO Guidelines on Drinking-water Quality; ICMR-NIN Dietary Guidelines for Indians (2024–2025).",
      lastReviewedDate = "November 2025",
      readTimeMinutes = 3
    ),
    HealthArticle(
      id = "circadian-sleep-hygiene",
      title = "Circadian Rhythms & Restorative Sleep Architecture",
      category = "Sleep",
      summary = "How consistent light exposure and bedtime routines regulate melatonin and support deep physical restoration.",
      fullContent = """
        Sleep is an active neurological state governed by two biological mechanisms: Homeostatic Sleep Pressure (adenosine buildup) and the Circadian Master Clock (suprachiasmatic nucleus).

        Deep slow-wave sleep (NREM Stage 3) facilitates muscular repair, immune cytokine release, and cellular recovery, while REM sleep stabilizes emotional regulation and memory consolidation.

        Disrupting the sleep-wake schedule—even on weekends—induces 'social jetlag'. Research from the National Sleep Foundation and clinical chronobiology emphasizes that waking up at the same hour each morning is the single most potent anchor for circadian synchrony.

        Key environmental factors: Keep sleeping quarters cool (around 18–21°C), minimize blue-spectrum light 45 minutes prior to rest, and abstain from caffeine at least 6 hours before bedtime.
      """.trimIndent(),
      keyTakeaways = listOf(
        "A consistent wake-up time anchors your body's master circadian clock.",
        "Deep sleep drives physical repair; REM sleep drives memory and emotional processing.",
        "Screen blue light suppresses natural melatonin onset."
      ),
      sourceOrg = "National Sleep Foundation & Chronobiology Consensus",
      sourceReference = "Consensus Conference on Sleep Duration and Health; Journal of Clinical Sleep Medicine.",
      lastReviewedDate = "January 2026",
      readTimeMinutes = 4
    ),
    HealthArticle(
      id = "daily-movement-breaks",
      title = "Breaking Sedentary Time: The Power of Micro-Movement",
      category = "Physical Activity",
      summary = "Why accumulating 8,000 daily steps and taking regular standing breaks protects cardiovascular and metabolic vitality.",
      fullContent = """
        Sedentary behavior—sitting for prolonged uninterrupted stretches—slows lipoprotein lipase enzyme activity, hampering your body's ability to clear glucose and triglycerides from the bloodstream.

        Landmark observational studies tracking over 100,000 adults demonstrate that taking 7,500 to 9,000 steps per day provides substantial cardiovascular and longevity benefits, with incremental benefits leveling off past 10,000 steps.

        Equally important is frequency: Breaking up every 60 minutes of sitting with a 2-minute light walk or posture stretch improves insulin sensitivity and prevents postural fatigue in the lumbar spine.

        Rest-day wisdom: Rest days are an essential phase of fitness, allowing tendon recovery and glycogen replenishment. Active recovery (gentle walking or light stretching) is ideal on rest days.
      """.trimIndent(),
      keyTakeaways = listOf(
        "7,500–9,000 daily steps provides peak cardiovascular and metabolic longevity benefits.",
        "2-minute movement breaks every hour significantly improve glycemic control.",
        "Rest days are necessary for cellular repair and preventing repetitive strain."
      ),
      sourceOrg = "World Health Organization (WHO)",
      sourceReference = "WHO Guidelines on Physical Activity and Sedentary Behaviour; The Lancet Public Health.",
      lastReviewedDate = "December 2025",
      readTimeMinutes = 3
    ),
    HealthArticle(
      id = "stress-and-breath",
      title = "Down-Regulating the Nervous System with Box Breathing",
      category = "Stress Management",
      summary = "Activating the parasympathetic vagus nerve through intentional, structured diaphragmatic breath cycles.",
      fullContent = """
        When psychological or physical stress occurs, the sympathetic nervous system triggers elevated cortisol and adrenaline, raising heart rate and muscle tension.

        Controlled diaphragmatic breathing directly stimulates the vagus nerve, signaling the heart's sinoatrial node to reduce heart rate variability stress spikes and initiating parasympathetic recovery.

        The '4-4-4-4 Box Breathing' protocol:
        1. Inhale deeply through the nose for 4 counts.
        2. Hold gently at full lung capacity for 4 counts.
        3. Exhale smoothly through the mouth for 4 counts.
        4. Hold lungs empty for 4 counts.

        Practicing this for just 3 to 5 minutes calms acute anxiety, lowers systolic blood pressure, and sharpens executive cognitive function.
      """.trimIndent(),
      keyTakeaways = listOf(
        "Diaphragmatic breathing directly stimulates the parasympathetic vagus nerve.",
        "Box breathing (4-4-4-4) lowers acute heart rate and cortisol spikes.",
        "A 3-minute practice before high-focus tasks restores emotional composure."
      ),
      sourceOrg = "Harvard Health Publishing & Mind-Body Institute",
      sourceReference = "Harvard Medical School Special Health Report: Stress Management and Resilience.",
      lastReviewedDate = "January 2026",
      readTimeMinutes = 3
    ),
    HealthArticle(
      id = "whole-foods-nutrition",
      title = "Nutritional Foundations: Fiber, Color & Unprocessed Whole Foods",
      category = "Nutrition",
      summary = "Focusing on dietary diversity, gut microbiota support, and whole foods rather than restrictive, faddish diets.",
      fullContent = """
        Sustainable nutritional wellness centers on quality and nourishment rather than punitive deprivation or extreme caloric deficit.

        The human gut microbiome thrives on dietary diversity, particularly prebiotic plant fiber from diverse vegetables, legumes, whole grains, and seeds. Epidemiological guidelines from ICMR and WHO emphasize filling half your plate with colorful vegetables and fruits, one quarter with quality protein, and one quarter with complex carbohydrates.

        Mindful eating practices—eating without digital screens, chewing thoroughly, and recognizing physiological hunger cues—naturally prevents overeating while improving digestive enzyme secretion.
      """.trimIndent(),
      keyTakeaways = listOf(
        "Aim for diverse plant fibers to cultivate a healthy, resilient gut microbiome.",
        "Prioritize whole, minimally processed ingredients over calorie obsession.",
        "Eating without digital distraction fosters healthy satiety cues."
      ),
      sourceOrg = "Indian Council of Medical Research (ICMR) & WHO",
      sourceReference = "ICMR-National Institute of Nutrition (NIN) Dietary Guidelines; WHO Healthy Diet Factsheet.",
      lastReviewedDate = "November 2025",
      readTimeMinutes = 4
    ),
    HealthArticle(
      id = "atomic-habits-routine",
      title = "Habit Stacking & Sustainable Routine Formation",
      category = "Healthy Routines",
      summary = "The behavioral psychology of micro-commitments, cue-routine-reward loops, and guilt-free streak preservation.",
      fullContent = """
        Behavioral research shows that willpower is a depletable resource. Sustainable habits succeed not through heroic motivation, but by reducing cognitive friction.

        'Habit Stacking' links a desired new micro-behavior to an established existing automatic routine. For instance: 'After I turn on the morning kettle (established cue), I will drink one full glass of water (new habit).'

        Equally important is avoiding the 'all-or-nothing' trap. Missing a single day of a habit does not erase neurological habit pathways. Research demonstrates that consistency over months depends on immediately forgiving slips and gently resuming the routine on the following day without guilt.
      """.trimIndent(),
      keyTakeaways = listOf(
        "Attach new micro-habits to existing daily anchors (Habit Stacking).",
        "Make initial habits so small they require negligible willpower.",
        "A single missed day does not break long-term habit formation; self-compassion maintains momentum."
      ),
      sourceOrg = "Behavioral Science & Cognitive Health Review",
      sourceReference = "European Journal of Social Psychology; Behavioral Medicine Quarterly.",
      lastReviewedDate = "January 2026",
      readTimeMinutes = 3
    )
  )

  fun getArticlesByCategory(category: String): List<HealthArticle> {
    return if (category == "All") articles else articles.filter { it.category.equals(category, ignoreCase = true) }
  }

  fun getCategories(): List<String> =
    listOf("All", "Sleep", "Hydration", "Physical Activity", "Stress Management", "Nutrition", "Healthy Routines")
}
