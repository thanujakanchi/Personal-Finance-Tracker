
package com.finance;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final Path dataFile = Path.of("data", "transactions.csv");
    private final List<Transaction> transactions = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public TransactionService() throws IOException {
        Files.createDirectories(dataFile.getParent());

        if (!Files.exists(dataFile)) {
            Files.writeString(
                    dataFile,
                    "id,timestamp,type,category,amount,remarks\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE
            );
        } else {
            load();
        }
    }

    private synchronized void load() throws IOException {
        List<String> lines =
                Files.readAllLines(dataFile, StandardCharsets.UTF_8);

        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                continue;
            }

            try {
                List<String> f = parseCsv(lines.get(i));

                if (f.size() != 6) {
                    continue;
                }

                Transaction t = new Transaction(
                        Long.parseLong(f.get(0)),
                        LocalDateTime.parse(f.get(1)),
                        Transaction.Type.valueOf(f.get(2)),
                        f.get(3),
                        new BigDecimal(f.get(4)),
                        f.get(5)
                );

                transactions.add(t);
                nextId.set(Math.max(nextId.get(), t.getId() + 1));

            } catch (RuntimeException ex) {
                System.err.println(
                        "Skipping invalid transaction CSV row " + (i + 1)
                );
            }
        }
    }

    public synchronized Transaction add(Transaction input)
            throws IOException {

        if (input.getType() == null) {
            throw new IllegalArgumentException(
                    "Choose income or expense."
            );
        }

        if (input.getCategory() == null
                || input.getCategory().isBlank()) {
            throw new IllegalArgumentException(
                    "Category is required."
            );
        }

        if (input.getAmount() == null
                || input.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        BigDecimal amount = input.getAmount()
                .setScale(2, RoundingMode.HALF_UP);

        Transaction saved = new Transaction(
                nextId.getAndIncrement(),
                LocalDateTime.now(),
                input.getType(),
                input.getCategory().trim(),
                amount,
                input.getRemarks()
        );

        transactions.add(saved);

        Files.writeString(
                dataFile,
                toCsv(saved) + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        return saved;
    }

    public synchronized List<Transaction> list(
            LocalDate start, LocalDate end, String category) {

        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException(
                    "End date must be on or after start date."
            );
        }

        return transactions.stream()
                .filter(t -> start == null
                        || !t.getTimestamp().toLocalDate().isBefore(start))
                .filter(t -> end == null
                        || !t.getTimestamp().toLocalDate().isAfter(end))
                .filter(t -> category == null
                        || category.isBlank()
                        || t.getCategory().equalsIgnoreCase(category.trim()))
                .sorted(
                        Comparator.comparing(Transaction::getTimestamp)
                                .reversed()
                )
                .collect(Collectors.toList());
    }

    public synchronized List<Transaction> forMonth(YearMonth month) {
        return transactions.stream()
                .filter(t -> YearMonth.from(t.getTimestamp()).equals(month))
                .collect(Collectors.toList());
    }

    public Map<String, Object> summary(YearMonth month) {
        List<Transaction> selected = forMonth(month);

        BigDecimal income = total(selected, Transaction.Type.INCOME);
        BigDecimal expenses = total(selected, Transaction.Type.EXPENSE);

        Map<String, BigDecimal> byCategory =
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        selected.stream()
                .filter(t -> t.getType() == Transaction.Type.EXPENSE)
                .forEach(t -> byCategory.merge(
                        t.getCategory(),
                        t.getAmount(),
                        BigDecimal::add
                ));

        Map<String, BigDecimal> percentages =
                new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        byCategory.forEach((category, amount) ->
                percentages.put(
                        category,
                        expenses.signum() == 0
                                ? BigDecimal.ZERO
                                : amount.multiply(new BigDecimal("100"))
                                        .divide(
                                                expenses,
                                                2,
                                                RoundingMode.HALF_UP
                                        )
                )
        );

        return Map.of(
                "month", month.toString(),
                "income", income,
                "expenses", expenses,
                "net", income.subtract(expenses),
                "categoryTotals", byCategory,
                "categoryPercentages", percentages
        );
    }

    public synchronized List<Transaction> all() {
        return new ArrayList<>(transactions);
    }

    public synchronized byte[] exportTransactions() {
        StringBuilder out = new StringBuilder(
                "id,timestamp,type,category,amount,remarks\n"
        );

        transactions.stream()
                .sorted(
                        Comparator.comparing(Transaction::getTimestamp)
                                .reversed()
                )
                .forEach(t -> out.append(toCsv(t)).append('\n'));

        return out.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] exportSummary(YearMonth month) {
        Map<String, Object> s = summary(month);

        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> totals =
                (Map<String, BigDecimal>) s.get("categoryTotals");

        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> percentages =
                (Map<String, BigDecimal>) s.get("categoryPercentages");

        StringBuilder out = new StringBuilder("Metric,Value\n");

        out.append("Month,").append(month).append('\n');
        out.append("Total Income,").append(s.get("income")).append('\n');
        out.append("Total Expenses,").append(s.get("expenses")).append('\n');
        out.append("Net Cash Flow,").append(s.get("net"))
                .append("\n\nCategory,Expense Amount,Percentage\n");

        totals.forEach((category, amount) ->
                out.append(csv(category)).append(',')
                        .append(amount.toPlainString()).append(',')
                        .append(percentages.get(category).toPlainString())
                        .append("%\n")
        );

        return out.toString().getBytes(StandardCharsets.UTF_8);
    }

    private BigDecimal total(
            List<Transaction> list, Transaction.Type type) {

        return list.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String toCsv(Transaction t) {
        return t.getId() + ","
                + csv(t.getTimestamp().toString()) + ","
                + t.getType() + ","
                + csv(t.getCategory()) + ","
                + t.getAmount().toPlainString() + ","
                + csv(t.getRemarks());
    }

    private String csv(String value) {
        String v = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + v + "\"";
    }

    private List<String> parseCsv(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (quoted
                        && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }

        fields.add(field.toString());
        return fields;
    }
}
