package cnpj.analyzr.lead;

import java.util.List;

public record LeadFilterRecord(
        Integer pageSize,
        Integer sentQuantity,
        List<String> emailExclude,
        List<String> emailInclude,
        Long lastIndex,
        Boolean shouldFetchUnsubscribed) {

    public LeadFilterRecord withLimit(int pageSize) {
        return new LeadFilterRecord(pageSize, sentQuantity,
                emailExclude, emailInclude, lastIndex, shouldFetchUnsubscribed);
    }
}
