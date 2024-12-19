package faang.school.accountservice.mapper.interest_rate;

import faang.school.accountservice.dto.interest_rate.InterestRateRequestDto;
import faang.school.accountservice.dto.interest_rate.InterestRateResponseDto;
import faang.school.accountservice.model.interest_rate.InterestRate;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterestRateMapper {
    InterestRateResponseDto toDto(InterestRate interestRate);

    InterestRate toEntity(InterestRateRequestDto interestRateRequestDto);
}