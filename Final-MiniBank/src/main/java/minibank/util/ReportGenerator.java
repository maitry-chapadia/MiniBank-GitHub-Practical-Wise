package minibank.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public final class ReportGenerator {
    private ReportGenerator() {}
    public static String generate(Path logsFolder, Path reportFile) throws IOException {
        Files.createDirectories(logsFolder);
        long deposits = 0, withdrawals = 0;
        List<String> details = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsFolder, "*.log")) {
            for (Path file : stream) {
                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length >= 3) {
                            try {
                                long amount = Long.parseLong(parts[2]);
                                if (parts[0].equals("DEPOSIT")) deposits += amount;
                                if (parts[0].equals("WITHDRAW")) withdrawals += amount;
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                details.add(file.getFileName() + " | size=" + attrs.size() + " bytes | modified=" + attrs.lastModifiedTime());
            }
        }
        String report = "===== END-OF-DAY REPORT =====\nTotal deposits: Rs. " + deposits +
            "\nTotal withdrawals: Rs. " + withdrawals + "\nNet change: Rs. " + (deposits - withdrawals) +
            "\nLog files:\n" + String.join("\n", details) + "\n=============================";
        Path parent = reportFile.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(reportFile, report + System.lineSeparator());
        return report;
    }
}
