package com.bosSearch.bossearch.Services;

import com.bosSearch.bossearch.Model.Person;
import com.bosSearch.bossearch.Repository.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServicePerson {

    private PersonRepo personRepo;

    @Autowired
    public ServicePerson(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Transactional
    public void savePerson(Person person) {
        personRepo.save(person);
    }

    public void saveAllPersons(List<Person> personList) {
        personRepo.saveAll(personList);
    }
}
