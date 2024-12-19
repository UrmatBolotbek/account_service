package faang.school.accountservice.mapper.tariff;

import faang.school.accountservice.dto.tariff.TariffRequestDto;
import faang.school.accountservice.dto.tariff.TariffResponseDto;
import faang.school.accountservice.model.tariff.Tariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TariffMapper {
    @Mapping(target = "currentInterestRate", source = "interestRate.interestRate")
    TariffResponseDto toDto(Tariff tariff);

    @Mapping(target = "interestRate", ignore = true)
    Tariff toEntity(TariffRequestDto tariffRequestDto);
}
