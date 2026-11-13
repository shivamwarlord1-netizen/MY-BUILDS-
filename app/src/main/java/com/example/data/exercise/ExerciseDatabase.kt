package com.example.data.exercise

data class Exercise(
  val id: String,
  val name: String,
  val muscleGroup: MuscleGroup,
  val primaryMuscle: String,
  val secondaryMuscles: List<String>,
  val equipment: String, // e.g. "Dumbbells", "Barbell", "Cables", "Bodyweight", "Kettlebell"
  val difficulty: String, // "Beginner", "Intermediate", "Advanced"
  val recommendedSetsReps: String = "3 Sets × 12 Reps",
  val visualTip: String = "Keep core braced & control the descent",
  val overview: String,
  val setupSteps: List<String>,
  val executionSteps: List<String>,
  val breathingPattern: String,
  val commonMistakes: List<String>,
  val anatomyDiagramType: AnatomyType
)

enum class MuscleGroup(val displayName: String) {
  SHOULDERS("Shoulders"),
  CHEST("Chest"),
  BACK("Back"),
  ARMS("Arms"),
  LEGS("Legs"),
  CORE("Core")
}

enum class AnatomyType {
  SHOULDERS,
  CHEST,
  BACK,
  BICEPS,
  TRICEPS,
  QUADS,
  HAMSTRINGS,
  CALVES,
  CORE
}

object ExerciseDatabase {
  val exercises: List<Exercise> = listOf(
    // ------------------- SHOULDERS -------------------
    Exercise(
      id = "overhead-dumbbell-press",
      name = "Overhead Dumbbell Shoulder Press",
      muscleGroup = MuscleGroup.SHOULDERS,
      primaryMuscle = "Anterior Deltoids & Medial Deltoids",
      secondaryMuscles = listOf("Triceps Brachii", "Upper Pectoralis", "Trapezius"),
      equipment = "Dumbbells & Bench",
      difficulty = "Intermediate",
      overview = "A gold-standard compound movement for building overhead vertical pushing power, clavicular stability, and rounded deltoid volume.",
      setupSteps = listOf(
        "Set an adjustable bench angle between 75° to 85° to prevent excessive lumbar hyperextension.",
        "Kick the dumbbells smoothly up to shoulder height using your knees.",
        "Brace your core, anchor feet flat against the floor, and pull shoulder blades firmly into the pad."
      ),
      executionSteps = listOf(
        "Press dumbbells upward along a natural arc without aggressively banging the weights together at the peak.",
        "Lock out elbows softly at the apex while keeping deltoids under continuous tension.",
        "Lower dumbbells slowly with control over 3 seconds until elbows reach roughly 90°."
      ),
      breathingPattern = "Inhale during the slow lowering eccentric; exhale powerfully on the upward drive.",
      commonMistakes = listOf(
        "Flaring elbows excessively wide at 90° (keep them tucked ~30° in the scapular plane).",
        "Arching lower back away from bench to recruit upper chest."
      ),
      anatomyDiagramType = AnatomyType.SHOULDERS
    ),
    Exercise(
      id = "lateral-deltoid-raises",
      name = "Dumbbell Lateral Deltoid Raise",
      muscleGroup = MuscleGroup.SHOULDERS,
      primaryMuscle = "Lateral (Medial) Deltoid",
      secondaryMuscles = listOf("Anterior Deltoid", "Upper Trapezius", "Forearm Extensors"),
      equipment = "Dumbbells",
      difficulty = "Beginner",
      overview = "The premier isolation exercise for targeting the lateral deltoid head to widen shoulder taper and improve V-taper aesthetics.",
      setupSteps = listOf(
        "Stand tall with feet hip-width apart and dumbbells resting slightly in front of thighs.",
        "Introduce a gentle 10° forward lean at the hips to align with lateral deltoid fibers.",
        "Maintain a soft, locked micro-bend in both elbows throughout the entire set."
      ),
      executionSteps = listOf(
        "Raise arms outward in the scapular plane (roughly 20° forward from direct sides).",
        "Lead with elbows rather than wrists until upper arms reach parallel with the floor.",
        "Pause for a fraction of a second at peak contraction, then lower slowly over 2.5 seconds."
      ),
      breathingPattern = "Exhale as you raise outward; inhale steadily during the controlled descent.",
      commonMistakes = listOf(
        "Using excessive hip swing or momentum to heave heavy weights up.",
        "Shrugging traps up towards ears rather than letting lateral deltoids do the work."
      ),
      anatomyDiagramType = AnatomyType.SHOULDERS
    ),
    Exercise(
      id = "rear-delt-face-pulls",
      name = "Cable Face Pull & External Rotation",
      muscleGroup = MuscleGroup.SHOULDERS,
      primaryMuscle = "Posterior Deltoids & Infraspinatus",
      secondaryMuscles = listOf("Rhomboids", "Middle Trapezius", "Teres Minor"),
      equipment = "Cable Machine & Rope Attachment",
      difficulty = "Beginner",
      overview = "Essential shoulder health movement that reverses rounded-shoulder desk posture and develops the posterior deltoid shelf.",
      setupSteps = listOf(
        "Attach a double-rope to a cable pulley positioned at upper chest or eye level.",
        "Grip the rope with thumbs pointing back towards you.",
        "Step back into a staggered athletic stance with core braced."
      ),
      executionSteps = listOf(
        "Pull the center of the rope directly toward the bridge of your nose.",
        "Simultaneously spread the rope ends apart, externally rotating your hands behind your ears.",
        "Squeeze the rear deltoids and scapular retractors for 1 second before extending arms forward."
      ),
      breathingPattern = "Exhale as you pull and externally rotate; inhale as arms return forward.",
      commonMistakes = listOf(
        "Letting elbows drop below wrists during the pull.",
        "Using too much load and leaning torso backwards."
      ),
      anatomyDiagramType = AnatomyType.SHOULDERS
    ),

    // ------------------- CHEST -------------------
    Exercise(
      id = "incline-dumbbell-press",
      name = "Incline Dumbbell Bench Press",
      muscleGroup = MuscleGroup.CHEST,
      primaryMuscle = "Clavicular Pectoralis (Upper Chest)",
      secondaryMuscles = listOf("Anterior Deltoids", "Triceps Brachii"),
      equipment = "Adjustable Bench & Dumbbells",
      difficulty = "Intermediate",
      overview = "Premier mass builder for the upper clavicular chest fibers, promoting full pectoral thickness without harsh acromial impingement.",
      setupSteps = listOf(
        "Set bench to an incline angle of 30° (higher angles transfer stress excessively to shoulders).",
        "Retract and depress shoulder blades, creating a firm, stable base on the bench.",
        "Plant heels firmly on the ground with dumbbells resting over upper chest."
      ),
      executionSteps = listOf(
        "Press the dumbbells up and slightly inward over mid-chest along a natural converging arc.",
        "Squeeze chest at the top without allowing shoulder blades to unlock from the bench.",
        "Descend under strict 3-second eccentric control until you feel a deep, comfortable pectoral stretch."
      ),
      breathingPattern = "Inhale deep belly air on descent; exhale forcefully through the concentric press.",
      commonMistakes = listOf(
        "Allowing elbows to flare perpendicular to torso (keep elbows tucked at 45°–60°).",
        "Lifting glutes off the seat during heavy repetitions."
      ),
      anatomyDiagramType = AnatomyType.CHEST
    ),
    Exercise(
      id = "flat-barbell-bench-press",
      name = "Barbell Flat Bench Press",
      muscleGroup = MuscleGroup.CHEST,
      primaryMuscle = "Sternal Pectoralis Major (Mid/Lower Chest)",
      secondaryMuscles = listOf("Anterior Deltoids", "Triceps Brachii", "Serratus Anterior"),
      equipment = "Olympic Barbell & Flat Bench",
      difficulty = "Intermediate",
      overview = "Foundational horizontal pushing compound exercise providing maximum mechanical tension for overall chest and pressing strength.",
      setupSteps = listOf(
        "Lie with eyes directly under the racked barbell.",
        "Grip the bar slightly wider than shoulder width (wrists stacked over elbows).",
        "Plant feet firmly, retract scapulae, and create a natural arch in your thoracic spine."
      ),
      executionSteps = listOf(
        "Unrack the bar and stabilize it directly over mid-chest with elbows locked.",
        "Lower the bar under control until it gently touches the lower sternum.",
        "Drive the bar explosively upward and slightly back toward the rack uprights."
      ),
      breathingPattern = "Inhale deep into the diaphragm as bar lowers; exhale as you press past the sticking point.",
      commonMistakes = listOf(
        "Bouncing the barbell violently off the ribs.",
        "Uncoupling shoulder blades at the top of the press."
      ),
      anatomyDiagramType = AnatomyType.CHEST
    ),
    Exercise(
      id = "cable-chest-fly",
      name = "Standing Mid-Cable Chest Flye",
      muscleGroup = MuscleGroup.CHEST,
      primaryMuscle = "Pectoralis Major (Sternal & Inner Squeeze)",
      secondaryMuscles = listOf("Anterior Deltoids", "Biceps Short Head (Stabilizer)"),
      equipment = "Dual Cable Pulley",
      difficulty = "Beginner",
      overview = "Continuous-tension isolation movement that maximizes horizontal adduction without joint strain at lockout.",
      setupSteps = listOf(
        "Position cable pulleys at chest height.",
        "Step forward into a staggered stance with a slight forward torso angle.",
        "Maintain a slight fixed bend in elbows like hugging a wide barrel."
      ),
      executionSteps = listOf(
        "Bring handles together in front of your chest along a wide circular arc.",
        "Cross hands slightly or touch knuckles for a 1-second peak isometric squeeze.",
        "Open arms back slowly to full horizontal stretch, feeling chest fibers lengthen."
      ),
      breathingPattern = "Exhale as handles meet in front; inhale deeply during the opening stretch.",
      commonMistakes = listOf(
        "Turning the flye into a pressing motion by bending and straightening elbows.",
        "Letting elbows travel behind torso without control."
      ),
      anatomyDiagramType = AnatomyType.CHEST
    ),

    // ------------------- BACK -------------------
    Exercise(
      id = "lat-pulldown-neutral",
      name = "Neutral-Grip Lat Pulldown",
      muscleGroup = MuscleGroup.BACK,
      primaryMuscle = "Latissimus Dorsi (Lats)",
      secondaryMuscles = listOf("Biceps Brachii", "Rhomboids", "Lower Trapezius", "Brachialis"),
      equipment = "Cable Lat Pulldown Machine & V-Bar",
      difficulty = "Beginner",
      overview = "Vertical pulling staple that widens the back and creates a tapered silhouette with minimal elbow and wrist discomfort.",
      setupSteps = listOf(
        "Adjust thigh pads securely so knees cannot lift during the movement.",
        "Grip the neutral handles and sit with chest elevated and slight 10° torso lean.",
        "Initiate by depressing your shoulder blades down and back."
      ),
      executionSteps = listOf(
        "Pull the handle down toward upper chest by driving elbows straight down toward your ribs.",
        "Contract your lats firmly at the bottom for a solid 1-second pause.",
        "Allow the bar to return upward smoothly over 3 seconds to achieve a full overhead lat stretch."
      ),
      breathingPattern = "Exhale smoothly as you pull down; inhale as arms extend overhead.",
      commonMistakes = listOf(
        "Leaning backwards 45° and turning the movement into a sloppy row.",
        "Pulling with forearms rather than leading with elbows."
      ),
      anatomyDiagramType = AnatomyType.BACK
    ),
    Exercise(
      id = "barbell-bent-over-row",
      name = "Barbell Bent-Over Row",
      muscleGroup = MuscleGroup.BACK,
      primaryMuscle = "Mid-Back (Rhomboids, Latissimus Dorsi, Traps)",
      secondaryMuscles = listOf("Rear Deltoids", "Erector Spinae", "Biceps", "Hamstrings"),
      equipment = "Olympic Barbell",
      difficulty = "Advanced",
      overview = "The quintessential back thickness movement, testing posterior chain static endurance while building powerful mid-back musculature.",
      setupSteps = listOf(
        "Hinge at hips with knees slightly bent until torso is roughly 45° to floor.",
        "Hold barbell with an overhand grip slightly wider than shoulder width.",
        "Keep lumbar spine neutral and core braced tightly throughout."
      ),
      executionSteps = listOf(
        "Pull the bar up toward your lower abdomen/navel, retracting shoulder blades.",
        "Drive elbows toward the ceiling and squeeze back musculature forcefully.",
        "Lower barbell with control until arms are fully extended without rounding spine."
      ),
      breathingPattern = "Exhale on upward pull; inhale as barbell returns toward knees.",
      commonMistakes = listOf(
        "Rounding the lower spine, which puts disc structures at risk.",
        "Using jerky torso momentum to whip the barbell upward."
      ),
      anatomyDiagramType = AnatomyType.BACK
    ),

    // ------------------- ARMS -------------------
    Exercise(
      id = "incline-dumbbell-curl",
      name = "Incline Dumbbell Bicep Curl",
      muscleGroup = MuscleGroup.ARMS,
      primaryMuscle = "Biceps Brachii (Long Head Peak)",
      secondaryMuscles = listOf("Brachialis", "Forearm Flexors"),
      equipment = "Incline Bench (45°–60°) & Dumbbells",
      difficulty = "Intermediate",
      overview = "Places the long head of the biceps under intense stretch overload at the shoulder joint, developing arm fullness and bicep peak.",
      setupSteps = listOf(
        "Sit on a 60° incline bench with back flat against the pad.",
        "Let arms hang straight down toward the floor perpendicular to the torso.",
        "Hold dumbbells with palms facing inward or slightly forward."
      ),
      executionSteps = listOf(
        "Curl dumbbells upward while supinating wrists (turning palms toward shoulders).",
        "Keep upper arms pinned in place without swinging elbows forward.",
        "Squeeze biceps hard at the peak, then lower slowly over 3 seconds to complete stretch."
      ),
      breathingPattern = "Exhale on the upward curl; inhale on the controlled descent.",
      commonMistakes = listOf(
        "Swinging elbows forward to cheat the initial portion of the lift.",
        "Failing to fully extend arms at the bottom."
      ),
      anatomyDiagramType = AnatomyType.BICEPS
    ),
    Exercise(
      id = "triceps-rope-pushdown",
      name = "Cable Triceps Rope Pushdown",
      muscleGroup = MuscleGroup.ARMS,
      primaryMuscle = "Triceps Brachii (Lateral & Medial Heads)",
      secondaryMuscles = listOf("Triceps Long Head", "Anconeus"),
      equipment = "High Cable Pulley & Rope Attachment",
      difficulty = "Beginner",
      overview = "The most popular triceps isolation exercise, allowing complete lockout and spreading of the rope for maximum lateral head engagement.",
      setupSteps = listOf(
        "Stand tall facing high cable pulley with a slight athletic forward hip hinge.",
        "Tuck elbows tightly against ribcage and grip rope ends firmly.",
        "Keep wrists neutral and shoulders depressed away from ears."
      ),
      executionSteps = listOf(
        "Extend forearms down until elbows lock out fully.",
        "Spread the rope handles apart at the bottom for intense triceps contraction.",
        "Return slowly until forearms are just past parallel with floor, keeping elbows stationary."
      ),
      breathingPattern = "Exhale as you push down to full lockout; inhale on the return.",
      commonMistakes = listOf(
        "Allowing elbows to drift forward and back like a pendulum.",
        "Leaning heavy bodyweight over the rope instead of using arm extension."
      ),
      anatomyDiagramType = AnatomyType.TRICEPS
    ),

    // ------------------- LEGS -------------------
    Exercise(
      id = "barbell-back-squat",
      name = "Barbell Back Squat",
      muscleGroup = MuscleGroup.LEGS,
      primaryMuscle = "Quadriceps & Gluteus Maximus",
      secondaryMuscles = listOf("Hamstrings", "Adductors", "Erector Spinae", "Core"),
      equipment = "Squat Rack & Olympic Barbell",
      difficulty = "Advanced",
      overview = "The king of lower-body exercises. Builds systemic lower body strength, knee joint resilience, and foundational athletic power.",
      setupSteps = listOf(
        "Rest barbell across upper traps (high bar) or across rear deltoids (low bar).",
        "Step back with feet shoulder-width apart, toes pointed slightly outward (15°–30°).",
        "Take a deep diaphragmatic breath, brace core 360°, and establish pelvic alignment."
      ),
      executionSteps = listOf(
        "Initiate by sitting hips back and down while knees track in line with toes.",
        "Descend until hip crease is at or below the top of knees (parallel depth).",
        "Drive through whole foot explosively to return to standing lockout."
      ),
      breathingPattern = "Inhale deep & brace into belly before descending; exhale past the sticking point.",
      commonMistakes = listOf(
        "Allowing knees to cave inward (valgus collapse) during the drive.",
        "Shifting weight onto toes and letting heels lift."
      ),
      anatomyDiagramType = AnatomyType.QUADS
    ),
    Exercise(
      id = "romanian-deadlift",
      name = "Dumbbell Romanian Deadlift (RDL)",
      muscleGroup = MuscleGroup.LEGS,
      primaryMuscle = "Hamstrings & Gluteus Maximus",
      secondaryMuscles = listOf("Erector Spinae", "Latissimus Dorsi", "Forearm Grip"),
      equipment = "Dumbbells or Barbell",
      difficulty = "Intermediate",
      overview = "Superior posterior chain movement that builds hamstring elasticity, hip hinge strength, and protects against lower-back strain.",
      setupSteps = listOf(
        "Stand tall holding dumbbells in front of thighs with feet hip-width apart.",
        "Keep shoulders pulled back and introduce a soft 15° micro-bend in knees.",
        "Fix your gaze 6 feet ahead to maintain cervical spine neutrality."
      ),
      executionSteps = listOf(
        "Push hips straight back as if touching a wall behind you with glutes.",
        "Glide dumbbells close down along thighs and shins until you feel a deep hamstring stretch.",
        "Drive hips forward powerfully and squeeze glutes to return to standing."
      ),
      breathingPattern = "Inhale and brace core as hips push back; exhale as hips lock out forward.",
      commonMistakes = listOf(
        "Bending knees excessively into a squat rather than a pure hip hinge.",
        "Rounding lumbar spine at the bottom of the stretch."
      ),
      anatomyDiagramType = AnatomyType.HAMSTRINGS
    ),
    Exercise(
      id = "standing-calf-raises",
      name = "Elevated Standing Calf Raise",
      muscleGroup = MuscleGroup.LEGS,
      primaryMuscle = "Gastrocnemius & Soleus",
      secondaryMuscles = listOf("Tibialis Posterior", "Foot Intrinsic Stabilizers"),
      equipment = "Step Platform & Dumbbell",
      difficulty = "Beginner",
      overview = "Direct plantarflexion exercise through full ankle range of motion to develop ankle stiffness and calf shape.",
      setupSteps = listOf(
        "Stand with balls of feet on an elevated step or edge, allowing heels to hang off.",
        "Hold a light dumbbell in one hand and rest other hand on a wall for balance.",
        "Keep leg straight with knee locked softly in extension."
      ),
      executionSteps = listOf(
        "Lower heels down into a deep calf stretch for 2 seconds.",
        "Push through big toes explosively to rise as high onto toes as possible.",
        "Hold the contracted peak for a full second before descending."
      ),
      breathingPattern = "Exhale on upward press; inhale on the downward stretch.",
      commonMistakes = listOf(
        "Bouncing rapidly at the bottom without pausing to eliminate Achilles tendon recoil.",
        "Rolling ankles outward rather than pushing evenly through balls of feet."
      ),
      anatomyDiagramType = AnatomyType.CALVES
    ),

    // ------------------- CORE -------------------
    Exercise(
      id = "hanging-knee-raise",
      name = "Hanging Knee / Leg Raise",
      muscleGroup = MuscleGroup.CORE,
      primaryMuscle = "Rectus Abdominis (Lower Region)",
      secondaryMuscles = listOf("Iliopsoas (Hip Flexors)", "Obliques", "Grip Forearms"),
      equipment = "Pull-Up Bar",
      difficulty = "Intermediate",
      overview = "Posterior pelvic tilt exercise that fires the entire abdominal wall while simultaneously decompressing the spinal column.",
      setupSteps = listOf(
        "Hang from a sturdy pull-up bar with an overhand grip slightly wider than shoulders.",
        "Engage lats slightly to stabilize shoulder girdle and eliminate passive swinging.",
        "Point toes forward and brace core."
      ),
      executionSteps = listOf(
        "Roll pelvis upward and draw knees smoothly toward chest.",
        "Focus on flexing the spine and curling pelvis forward rather than just lifting thighs.",
        "Lower legs with strict control over 2.5 seconds to zero swing."
      ),
      breathingPattern = "Exhale completely as knees curl up; inhale as legs lower.",
      commonMistakes = listOf(
        "Swinging body wildly to use momentum rather than abdominal contraction.",
        "Arching lower back at the bottom of each repetition."
      ),
      anatomyDiagramType = AnatomyType.CORE
    ),
    Exercise(
      id = "cable-woodchopper",
      name = "Standing Cable Rotational Woodchopper",
      muscleGroup = MuscleGroup.CORE,
      primaryMuscle = "Internal & External Obliques",
      secondaryMuscles = listOf("Rectus Abdominis", "Transverse Abdominis", "Shoulders"),
      equipment = "Cable Machine with Single D-Handle",
      difficulty = "Intermediate",
      overview = "Rotational core exercise developing transverse plane athletic power, rotational torque, and defined lateral waistlines.",
      setupSteps = listOf(
        "Set cable pulley at shoulder height and attach a single D-handle.",
        "Stand perpendicular to cable machine in a wide athletic stance, holding handle with both hands.",
        "Keep arms extended with a soft elbow bend."
      ),
      executionSteps = listOf(
        "Rotate torso smoothly across body while pivoting on rear foot.",
        "Keep arms extended and let core musculature generate the rotational power.",
        "Control the handle back slowly against cable resistance to starting point."
      ),
      breathingPattern = "Exhale sharply on the rotational drive; inhale on the return.",
      commonMistakes = listOf(
        "Pulling solely with arms rather than rotating through the thoracic spine and hips.",
        "Keeping rear foot glued flat, which can torque the knee."
      ),
      anatomyDiagramType = AnatomyType.CORE
    )
  )

  fun getExercisesByGroup(group: MuscleGroup?): List<Exercise> {
    return if (group == null) exercises else exercises.filter { it.muscleGroup == group }
  }

  fun getExerciseById(id: String): Exercise? {
    return exercises.find { it.id == id }
  }
}
