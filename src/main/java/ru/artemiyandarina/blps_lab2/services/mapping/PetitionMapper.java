package ru.artemiyandarina.blps_lab2.services.mapping;

import org.springframework.stereotype.Service;
import ru.artemiyandarina.blps_lab2.exceptions.UnknownCountryException;
import ru.artemiyandarina.blps_lab2.models.Country;
import ru.artemiyandarina.blps_lab2.models.Petition;
import ru.artemiyandarina.blps_lab2.schemas.petition.PetitionBase;
import ru.artemiyandarina.blps_lab2.schemas.petition.PetitionCreate;
import ru.artemiyandarina.blps_lab2.schemas.petition.PetitionRead;

@Service
public class PetitionMapper {
    private final UserMapper userMapper;

    public PetitionMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    protected Petition mapPetitionBaseToEntity(PetitionBase schema, Petition entity) {
        entity.setTitle(schema.getTitle());
        entity.setDescription(schema.getDescription());
        return entity;
    }

    protected Petition mapPetitionBaseToEntity(PetitionBase schema) {
        return mapPetitionBaseToEntity(schema, new Petition());
    }

    public Petition mapPetitionCreateToEntity(PetitionCreate schema) {
        Petition newPetition = mapPetitionBaseToEntity(schema);
        try {
            newPetition.setCountry(Country.valueOf(schema.getCountry().toUpperCase()).toString());
        } catch (IllegalArgumentException e) {
            throw new UnknownCountryException();
        }
        // todo: почему blya страна не сетится.....
        return newPetition;
    }

    public PetitionRead mapEntityToPetitionRead(Petition entity) {
        PetitionRead schema = new PetitionRead();
        schema.setId(entity.getId());
        schema.setOwner(userMapper.mapEntityToUserRead(entity.getOwner()));
        schema.setTitle(entity.getTitle());
        schema.setDescription(entity.getDescription());
        schema.setCreationDate(entity.getCreationDate());
        schema.setCountry(entity.getCountry());
        return schema;
    }
}
