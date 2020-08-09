package eu.levenproxy.netcache.utils;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class Logger {

    private final String date = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());
    private FileHandler fileHandler;

    private Terminal terminal;
    private LineReader lineReader;

    private final Map<String, Command> commandMap;

    public Logger() {
        File logDir = new File("logs/");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        int count = 1;
        for (File file : logDir.listFiles()) {
            if (file.getName().startsWith(date)) {
                count += 1;
            }
        }
        String logFile = new StringBuilder().append(logDir.getPath()).append("/").append(date).append("-").append(count).append(".log").toString();
        try {
            fileHandler = new FileHandler(logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.setUseParentHandlers(false);
        fileHandler.setFormatter(new LogFormatter());
        logger.addHandler(fileHandler);

        TerminalBuilder builder = TerminalBuilder.builder();
        builder.system(true);
        this.commandMap = new HashMap<>();
        try {
            terminal = builder.build();
            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .variable(LineReader.SECONDARY_PROMPT_PATTERN, " > ")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (true) {
                if(lineReader != null) {
                    String line = lineReader.readLine(" > ");
                    line = line.trim();
                    if (!hasConsoleCommand(line.toLowerCase())) {
                        info("Sorry didn't found a valid command for that!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerCommand(Command levenCommand) {
        for (String command : levenCommand.getCommands()) {
            if (!this.commandMap.containsKey(command)) {
                this.commandMap.put(command, levenCommand);
            }
        }
    }

    public boolean hasConsoleCommand(String input) {
        String[] args = input.split(" ");
        String command = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);
        if (this.commandMap.containsKey(command)) {
            this.commandMap.get(command).execute(command, args);
            return true;
        } else {
            return false;
        }
    }

    public void info(String message) {
        if (terminal != null) {
            message = getFormat(message, System.currentTimeMillis());
            if (lineReader != null) {
                lineReader.printAbove(message);
            } else {
                terminal.writer().println(message);
                terminal.flush();
            }
        }
        logger.info(message);
    }

    public void debug(String message) {
        if (terminal != null) {
            message = getFormat(message, System.currentTimeMillis());
            if (lineReader != null) {
                lineReader.printAbove(message);
            } else {
                terminal.writer().println(message);
                terminal.flush();
            }
        }
        logger.warning(message);
    }

    public void error(String message, Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        if (terminal != null) {
            message = getFormat(message, System.currentTimeMillis());
            if (lineReader != null) {
                lineReader.printAbove(message);
                lineReader.printAbove(exceptionAsString);
            } else {
                terminal.writer().println(message);
                terminal.writer().println(exceptionAsString);
                terminal.flush();
            }
        }
        logger.warning(new StringBuilder().append(message).append(": ").append(exceptionAsString).toString());
    }

    public void warn(String logMessage, Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        String throwableString = stringWriter.toString();
        if (terminal != null) {
            logMessage = getFormat(logMessage, System.currentTimeMillis());
            if (lineReader != null) {
                lineReader.printAbove(logMessage);
                lineReader.printAbove(throwableString);
            } else {
                terminal.writer().println(logMessage);
                terminal.writer().println(throwableString);
                terminal.flush();
            }
        }
        logger.warning(new StringBuilder().append(logMessage).append(": ").append(throwableString).toString());
    }

    public static String getFormat(String logMessage, long logTime) {
        String logTimeString = new SimpleDateFormat("HH:mm:ss").format(logTime);
        String logDateString = new SimpleDateFormat("yyyy-MM-dd").format(logTime);
        return "[" + logDateString + "] " + "[" + logTimeString + "] " + logMessage + System.lineSeparator();
    }

    public class LogFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            String time = new SimpleDateFormat("HH:mm:ss").format(record.getMillis());
            String date = new SimpleDateFormat("yyyy-MM-dd").format(record.getMillis());
            StringBuilder logLine = new StringBuilder().append("[").append(time).append("] ").append("[").append(date).append("] [").append(record.getLevel()).append("] ").append(record.getMessage());
            return logLine.toString() + System.lineSeparator();
        }
    }

    public static abstract class Command {

        private final String[] commands;

        public Command(String[] commands) {
            this.commands = commands;
        }

        public String[] getCommands() {
            return commands;
        }

        public abstract void execute(String command, String[] args);
    }
}
