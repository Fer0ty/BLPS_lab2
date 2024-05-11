package ru.artemiyandarina.blps_lab2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.artemiyandarina.blps_lab2.exceptions.NotFoundException;
import ru.artemiyandarina.blps_lab2.models.Petition;
import ru.artemiyandarina.blps_lab2.repositories.PetitionRepository;
import ru.artemiyandarina.blps_lab2.schemas.petition.PetitionCreate;
import ru.artemiyandarina.blps_lab2.schemas.petition.PetitionRead;
import ru.artemiyandarina.blps_lab2.services.mapping.PetitionMapper;

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

    public Set<PetitionRead> getAll() {
        return petitionRepository.findAll().stream().map(petitionMapper::mapEntityToPetitionRead).collect(Collectors.toSet());
    }

    public PetitionRead create(PetitionCreate schema) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            Petition newPetition = petitionMapper.mapPetitionCreateToEntity(schema);
            newPetition.setOwner(securityService.getCurrentUser());
            petitionRepository.save(newPetition);
            transactionManager.commit(status);
            return petitionMapper.mapEntityToPetitionRead(newPetition);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    //проверка, что у петиции статус is_approved = true
    public PetitionRead getById(Long id) {
        Petition petition = petitionRepository.findById(id).orElseThrow(NotFoundException::new);
        return petitionMapper.mapEntityToPetitionRead(petition);
    }
    // Проверка, что владелец или модер.
    public void delete(Long documentId) {
        Petition petition = petitionRepository.findById(documentId).orElseThrow(NotFoundException::new);
        securityService.userRequired(petition.getOwner());
        petitionRepository.delete(petition);
    }
}

