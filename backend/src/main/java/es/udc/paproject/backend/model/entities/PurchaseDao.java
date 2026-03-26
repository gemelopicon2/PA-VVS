package es.udc.paproject.backend.model.entities;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Repository
public interface PurchaseDao extends CrudRepository<Purchase, Long> {
    Slice<Purchase> findByUserIdOrderByDateDesc(User user, Pageable pageable);
}