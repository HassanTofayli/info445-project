package com.example.info445project;

import java.util.Arrays;

public class App_Database {


    public static Course[] courses = {
            new Course("Mathematics", "Dive Integer[o the world of numbers and explore the beauty of algebra, geometry, calculus, and more. Whether you're a beginner or looking to enhance your mathematical skills, this course offers a comprehensive journey through mathematical theories and applications.", "images/courses/1.jpg", 1),
            new Course("Physics", "Unlock the mysteries of the universe with our Physics course. From classical mechanics to quantum physics, learn how the fundamental laws of nature govern the world around us. Ideal for students passionate about understanding the physical principles that underlie our daily existence.", "images/courses/2.jpg", 2),
            new Course("Chemistry", "Explore the elemental composition of the world in this engaging Chemistry course. Discover the reactions, bonds, and structures that define matter. This course is perfect for students Integer[erested in the sciences behind materials, medicines, and environmental change.", "images/courses/3.jpg", 3),
            new Course("Biology", "Embark on a journey through life itself with our Biology course. Study the Integer[ricate systems and organisms that make life on Earth possible. From cellular biology to ecology, this course covers a wide range of topics for budding biologists.", "images/courses/4.jpg", 4),
            new Course("English Literature", "Immerse yourself in the rich and diverse world of English Literature. Analyze and appreciate works from a variety of periods, genres, and authors. This course is designed for students who love reading and discussing literary masterpieces.", "images/courses/5.jpg", 5),
            new Course("Computer Science", "Enter the fast-evolving field of Computer Science. Learn about algorithms, data structures, software development, and more. This course is tailored for individuals aiming to pursue a career in technology and innovation.", "images/courses/6.jpg", 6),
            new Course("History", "Travel back in time with our History course. Discover the events, cultures, and people that have shaped the world. This course provides a comprehensive overview of world history, encouraging students to think critically about the past.", "images/courses/7.jpg", 7),
            new Course("Art and Design", "Unleash your creativity with our Art and Design course. Explore various artistic mediums, techniques, and historical contexts. Perfect for students looking to express themselves and explore their artistic potential.", "images/courses/8.jpg", 8),
            new Course("Psychology", "Delve Integer[o the human mind with our Psychology course. Understand behavior, cognition, and emotions through various psychological theories and applications. Ideal for students fascinated by the workings of the mind.", "images/courses/9.jpg", 9),
            new Course("Economics", "Navigate the complexities of economies with our Economics course. Analyze markets, policies, and consumer behavior. This course is suited for students Integer[erested in the forces that drive economic systems.", "images/courses/10.jpg", 10),
            new Course("Philosophy", "Embark on a quest for wisdom and explore the fundamental questions of existence, knowledge, and ethics in our Philosophy course. Delve Integer[o the minds of history's greatest thinkers and apply philosophical concepts to modern-day issues. Ideal for students seeking to understand the deeper meanings of life.", "images/courses/11.jpg", 11),
            new Course("Environmental Science", "Dive Integer[o the study of the environment and our impact on the Earth with our Environmental Science course. Explore ecosystems, biodiversity, and sustainability practices. This course is perfect for students passionate about conservation and environmental management.", "images/courses/12.jpg", 12),
            new Course("Sociology", "Examine the structure of societies and the complex relationships within them in our Sociology course. Understand social behavior, institutions, and cultural norms. Suitable for students Integer[erested in the dynamics of social Integer[eractions and societal challenges.", "images/courses/13.jpg", 13),
            new Course("Music Theory", "Explore the language of music through our Music Theory course. Learn about melody, harmony, rhythm, and musical forms. This course is designed for both beginners and seasoned musicians wanting to deepen their understanding of music composition and performance.", "images/courses/14.jpg", 14),
            new Course("Political Science", "Navigate the Integer[ricacies of political systems, governance, and policy-making in our Political Science course. Analyze political behavior, electoral systems, and public administration. Ideal for students aiming to pursue careers in politics, law, or public service.", "images/courses/15.jpg", 15),
            new Course("Creative Writing", "Unleash your imagination and hone your writing skills with our Creative Writing course. Explore various genres, develop your narrative techniques, and create compelling stories. Perfect for aspiring writers seeking to express their creativity.", "images/courses/16.jpg", 16),
            new Course("Astronomy", "Journey through the cosmos in our Astronomy course. Study celestial objects, phenomena, and the universe's origins. This course is ideal for students fascinated by the mysteries of space and the science behind astronomical discoveries.", "images/courses/17.jpg", 17),
            new Course("Sports Science", "Delve Integer[o the scientific principles underlying sports performance and physical activity in our Sports Science course. Explore anatomy, physiology, biomechanics, and nutrition in sports. Suitable for students Integer[erested in a career in sports management, coaching, or fitness training.", "images/courses/18.jpg", 18),
            new Course("Digital Marketing", "Navigate the digital landscape with our Digital Marketing course. Learn about SEO, social media marketing, content strategy, and analytics. This course is tailored for students and professionals looking to enhance their marketing skills in the digital era.", "images/courses/19.jpg", 19),
            new Course("Cybersecurity", "Protect data and networks from digital threats in our Cybersecurity course. Understand the principles of information security, ethical hacking, and cyber defense strategies. Ideal for students pursuing a career in IT security or network administration.", "images/courses/20.jpg", 20)
    };
    public static Student[] students = {
            new Student("Joe", "Joe123", Arrays.asList()),
            new Student("Sara", "SaraPass", Arrays.asList(1, 5)),
            new Student("Liam", "Liam2023", Arrays.asList(2, 4, 7)),
            new Student("Mia", "MiaPwd", Arrays.asList()),
            new Student("Alex", "Alex1234", Arrays.asList(3, 6, 9, 12)),
            new Student("Emma", "EmmaSecure", Arrays.asList(8, 10)),
            new Student("Noah", "NoahPass123", Arrays.asList(11, 14, 17)),
            new Student("Ava", "Ava2023", Arrays.asList()),
            new Student("Oliver", "OllieBean", Arrays.asList(3, 7, 10, 13)),
            new Student("Sophia", "SophSoph", Arrays.asList(5, 20)),
            new Student("Ethan", "EthanRocks", Arrays.asList()),
            new Student("Isabella", "Bella123", Arrays.asList(2, 4, 6, 8)),
            new Student("Lucas", "Lucas2023", Arrays.asList(9, 12, 15)),
            new Student("Mason", "MasonJar", Arrays.asList()),
            new Student("Charlotte", "Charl2023", Arrays.asList(3, 16, 19)),
            new Student("Logan", "LoganLog", Arrays.asList(5, 7)),
            new Student("Amelia", "AmeliaAir", Arrays.asList()),
            new Student("James", "James007", Arrays.asList(2, 4, 18)),
            new Student("Harper", "HarperP", Arrays.asList(10, 13, 15)),
            new Student("Benjamin", "Benjie123", Arrays.asList()),
            new Student("Evelyn", "Eve2023", Arrays.asList(6, 8, 14)),
            new Student("Alexander", "AlexGreat", Arrays.asList(12, 17)),
            new Student("Abigail", "AbbyRoad", Arrays.asList()),
            new Student("Sebastian", "Seb2023", Arrays.asList(1, 9)),
            new Student("Elizabeth", "ElizaB", Arrays.asList(3, 5, 7, 20)),
            new Student("Jack", "JackSparrow", Arrays.asList()),
            new Student("Emily", "EmilyEm", Arrays.asList(11, 13, 16)),
            new Student("Henry", "HenRy2023", Arrays.asList(2, 4, 19)),
            new Student("Avery", "AveMaria", Arrays.asList()),
            new Student("Samuel", "SammyBoy", Arrays.asList(6, 12)),
            new Student("Sofia", "Sofia2023", Arrays.asList(8, 10, 14)),
            new Student("Joseph", "JoeCool", Arrays.asList()),
            new Student("Brooklyn", "Brook2023", Arrays.asList(15, 17, 18)),
            new Student("Gabriel", "GabeLogan", Arrays.asList(1, 3, 5)),
            new Student("Chloe", "ChloeStar", Arrays.asList(7, 11, 13)),
            new Student("Michael", "Mike2023", Arrays.asList()),
            new Student("Grace", "Graceful", Arrays.asList(2, 4, 6, 8, 10)),
            new Student("Daniel", "DanTheMan", Arrays.asList(12, 14, 16, 18)),
            new Student("Hannah", "HannahBanana", Arrays.asList(19, 20)),
            new Student("Matthew", "MattMatt", Arrays.asList()),
    };

    public static Teacher[] teachers = {
            new Teacher("Mr. Smith", "smith01", Arrays.asList(1, 2)),
            new Teacher("Mrs. Johnson", "johnson02", Arrays.asList(3, 4, 5)),
            new Teacher("Ms. Williams", "williams03", Arrays.asList(6)),
            new Teacher("Mr. Brown", "brown04", Arrays.asList()),
            new Teacher("Dr. Jones", "jones05", Arrays.asList(7, 8, 9)),
            new Teacher("Prof. Garcia", "garcia06", Arrays.asList(10, 11)),
            new Teacher("Mrs. Miller", "miller07", Arrays.asList(12, 13, 14)),
            new Teacher("Mr. Davis", "davis08", Arrays.asList(15)),
            new Teacher("Ms. Rodriguez", "rodriguez09", Arrays.asList(16, 17)),
            new Teacher("Dr. Wilson", "wilson10", Arrays.asList(18, 19, 20))
    };



}
