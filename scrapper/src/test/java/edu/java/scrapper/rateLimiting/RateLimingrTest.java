//package edu.java.scrapper.rateLimiting;
//
//import io.github.bucket4j.Bucket;
//import org.junit.ClassRule;
//import org.junit.Rule;
//import org.junit.rules.TestRule;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.testcontainers.containers.GenericContainer;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@RunWith(SpringRunner.class)
//public class GatewayRateLimiterTest {
//
//
//    @Rule
//    public TestRule benchmarkRun = new Bucket();
//
//    @ClassRule
//    public static MockServerContainer mockServer = new MockServerContainer();
//    @ClassRule
//    public static GenericContainer redis = new GenericContainer("redis:5.0.6").withExposedPorts(6379);
//
//    @Autowired
//    TestRestTemplate template;
//
//    @BeforeClass
//    public static void init() {
//        System.setProperty("spring.cloud.gateway.routes[0].id", "account-service");
//        System.setProperty("spring.cloud.gateway.routes[0].uri", "http://192.168.99.100:" + mockServer.getServerPort());
//        System.setProperty("spring.cloud.gateway.routes[0].predicates[0]", "Path=/account/**");
//        System.setProperty("spring.cloud.gateway.routes[0].filters[0]", "RewritePath=/account/(?<path>.*), /$\\{path}");
//        System.setProperty("spring.cloud.gateway.routes[0].filters[1].name", "RequestRateLimiter");
//        System.setProperty("spring.cloud.gateway.routes[0].filters[1].args.redis-rate-limiter.replenishRate", "10");
//        System.setProperty("spring.cloud.gateway.routes[0].filters[1].args.redis-rate-limiter.burstCapacity", "20");
//        System.setProperty("spring.redis.host", "192.168.99.100");
//        System.setProperty("spring.redis.port", "" + redis.getMappedPort(6379));
//        new MockServerClient(mockServer.getContainerIpAddress(), mockServer.getServerPort())
//                .when(HttpRequest.request()
//                                 .withPath("/1"))
//                .respond(response()
//                        .withBody("{\"id\":1,\"number\":\"1234567890\"}")
//                        .withHeader("Content-Type", "application/json"));
//    }
//
//    @Test
//    @BenchmarkOptions(warmupRounds = 0, concurrency = 6, benchmarkRounds = 600)
//    public void testAccountService() {
//        ResponseEntity<Account> r = template.exchange("/account/{id}", HttpMethod.GET, null, Account.class, 1);
//        LOGGER.info("Received: status->{}, payload->{}, remaining->{}", r.getStatusCodeValue(), r.getBody(), r.getHeaders().get("X-RateLimit-Remaining"));
//    }
//
//}