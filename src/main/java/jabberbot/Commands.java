package jabberbot;

/**
 * Created by Nataly on 04.05.2015.
 */
public enum Commands {
    HELP(""), TIME("time"), MONTH("month"), JOKE("petrov");

    private String command;

    Commands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
