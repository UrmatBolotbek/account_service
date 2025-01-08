package faang.school.accountservice.mapper.account;

import faang.school.accountservice.dto.RequestAccountDto;
import faang.school.accountservice.dto.ResponseAccountDto;
import faang.school.accountservice.model.account.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toAccount(RequestAccountDto account);

    ResponseAccountDto toResponseAccountDto(Account account);

}
