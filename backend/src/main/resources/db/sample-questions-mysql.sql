INSERT INTO questions (question_text, subject_id, topic, difficulty_level, marks)
SELECT sample.question_text, subject.id, sample.topic, sample.difficulty_level, sample.marks
FROM (
    SELECT 'Mathematics' AS subject_name, 'Algebra' AS topic, 1 AS sequence_no, 'Simplify 4(2x - 3) + 5x and state the coefficient of x.' AS question_text, 'EASY' AS difficulty_level, 3 AS marks
    UNION ALL SELECT 'Mathematics', 'Algebra', 2, 'Solve 7x - 9 = 26 and verify your answer by substitution.', 'EASY', 4
    UNION ALL SELECT 'Mathematics', 'Algebra', 3, 'Factorise x^2 + 9x + 20 completely.', 'EASY', 4
    UNION ALL SELECT 'Mathematics', 'Algebra', 4, 'Solve the simultaneous equations 2x + y = 11 and x - y = 1.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Algebra', 5, 'Expand and simplify (3a - 2)(a + 5).', 'EASY', 4
    UNION ALL SELECT 'Mathematics', 'Algebra', 6, 'Solve the inequality 5 - 2x > 13 and represent its solution on a number line.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Algebra', 7, 'A taxi charges a fixed fee of 40 units plus 12 units per kilometre. Write a formula for the fare and find the fare for 8 kilometres.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Algebra', 8, 'Factorise 2x^2 - 7x + 3 and use the result to solve 2x^2 - 7x + 3 = 0.', 'MEDIUM', 6
    UNION ALL SELECT 'Mathematics', 'Algebra', 9, 'Rearrange v = u + at to make t the subject, then evaluate t when v = 25, u = 7 and a = 3.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Algebra', 10, 'The length of a rectangle is 3 centimetres more than twice its width. If its perimeter is 42 centimetres, find both dimensions.', 'MEDIUM', 6
    UNION ALL SELECT 'Mathematics', 'Algebra', 11, 'Solve the quadratic equation 3x^2 - 11x - 4 = 0 by factorisation.', 'HARD', 7
    UNION ALL SELECT 'Mathematics', 'Algebra', 12, 'A number is increased by 20 percent and the result is 84. Form an equation and find the original number.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Algebra', 13, 'Solve the system 3x + 2y = 16 and 5x - y = 7 using elimination.', 'HARD', 7
    UNION ALL SELECT 'Mathematics', 'Algebra', 14, 'For what values of k does x^2 + kx + 16 = 0 have exactly one real solution?', 'HARD', 8
    UNION ALL SELECT 'Mathematics', 'Calculus', 1, 'Differentiate f(x) = 5x^3 - 2x + 6.', 'EASY', 3
    UNION ALL SELECT 'Mathematics', 'Calculus', 2, 'Find dy/dx if y = 4x^2 + 7x - 9.', 'EASY', 3
    UNION ALL SELECT 'Mathematics', 'Calculus', 3, 'Evaluate the limit as x approaches 2 of (x^2 - 4)/(x - 2).', 'MEDIUM', 4
    UNION ALL SELECT 'Mathematics', 'Calculus', 4, 'Find the gradient of y = x^3 - 4x at x = 2.', 'EASY', 4
    UNION ALL SELECT 'Mathematics', 'Calculus', 5, 'Differentiate y = (2x + 1)(x^2 - 3) by first expanding the product.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Calculus', 6, 'Find the stationary points of f(x) = x^2 - 6x + 5.', 'MEDIUM', 6
    UNION ALL SELECT 'Mathematics', 'Calculus', 7, 'Find an antiderivative of 6x^2 - 4x + 3.', 'EASY', 4
    UNION ALL SELECT 'Mathematics', 'Calculus', 8, 'Evaluate the definite integral of 2x + 1 from x = 0 to x = 3.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Calculus', 9, 'A particle has displacement s(t) = t^3 - 6t^2 + 9t. Find its velocity at t = 2.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Calculus', 10, 'Differentiate y = (x^2 + 1)/x for x not equal to zero.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Calculus', 11, 'Use the chain rule to differentiate y = (3x^2 - 1)^4.', 'HARD', 6
    UNION ALL SELECT 'Mathematics', 'Calculus', 12, 'Find the equation of the tangent to y = x^2 + 2x at x = 1.', 'MEDIUM', 6
    UNION ALL SELECT 'Mathematics', 'Calculus', 13, 'Determine the intervals where f(x) = x^3 - 3x is increasing and decreasing.', 'HARD', 7
    UNION ALL SELECT 'Mathematics', 'Calculus', 14, 'A rectangle has perimeter 40 centimetres. Use calculus to find the dimensions that maximize its area.', 'HARD', 8
    UNION ALL SELECT 'Mathematics', 'Geometry', 1, 'Find the area of a parallelogram with base 12 centimetres and perpendicular height 7 centimetres.', 'EASY', 3
    UNION ALL SELECT 'Mathematics', 'Geometry', 2, 'Two angles of a triangle are 48 degrees and 67 degrees. Find the third angle.', 'EASY', 3
    UNION ALL SELECT 'Mathematics', 'Geometry', 3, 'Find the circumference of a circle with radius 5 centimetres. Give your answer in terms of pi.', 'EASY', 3
    UNION ALL SELECT 'Mathematics', 'Geometry', 4, 'A right triangle has legs of 9 centimetres and 12 centimetres. Find the hypotenuse.', 'EASY', 4
    UNION ALL SELECT 'Mathematics', 'Geometry', 5, 'Find the area of a trapezium with parallel sides 8 and 14 centimetres and height 6 centimetres.', 'MEDIUM', 4
    UNION ALL SELECT 'Mathematics', 'Geometry', 6, 'Find the volume of a cylinder of radius 3 centimetres and height 10 centimetres in terms of pi.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Geometry', 7, 'The exterior angle of a regular polygon is 24 degrees. How many sides does the polygon have?', 'MEDIUM', 4
    UNION ALL SELECT 'Mathematics', 'Geometry', 8, 'Find the distance between points A(2, 3) and B(8, 11) on a coordinate plane.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Geometry', 9, 'A sector has radius 6 centimetres and central angle 60 degrees. Find its arc length in terms of pi.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Geometry', 10, 'A solid cone has radius 4 centimetres and perpendicular height 9 centimetres. Find its volume in terms of pi.', 'MEDIUM', 5
    UNION ALL SELECT 'Mathematics', 'Geometry', 11, 'Prove that the base angles of an isosceles triangle are equal.', 'HARD', 6
    UNION ALL SELECT 'Mathematics', 'Geometry', 12, 'Find the area of a triangle with side lengths 13, 14 and 15 centimetres using Heron’s formula.', 'HARD', 7
    UNION ALL SELECT 'Mathematics', 'Geometry', 13, 'A chord of length 16 centimetres lies 6 centimetres from the centre of a circle. Find the circle’s radius.', 'HARD', 7
    UNION ALL SELECT 'Mathematics', 'Geometry', 14, 'A regular hexagon is inscribed in a circle of radius 8 centimetres. Find its perimeter and area.', 'HARD', 8
    UNION ALL SELECT 'Physics', 'Mechanics', 1, 'A 2 kilogram object accelerates at 3 metres per second squared. Calculate the net force acting on it.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Mechanics', 2, 'A cyclist travels 150 metres in 30 seconds. Calculate the cyclist’s average speed.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Mechanics', 3, 'Convert a speed of 72 kilometres per hour to metres per second.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Mechanics', 4, 'A 5 kilogram box is lifted vertically by 2 metres. Calculate the gain in gravitational potential energy using g = 9.8 metres per second squared.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Mechanics', 5, 'A car speeds up uniformly from 4 metres per second to 20 metres per second in 8 seconds. Find its acceleration.', 'EASY', 4
    UNION ALL SELECT 'Physics', 'Mechanics', 6, 'A 0.5 kilogram ball moves at 12 metres per second. Calculate its momentum.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Mechanics', 7, 'A force of 30 newtons moves a crate 5 metres in the direction of the force. Calculate the work done.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Mechanics', 8, 'A 1200 kilogram car travels at 15 metres per second. Calculate its kinetic energy.', 'MEDIUM', 5
    UNION ALL SELECT 'Physics', 'Mechanics', 9, 'A 60 kilogram passenger experiences a resultant force of 180 newtons. Find the passenger’s acceleration.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Mechanics', 10, 'A stone is dropped from rest and falls for 3 seconds. Estimate its speed just before impact using g = 9.8 metres per second squared.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Mechanics', 11, 'A 20 newton force acts 0.4 metres from a pivot. Calculate the moment of the force.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Mechanics', 12, 'A 1000 kilogram vehicle moving at 20 metres per second stops in 5 seconds. Find the average braking force.', 'HARD', 6
    UNION ALL SELECT 'Physics', 'Mechanics', 13, 'A 0.2 kilogram ball moving at 10 metres per second rebounds at 6 metres per second in the opposite direction. Find the magnitude of its change in momentum.', 'HARD', 6
    UNION ALL SELECT 'Physics', 'Mechanics', 14, 'A 25 newton force pulls a 10 kilogram block up a frictionless incline at 30 degrees. Calculate its acceleration, taking g = 9.8 metres per second squared.', 'HARD', 7
    UNION ALL SELECT 'Physics', 'Electricity', 1, 'A charge of 24 coulombs passes a point in 6 seconds. Calculate the current.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Electricity', 2, 'A 12 volt supply is connected across a 4 ohm resistor. Find the current.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Electricity', 3, 'Calculate the resistance of a component carrying 0.5 amperes when the potential difference is 9 volts.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Electricity', 4, 'A 6 ohm resistor carries a current of 2 amperes. Calculate its power dissipation.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Electricity', 5, 'Two resistors of 3 ohms and 5 ohms are connected in series. Find their total resistance.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Electricity', 6, 'Two 6 ohm resistors are connected in parallel. Calculate the equivalent resistance.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Electricity', 7, 'A 60 watt lamp operates for 5 minutes. Calculate the electrical energy it uses in joules.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Electricity', 8, 'A 2 kilowatt heater is used for 3 hours. Calculate the energy consumed in kilowatt-hours.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Electricity', 9, 'Explain how a fuse protects a household circuit when an excessive current flows.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Electricity', 10, 'A 12 volt battery supplies 0.8 amperes to a device. Calculate the device’s power.', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Electricity', 11, 'A 10 ohm resistor and a 15 ohm resistor are connected in parallel across 30 volts. Find the current in each branch.', 'MEDIUM', 5
    UNION ALL SELECT 'Physics', 'Electricity', 12, 'A battery has an electromotive force of 12 volts and internal resistance of 1 ohm. Find the terminal voltage when it supplies 2 amperes.', 'HARD', 6
    UNION ALL SELECT 'Physics', 'Electricity', 13, 'Three resistors of 2, 4 and 6 ohms are connected in series to an 18 volt supply. Calculate the current and the voltage across each resistor.', 'HARD', 7
    UNION ALL SELECT 'Physics', 'Electricity', 14, 'A 100 metre copper wire has a resistance of 0.2 ohms. Find its resistance when its length is doubled and its cross-sectional area is halved, assuming resistivity is constant.', 'HARD', 7
    UNION ALL SELECT 'Physics', 'Optics', 1, 'State the law of reflection relating the angle of incidence to the angle of reflection.', 'EASY', 2
    UNION ALL SELECT 'Physics', 'Optics', 2, 'Light travels at 3.0 × 10^8 metres per second in vacuum. Find its frequency when its wavelength is 600 nanometres.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Optics', 3, 'A ray enters glass from air and bends towards the normal. What does this indicate about its speed in glass?', 'EASY', 3
    UNION ALL SELECT 'Physics', 'Optics', 4, 'Calculate the refractive index of a material in which light travels at 2.0 × 10^8 metres per second.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Optics', 5, 'An object is placed 30 centimetres from a plane mirror. How far behind the mirror is its image?', 'EASY', 2
    UNION ALL SELECT 'Physics', 'Optics', 6, 'A converging lens has focal length 20 centimetres. Find its power in dioptres.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Optics', 7, 'A ray passes from glass into air at an angle greater than the critical angle. Name the phenomenon that occurs.', 'EASY', 2
    UNION ALL SELECT 'Physics', 'Optics', 8, 'An object is 30 centimetres from a convex lens of focal length 15 centimetres. Use the lens formula to find the image distance.', 'MEDIUM', 5
    UNION ALL SELECT 'Physics', 'Optics', 9, 'Describe how a prism separates white light into a spectrum.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Optics', 10, 'A concave mirror forms an image 40 centimetres from the mirror when the object is 20 centimetres away. Use the mirror formula to find its focal length.', 'MEDIUM', 5
    UNION ALL SELECT 'Physics', 'Optics', 11, 'An image formed by a lens is real, inverted and twice the object’s height. State the magnification and describe the relative object and image distances.', 'MEDIUM', 4
    UNION ALL SELECT 'Physics', 'Optics', 12, 'The critical angle for a transparent material is 42 degrees. Calculate its refractive index relative to air.', 'HARD', 6
    UNION ALL SELECT 'Physics', 'Optics', 13, 'An object is 18 centimetres from a converging lens and forms a real image 36 centimetres from it. Find the focal length and magnification.', 'HARD', 6
    UNION ALL SELECT 'Physics', 'Optics', 14, 'Explain total internal reflection and state the two conditions required for it to occur.', 'HARD', 5
    UNION ALL SELECT 'Literature', 'Drama', 1, 'How does a playwright use stage directions to shape an audience’s first impression of a character?', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Drama', 2, 'Explain how a soliloquy can reveal a character’s private conflict to the audience.', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Drama', 3, 'Identify two ways dramatic irony can create tension in a scene.', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Drama', 4, 'How can a change in setting between acts influence the mood of a play?', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Drama', 5, 'Discuss how an aside differs from a soliloquy and explain one effect of using an aside.', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Drama', 6, 'Choose a pivotal confrontation in a play you have studied and explain how dialogue builds tension.', 'MEDIUM', 5
    UNION ALL SELECT 'Literature', 'Drama', 7, 'How might a recurring prop gain symbolic meaning as a play develops?', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Drama', 8, 'Compare the language used by a powerful character and a subordinate character in the same scene.', 'MEDIUM', 5
    UNION ALL SELECT 'Literature', 'Drama', 9, 'Explain how pauses or silences can communicate conflict in a dramatic performance.', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Drama', 10, 'Analyse how a playwright can use mistaken identity to advance a plot and develop a theme.', 'MEDIUM', 5
    UNION ALL SELECT 'Literature', 'Drama', 11, 'Discuss whether the ending of a play you have studied resolves its central conflict convincingly.', 'HARD', 6
    UNION ALL SELECT 'Literature', 'Drama', 12, 'How does the playwright use contrasting characters to examine ideas about loyalty or ambition?', 'HARD', 7
    UNION ALL SELECT 'Literature', 'Drama', 13, 'Analyse the dramatic significance of the opening scene and connect it to later events.', 'HARD', 7
    UNION ALL SELECT 'Literature', 'Drama', 14, 'Evaluate how performance choices such as lighting, movement and vocal delivery could alter the interpretation of a key scene.', 'HARD', 8
    UNION ALL SELECT 'Literature', 'Prose', 1, 'What is the function of a narrator in a prose narrative?', 'EASY', 2
    UNION ALL SELECT 'Literature', 'Prose', 2, 'Explain how a writer can use sensory detail to establish a setting.', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Prose', 3, 'Define a flashback and describe one reason a novelist might use it.', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Prose', 4, 'How can sentence length influence the pace of a passage?', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Prose', 5, 'Describe how a character’s actions can reveal a trait without directly naming it.', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Prose', 6, 'Explain the difference between first-person and third-person limited narration.', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Prose', 7, 'How can contrast between a character’s words and actions create irony?', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Prose', 8, 'Analyse how a writer uses dialogue to distinguish two characters’ voices.', 'MEDIUM', 5
    UNION ALL SELECT 'Literature', 'Prose', 9, 'Discuss how the description of weather can reflect a character’s emotional state.', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Prose', 10, 'Explain how a minor character can act as a foil to the protagonist.', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Prose', 11, 'Analyse how the order in which information is revealed shapes suspense in a passage.', 'HARD', 6
    UNION ALL SELECT 'Literature', 'Prose', 12, 'Discuss how a novel’s point of view affects the reader’s sympathy for its central character.', 'HARD', 6
    UNION ALL SELECT 'Literature', 'Prose', 13, 'Evaluate how a writer develops the theme of belonging through setting and character relationships.', 'HARD', 7
    UNION ALL SELECT 'Literature', 'Prose', 14, 'Compare the opening and closing passages of a novel and explain how their relationship shapes its overall meaning.', 'HARD', 8
    UNION ALL SELECT 'Literature', 'Poetry', 1, 'Identify one effect of using alliteration in a line of poetry.', 'EASY', 2
    UNION ALL SELECT 'Literature', 'Poetry', 2, 'What is a metaphor? Give an example of how it can make an abstract idea vivid.', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Poetry', 3, 'Explain how the rhythm of a poem can support its subject matter.', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Poetry', 4, 'Distinguish between a simile and a metaphor.', 'EASY', 2
    UNION ALL SELECT 'Literature', 'Poetry', 5, 'How can enjambment affect the pace or meaning of a poem?', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Poetry', 6, 'Explain how the speaker of a poem differs from the poet.', 'EASY', 3
    UNION ALL SELECT 'Literature', 'Poetry', 7, 'Analyse the effect of a repeated phrase or refrain in a poem.', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Poetry', 8, 'How does a poem’s rhyme scheme influence its tone or structure?', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Poetry', 9, 'Explain how a poet can use personification to develop an image of nature.', 'MEDIUM', 4
    UNION ALL SELECT 'Literature', 'Poetry', 10, 'Discuss how a shift in tone between stanzas changes the reader’s understanding of a poem.', 'MEDIUM', 5
    UNION ALL SELECT 'Literature', 'Poetry', 11, 'Analyse the relationship between the central image and the theme of a poem you have studied.', 'HARD', 6
    UNION ALL SELECT 'Literature', 'Poetry', 12, 'How do sound devices and word choice work together to create the mood of a poem?', 'HARD', 6
    UNION ALL SELECT 'Literature', 'Poetry', 13, 'Compare how two poems present the experience of memory, using evidence from their language and structure.', 'HARD', 7
    UNION ALL SELECT 'Literature', 'Poetry', 14, 'Evaluate how a poem’s form, imagery and ending contribute to its interpretation.', 'HARD', 8
) AS sample
JOIN subjects AS subject
    ON subject.name = sample.subject_name
WHERE sample.sequence_no <= GREATEST(
    15 - (
        SELECT COUNT(*)
        FROM questions AS existing
        WHERE existing.subject_id = subject.id
          AND existing.topic = sample.topic
    ),
    0
)
AND NOT EXISTS (
    SELECT 1
    FROM questions AS duplicate
    WHERE duplicate.subject_id = subject.id
      AND duplicate.question_text = sample.question_text
);

SELECT subject.name AS subject, question.topic, COUNT(*) AS question_count
FROM questions AS question
JOIN subjects AS subject ON subject.id = question.subject_id
GROUP BY subject.name, question.topic
ORDER BY subject.name, question.topic;
