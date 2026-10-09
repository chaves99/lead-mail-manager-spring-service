package cnpj.analyzr.payload;

import lombok.Builder;

@Builder
public record DashboardStatusRecord(LeadTotal leads, EmailStatus emails) {

    @Builder
    public static record EmailStatus(Long total, Long pending, Long success, Long error, Long clicked, Long opened) {
    }

    @Builder
    public static record LeadTotal(Integer totalLeads, Integer emailed,
            Integer totalOpened, Integer totalClicked, Integer leadsOpened,
            Integer leadsClicked, Integer unsubscribed, Integer unreachable) {

        public LeadTotal withRegistered(Integer registered) {
            return new LeadTotal(registered, emailed,
                    totalOpened, totalClicked, leadsOpened,
                    leadsClicked, unsubscribed, unreachable);
        }
    }
}
