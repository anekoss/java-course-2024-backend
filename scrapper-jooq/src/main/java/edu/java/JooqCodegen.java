package edu.java;

public class JooqCodegen {
    private JooqCodegen() {

    }

//    public static void main(String[] args) throws Exception {
//        Database database = new Database()
//            .withName("org.jooq.meta.extensions.liquibase.LiquibaseDatabase")
//            .withProperties(
//                new Property().withKey("rootPath").withValue("scrapper/src/main/resources/migrations"),
//                new Property().withKey("scripts").withValue("master.xml")
//            );
//        Generate options = new Generate()
//            .withGeneratedAnnotation(true)
//            .withGeneratedAnnotationDate(false)
//            .withNullableAnnotation(true)
//            .withNullableAnnotationType("org.jetbrains.annotations.Nullable")
//            .withNonnullAnnotation(true)
//            .withNonnullAnnotationType("org.jetbrains.annotations.NotNull")
//            .withJpaAnnotations(false)
//            .withValidationAnnotations(true)
//            .withSpringAnnotations(true)
//            .withConstructorPropertiesAnnotation(true)
//            .withConstructorPropertiesAnnotationOnPojos(true)
//            .withConstructorPropertiesAnnotationOnRecords(true)
//            .withFluentSetters(false)
//            .withDaos(false)
//            .withPojos(true);
//
//        Target target = new Target()
//            .withPackageName("edu.java.domain.jooq")
//            .withDirectory("scrapper/src/main/java");
//
//        Configuration configuration = new Configuration()
//            .withGenerator(
//                new Generator()
//                    .withDatabase(database)
//                    .withGenerate(options)
//                    .withTarget(target)
//            );
//
//        GenerationTool.generate(configuration);
//    }
}
