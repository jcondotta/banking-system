package com.jcondotta.banking.accounts.infrastructure.adapters;

import com.jcondotta.banking.accounts.application.bankaccount.ports.output.facade.IbanGeneratorFacade;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import org.springframework.stereotype.Component;

@Component
public class IbanGenerator implements IbanGeneratorFacade {

  @Override
  public Iban generate() {
    return Iban.of("DE44500105175407324931");
  }
}
