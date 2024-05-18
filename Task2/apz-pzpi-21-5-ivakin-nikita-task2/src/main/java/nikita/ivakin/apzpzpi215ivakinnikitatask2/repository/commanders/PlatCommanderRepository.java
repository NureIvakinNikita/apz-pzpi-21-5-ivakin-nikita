package nikita.ivakin.apzpzpi215ivakinnikitatask2.repository.commanders;

import nikita.ivakin.apzpzpi215ivakinnikitatask2.entity.commanders.PlatCommander;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatCommanderRepository extends JpaRepository<PlatCommander, Long> {

    Optional<PlatCommander> findPlatCommanderById(Integer id);

    Optional<PlatCommander> findPlatCommanderByEmail(String email);
}
