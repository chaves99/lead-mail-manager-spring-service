package cnpj.analyzr.payload;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetEmailRequestFilter extends GetCompanyRequestFilter {

    private Integer companyPerEmail;

    public GetEmailRequestFilter(Long lastIndex, int pageSize, Boolean neverSend, LocalDate lastCampaignBefore,
            List<String> emailExclude, List<String> emailInclude, List<String> fantasyNameExclude,
            List<String> fantasyNameInclude, List<CnaeRequestFilter> cnae, Integer motherBranchId,
            Integer companyPerEmail) {
        super(lastIndex, pageSize, neverSend, lastCampaignBefore, emailExclude, emailInclude, fantasyNameExclude,
                fantasyNameInclude, cnae, motherBranchId);
        this.companyPerEmail = companyPerEmail;
    }

    public GetEmailRequestFilter() {
        super(null, 0, null, null, null, null, null, null, null, null);
    }

    @Override
    public GetEmailRequestFilter withLimit(int pageSize) {
        return new GetEmailRequestFilter(getLastIndex(),
                pageSize,
                getNeverSend(),
                getLastCampaignBefore(),
                getEmailExclude(),
                getEmailInclude(),
                getFantasyNameExclude(),
                getFantasyNameInclude(),
                getCnae(),
                getMotherBranchId(),
                companyPerEmail);
    }


}
