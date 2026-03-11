package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder;

import com.jcondotta.application.core.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.mapper.AddJointHolderRequestControllerMapper;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class AddJointHolderControllerImpl implements AddJointHolderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddJointHolderControllerImpl.class);

    private final CommandHandler<AddJointHolderCommand> commandHandler;
    private final AddJointHolderRequestControllerMapper requestMapper;

    @Override
    public ResponseEntity<Void> createJointAccountHolder(UUID bankAccountId, AddJointHolderRequest restRequest) {
        LOGGER.atInfo()
                .setMessage("Received request to create a joint account holder for Bank Account ID")
                .addKeyValue("id", bankAccountId)
                .log();

        commandHandler.handle(requestMapper.toCommand(bankAccountId, restRequest));
        return ResponseEntity.ok().build();
    }
}