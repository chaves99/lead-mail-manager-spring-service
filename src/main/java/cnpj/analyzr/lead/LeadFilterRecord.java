package cnpj.analyzr.lead;

import java.util.List;

import cnpj.analyzr.payload.GetCompanyRequestFilter.CnaeRequestFilter;

public record LeadFilterRecord(
        Integer pageSize,
        Integer sentQuantity,
        List<String> emailExclude,
        List<String> emailInclude,
        List<String> fantasyNameExclude,
        List<String> fantasyNameInclude,
        List<CnaeRequestFilter> cnae,
        Integer motherBranchId,
        Long lastIndex) {

    public boolean validMotherBranchId() {
        return motherBranchId() != null
                && (motherBranchId() == 1 || motherBranchId() == 2);
    }

    public LeadFilterRecord withLimit(int pageSize) {
        return new LeadFilterRecord(pageSize, sentQuantity,
                emailExclude, emailInclude,
                fantasyNameExclude, fantasyNameInclude,
                cnae, motherBranchId, lastIndex);
    }
}
