package edu.java.bot.printer;

public interface Formatter {

    String boldText(String text);

    String urlText(String text);

    String nextLine();

    String commandDescriptionText(String name, String description);

}
