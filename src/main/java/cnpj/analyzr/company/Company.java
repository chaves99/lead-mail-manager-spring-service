package cnpj.analyzr.company;

import lombok.Builder;

@Builder
public record Company(
        Long id,
        Long cnpj,
        String motherBranchIdentifier,
        String fantasyName,
        String registrySituation,
        String registrySituationDate,
        String reasonRegistrySituation,
        String outdoorCityName,
        String country,
        String startActivityDate,
        String cnaeTaxPrimary,
        String cnaeTaxSecondary,
        String addressStreetType,
        String addressStreet,
        String addressNumber,
        String addressComplement,
        String addressNeighborhood,
        String addressCode,
        String addressState,
        String addressCity,
        String telephoneCode1,
        String telephone1,
        String telephoneCode2,
        String telephone2,
        String email,
        String specialSituation,
        String specialSituationDate) {

    public enum RegistrySituation {
        NULL(),
        ACTIVE(),
        SUSPENDED(),
        UNABLE(),
        DOWNLOADED();
    }

}
