package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.mapper.AddJointHolderRequestControllerMapper;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class AddJointHolderControllerImpl implements AddJointHolderController {

    private final CommandHandler<AddJointHolderCommand> commandHandler;
    private final AddJointHolderRequestControllerMapper requestMapper;

    @Override
    public ResponseEntity<Void> createJointAccountHolder(UUID bankAccountId, AddJointHolderRequest restRequest, UUID idempotencyKey) {
        commandHandler.handle(requestMapper.toCommand(bankAccountId, restRequest, IdempotencyKey.of(idempotencyKey)));
        return ResponseEntity.noContent().build();
    }
}
