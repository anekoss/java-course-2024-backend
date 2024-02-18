package edu.java.bot.printer;

import edu.java.bot.printer.HtmlPrinter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThat;

public class HtmlPrinterTest {
    private final HtmlPrinter printer = new HtmlPrinter();

    @ParameterizedTest
    @CsvSource({"Привет!, <b>Привет!</b>", "Отслеживаемые сслыки, <b>Отслеживаемые сслыки</b>"})
    public void testBoldText(String text, String excepted) {
        assertThat(printer.boldText(text)).isEqualTo(excepted);
    }

    @ParameterizedTest
    @CsvSource({"/help, вывести описание комманд, '<b>/help</b> - вывести описание комманд\n'",
        "/list, показать список отслеживаемых ссылок, '<b>/list</b> - показать список отслеживаемых ссылок\n'"})
    public void testCommandDescriptionText(String name, String description, String excepted) {
        assertThat(printer.commandDescriptionText(name, description)).isEqualTo(excepted);
    }

    @ParameterizedTest
    @CsvSource({"https://www.tinkoff.ru/, <a href=\"https://www.tinkoff.ru/\">https://www.tinkoff.ru/</a>",
        "https://edu.tinkoff.ru, <a href=\"https://edu.tinkoff.ru\">https://edu.tinkoff.ru</a>"})
    public void testUrlText(String name, String excepted) {
        assertThat(printer.urlText(name)).isEqualTo(excepted);
    }

}
