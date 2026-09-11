package cnpj.analyzr.csv.processor;

import java.util.List;
import java.util.Optional;

public interface CsvProcessor<T> {

    final static String[] CNAES_FILTER = { "5611204", "5611205", "5611202", "5612100", "5611201", "5611203",
            "5620103" };

    Optional<T> parse(String[] tokens);

    public void insertAll(List<T> list);

    int getBatchSize();

}
