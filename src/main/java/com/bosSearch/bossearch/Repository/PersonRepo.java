package com.bosSearch.bossearch.Repository;

import com.bosSearch.bossearch.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {
    @Async
    @Override
    <S extends Person> S save(S person);

    @Query(value = "SELECT username FROM person", nativeQuery = true)
    List<String> findUsernames();
}
