package cnpj.analyzr.payload;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCompanyRequestFilter {
    private Long lastIndex;
    private int pageSize;
    private Boolean neverSend;
    private LocalDate lastCampaignBefore;
    private List<String> emailExclude;
    private List<String> emailInclude;
    private List<String> fantasyNameExclude;
    private List<String> fantasyNameInclude;
    private List<CnaeRequestFilter> cnae;
    private Integer motherBranchId;

    public static record CnaeRequestFilter(Long code) {
    }

    public boolean validMotherBranchId() {
        return getMotherBranchId() != null
                && (getMotherBranchId() == 1 || getMotherBranchId() == 2);
    }

    public GetCompanyRequestFilter withLimit(int pageSize) {
        return new GetCompanyRequestFilter(lastIndex,
                pageSize,
                neverSend,
                lastCampaignBefore,
                emailExclude,
                emailInclude,
                fantasyNameExclude,
                fantasyNameInclude,
                cnae,
                motherBranchId);
    }
}
