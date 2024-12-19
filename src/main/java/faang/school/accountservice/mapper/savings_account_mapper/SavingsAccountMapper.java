package faang.school.accountservice.mapper.savings_account_mapper;

import faang.school.accountservice.dto.savings_account.SavingsAccountRequestDto;
import faang.school.accountservice.dto.savings_account.SavingsAccountResponseDto;
import faang.school.accountservice.model.savings_account.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "currentTariffId", source = "tariff.tariffType")
    SavingsAccountResponseDto toDto(SavingsAccount savingsAccount);
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "tariff", ignore = true)
    SavingsAccount toEntity(SavingsAccountRequestDto savingsAccountRequestDto);
}
