package com.example.data

import com.example.model.Announcement
import com.example.model.OlympiadCategory
import com.example.model.PastPaper
import com.example.model.QuizQuestion
import com.example.model.ScienceTopic
import com.example.model.StudentResult

object BDJSOData {

    val announcements = listOf(
        Announcement(
            id = "ann_1",
            title = "BDJSO 2026 National Round Registration Open!",
            date = "Sept 20, 2026",
            tag = "Registration",
            description = "Students from Class 3 to 10 across Bangladesh are invited to register online for the 12th Bangladesh Junior Science Olympiad. Winners will qualify for the IJSO Bangladesh Camp.",
            isImportant = true
        ),
        Announcement(
            id = "ann_2",
            title = "Regional Science Camp Schedule Announced",
            date = "Sept 15, 2026",
            tag = "Schedule",
            description = "Dhaka, Chattogram, Rajshahi, Khulna, Sylhet, and Barishal regional rounds will commence from October. Check your divisional venue list.",
            isImportant = false
        ),
        Announcement(
            id = "ann_3",
            title = "Syllabus Update for Secondary Category Biology",
            date = "Sept 05, 2026",
            tag = "Academic",
            description = "Molecular genetics, enzyme kinetics, and cell respiration modules have been updated based on the IJSO 2026 international syllabus.",
            isImportant = false
        ),
        Announcement(
            id = "ann_4",
            title = "IJSO Bangladesh Team Won 2 Silver & 4 Bronze Medals!",
            date = "August 28, 2026",
            tag = "Achievement",
            description = "Congratulations to Team Bangladesh for an exceptional performance at the 22nd International Junior Science Olympiad!",
            isImportant = true
        )
    )

    val sampleQuestions = listOf(
        // === PRIMARY CATEGORY QUESTIONS (Exact from PDF Page 6 & 7) ===
        QuizQuestion(
            id = 1,
            code = "Primary-Q1",
            subject = "Physics",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "রেজা ফর্মুলা-১ গাড়ি চালাচ্ছে। নিচে রেজার গাড়ির দূরত্ব এবং সময় দেখানো হয়েছে। গাড়ির গড় বেগ কত?",
            questionEnglish = "Reza is driving a Formula-1 car. The distance and time taken by Reza's car are shown below:\nWhat is the average speed of the car?",
            question = "Reza is driving a Formula-1 car. What is the average speed from the distance-time graph?",
            questionType = com.example.model.QuestionType.NUMERIC,
            correctNumericAnswer = "30",
            answerLabel = "Answer: 30",
            diagramType = "SPEED_TIME_GRAPH",
            explanation = "গাড়ির গড় বেগ = মোট দূরত্ব / মোট সময় = 60 km / 2 hours = 30 km/h (অথবা 30)।"
        ),
        QuizQuestion(
            id = 2,
            code = "Primary-Q2",
            subject = "Physics",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "নিচের লিভারটি ব্যবহার করে সিস্টেমটিকে ভারসাম্যে রাখতে প্রয়োজনীয় বল নির্ণয় কর।",
            questionEnglish = "Using the lever shown, determine the effort required to keep the system in balance.",
            question = "Using the lever shown, determine the effort required to keep the system in balance.",
            questionType = com.example.model.QuestionType.NUMERIC,
            correctNumericAnswer = "10",
            answerLabel = "Answer: 10",
            diagramType = "LEVER_BALANCE",
            explanation = "লিভারের মূলনীতি অনুযায়ী: বল × বলের বাহু = ভার × ভারের বাহু।\nEffort × 6 m = 30 N × 2 m = 60 N·m। অতএব, Effort = 60 / 6 = 10 N।"
        ),
        QuizQuestion(
            id = 3,
            code = "Primary-Q3",
            subject = "Physics",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "রাহাতের ভর 50 কেজি। সে নিচতলার একটি লিফটে প্রবেশ করে। লিফটি তাকে পঞ্চম তলায় নিয়ে যায়। রাহাতের পঞ্চম তলায় তুলতে লিফটটি কী পরিমাণ কাজ করে? প্রতিটি তলার উচ্চতা 3 মিটার এবং g=10 m/s² ধরা হল।",
            questionEnglish = "Rahat, who has a mass of 50 kg, steps into a lift on the ground floor. The lift carries him up to the 5th floor. How much work does the lift do to raise Rahat to the 5th floor? Each floor is 3 m high, and take g = 10 m/s².",
            question = "Rahat (mass 50 kg) enters ground floor lift to 5th floor (each 3m, g=10). Work done?",
            questionType = com.example.model.QuestionType.MCQ,
            options = listOf("500 J", "1500 J", "15000 J", "7500 J"),
            correctIndex = 3,
            answerLabel = "Answer: 7500 J (Option D)",
            diagramType = "NONE",
            explanation = "মোট উচ্চতা h = 5 × 3 m = 15 m।\nকৃতকাজ W = mgh = 50 kg × 10 m/s² × 15 m = 7500 J।"
        ),
        QuizQuestion(
            id = 4,
            code = "Primary-Q4",
            subject = "Physics",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "দুর্গা একটি আম খাড়াভাবে ফেলে দেয়; একই সাথে একটা আমকে অনুভূমিকভাবে ছুড়ে দেয়া হয়। দুর্গার আমটি 2s পর মাটিতে পড়লে, অন্য আমটি মাটিতে পড়তে কত সেকেন্ড লাগবে?",
            questionEnglish = "Durga drops a mango straight down; at the same instant a bird flicks another mango horizontally off the same branch. If Durga's mango hits the ground after 2s, how many seconds will it take for the other mango to hit the ground?",
            question = "Durga drops a mango straight down (hits in 2s). Another flicked horizontally. Time to ground?",
            questionType = com.example.model.QuestionType.MCQ,
            options = listOf("1s", "2s", "4s", "0.5s", "0.75s"),
            correctIndex = 1,
            answerLabel = "Answer: 2s (Option B)",
            diagramType = "NONE",
            explanation = "উভয় আমেরই আদি উলম্ব বেগ শূন্য (u_y = 0)। অভিকর্ষজ ত্বরণ উভয়ের জন্যই সমান হওয়ায় উল্লম্ব পতনের সময় অনুভূমিক বেগের ওপর নির্ভর করে না: t = √(2h/g) = 2 সেকেন্ড।"
        ),
        QuizQuestion(
            id = 5,
            code = "Primary-Q11",
            subject = "Chemistry",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "লিটল টুনটুনি অসাবধানতাবশত দুটি পাত্রে ধাতু ফেলে দিল। প্রথম পাত্রে লোহার পাত কপার সালফেট (CuSO4) দ্রবণে এবং দ্বিতীয় পাত্রে অ্যালুমিনিয়াম পাত সিলভার নাইট্রেট (AgNO3) দ্রবণে রাখা হলো। কয়েকদিন পর দেখা গেল উভয় পাত্রেই বড় পরিবর্তন হয়েছে। কী ঘটেছিল?",
            questionEnglish = "Little Tuntuni accidentally put metals in two different containers, avoiding Chotachchu's eyes and forgot.\nFirst container: An iron (Fe) sheet was placed in a copper sulfate (CuSO4) solution.\nSecond container: An aluminium (Al) sheet was placed in a silver nitrate (AgNO3) solution.\nAfter a few days, Chotachchu saw that a big change had occurred in both containers. What had happened to the two containers?",
            question = "Iron in CuSO4 and Aluminium in AgNO3 solutions. What reaction happened?",
            questionType = com.example.model.QuestionType.MCQ,
            options = listOf(
                "Iron displaced copper, and aluminium displaced silver",
                "Copper displaced iron, and silver displaced aluminium",
                "No chemical reaction occurred in either container",
                "Iron and aluminium melted and turned into liquid",
                "Both solutions only changed colour; no metal was displaced"
            ),
            correctIndex = 0,
            answerLabel = "Answer: A",
            diagramType = "NONE",
            explanation = "সক্রিয়তা ক্রম অনুযায়ী লোহা তামার চেয়ে অধিক সক্রিয় হওয়ায় লোহা কপারকে প্রতিস্থাপিত করে (Fe + CuSO4 → FeSO4 + Cu)। অনুরূপভাবে অ্যালুমিনিয়াম সিলভারের চেয়ে সক্রিয় হওয়ায় সিলভারকে প্রতিস্থাপিত করে (Al + 3AgNO3 → Al(NO3)3 + 3Ag)।"
        ),
        QuizQuestion(
            id = 6,
            code = "Primary-Q12",
            subject = "Biology",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "কালুর সর্দিকাশ ও কয়েকবার বমি হয়েছে—টক স্বাদ, পাকস্থলীর অর্ধহজমকৃত খাদ্য ও তরল। এই খাদ্য ও তরল কোথা থেকে এসেছে?",
            questionEnglish = "Kalu has a cold and has vomited a few times - tasting sour, with half-digested food and liquid. Where is this food and liquid coming from?",
            question = "Kalu has vomited tasting sour, half-digested food. Where did it come from?",
            questionType = com.example.model.QuestionType.MCQ,
            options = listOf("Stomach (পাকস্থলী)", "Small intestine (ক্ষুদ্রান্ত্র)", "Large intestine (বৃহদান্ত্র)", "Mouth cavity (মুখগহ্বর)", "Liver (যকৃত)"),
            correctIndex = 0,
            answerLabel = "Answer: A (Stomach)",
            diagramType = "NONE",
            explanation = "পাকস্থলী হাইড্রোক্লোরিক অ্যাসিড (HCl) নিঃসৃত করে যা খাদ্যমণ্ডকে অম্লীয় বা টক স্বাদ দেয় এবং আংশিক পরিপাকে সহায়তা করে।"
        ),
        QuizQuestion(
            id = 7,
            code = "Primary-Q13",
            subject = "Biology",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "একজন জিমন্যাস্ট মেরুদণ্ড পেছনে বাঁকিয়ে আর্চ তৈরি করতে পারে, আবার দাঁড়িয়ে পুরো শরীরের ওজন বহন করে। মেরুদণ্ডের কোন বৈশিষ্ট্য তাকে নমনীয়তা ও দৃঢ়তা দুটোই দেয়?",
            questionEnglish = "A gymnast can arch her spine backward yet the same spine supports her full body weight while standing. What property gives the spine both flexibility and support?",
            question = "What property gives human spine both flexibility and support?",
            questionType = com.example.model.QuestionType.MCQ,
            options = listOf(
                "Complete rigid attachment of the vertebrae",
                "The absence of cartilage in the spine",
                "Only the contraction and relaxation of muscles",
                "A combination of flexible cartilage and strong bones between the vertebrae",
                "The presence of only hard and dense bones"
            ),
            correctIndex = 3,
            answerLabel = "Answer: D",
            diagramType = "NONE",
            explanation = "কশেরুকাগুলোর মধ্যকার স্থিতিস্থাপক তরুণাস্থি (Cartilage discs) নমনীয়তা দেয় এবং শক্ত কশেরুকা দেহের ওজনকে দৃঢ়ভাবে সাপোর্ট করে।"
        ),
        QuizQuestion(
            id = 8,
            code = "Primary-Q14",
            subject = "Biology",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "একটি ব্যাকটেরিয়া প্রতি ২০ মিনিটে দ্বিগুণ হয়। ১টি ব্যাকটেরিয়া থেকে ১ ঘণ্টায় মোট কতটি ব্যাকটেরিয়া হবে?",
            questionEnglish = "A bacterium doubles every 20 minutes. Starting from 1, how many bacteria are there after 1 hour?",
            question = "A bacterium doubles every 20 minutes. Total after 1 hour starting from 1?",
            questionType = com.example.model.QuestionType.NUMERIC,
            correctNumericAnswer = "8",
            answerLabel = "Answer: 8",
            diagramType = "NONE",
            explanation = "১ ঘণ্টা = ৬০ মিনিট। বিভাজনের সংখ্যা n = 60 / 20 = 3 বার। ব্যাকটেরিয়া সংখ্যা = 1 × 2³ = 8টি।"
        ),
        QuizQuestion(
            id = 9,
            code = "Primary-Q15",
            subject = "Biology",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "একটি পুকুরের মাছের জন্মহার প্রতি বছর ৪০টি এবং মৃত্যুহার প্রতি বছর ১৫টি। প্রতি বছর ১০টি মাছ অভিবাসনের মাধ্যমে আসে এবং ৫টি মাছ স্থানান্তরিত হয়ে চলে যায়। শুরুতে ৫০০টি মাছ থাকলে ১ বছর পর মাছের সংখ্যা কত হবে?",
            questionEnglish = "A pond population of fish has a birth rate of 40 individuals/year and a death rate of 15 individuals/year. Immigration adds 10 individuals/year, and emigration removes 5 individuals/year. If the initial population is 500, find the population size after 1 year.",
            question = "Initial fish 500. Birth 40, Death 15, Immigration 10, Emigration 5. Size after 1 yr?",
            questionType = com.example.model.QuestionType.NUMERIC,
            correctNumericAnswer = "530",
            answerLabel = "Answer: 530",
            diagramType = "NONE",
            explanation = "চূড়ান্ত সংখ্যা = 500 + (40 - 15) + (10 - 5) = 500 + 25 + 5 = 530টি।"
        ),
        QuizQuestion(
            id = 10,
            code = "Primary-Q16",
            subject = "Biology",
            category = OlympiadCategory.PRIMARY,
            questionBangla = "খাদ্যশৃঙ্খলে শক্তি স্থানান্তর: প্রথম স্তর (ঘাস) = ৫০০০ ক্যালরি। ১০% সূত্র অনুযায়ী তৃতীয় স্তর (বাজপাখি) কত ক্যালরি শক্তি পাবে?",
            questionEnglish = "How many calories will the third level in this food chain receive?",
            question = "Food chain: Grass 5000 cal -> Grasshopper -> Eagle. How many calories in 3rd level?",
            questionType = com.example.model.QuestionType.NUMERIC,
            correctNumericAnswer = "50",
            answerLabel = "Answer: 50",
            diagramType = "FOOD_CHAIN",
            explanation = "১ম স্তর (ঘাস) = ৫০০০ ক্যালরি ➔ ২য় স্তর (ফড়িং) পায় ১০% = ৫০০ ক্যালরি ➔ ৩য় স্তর (বাজপাখি) পায় ৫০০ ক্যালরির ১০% = ৫০ ক্যালরি।"
        ),

        // === JUNIOR CATEGORY QUESTIONS (Exact from PDF Page 14) ===
        QuizQuestion(
            id = 11,
            code = "Junior-Q2",
            subject = "Physics",
            category = OlympiadCategory.JUNIOR,
            questionBangla = "নিলুর কাছে একটি দণ্ডচুম্বক ছিল যার বাম প্রান্তে N এবং ডান প্রান্তে S চিহ্নিত। খেলার সময় এটি ঠিক মাঝখান দিয়ে ভেঙে দুটি টুকরো হয়ে গেল (টুকরো-১ ও টুকরো-২)। নিলু ভাঙা মুখ দুটি পরস্পরের কাছে আনলে কী ঘটবে এবং কেন?",
            questionEnglish = "Nilu had a bar magnet marked N on its left end and S on its right end. While he was playing, it snapped into two pieces exactly at the middle — Piece 1 and Piece 2. Nilu brought the two freshly broken faces together to rejoin them. Will the two pieces attract or repel each other, and why?",
            question = "Bar magnet snapped into 2 pieces. Will the two freshly broken faces attract or repel, and why?",
            questionType = com.example.model.QuestionType.MCQ,
            options = listOf(
                "Attract — each piece instantly becomes a complete magnet, so the two fresh faces are opposite poles",
                "Repel — the two freshly broken faces become the same pole",
                "No force at all — the magnetism is destroyed the moment it breaks",
                "Attract — Piece 1 keeps only an N pole and Piece 2 keeps only an S pole",
                "It depends on whether the two pieces are of equal length"
            ),
            correctIndex = 0,
            answerLabel = "Answer: A",
            diagramType = "NONE",
            explanation = "চুম্বকের একক মেরুর অস্তিত্ব নেই। মাঝখানে ভাঙলে টুকরো-১ এর ডানপাশে S এবং টুকরো-২ এর বামপাশে N মেরু সৃষ্টি হয়। বিপরীত মেরু পরস্পরকে আকর্ষণ করে।"
        ),
        QuizQuestion(
            id = 12,
            code = "Junior-Q3",
            subject = "Physics",
            category = OlympiadCategory.JUNIOR,
            questionBangla = "সামির পড়েছে আবদ্ধ তরলের ওপর প্রযুক্ত চাপ সবদিকে সমানভাবে সঞ্চালিত হয়। এটি পরীক্ষা করার জন্য সে পানিভর্তি দুটি সিরিঞ্জ একটি নল দিয়ে যুক্ত করল। সিরিঞ্জ A-এর পিস্টনের ক্ষেত্রফল 1 cm² এবং সিরিঞ্জ B-এর পিস্টনের ক্ষেত্রফল 5 cm²। সামির সিরিঞ্জ A-এর পিস্টনের ওপর 2 kg ভরের একটি বাটখারা রাখল। পুরো ব্যবস্থাটিকে ভারসাম্যে রাখতে সিরিঞ্জ B-এর পিস্টনের ওপর কত কেজি ভরের বাটখারা রাখতে হবে? (g=9.8 m/s²)",
            questionEnglish = "Samir has read that pressure applied to an enclosed liquid is transmitted equally in all directions. To test it he connects two water-filled syringes with a tube. The piston of syringe A has an area of 1 cm² and the piston of syringe B has an area of 5 cm². Samir places a 2 kg weight on the piston of syringe A. What mass, in kilograms, must be placed on the piston of syringe B to hold the whole system in equilibrium? (g = 9.8 m/s²)",
            question = "Syringe A (1 cm², 2 kg) and Syringe B (5 cm²). Mass needed on B for equilibrium?",
            questionType = com.example.model.QuestionType.NUMERIC,
            correctNumericAnswer = "10",
            answerLabel = "Answer: 10",
            diagramType = "HYDRAULIC_SYRINGE",
            explanation = "প্যাসকেলের সূত্রানুসারে: P1 = P2 ➔ F1/A1 = F2/A2 ➔ (m1·g)/A1 = (m2·g)/A2 ➔ 2 kg / 1 cm² = m2 / 5 cm² ➔ m2 = 2 × 5 = 10 kg।"
        ),
        QuizQuestion(
            id = 13,
            code = "Junior-Q4",
            subject = "Physics",
            category = OlympiadCategory.JUNIOR,
            questionBangla = "শাওনদের বাসায় 220 V মেইনে একই সাথে চলে একটি 880 W হিটার, একটি 440 W ফ্যান এবং একটি 22 W বাতি। দোকানে ফিউজ পাওয়া যায় 5 A, 10 A, 15 A ও 20 A। শাওনের কোন ফিউজটি কেনা উচিত যা স্বাভাবিক ব্যবহারে পুড়বে না, অথচ অপ্রয়োজনীয়ভাবে বেশি ক্ষমতার নয়?",
            questionEnglish = "In Shawon's house an 880 W heater, a 440 W fan and a 22 W lamp all run together on the 220 V mains. The shop stocks 5 A, 10 A, 15 A and 20 A fuses. Which one should Shawon buy — the one that will not blow under normal use, yet is not needlessly oversized?",
            question = "Heater 880W, fan 440W, lamp 22W on 220V. Which fuse to buy (5A, 10A, 15A, 20A)?",
            questionType = com.example.model.QuestionType.MCQ,
            options = listOf("20 A Fuse", "5 A Fuse", "10 A Fuse", "15 A Fuse", "None of these is suitable"),
            correctIndex = 2,
            answerLabel = "Answer: C (10 A Fuse)",
            diagramType = "NONE",
            explanation = "মোট ক্ষমতা P = 880 + 440 + 22 = 1342 W।\nপ্রয়োজনীয় তড়িৎপ্রবাহ I = P / V = 1342 W / 220 V = 6.1 A।\n৫ অ্যাম্পিয়ারের ফিউজ পুড়ে যাবে। অতএব ৬.১ অ্যাম্পিয়ারের জন্য সবচেয়ে নিরাপদ ও যথাযথ রেটিং হলো ১০ অ্যাম্পিয়ার (10 A)।"
        ),
        QuizQuestion(
            id = 14,
            code = "Junior-Q5",
            subject = "Physics",
            category = OlympiadCategory.JUNIOR,
            questionBangla = "রাফি ইন্টারন্যাশনাল ফিজিক্স অলিম্পিয়াডের প্রস্তুতি নিচ্ছে। ল্যাবে সে একটি ধ্রুব বলক্ষেত্র পায় যেখানে x-অক্ষের সমান্তরালে বল F = 10 N ক্রিয়া করে। সে একটি কণাকে A বিন্দু থেকে B বিন্দুতে তিনটি ভিন্ন পথে নিয়ে যায় (Path 1, Path 2 ও Path 3)। যদি পথ তিনটিতে কৃতকাজ যথাক্রমে W1, W2 ও W3 হয়, তবে W1 + W2 + W3 এর মান কত জুল?",
            questionEnglish = "Rafi is preparing for the International Physics Olympiad. In the lab he finds a force field in which a constant force F = 10 N acts parallel to the x-axis. He moves a particle from point A to point B along three different routes — Path 1, Path 2 and Path 3 (see figure). If the work done by the force along the three routes is W1, W2 and W3 respectively, what is W1 + W2 + W3, in joules?",
            question = "Constant force F=10 N along x-axis from A(0,0) to B(6,4) along 3 paths. W1+W2+W3 in Joules?",
            questionType = com.example.model.QuestionType.NUMERIC,
            correctNumericAnswer = "180",
            answerLabel = "Answer: 180",
            diagramType = "FORCE_COORDINATE_GRAPH",
            explanation = "বলটি x-অক্ষের সমান্তরাল ধ্রুবক হওয়ায় কাজ শুধুমাত্র x-অক্ষ বরাবর সরণের ওপর নির্ভর করে: Δx = 6 - 0 = 6 m।\nযেকোনো পথেই কৃতকাজ W = F × Δx = 10 N × 6 m = 60 J।\nসুতরাং W1 = W2 = W3 = 60 J।\nঅতএব W1 + W2 + W3 = 60 + 60 + 60 = 180 J।"
        ),
        QuizQuestion(
            id = 15,
            code = "Junior-Q6",
            subject = "Physics",
            category = OlympiadCategory.JUNIOR,
            questionBangla = "ইরাম একজন অ্যাস্ট্রোবায়োলজিস্ট। UTV-259 গ্যালাক্সিতে সে একটি টোরোয়েড-আকৃতির (ডোনাট আকারের) গ্রহ খুঁজে পেল যার ভর 8×10²⁴ kg। ইরাম তার 2000 kg ভরের মহাকাশযান নিয়ে গ্রহটির ঠিক জ্যামিতিক কেন্দ্রে প্রবেশ করল। গ্রহটির মহাকর্ষের কারণে ওই কেন্দ্রবিন্দুতে ইরামের ত্বরণ কত m/s² হবে?",
            questionEnglish = "Iram is an astrobiologist. In galaxy UTV-259 she finds a toroidal (doughnut-shaped) planet of mass 8×10²⁴ kg. Iram (mass 80 kg) travels with his 2000 kg spacecraft towards the exact geometric centre of the planet (see figure). What is the gravitational acceleration of Iram and her craft, in m/s², at that exact centre?",
            question = "Toroidal (donut) planet of mass 8x10^24 kg. Gravitational acceleration at geometric center?",
            questionType = com.example.model.QuestionType.NUMERIC,
            correctNumericAnswer = "0",
            answerLabel = "Answer: 0",
            diagramType = "TOROIDAL_PLANET",
            explanation = "টোরোয়েডের সুষম বৃত্তাকার প্রতিসাম্যের কারণে কেন্দ্রের চারপাশে বিপরীতমুখী ভর উপাদানগুলোর মহাকর্ষীয় টান পরস্পরকে সম্পূর্ণ নিষ্ক্রিয় করে দেয়। সুতরাং কেন্দ্রে লব্ধি বল ও ত্বরণ = 0 m/s²।"
        )
    )

    val pastPapers = listOf(
        PastPaper("pp_2025_nat", 2025, "National Round", OlympiadCategory.JUNIOR, 100, 90, 25),
        PastPaper("pp_2025_reg", 2025, "Regional Round", OlympiadCategory.JUNIOR, 60, 60, 20),
        PastPaper("pp_2025_pri", 2025, "National Round", OlympiadCategory.PRIMARY, 50, 45, 15),
        PastPaper("pp_2025_sec", 2025, "National Round", OlympiadCategory.SECONDARY, 120, 120, 30),
        PastPaper("pp_2024_nat", 2024, "National Round", OlympiadCategory.JUNIOR, 100, 90, 25),
        PastPaper("pp_2024_camp", 2024, "IJSO Team Selection Camp", OlympiadCategory.SECONDARY, 150, 180, 35),
        PastPaper("pp_2023_nat", 2023, "National Round", OlympiadCategory.JUNIOR, 100, 90, 25),
        PastPaper("pp_2022_reg", 2022, "Regional Round", OlympiadCategory.PRIMARY, 50, 45, 15)
    )

    val scienceTopics = listOf(
        ScienceTopic(
            id = "top_mechanics",
            subject = "Physics",
            title = "Classical Mechanics & Kinematics",
            category = OlympiadCategory.JUNIOR,
            summary = "Fundamental laws governing objects in motion, forces, acceleration, and conservation of momentum.",
            formulas = listOf(
                "v = u + at",
                "s = ut + ½at²",
                "v² = u² + 2as",
                "F = ma",
                "Work = F × d × cos(θ)",
                "Kinetic Energy = ½mv²"
            ),
            tips = listOf(
                "Always check units: convert km/h to m/s by multiplying with 5/18.",
                "Draw free body diagrams (FBD) for equilibrium and friction problems.",
                "Remember normal force is perpendicular to the contacting surface."
            )
        ),
        ScienceTopic(
            id = "top_optics",
            subject = "Physics",
            title = "Geometrical Optics & Light",
            category = OlympiadCategory.JUNIOR,
            summary = "Reflection, refraction, Snell's law, spherical mirrors, and thin lenses.",
            formulas = listOf(
                "1/f = 1/v + 1/u (Mirror Formula)",
                "1/f = 1/v - 1/u (Lens Formula)",
                "n₁ sin(θ₁) = n₂ sin(θ₂) (Snell's Law)",
                "Power of lens P = 1/f (in meters)"
            ),
            tips = listOf(
                "Use Cartesian sign convention consistently.",
                "Focal length of concave mirror is negative; convex lens is positive."
            )
        ),
        ScienceTopic(
            id = "top_stoichiometry",
            subject = "Chemistry",
            title = "Mole Concept & Stoichiometry",
            category = OlympiadCategory.SECONDARY,
            summary = "Quantitative relationships between chemical reactants and products in balanced reactions.",
            formulas = listOf(
                "Moles n = Mass / Molar Mass (m / M)",
                "n = Number of particles / 6.022 × 10²³",
                "Molarity M = Moles of solute / Volume of solution in Liters",
                "Ideal Gas: PV = nRT"
            ),
            tips = listOf(
                "Always balance the chemical equation before applying mole ratios.",
                "Identify the limiting reactant by comparing mole to coefficient ratios."
            )
        ),
        ScienceTopic(
            id = "top_cell_bio",
            subject = "Biology",
            title = "Cellular Biology & Genetics",
            category = OlympiadCategory.JUNIOR,
            summary = "Structure of plant and animal cells, organelle functions, mitosis, meiosis, and Mendelian inheritance.",
            formulas = listOf(
                "Monohybrid Phenotypic Ratio: 3 : 1",
                "Monohybrid Genotypic Ratio: 1 : 2 : 1",
                "Dihybrid Phenotypic Ratio: 9 : 3 : 3 : 1"
            ),
            tips = listOf(
                "Distinguish between prokaryotic (no nucleus) and eukaryotic cells.",
                "Mitosis produces 2 identical diploid cells; Meiosis produces 4 diverse haploid gametes."
            )
        )
    )

    val sampleResults = listOf(
        StudentResult("BDJSO-2026-1042", "Tahmid Rahman", OlympiadCategory.JUNIOR, "Dhaka", "Notre Dame School & College", 94.5, 1, "Gold Medal", true),
        StudentResult("BDJSO-2026-1188", "Nafisa Anjum", OlympiadCategory.JUNIOR, "Chattogram", "Chattogram Collegiate School", 91.0, 2, "Gold Medal", true),
        StudentResult("BDJSO-2026-0921", "Tanvir Hasan", OlympiadCategory.SECONDARY, "Rajshahi", "Rajshahi Collegiate School", 93.0, 1, "Gold Medal", true),
        StudentResult("BDJSO-2026-2104", "Sumaiya Akter", OlympiadCategory.PRIMARY, "Sylhet", "Sylhet Govt. Pilot High School", 88.5, 3, "Silver Medal", true),
        StudentResult("BDJSO-2026-1567", "Abrar Jawad", OlympiadCategory.JUNIOR, "Khulna", "Khulna Zilla School", 84.0, 8, "Silver Medal", true),
        StudentResult("BDJSO-2026-3045", "Farhan Kabir", OlympiadCategory.SECONDARY, "Barishal", "Barishal Zilla School", 79.5, 14, "Bronze Medal", false)
    )
}
