package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.entity.Module;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public void run(String... args) throws Exception {
        if (lessonRepository.count() <= 5) {
            System.out.println("Seeding database with massive educational curriculum (Real Data)...");
            seedLessons();
        }
        if (projectRepository.count() == 0) {
            System.out.println("Seeding database with projects...");
            seedProjects();
        }
        System.out.println("Database seeding completed.");
    }

    private void seedLessons() {
        createLessonTrack("Zero Waste Lifestyle", "Resource Management", "Learn to minimize your ecological footprint by reducing, reusing, and recycling in your daily life.",
                "Zero Waste Basics", "Composting Mastery");
        createLessonTrack("Sustainable Fashion", "Lifestyle", "Explore the impact of fast fashion and learn how to build an eco-friendly, ethical wardrobe.",
                "The Fast Fashion Crisis", "Ethical Wardrobe Building");
        createLessonTrack("Eco-Friendly Architecture", "Design", "Understand the principles of sustainable building design, green materials, and energy efficiency.",
                "Green Building Materials", "Passive Solar Design");
        createLessonTrack("Ocean Conservation", "Ecosystems", "Dive into marine biology and understand how to protect our oceans from pollution and overfishing.",
                "Coral Reef Protection", "Plastic Pollution Prevention");
        createLessonTrack("Wildlife Protection", "Ecosystems", "Learn about endangered species, habitat restoration, and global conservation efforts.",
                "Endangered Species 101", "Habitat Restoration");
    }

    private void createLessonTrack(String lessonTitle, String category, String description, String module1Title, String module2Title) {
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonTitle);
        lesson.setCategory(category);
        lesson.setDescription(description);
        lesson = lessonRepository.save(lesson);

        // Create Module 1
        Module m1 = new Module();
        m1.setLesson(lesson);
        m1.setTitle(module1Title);
        m1.setOrderIndex(1);
        m1 = moduleRepository.save(m1);

        Page m1p1 = new Page();
        m1p1.setModule(m1);
        m1p1.setPageNumber(1);
        m1p1.setContent("Welcome to " + module1Title + ". This module will cover the foundational concepts of " + lessonTitle + ".\n\nThe goal is to understand the global impact of our choices and how small changes can lead to massive environmental benefits.");
        pageRepository.save(m1p1);

        Page m1p2 = new Page();
        m1p2.setModule(m1);
        m1p2.setPageNumber(2);
        m1p2.setContent("In practice, addressing the issues discussed in " + module1Title + " requires a combination of individual responsibility and systemic change.\n\nTake notes as you prepare for the comprehensive 10-question quiz at the end of this module.");
        pageRepository.save(m1p2);

        Quiz q1 = new Quiz();
        q1.setModule(m1);
        q1.setQuestionsJson(getRealQuestions(module1Title));
        quizRepository.save(q1);

        // Create Module 2
        Module m2 = new Module();
        m2.setLesson(lesson);
        m2.setTitle(module2Title);
        m2.setOrderIndex(2);
        m2 = moduleRepository.save(m2);

        Page m2p1 = new Page();
        m2p1.setModule(m2);
        m2p1.setPageNumber(1);
        m2p1.setContent("Welcome to " + module2Title + ". Now that you understand the basics, we will dive deeper into advanced applications of " + lessonTitle + ".");
        pageRepository.save(m2p1);

        Quiz q2 = new Quiz();
        q2.setModule(m2);
        q2.setQuestionsJson(getRealQuestions(module2Title));
        quizRepository.save(q2);
    }

    private String getRealQuestions(String topic) {
        if (topic.equals("Zero Waste Basics")) {
            return "[\n" +
                "  {\"question\": \"What is the primary goal of a zero-waste lifestyle?\", \"options\": [\"To send nothing to the landfill\", \"To burn all waste\", \"To double recycling limits\", \"To only buy plastic\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"Which of the 5 R's comes first?\", \"options\": [\"Recycle\", \"Refuse\", \"Rot\", \"Reuse\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is a common zero-waste alternative to plastic wrap?\", \"options\": [\"Aluminum foil\", \"Beeswax wrap\", \"Styrofoam\", \"Paper bags\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"How long does a plastic bottle take to decompose?\", \"options\": [\"10 years\", \"50 years\", \"450 years\", \"1000+ years\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"What does 'upcycling' mean?\", \"options\": [\"Recycling at a higher altitude\", \"Reusing an object in a way that creates a higher value product\", \"Cycling to work\", \"Burning waste\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Which item is generally NOT recyclable?\", \"options\": [\"Glass bottles\", \"Cardboard\", \"Styrofoam containers\", \"Aluminum cans\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"What is a 'circular economy'?\", \"options\": [\"An economy based on round coins\", \"An economic system aimed at eliminating waste and the continual use of resources\", \"A fast fashion model\", \"Trading goods in circles\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is the main problem with single-use plastics?\", \"options\": [\"They are too expensive\", \"They rarely get recycled and cause massive environmental pollution\", \"They are heavy\", \"They are hard to find\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Which of these is a reusable alternative to paper towels?\", \"options\": [\"Swedish dishcloths\", \"Plastic wrap\", \"Tissues\", \"More paper towels\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"What is 'greenwashing'?\", \"options\": [\"Washing clothes in cold water\", \"When a company claims to be eco-friendly but actually isn't\", \"A type of organic soap\", \"Painting a house green\"], \"answerIndex\": 1}\n" +
                "]";
        } else if (topic.equals("Composting Mastery")) {
            return "[\n" +
                "  {\"question\": \"What are the two main types of materials needed for composting?\", \"options\": [\"Reds and Blues\", \"Greens and Browns\", \"Lights and Darks\", \"Wets and Drys\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Which of these is considered a 'Green' compost material?\", \"options\": [\"Dried leaves\", \"Cardboard\", \"Coffee grounds\", \"Twigs\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"Which of these should NEVER go in a home compost bin?\", \"options\": [\"Vegetable peels\", \"Meat and dairy\", \"Eggshells\", \"Grass clippings\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What role do worms play in vermicomposting?\", \"options\": [\"They eat the bin\", \"They aerate the soil and break down food scraps quickly\", \"They make it smell better\", \"They slow down the process\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Why does compost need to be turned or aerated?\", \"options\": [\"To make it look nice\", \"To provide oxygen to the decomposing microbes\", \"To dry it out completely\", \"To stop worms from escaping\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is 'compost tea'?\", \"options\": [\"A drink made from soil\", \"A liquid fertilizer made by steeping compost in water\", \"A brand of tea\", \"Tea leaves thrown in the compost\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"How can you tell when compost is ready to use?\", \"options\": [\"It is hot and smells bad\", \"It is dark, crumbly, and smells like earthy soil\", \"It is mostly recognizable food scraps\", \"It is bright green\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What happens if your compost is too wet?\", \"options\": [\"It turns to gold\", \"It becomes anaerobic and starts to smell like rotten eggs\", \"It catches fire\", \"It completes faster\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is the ideal carbon-to-nitrogen ratio for compost?\", \"options\": [\"1:1\", \"30:1\", \"100:1\", \"5:1\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What does compost do for garden soil?\", \"options\": [\"Kills plants\", \"Makes it highly acidic\", \"Improves soil structure and adds vital nutrients\", \"Turns it into clay\"], \"answerIndex\": 2}\n" +
                "]";
        } else if (topic.equals("The Fast Fashion Crisis")) {
             return "[\n" +
                "  {\"question\": \"What is 'fast fashion'?\", \"options\": [\"Running shoes\", \"Inexpensive clothing produced rapidly by mass-market retailers in response to the latest trends\", \"Clothes that dry fast\", \"High-end designer wear\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"How much water does it take to make one cotton t-shirt?\", \"options\": [\"10 liters\", \"100 liters\", \"2,700 liters\", \"10,000 liters\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"What percentage of clothing ends up in landfills or incinerators?\", \"options\": [\"10%\", \"25%\", \"50%\", \"85%\"], \"answerIndex\": 3},\n" +
                "  {\"question\": \"What are microplastics in fashion?\", \"options\": [\"Small plastic buttons\", \"Tiny synthetic fibers released into oceans when washing clothes like polyester\", \"Small tags\", \"Tiny shoes\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Which material is entirely synthetic and derived from petroleum?\", \"options\": [\"Cotton\", \"Polyester\", \"Linen\", \"Hemp\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is a major social issue associated with fast fashion?\", \"options\": [\"High wages\", \"Safe working conditions\", \"Sweatshops and unethical child labor\", \"Too many holidays\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"How many collections do fast fashion brands typically release per year?\", \"options\": [\"2\", \"4\", \"12\", \"52+\"], \"answerIndex\": 3},\n" +
                "  {\"question\": \"What is the most environmentally damaging phase of a garment's life cycle?\", \"options\": [\"Dyeing and finishing\", \"Cutting\", \"Tagging\", \"Folding\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"Which country is currently the world's largest exporter of clothing?\", \"options\": [\"USA\", \"Italy\", \"China\", \"Brazil\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"What is 'planned obsolescence'?\", \"options\": [\"Clothes designed to last forever\", \"Designing garments to fall apart or go out of style quickly so consumers buy more\", \"A fashion trend from the 90s\", \"A type of fabric\"], \"answerIndex\": 1}\n" +
                "]";
        } else if (topic.equals("Ethical Wardrobe Building")) {
             return "[\n" +
                "  {\"question\": \"What is a 'capsule wardrobe'?\", \"options\": [\"A closet shaped like a capsule\", \"A small, curated collection of versatile, timeless clothing items\", \"Clothes worn by astronauts\", \"A massive collection of trendy items\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Which fabric is generally considered the most sustainable?\", \"options\": [\"Polyester\", \"Nylon\", \"Organic Linen\", \"Acrylic\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"What does 'GOTS' stand for in textile certification?\", \"options\": [\"Global Organic Textile Standard\", \"Great Old T-Shirts\", \"Green Output Test System\", \"Global Organization of Tailors\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"How can you extend the life of your clothes?\", \"options\": [\"Wash them in hot water daily\", \"Tumble dry on high heat\", \"Wash less frequently in cold water and air dry\", \"Iron them constantly\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"What is the most eco-friendly way to get rid of clothes you don't wear?\", \"options\": [\"Throw them in the trash\", \"Burn them\", \"Host a clothing swap or donate them to a local shelter\", \"Bury them\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"What is 'thrifting'?\", \"options\": [\"Making your own clothes\", \"Buying second-hand clothes at vintage or charity shops\", \"Stealing clothes\", \"Buying brand new expensive clothes\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What does 'Cost Per Wear' mean?\", \"options\": [\"The price of a garment divided by the number of times it's worn\", \"The cost to wash it\", \"The cost to rent it\", \"The shipping fee\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"Which dye type is safest for the environment?\", \"options\": [\"Azo dyes\", \"Natural plant-based dyes\", \"Heavy metal dyes\", \"Synthetic reactive dyes\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is 'slow fashion'?\", \"options\": [\"Clothes that are hard to put on\", \"A movement advocating for quality, longevity, and fair wages in garment production\", \"Shipping that takes months\", \"A type of thick wool\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is 'deadstock' fabric?\", \"options\": [\"Fabric from extinct animals\", \"Leftover fabric from textile mills that would otherwise be thrown away\", \"Fabric that is physically heavy\", \"A brand of jeans\"], \"answerIndex\": 1}\n" +
                "]";
        } else if (topic.equals("Green Building Materials")) {
            return "[\n" +
                "  {\"question\": \"Which of the following is considered a rapidly renewable building material?\", \"options\": [\"Concrete\", \"Steel\", \"Bamboo\", \"Brick\"], \"answerIndex\": 2},\n" +
                "  {\"question\": \"What does 'VOC' stand for in paints and finishes?\", \"options\": [\"Very Old Colors\", \"Volatile Organic Compounds\", \"Visual Optical Clarity\", \"Variable Opaque Coating\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Why is Hempcrete an eco-friendly alternative to concrete?\", \"options\": [\"It is completely invisible\", \"It absorbs carbon dioxide as it cures and provides excellent insulation\", \"It is made of plastic\", \"It conducts electricity\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is Rammed Earth construction?\", \"options\": [\"Building a house underground\", \"Compressing damp earth mixtures into solid load-bearing walls\", \"A type of carpet\", \"Throwing mud at a frame\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Which insulation material is both natural and highly effective?\", \"options\": [\"Fiberglass\", \"Sheep's wool\", \"Polystyrene foam\", \"Asbestos\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is the environmental benefit of using reclaimed wood?\", \"options\": [\"It smells better\", \"It requires cutting down zero new trees and diverts waste from landfills\", \"It is always cheaper\", \"It comes painted\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is cork harvested from?\", \"options\": [\"The bark of cork oak trees, without killing the tree\", \"Plastic factories\", \"Underground mines\", \"The core of pine trees\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"What is 'embodied energy' in a building material?\", \"options\": [\"The electricity it generates\", \"The total energy required to extract, manufacture, and transport the material\", \"Its thermal mass\", \"How much light it reflects\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Why are traditional Portland cement and concrete harmful to the environment?\", \"options\": [\"They are too brittle\", \"Their production accounts for roughly 8% of global CO2 emissions\", \"They melt in rain\", \"They are radioactive\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Which green material is made from compressed straw?\", \"options\": [\"Straw-bale\", \"Brick\", \"Glass\", \"Steel\"], \"answerIndex\": 0}\n" +
                "]";
        } else {
            // Generic fallback but with realistic placeholders
            return "[\n" +
                "  {\"question\": \"What is the primary objective of " + topic + "?\", \"options\": [\"Conservation and sustainability\", \"Profit maximization\", \"Resource depletion\", \"Ignoring the environment\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"Which action best supports the goals of " + topic + "?\", \"options\": [\"Increasing waste\", \"Educating others and taking sustainable actions\", \"Using more single-use plastics\", \"Driving more cars\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is a major challenge in implementing " + topic + "?\", \"options\": [\"Lack of public awareness and funding\", \"Too many trees\", \"Too much clean water\", \"Birds\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"Who plays a vital role in advancing " + topic + "?\", \"options\": [\"Only politicians\", \"Individuals, communities, and policymakers together\", \"Nobody\", \"Only corporations\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is a common metric to measure success in " + topic + "?\", \"options\": [\"Carbon footprint reduction\", \"Number of plastic bags used\", \"Amount of oil spilled\", \"Deforestation rate\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"Why is " + topic + " important for future generations?\", \"options\": [\"It ensures a healthy, habitable planet\", \"It makes things more expensive\", \"It isn't important\", \"It depletes resources faster\"], \"answerIndex\": 0},\n" +
                "  {\"question\": \"What technology aids in " + topic + "?\", \"options\": [\"Coal power plants\", \"Renewable energy and smart sensors\", \"Gas guzzling engines\", \"Single-use manufacturing\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"Which policy strongly aligns with " + topic + "?\", \"options\": [\"Subsidizing fossil fuels\", \"Implementing a carbon tax or emissions cap\", \"Banning solar panels\", \"Encouraging clear-cutting\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What everyday habit supports " + topic + "?\", \"options\": [\"Leaving lights on\", \"Reducing meat consumption and conserving energy\", \"Throwing batteries in the trash\", \"Buying fast fashion\"], \"answerIndex\": 1},\n" +
                "  {\"question\": \"What is the ultimate vision of " + topic + "?\", \"options\": [\"A world reliant on fossil fuels\", \"A balanced, regenerative ecosystem where humans and nature thrive\", \"A paved planet\", \"Complete urbanization\"], \"answerIndex\": 1}\n" +
                "]";
        }
    }

    private void seedProjects() {
        Project p1 = new Project();
        p1.setTitle("Build a DIY Rainwater Catchment System");
        p1.setDescription("Collect rainwater from your roof using a downspout diverter and a rain barrel.");
        p1.setDifficulty("Medium");
        projectRepository.save(p1);

        Project p2 = new Project();
        p2.setTitle("Start a Kitchen Compost Bin");
        p2.setDescription("Reduce methane emissions from landfills by composting your organic food waste at home.");
        p2.setDifficulty("Easy");
        projectRepository.save(p2);

        Project p3 = new Project();
        p3.setTitle("Conduct a Home Energy Audit");
        p3.setDescription("A home energy audit helps you pinpoint exactly where your house is losing energy.");
        p3.setDifficulty("Medium");
        projectRepository.save(p3);
    }
}
