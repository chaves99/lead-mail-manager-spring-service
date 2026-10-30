package cnpj.analyzr.payload;

import java.time.LocalDate;
import java.util.List;

public record LeadFilterRecord(
        Integer pageSize,
        Integer sentQuantity,
        List<String> emailExclude,
        List<String> emailInclude,
        List<String> names,
        Boolean nameNotNull,
        Long lastIndex,
        Boolean shouldFetchUnsubscribed,
        Boolean emailOpened,
        LocalDate sentEmailBeforeDate) {

    public LeadFilterRecord withLimit(int pageSize) {
        return new LeadFilterRecord(pageSize, sentQuantity,
                emailExclude, emailInclude, names,
                nameNotNull, lastIndex, shouldFetchUnsubscribed,
                emailOpened, sentEmailBeforeDate);
    }
}
