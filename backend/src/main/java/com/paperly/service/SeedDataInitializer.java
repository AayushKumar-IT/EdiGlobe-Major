package com.paperly.service;

import java.util.List;
import javax.sql.DataSource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import com.paperly.config.AppProperties;
import com.paperly.entity.DifficultyLevel;
import com.paperly.entity.Question;
import com.paperly.entity.Subject;
import com.paperly.entity.User;
import com.paperly.entity.UserRole;
import com.paperly.repository.QuestionRepository;
import com.paperly.repository.SubjectRepository;
import com.paperly.repository.UserRepository;

@Component
public class SeedDataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final DataSource dataSource;

    public SeedDataInitializer(UserRepository userRepository,
                              SubjectRepository subjectRepository,
                              QuestionRepository questionRepository,
                              PasswordEncoder passwordEncoder,
                              AppProperties appProperties,
                              DataSource dataSource) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User(appProperties.getSeedAdmin().getUsername(), passwordEncoder.encode(appProperties.getSeedAdmin().getPassword()), UserRole.ADMIN));
            userRepository.save(new User(appProperties.getSeedFaculty().getUsername(), passwordEncoder.encode(appProperties.getSeedFaculty().getPassword()), UserRole.FACULTY));
        }

        if (subjectRepository.count() == 0) {
            Subject mathematics = subjectRepository.save(new Subject("Mathematics", "Core algebra, calculus, and geometry problems."));
            Subject physics = subjectRepository.save(new Subject("Physics", "Mechanics, electricity, and optics practice questions."));
            Subject literature = subjectRepository.save(new Subject("Literature", "Poetry, prose, and critical analysis questions."));

            questionRepository.saveAll(List.of(
                new Question("Solve for x: 3x + 7 = 22", mathematics, "Algebra", DifficultyLevel.EASY, 5),
                new Question("Differentiate x^3 + 4x^2 - 3x + 7", mathematics, "Calculus", DifficultyLevel.MEDIUM, 8),
                new Question("Find the area of a triangle with sides 5, 6, and 7 units", mathematics, "Geometry", DifficultyLevel.MEDIUM, 7),
                new Question("Solve the quadratic equation x^2 - 5x + 6 = 0", mathematics, "Algebra", DifficultyLevel.EASY, 5),
                new Question("State Newton's second law of motion and explain it", physics, "Mechanics", DifficultyLevel.EASY, 6),
                new Question("Calculate the force required to accelerate a 10 kg body at 3 m/s^2", physics, "Mechanics", DifficultyLevel.MEDIUM, 8),
                new Question("Explain the behaviour of a current-carrying conductor in a magnetic field", physics, "Electricity", DifficultyLevel.HARD, 10),
                new Question("Describe the image formation by a convex lens", physics, "Optics", DifficultyLevel.MEDIUM, 7),
                new Question("Write a short note on the themes of Shakespeare's Macbeth", literature, "Drama", DifficultyLevel.EASY, 5),
                new Question("Compare the narrative voice in two prose passages", literature, "Prose", DifficultyLevel.MEDIUM, 8),
                new Question("Analyse the symbolism of the road in Frost's 'The Road Not Taken'", literature, "Poetry", DifficultyLevel.HARD, 12)
            ));
        }

        new ResourceDatabasePopulator(new ClassPathResource("db/sample-questions-mysql.sql"))
            .execute(dataSource);
    }
}
