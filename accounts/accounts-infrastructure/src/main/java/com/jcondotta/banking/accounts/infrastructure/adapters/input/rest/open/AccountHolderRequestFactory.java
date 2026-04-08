package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open;

import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.*;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.DocumentCountryRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.DocumentTypeRequest;

import java.time.LocalDate;
import java.time.Month;

public class AccountHolderRequestFactory {

    public static AccountHolderRequest random(int i) {

        return new AccountHolderRequest(
            new PersonalInfoRequest(
                "Jefferson" + i,
                "Condotta",
                new IdentityDocumentRequest(
                    DocumentTypeRequest.FOREIGNER_ID,
                    DocumentCountryRequest.SPAIN,
                    "Y8451167S"
                ),
              LocalDate.of(1988, Month.JUNE, 10)
            ),
            new ContactInfoRequest(
                "jefferson" + i + "@email.com",
                "+4912345678" + i
            ),
            new AddressRequest(
                "Carrer de Mallorca",
                "401",
                "4B",
                "08013",
                "Barcelona"
            )
        );
    }
}