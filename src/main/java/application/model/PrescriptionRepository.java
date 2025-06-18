package application.model;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrescriptionRepository extends MongoRepository<Prescription, Integer> {

  Prescription findByRxid(int rxid);

}
