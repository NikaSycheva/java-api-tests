package tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.People;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

@DisplayName("Простые JUnit тесты")
public class SimpleJunitTests {
    private static int age = 5;

    public static Stream<Arguments> namesContainsSFromMethodTests() {
        return Stream.of(
                Arguments.of(new People("Stas", 18, "male")),
                Arguments.of(new People("Sveta", 22, "female")),
                Arguments.of(new People("Misha", 36, "male")));
    }

    @BeforeAll
    static void setUp() {
        age = age + 10;
    }

    @AfterAll
    static void afterAll() {
    }

    @BeforeEach
    void before() {
    }

    @AfterEach
    void afterEach() {
    }

    @Test
    @DisplayName("Результат сложения 2+3=5")
    @Disabled("CARGOCORE-1405")
    void resultTwoPlusThreeTest() {
    }

    @ParameterizedTest
    @DisplayName("Имя содержит букву S")
    @CsvSource(value = {"Stas, 18, male", "Sveta, 22, female", "Misha, 36, male"})
    void namesContainsSTests(String name, String age, String sex) {
        System.out.println(name + " " + age + " " + sex + " ");
        Assertions.assertTrue(name.toLowerCase().contains("s"));
    }

    @ParameterizedTest
    @DisplayName("Имя содержит букву S через через файл")
    @CsvFileSource(resources = "/people.csv")
    void namesContainsSFromFileTests(String name, String age, String sex) {
        System.out.println(name + " " + age + " " + sex + " ");
        Assertions.assertTrue(name.toLowerCase().contains("s"));
    }

    @ParameterizedTest
    @DisplayName("Имя содержит букву S через метод")
    @MethodSource(value = "namesContainsSFromMethodTests")
    void namesContainsSFromMethodTests(People people) {
        System.out.println(people.getName() + " " + people.getAge() + " " + people.getSex() + " ");
        Assertions.assertTrue(people.getName().toLowerCase().contains("s"));
    }

    @ParameterizedTest()
    @DisplayName("Проверка на палиндром")
    @ValueSource(strings = {"12&^321", "53035", "pop", "Нажал кабан на баклажан", "привет"})
    void isPalindromTest(String string){
        Assertions.assertTrue(isPalindrome(string));
    }

    @Test
    @DisplayName("Библиотека Jackson")
    void readFromJsonTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("src/test/resources/stas.json");
        People stas = objectMapper.readValue(file, People.class);
        System.out.println(stas.getName() + " " + stas.getAge() + " " + stas.getSex());

        People sasha = new People("sasha", 28, "female");
        String json = objectMapper.writeValueAsString(sasha);
        System.out.println(json);
    }

    public boolean isPalindrome(String text){
        text = text.replaceAll("[^a-яА-Я0-9]", "");
        StringBuilder sb = new StringBuilder(text);
        String stringAfterReverse = sb.reverse().toString();
        return text.equalsIgnoreCase(stringAfterReverse);
    }
}