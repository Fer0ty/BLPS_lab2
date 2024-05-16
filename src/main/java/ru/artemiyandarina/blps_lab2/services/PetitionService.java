package ru.artemiyandarina.blps_lab2.services;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import ru.artemiyandarina.blps_lab2.exceptions.NotFoundException;
import ru.artemiyandarina.blps_lab2.exceptions.PermissionDeniedException;
import ru.artemiyandarina.blps_lab2.models.ApproveStatus;
import ru.artemiyandarina.blps_lab2.models.Petition;
import ru.artemiyandarina.blps_lab2.models.Role;
import ru.artemiyandarina.blps_lab2.repositories.PetitionRepository;
import ru.artemiyandarina.blps_lab2.schemas.petition.PetitionCreate;
import ru.artemiyandarina.blps_lab2.schemas.petition.PetitionRead;
import ru.artemiyandarina.blps_lab2.services.mapping.PetitionMapper;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PetitionService {
    final PetitionRepository petitionRepository;
    final PetitionMapper petitionMapper;
    final SecurityService securityService;
    final PlatformTransactionManager transactionManager;

    @Autowired
    public PetitionService(PetitionRepository petitionRepository, PetitionMapper petitionMapper, SecurityService securityService, PlatformTransactionManager transactionManager) {
        this.petitionRepository = petitionRepository;
        this.petitionMapper = petitionMapper;
        this.securityService = securityService;
        this.transactionManager = transactionManager;
    }
public Set<PetitionRead> getConfirmed() {
    return petitionRepository.findAll()
            .stream().filter(petition -> petition.getApproveStatus().equals(ApproveStatus.CONFIRMED.toString()))
            .map(petitionMapper::mapEntityToPetitionRead)
            .collect(Collectors.toSet());
}

    @SneakyThrows
    public PetitionRead create(PetitionCreate schema) {
        BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
        try {
            btm.begin();
            Petition newPetition = petitionMapper.mapPetitionCreateToEntity(schema);
            newPetition.setOwner(securityService.getCurrentUser());
            petitionRepository.save(newPetition);
            btm.commit();
            return petitionMapper.mapEntityToPetitionRead(newPetition);
        } catch (HeuristicRollbackException | RollbackException | NotSupportedException | HeuristicMixedException |
                 SystemException e) {
            btm.rollback();
            throw new RuntimeException(e);
        }
    }

    public PetitionRead getById(Long id) {
        Petition petition = petitionRepository.findById(id).orElseThrow(() -> new NotFoundException(id, "Petition"));
        return petitionMapper.mapEntityToPetitionRead(petition);
    }
    public void delete(Long petitionID) {
        Petition petition = petitionRepository.findById(petitionID).orElseThrow(() -> new NotFoundException(petitionID, "Petition"));
        securityService.userRequired(petition.getOwner());
        petitionRepository.delete(petition);
    }
    @SneakyThrows
    public PetitionRead update(Long id, PetitionCreate updatedSchema) {
        BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
        try {
            btm.begin();
            Petition existingPetition = petitionRepository.findById(id).orElseThrow(() -> new NotFoundException(id, "Petition"));
            securityService.userRequired(existingPetition.getOwner());
            Petition updatedPetition = petitionMapper.mapPetitionCreateToEntity(updatedSchema);
            updatedPetition.setId(existingPetition.getId());
            updatedPetition.setOwner(existingPetition.getOwner());
            updatedPetition.setApproveStatus(ApproveStatus.ON_HOLD.toString());
            Petition savedPetition = petitionRepository.save(updatedPetition);
            btm.commit();
            return petitionMapper.mapEntityToPetitionRead(savedPetition);
        } catch (HeuristicRollbackException | RollbackException | NotSupportedException | HeuristicMixedException |
                 SystemException e) {
            btm.rollback();
            throw new RuntimeException(e);
        }
    }

    public PetitionRead updateStatus(Long id, ApproveStatus newStatus) throws SystemException {
        BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
        try {
            btm.begin();
            Petition existingPetition = petitionRepository.findById(id).orElseThrow(() -> new NotFoundException(id, "Petition"));
            if (!(securityService.getCurrentUser().getRole() == Role.ROLE_MODER)){
                throw new PermissionDeniedException();
            }
            // Проверка статуса петиции
            if (!existingPetition.getApproveStatus().equals(ApproveStatus.ON_HOLD.toString())) {
                throw new AccessDeniedException("Невозможно изменить статус петиции. Петиция должна быть в статусе ON_HOLD.");
            }
            existingPetition.setApproveStatus(newStatus.toString());
            Petition savedPetition = petitionRepository.save(existingPetition);
            btm.commit();
            return petitionMapper.mapEntityToPetitionRead(savedPetition);
        } catch (HeuristicRollbackException | RollbackException | NotSupportedException | HeuristicMixedException |
                 SystemException e) {
            btm.rollback();
            throw new RuntimeException(e);
        }
    }
}

